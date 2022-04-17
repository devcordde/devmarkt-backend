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

import club.devcord.devmarkt.entities.auth.Operation;
import club.devcord.devmarkt.entities.auth.Permission;
import club.devcord.devmarkt.services.PermissionService;
import graphql.GraphQL;
import graphql.schema.GraphQLEnumType;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInterfaceType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLNamedSchemaElement;
import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLScalarType;
import graphql.schema.GraphQLSchemaElement;
import graphql.schema.GraphQLUnionType;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.http.server.exceptions.InternalServerException;
import jakarta.inject.Singleton;
import java.util.HashSet;
import java.util.stream.Stream;

@Singleton
public class PermissionUpdater implements ApplicationEventListener<StartupEvent> {

  public static final String PERMISSION_SEPARATOR = ".";

  private final PermissionService service;
  private final GraphQL graphQL;

  public PermissionUpdater(PermissionService service, GraphQL graphQL) {
    this.service = service;
    this.graphQL = graphQL;
  }

  @Override
  public void onApplicationEvent(StartupEvent event) {
    var schema = graphQL.getGraphQLSchema();
    var set = new HashSet<Permission>();
    addPermissions(set, schema.getQueryType(), Operation.QUERY);
    addPermissions(set, schema.getMutationType(), Operation.MUTATION);
    addPermissions(set, schema.getSubscriptionType(), Operation.SUBSCRIPTION);
    service.updatePermissions(set);
  }

  private void addPermissions(HashSet<Permission> set, GraphQLObjectType type,
      Operation operation) {
    if (type == null) {
      return;
    }
    type
        .getFieldDefinitions()
        .stream()
        .flatMap(fieldDefinition -> addLevel(fieldDefinition.getChildren().get(0),
            fieldDefinition.getName(), false))
        .forEach(s -> set.add(new Permission(-1, operation, s)));
  }

  private Stream<String> addLevel(GraphQLSchemaElement definition, String perm,
      boolean includeName) {
    if (definition instanceof GraphQLNonNull nonNull) {
      return addLevel(nonNull.getWrappedType(), perm, false);
    }
    if (definition instanceof GraphQLScalarType || definition instanceof GraphQLEnumType) {
      return Stream.of(perm);
    }
    if (definition instanceof GraphQLInterfaceType) {
      return Stream.of();
    }
    if (definition instanceof GraphQLFieldDefinition type) {
      return addChildren(type, perm + PERMISSION_SEPARATOR + type.getName(), false);
    }
    if (definition instanceof GraphQLNamedSchemaElement element) {
      perm += includeName
          ? PERMISSION_SEPARATOR + element.getName()
          : "";
      return addChildren(element, perm, element instanceof GraphQLUnionType);
    }
    if (definition instanceof GraphQLList) {
      return addChildren(definition, perm, false);
    }
    throw new InternalServerException("You're an idiot :3");
  }

  private Stream<String> addChildren(GraphQLSchemaElement schemaElement, String perm,
      boolean includeName) {
    return schemaElement.getChildren()
        .stream()
        .flatMap(e -> addLevel(e, perm, includeName));
  }
}
