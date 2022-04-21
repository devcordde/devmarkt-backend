/*
 * Copyright 2022 Contributors to the Devmarkt-Backend project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package club.devcord.devmarkt.auth;

import club.devcord.devmarkt.entities.auth.Permission;
import graphql.language.OperationDefinition.Operation;
import graphql.schema.GraphQLEnumType;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLImplementingType;
import graphql.schema.GraphQLInterfaceType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLNamedSchemaElement;
import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLScalarType;
import graphql.schema.GraphQLSchema;
import graphql.schema.GraphQLSchemaElement;
import graphql.schema.GraphQLUnionType;
import io.micronaut.http.server.exceptions.InternalServerException;
import jakarta.inject.Singleton;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Singleton
public class SchemaPermissionGenerator {

  public static final String PERMISSION_SEPARATOR = ".";

  public Collection<Permission> generate(GraphQLSchema schema) {
    var permissionSet = new HashSet<Permission>();
    permissionSet.addAll(generateOperation(schema.getQueryType(), Operation.QUERY));
    permissionSet.addAll(generateOperation(schema.getMutationType(), Operation.MUTATION));
    permissionSet.addAll(generateOperation(schema.getSubscriptionType(), Operation.QUERY));
    return permissionSet;
  }

  private Collection<Permission> generateOperation(GraphQLObjectType type, Operation operation) {
    if (type == null) {
      return Set.of();
    }
    return type.getFieldDefinitions()
        .stream()
        .flatMap(element -> generateLayer(element.getChildren().get(0), element.getName(),
            type)) // first child is the return type, we always have one here
        .map(s -> new Permission(-1, operation, s))
        .collect(Collectors.toSet());
  }

  private Stream<String> generateLayer(GraphQLSchemaElement element, String perm,
      GraphQLSchemaElement parent) {
    if (element instanceof GraphQLNonNull nonNull) {
      return generateLayer(nonNull.getWrappedType(), perm, nonNull); // unwrap single type
    }
    if (element instanceof GraphQLScalarType || element instanceof GraphQLEnumType) {
      return Stream.of(perm); // finalize this permission
    }
    if (element instanceof GraphQLList
        || element instanceof GraphQLUnionType) { // unwrap multiple union and list types
      return element.getChildren()
          .stream()
          .flatMap(e -> generateLayer(e, perm, element));
    }
    if (element instanceof GraphQLObjectType objectType) {
      return element.getChildren()
          .stream()
          .flatMap(e -> {
            // Skip interface fields, they will be added as part of the interface type later
            if (isParentInterfaceField(e, objectType)) {
              return Stream.of();
            }

            // object type children include implemented interface
            // they will be passed to the addLayer method later, but we want the interface name and not the
            // object type name in the permission, so we need to exclude them
            var newPerm = parent instanceof GraphQLUnionType && !(e instanceof GraphQLInterfaceType)
                ? addNode(perm, objectType.getName())
                : perm;

            return generateLayer(e, newPerm, objectType);
          });
    }
    if (element instanceof GraphQLNamedSchemaElement namedElement) { // add named schema elements e.g. fields or interface with its name to the permission
      return namedElement
          .getChildren()
          .stream()
          .flatMap(e -> generateLayer(e, addNode(perm, namedElement.getName()), namedElement));
    }
    throw new InternalServerException("You're an idiot :3");
  }

  private boolean isParentInterfaceField(GraphQLSchemaElement element, GraphQLNamedSchemaElement type) {
    return type instanceof GraphQLImplementingType implementingType
        && element instanceof GraphQLFieldDefinition named
        && implementingType
        .getInterfaces()
        .stream()
        .map(o -> (GraphQLInterfaceType) o)
        .anyMatch(i -> i.getFieldDefinition(named.getName()) != null);
  }

  private String addNode(String perm, String node) {
    return perm + PERMISSION_SEPARATOR + node;
  }

}
