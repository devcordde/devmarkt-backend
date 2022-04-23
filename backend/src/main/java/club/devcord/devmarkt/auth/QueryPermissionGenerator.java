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

import graphql.normalized.ExecutableNormalizedField;
import graphql.normalized.ExecutableNormalizedOperation;
import graphql.schema.GraphQLModifiedType;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLSchema;
import graphql.schema.GraphQLUnionType;
import java.util.stream.Stream;

public class QueryPermissionGenerator {

  private final GraphQLSchema schema;
  private final ExecutableNormalizedOperation operation;

  public QueryPermissionGenerator(GraphQLSchema schema,
      ExecutableNormalizedOperation operation) {
    this.schema = schema;
    this.operation = operation;
  }

  public Stream<String> generate() {
    return operation.getTopLevelFields()
        .stream()
        .flatMap(field -> generateLayer(field, "", false));
  }

  private Stream<String> generateLayer(ExecutableNormalizedField field, String perm,
      boolean union) {
    boolean isUnion = isUnion(field.getOneFieldDefinition(schema).getType());

    return field
        .getObjectTypeNames()
        .stream()
        .flatMap(s -> {
          var newPerm = union
              ? node(perm, node(s, field.getName()))
              : node(perm, field.getName());

          if (field.getChildren().isEmpty()) {
            return Stream.of(newPerm);
          }

          return field.getChildren()
              .stream()
              .flatMap(
                  executableNormalizedField -> generateLayer(executableNormalizedField, newPerm,
                      isUnion));
        });
  }

  private boolean isUnion(GraphQLOutputType type) {
    if (type instanceof GraphQLModifiedType modifiedType) {
      return modifiedType.getWrappedType() instanceof GraphQLUnionType;
    }
    return type instanceof GraphQLUnionType;
  }

  private String node(String perm, String next) {
    return perm.isBlank()
        ? next
        : perm + SchemaPermissionGenerator.PERMISSION_SEPARATOR + next;
  }

}
