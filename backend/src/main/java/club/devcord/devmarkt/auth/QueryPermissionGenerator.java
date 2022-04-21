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

import graphql.language.Field;
import graphql.language.FragmentDefinition;
import graphql.language.FragmentSpread;
import graphql.language.InlineFragment;
import graphql.language.OperationDefinition;
import graphql.language.OperationDefinition.Operation;
import graphql.language.Selection;
import graphql.language.SelectionSetContainer;
import graphql.language.SourceLocation;
import graphql.language.TypeName;
import graphql.schema.GraphQLImplementingType;
import graphql.schema.GraphQLInterfaceType;
import graphql.schema.GraphQLModifiedType;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import graphql.schema.GraphQLType;
import graphql.schema.GraphQLUnionType;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class QueryPermissionGenerator {

  private final Map<String, FragmentDefinition> fragments;
  private final GraphQLSchema schema;
  private final OperationDefinition operationDefinition;

  public QueryPermissionGenerator(
      Map<String, FragmentDefinition> fragments, GraphQLSchema schema,
      OperationDefinition definition) {
    this.fragments = fragments;
    this.schema = schema;
    this.operationDefinition = definition;
  }

  public Set<String> generate() {
    var permissions = new HashSet<String>();
    var selections = operationDefinition.getSelectionSet().getSelections();
    var operation = schemaOperation(operationDefinition.getOperation());
    for (var selection : selections) {
      if (selection instanceof Field field) {
        var newPermissions = generateOperationField(field, operation)
            .collect(Collectors.toSet());
        permissions.addAll(newPermissions);
      } else {
        throw new UnknownTypeException(null);
      }
    }
    return permissions;
  }

  private Stream<String> generateOperationField(Field field, GraphQLObjectType operation) {
    var returnType = operation.getFieldDefinition(field.getName()).getType();
    var union = isUnion(returnType);
    if (field.getSelectionSet() == null) {
      return Stream.of(field.getName());
    }
    return field.getSelectionSet().getSelections()
        .stream()
        .flatMap(selection -> generateLayer(selection, field.getName(), union));
  }

  private boolean isUnion(GraphQLType type) {
    if (type instanceof GraphQLModifiedType modifiedType) {
      return modifiedType.getWrappedType() instanceof GraphQLUnionType;
    }
    return type instanceof GraphQLUnionType;
  }

  private GraphQLObjectType schemaOperation(Operation operation) {
    return switch (operation) {
      case QUERY -> schema.getQueryType();
      case MUTATION -> schema.getMutationType();
      case SUBSCRIPTION -> schema.getSubscriptionType();
    };
  }

  private Stream<String> generateLayer(Selection<?> node, String perm, boolean unionMember) {
    if (node instanceof Field field) {
      if (field.getName().startsWith("__")) {
        return Stream.of(); // ignore introspection queries
      }
      var newPerm = node(perm, field.getName());
      if (field.getSelectionSet() == null) {
        return Stream.of(newPerm);
      }
      return field.getSelectionSet().getSelections()
          .stream()
          .flatMap(selection -> generateLayer(selection, newPerm, false));
    }

    if (node instanceof InlineFragment inlineFragment) {
      return fragmentSelections(inlineFragment.getTypeCondition(),
          inlineFragment, unionMember, perm);
    }

    if (node instanceof FragmentSpread fragmentSpread) {
      var fragment = fragments.get(fragmentSpread.getName());
      return fragmentSelections(fragment.getTypeCondition(), fragment, unionMember, perm);
    }
    throw new UnknownTypeException(node.getSourceLocation());
  }

  private Stream<String> fragmentSelections(TypeName typeName, SelectionSetContainer<?> setContainer,
      boolean unionMember, String perm) {
    var name = typeName.getName();
    var union = isUnion(schema.getType(name));
    return setContainer.getSelectionSet().getSelections()
        .stream()
        .flatMap(selection -> {
          var newPerm = unionMember
              ? node(perm, resolveName(name, nameFromSelection(selection)))
              : perm;

          return generateLayer(selection, newPerm, union);
        });
  }

  private String nameFromSelection(Selection<?> selection) {
    if (selection instanceof InlineFragment fragment) {
      return fragment.getTypeCondition().getName();
    }
    if (selection instanceof FragmentSpread fragmentSpread) {
      return fragmentSpread.getName();
    }
    if (selection instanceof Field field) {
      return field.getName();
    }
    throw new UnknownTypeException(selection.getSourceLocation());
  }

  private String resolveName(String name, String field) throws UnknownTypeException {
    var type = schema.getType(name);
    if (type instanceof GraphQLInterfaceType interfaceType) {
      return interfaceType.getName();
    }
    if (type instanceof GraphQLImplementingType implementingType) {
      return implementingType.getInterfaces()
          .stream()
          .map(o -> (GraphQLInterfaceType) o)
          .filter(interfaceType -> interfaceType.getFieldDefinition(field) != null)
          .findFirst()
          .map(GraphQLInterfaceType::getName)
          .orElse(name);
    }
    throw new UnknownTypeException(null);
  }

  private String node(String perm, String next) {
    return perm + SchemaPermissionGenerator.PERMISSION_SEPARATOR + next;
  }

  public static class UnknownTypeException extends RuntimeException {
    private final SourceLocation sourceLocation;

    public UnknownTypeException(SourceLocation sourceLocation) {
      this.sourceLocation = sourceLocation;
    }

    public SourceLocation sourceLocation() {
      return sourceLocation;
    }
  }

}
