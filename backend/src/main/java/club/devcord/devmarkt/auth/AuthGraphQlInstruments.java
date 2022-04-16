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

import club.devcord.devmarkt.auth.results.UnauthenticatedError;
import graphql.ExecutionResult;
import graphql.execution.instrumentation.InstrumentationContext;
import graphql.execution.instrumentation.SimpleInstrumentation;
import graphql.execution.instrumentation.SimpleInstrumentationContext;
import graphql.execution.instrumentation.parameters.InstrumentationExecuteOperationParameters;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class AuthGraphQlInstruments extends SimpleInstrumentation {

  private static final Logger LOGGER = LoggerFactory.getLogger(AuthGraphQlInstruments.class);

  private final AuthenticationBridge bridge;

  public AuthGraphQlInstruments(AuthenticationBridge authCache) {
    this.bridge = authCache;
  }

  @Override
  public InstrumentationContext<ExecutionResult> beginExecuteOperation(
      InstrumentationExecuteOperationParameters parameters) {
    var context = parameters.getExecutionContext();

    return SimpleInstrumentationContext.whenDispatched(future -> {
      var token = context.getExecutionInput().getVariables().get("Authorization");
      if (token == null) {
        LOGGER.warn("No token found on authorisation step");
        future.complete(new UnauthenticatedError().toResult());
        return;
      }

      bridge.authentication((String) token)
          .ifPresentOrElse(authentication -> {
            var userId = authentication.getName();
            context.getOperationDefinition()
                .getChildren()
                .stream()
                .forEach(System.out::println);
          }, () -> {
            future.complete(new UnauthenticatedError().toResult());
            LOGGER.warn("No authentication found for token {}", token);
          });

    });
  }
}
