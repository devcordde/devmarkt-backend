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
import graphql.ExecutionResult;
import io.micronaut.configuration.graphql.DefaultGraphQLInvocation;
import io.micronaut.configuration.graphql.GraphQLInvocation;
import io.micronaut.configuration.graphql.GraphQLInvocationData;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.token.jwt.validator.JwtTokenValidator;
import jakarta.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

@Singleton
@Replaces(DefaultGraphQLInvocation.class)
public class AuthGraphQlInvocation implements GraphQLInvocation {

  private final static Pattern USERID_REGEX = Pattern.compile("[a-zA-Z]+:[0-9]+");

  private final JwtTokenValidator validator;
  private final DefaultGraphQLInvocation defaultGraphQLInvocation;

  public AuthGraphQlInvocation(
      JwtTokenValidator validator,
      DefaultGraphQLInvocation defaultGraphQLInvocation) {
    this.validator = validator;
    this.defaultGraphQLInvocation = defaultGraphQLInvocation;
  }

  @Override
  public Publisher<ExecutionResult> invoke(GraphQLInvocationData invocationData,
      HttpRequest httpRequest, MutableHttpResponse<String> httpResponse) {

    var token = extractToken(invocationData.getVariables());
    return Mono.from(
      token
          .map(s -> Mono.from(validator.validateToken(s, httpRequest)).block())
          .flatMap(this::validateAndParseUserId)
          .map(userId -> {
            var variables = new HashMap<>(invocationData.getVariables());
            variables.put("Authorization", userId);
            return new GraphQLInvocationData(invocationData.getQuery(),
                invocationData.getOperationName(), variables);
          })
          .map(data -> defaultGraphQLInvocation.invoke(data, httpRequest, httpResponse))
          .orElseGet(() -> Mono.just(new UnauthorizedError().toResult()))
    );
  }

  private Optional<String> extractToken(Map<String, Object> vars) {
    return Optional.ofNullable(vars.get("Authorization"))
        .filter(o -> o instanceof String)
        .map(o -> (String) o);
  }

  private UserId parseUserIdUnsafe(String token) {
    var array = token.split(":", 2);
    var type = array[0];
    try {
      long id = Long.parseLong(array[1]);
      return new UserId(type, id);
    } catch (NumberFormatException e) {
      return null;
    }
  }

  private Optional<UserId> validateAndParseUserId(Authentication authentication) {
    return Optional.ofNullable(authentication.getName())
        .filter(name -> USERID_REGEX.matcher(name).matches())
        .map(this::parseUserIdUnsafe);
  }
}
