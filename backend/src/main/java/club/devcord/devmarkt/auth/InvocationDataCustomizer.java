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

import club.devcord.devmarkt.ws.SessionMetaData;
import graphql.ExecutionInput;
import io.micronaut.configuration.graphql.GraphQLExecutionInputCustomizer;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpResponse;
import jakarta.inject.Singleton;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

@Singleton
public class InvocationDataCustomizer implements GraphQLExecutionInputCustomizer {

  private final UserProvider provider;
  private final SessionMetaData sessionMetaData;

  public InvocationDataCustomizer(UserProvider provider, SessionMetaData sessionMetaData) {
    this.provider = provider;
    this.sessionMetaData = sessionMetaData;
  }

  @Override
  public Publisher<ExecutionInput> customize(ExecutionInput executionInput, HttpRequest httpRequest,
      MutableHttpResponse<String> httpResponse) {
    httpRequest.getHeaders().getAuthorization()
        .flatMap(provider::validate)
        .ifPresent(user -> {
          executionInput.getGraphQLContext().put("user", user);
          var session = sessionMetaData.getHttpRequestWebSocketSessionMap().get(httpRequest);
          if (session != null) {
            sessionMetaData.getUserSessions().put(user.id(), session);
          }
        });
    sessionMetaData.getHttpRequestWebSocketSessionMap().remove(httpRequest);
    return Mono.just(executionInput);
  }
}
