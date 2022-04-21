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
import jakarta.inject.Singleton;
import java.util.Set;
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
    var generator = new QueryPermissionGenerator(context.getFragmentsByName(), context.getGraphQLSchema(),
        context.getOperationDefinition());

    var permissions = generator.generate();
    permissions.forEach(System.out::println);

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
}
