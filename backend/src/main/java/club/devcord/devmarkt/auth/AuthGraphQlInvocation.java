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

import club.devcord.devmarkt.auth.error.AuthError;
import club.devcord.devmarkt.auth.error.InvalidTokenError;
import club.devcord.devmarkt.auth.error.UnauthenticatedError;
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
import java.util.Map;
import java.util.regex.Pattern;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Singleton
@Replaces(DefaultGraphQLInvocation.class)
public class AuthGraphQlInvocation implements GraphQLInvocation {

  private final static Pattern USERID_REGEX = Pattern.compile("[a-zA-Z]+:[0-9]+");

  private final JwtTokenValidator validator;
  private final DefaultGraphQLInvocation defaultGraphQLInvocation;
  private final AuthenticationBridge cache;

  public AuthGraphQlInvocation(
      JwtTokenValidator validator,
      DefaultGraphQLInvocation defaultGraphQLInvocation,
      AuthenticationBridge cache) {
    this.validator = validator;
    this.defaultGraphQLInvocation = defaultGraphQLInvocation;
    this.cache = cache;
  }

  @Override
  public Publisher<ExecutionResult> invoke(GraphQLInvocationData invocationData,
      HttpRequest httpRequest, MutableHttpResponse<String> httpResponse) {

    return Flux.from(extractAuth(invocationData.getVariables()))
        .flatMap(token -> Flux.from(validator.validateToken(token, httpRequest))
            .flatMap(authentication -> Flux.from(validateUserId(authentication))
                .doOnNext(a -> cache.authentication(token, a))
                .flatMap(a -> defaultGraphQLInvocation.invoke(invocationData, httpRequest, httpResponse))
                .switchIfEmpty(
                    Mono.just(new InvalidTokenError(token, AuthError.INVALID_USERID).toResult()))
            )
            .switchIfEmpty(
                Mono.just(new InvalidTokenError(token, AuthError.INVALID_TOKEN).toResult()))
        )
        .switchIfEmpty(Mono.just(new UnauthenticatedError().toResult()));
  }

  private Publisher<Authentication> validateUserId(Authentication authentication) {
    return Mono.just(authentication.getName())
        .filter(name -> name != null && USERID_REGEX.matcher(name).matches())
        .map(s -> authentication);
  }

  private Publisher<String> extractAuth(Map<String, Object> vars) {
    return Mono.justOrEmpty(vars.get("Authorization"))
        .filter(o -> o instanceof String)
        .map(o -> (String) o);
  }
}
