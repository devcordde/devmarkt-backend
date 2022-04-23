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
import graphql.schema.GraphQLInterfaceType;
import graphql.schema.GraphQLList;
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
        .flatMap(element -> generateLayer(element.getType(), element.getName(),
            false)) // first child is the return type, we always have one here
        .map(s -> new Permission(-1, operation, s))
        .collect(Collectors.toSet());
  }

  private Stream<String> generateLayer(GraphQLSchemaElement element, String perm,
      boolean union) {
    if (element instanceof GraphQLInterfaceType) {
      return Stream.of(); // ignore interfaces
    }
    if (element instanceof GraphQLNonNull nonNull) {
      return generateLayer(nonNull.getWrappedType(), perm, false); // unwrap single type
    }
    if (element instanceof GraphQLScalarType || element instanceof GraphQLEnumType) {
      return Stream.of(perm); // finalize this permission
    }
    if (element instanceof GraphQLList
        || element instanceof GraphQLUnionType) { // unwrap multiple union and list types
      return element.getChildren()
          .stream()
          .flatMap(e -> generateLayer(e, perm, element instanceof GraphQLUnionType));
    }
    if (element instanceof GraphQLObjectType objectType) {
      return element.getChildren()
          .stream()
          .flatMap(e -> {
            var newPerm = union
                ? addNode(perm, objectType.getName())
                : perm;

            return generateLayer(e, newPerm, false);
          });
    }
    if (element instanceof GraphQLFieldDefinition namedElement) {
      return generateLayer(namedElement.getType(), addNode(perm, namedElement.getName()), false);
    }
    throw new InternalServerException("You're an idiot :3");
  }

  private String addNode(String perm, String node) {
    return perm + PERMISSION_SEPARATOR + node;
  }

}
