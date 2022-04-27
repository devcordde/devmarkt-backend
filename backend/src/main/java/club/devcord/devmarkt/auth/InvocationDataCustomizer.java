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

import club.devcord.devmarkt.services.UserService;
import graphql.ExecutionInput;
import io.micronaut.configuration.graphql.GraphQLExecutionInputCustomizer;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpResponse;
import jakarta.inject.Singleton;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

@Singleton
public class InvocationDataCustomizer implements GraphQLExecutionInputCustomizer {

  private final UserService userService;
  private final UserIdValidator validator;

  public InvocationDataCustomizer(UserService userService,
      UserIdValidator validator) {
    this.userService = userService;
    this.validator = validator;
  }

  @Override
  public Publisher<ExecutionInput> customize(ExecutionInput executionInput, HttpRequest httpRequest,
      MutableHttpResponse<String> httpResponse) {
    httpRequest.getHeaders().getAuthorization()
            .map(validator::parseAndValidate)
            .flatMap(userService::findDirect)
            .ifPresent(user -> executionInput.getGraphQLContext().put("user", user));
    return Mono.just(executionInput);
  }
}
