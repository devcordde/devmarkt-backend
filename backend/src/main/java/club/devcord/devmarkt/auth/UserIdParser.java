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
import java.util.Set;
import java.util.regex.Pattern;
import reactor.core.publisher.Mono;

@Singleton
public class UserIdParser {

  private final static Pattern USERID_REGEX = Pattern.compile("[a-zA-Z]+:[0-9]+");
  private final static String AUTHORIZATION_KEY = "Authorization";

  private final JwtTokenValidator validator;

  public UserIdParser(JwtTokenValidator validator) {
    this.validator = validator;
  }

  public UserId parseAndValidate(ExecutionInput input) {
    return extractUserId(input.getVariables());
  }

  private UserId extractUserId(Map<String, Object> vars) {
    if (vars.get(AUTHORIZATION_KEY) instanceof String token) {
      return validateTokenAndParseUserId(token);
    }
    return abort();
  }

  private UserId validateTokenAndParseUserId(String token) {
    var auth = Mono.from(validator.validateToken(token, null)).block();
    if (auth != null) {
      return validateAndParseUserId(auth);
    }
    return abort();
  }

  private UserId parseUserIdUnsafe(String token) {
    var array = token.split(":", 2);
    var type = array[0];
    try {
      long id = Long.parseLong(array[1]);
      return new UserId(type, id);
    } catch (NumberFormatException e) {
      return abort();
    }
  }

  private UserId validateAndParseUserId(Authentication authentication) {
    var token = authentication.getName();
    if (USERID_REGEX.matcher(token).matches()) {
      return parseUserIdUnsafe(token);
    }
    return abort();
  }

  private <T> T abort() {
    throw new AbortExecutionException(Set.of(new UnauthorizedError()));
  }
}
