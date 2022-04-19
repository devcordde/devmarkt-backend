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

import club.devcord.devmarkt.auth.error.SelectionNoFieldError;
import club.devcord.devmarkt.auth.error.UnauthorizedError;
import club.devcord.devmarkt.entities.auth.UserId;
import club.devcord.devmarkt.services.PermissionService;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQLError;
import graphql.execution.AbortExecutionException;
import graphql.execution.instrumentation.InstrumentationContext;
import graphql.execution.instrumentation.SimpleInstrumentation;
import graphql.execution.instrumentation.SimpleInstrumentationContext;
import graphql.execution.instrumentation.parameters.InstrumentationExecuteOperationParameters;
import graphql.language.Field;
import graphql.language.FragmentDefinition;
import graphql.language.FragmentSpread;
import graphql.language.InlineFragment;
import graphql.language.Selection;
import graphql.language.SelectionSetContainer;
import graphql.validation.ValidationError;
import graphql.validation.ValidationErrorType;
import jakarta.inject.Singleton;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class AuthGraphQlInstruments extends SimpleInstrumentation {

  private static final Logger LOGGER = LoggerFactory.getLogger(AuthGraphQlInstruments.class);

  private final PermissionService permissionService;

  public AuthGraphQlInstruments(PermissionService permissionService) {
    this.permissionService = permissionService;
  }

  @Override
  public InstrumentationContext<ExecutionResult> beginExecuteOperation(
      InstrumentationExecuteOperationParameters parameters) {
    var context = parameters.getExecutionContext();
    var userId = extractUserId(context.getExecutionInput());
    var permissions = new HashSet<String>();
    for (var selection : context.getOperationDefinition().getSelectionSet().getSelections()) {
      if (!(selection instanceof Field field)) {
        LOGGER.warn("selection {} is no instance of Field", selection);
        abort(new SelectionNoFieldError(selection));
        return SimpleInstrumentationContext.noOp();
      }
      if (field.getSelectionSet() == null) {
        permissions.add(field.getName());
        continue;
      }
      permissions.addAll(
          addChildren(field, field.getName(), context.getFragmentsByName()).collect(
              Collectors.toSet()));
    }

    permissions
        .forEach(System.out::println);
    //TODO: implement check
    return SimpleInstrumentationContext.noOp();
  }

  private UserId extractUserId(ExecutionInput input) {
    var value = input.getVariables().get("Authorization");
    if (!(value instanceof UserId userId)) {
      abort(new UnauthorizedError());
      return null;
    }
    return userId;
  }

  private void abort(GraphQLError error) {
    throw new AbortExecutionException(Set.of(error));
  }

  private Stream<String> addLevel(Selection<?> node, String perm,
      Map<String, FragmentDefinition> fragments) {
    if (node instanceof Field field) {
      if (field.getName().startsWith("__")) {
        return Stream.of();
      }
      perm += PermissionUpdater.PERMISSION_SEPARATOR + field.getName();
      if (field.getSelectionSet() == null) {
        return Stream.of(perm);
      }
      return addChildren(field, perm, fragments);
    }
    if (node instanceof InlineFragment inlineFragment) {
      perm += PermissionUpdater.PERMISSION_SEPARATOR + inlineFragment.getTypeCondition().getName();
      return addChildren(inlineFragment, perm, fragments);
    }
    if (node instanceof FragmentSpread fragmentSpread) {
      var fragment = fragments.get(fragmentSpread.getName());
      perm += PermissionUpdater.PERMISSION_SEPARATOR + fragment.getTypeCondition().getName();
      return addChildren(fragment, perm, fragments);
    }
    abort(new ValidationError(ValidationErrorType.UnknownType, node.getSourceLocation(),
        "Unknown Type during permission generation"));
    return null;
  }

  private Stream<String> addChildren(SelectionSetContainer<?> field, String perm,
      Map<String, FragmentDefinition> fragments) {
    return field
        .getSelectionSet()
        .getSelections()
        .stream()
        .flatMap(node -> addLevel(node, perm, fragments));
  }
}
