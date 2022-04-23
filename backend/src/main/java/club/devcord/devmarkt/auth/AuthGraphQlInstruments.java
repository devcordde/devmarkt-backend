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

import club.devcord.devmarkt.auth.QueryPermissionGenerator.UnknownTypeException;
import club.devcord.devmarkt.auth.error.ForbiddenError;
import club.devcord.devmarkt.auth.error.UnauthorizedError;
import club.devcord.devmarkt.entities.auth.UserId;
import club.devcord.devmarkt.services.UserService;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQLError;
import graphql.execution.AbortExecutionException;
import graphql.execution.instrumentation.InstrumentationContext;
import graphql.execution.instrumentation.SimpleInstrumentation;
import graphql.execution.instrumentation.SimpleInstrumentationContext;
import graphql.execution.instrumentation.parameters.InstrumentationExecuteOperationParameters;
import graphql.validation.ValidationError;
import graphql.validation.ValidationErrorType;
import jakarta.inject.Singleton;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class AuthGraphQlInstruments extends SimpleInstrumentation {

  private static final Logger LOGGER = LoggerFactory.getLogger(AuthGraphQlInstruments.class);

  private final UserService userService;

  public AuthGraphQlInstruments(UserService userService) {
    this.userService = userService;
  }

  @Override
  public InstrumentationContext<ExecutionResult> beginExecuteOperation(
      InstrumentationExecuteOperationParameters parameters) {
    var context = parameters.getExecutionContext();
    var userId = extractUserId(context.getExecutionInput());
    var operation = context.getOperationDefinition().getOperation();
    var generator = new QueryPermissionGenerator(context.getFragmentsByName(),
        context.getGraphQLSchema(),
        context.getNormalizedQueryTree().get());

    try {
      var permissions = generator.generate();
      System.out.println(permissions);
      var unauthorizedPermissions = userService.checkPermissions(operation,
          permissions, userId);
      var forbiddenErrors = unauthorizedPermissions
          .stream()
          .map(s -> new ForbiddenError(operation, s))
          .map(forbiddenError -> (GraphQLError) forbiddenError)
          .collect(Collectors.toSet());
      if (!forbiddenErrors.isEmpty()) {
        abort(forbiddenErrors);
      }
    } catch (UnknownTypeException typeException) {
      var error = ValidationError.newValidationError()
          .validationErrorType(ValidationErrorType.UnknownType)
          .sourceLocation(typeException.sourceLocation())
          .description("Unknown type found during permission generation.")
          .build();
      abort(error);
    }
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

  private void abort(Collection<GraphQLError> errors) {
    throw new AbortExecutionException(errors);
  }

  private void abort(GraphQLError error) {
    abort(Set.of(error));
  }
}
