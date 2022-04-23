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
import graphql.ExecutionInput;
import graphql.execution.AbortExecutionException;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.token.jwt.validator.JwtTokenValidator;
import jakarta.inject.Singleton;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import reactor.core.publisher.Mono;

@Singleton
public class UserIdParser {

  private final static Pattern USERID_REGEX = Pattern.compile("[a-zA-Z]+:[0-9]+");

  private final JwtTokenValidator validator;

  public UserIdParser(JwtTokenValidator validator) {
    this.validator = validator;
  }

  public UserId parseAndValidate(ExecutionInput input) {
    return extractToken(input.getVariables())
        .map(s -> Mono.from(validator.validateToken(s, null)).block())
        .flatMap(this::validateAndParseUserId)
        .orElseThrow(() -> new AbortExecutionException(Set.of(new UnauthorizedError())));
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
