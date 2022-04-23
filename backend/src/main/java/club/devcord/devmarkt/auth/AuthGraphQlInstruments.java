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

import club.devcord.devmarkt.auth.error.ForbiddenError;
import club.devcord.devmarkt.services.UserService;
import graphql.ExecutionResult;
import graphql.GraphQLError;
import graphql.execution.AbortExecutionException;
import graphql.execution.instrumentation.InstrumentationContext;
import graphql.execution.instrumentation.SimpleInstrumentation;
import graphql.execution.instrumentation.SimpleInstrumentationContext;
import graphql.execution.instrumentation.parameters.InstrumentationExecuteOperationParameters;
import jakarta.inject.Singleton;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class AuthGraphQlInstruments extends SimpleInstrumentation {

  private static final Logger LOGGER = LoggerFactory.getLogger(AuthGraphQlInstruments.class);

  private final UserService userService;
  private final UserIdParser userIdParser;

  public AuthGraphQlInstruments(UserService userService, UserIdParser parser) {
    this.userService = userService;
    this.userIdParser = parser;
  }

  @Override
  public InstrumentationContext<ExecutionResult> beginExecuteOperation(
      InstrumentationExecuteOperationParameters parameters) {
    var context = parameters.getExecutionContext();
    var operation = context.getOperationDefinition().getOperation();
    var permissions = new QueryPermissionGenerator(context.getGraphQLSchema(),
        context.getNormalizedQueryTree().get())
        .generate()
        .filter(s -> !s.startsWith("__"))  // allow root introspections
        .collect(Collectors.toSet());

    if (permissions.isEmpty()) {
      return SimpleInstrumentationContext.noOp();
    }

    var userId = userIdParser.parseAndValidate(context.getExecutionInput());
    var forbiddenErrors = userService.checkPermissions(operation,
            permissions.stream(), userId)
        .map(s -> new ForbiddenError(operation, s))
        .map(forbiddenError -> (GraphQLError) forbiddenError)
        .collect(Collectors.toSet());
    if (!forbiddenErrors.isEmpty()) {
      throw new AbortExecutionException(forbiddenErrors);
    }
    return SimpleInstrumentationContext.noOp();
  }
}
