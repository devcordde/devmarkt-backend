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

import club.devcord.devmarkt.entities.auth.UserId;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.token.jwt.validator.JwtTokenValidator;
import jakarta.inject.Singleton;
import java.security.Principal;
import java.util.regex.Pattern;
import reactor.core.publisher.Mono;

@Singleton
public class UserIdValidator {

  private final static Pattern USERID_REGEX = Pattern.compile("[a-zA-Z]+:[0-9]+");

  private final JwtTokenValidator validator;

  public UserIdValidator(JwtTokenValidator validator) {
    this.validator = validator;
  }

  public UserId validateUserIdFromToken(String token) {

    Mono<Authentication> authenticationMono = Mono.<Authentication>from(validator.validateToken(token, null));

    return authenticationMono
        .map(Authentication::getName)
        .mapNotNull(this::validateUserId)
        .block();
  }

  public UserId validateUserId(String id) {
    if (USERID_REGEX.matcher(id).matches()) {
      return parseUserIdUnsafe(id);
    }
    return null;
  }

  private UserId parseUserIdUnsafe(String rawId) {
    var array = rawId.split(":", 2);
    var type = array[0];
    try {
      long id = Long.parseLong(array[1]);
      return new UserId(type, id);
    } catch (NumberFormatException e) {
      return null;
    }
  }
}
