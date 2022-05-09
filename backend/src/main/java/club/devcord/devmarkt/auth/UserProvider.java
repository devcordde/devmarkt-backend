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

import club.devcord.devmarkt.entities.auth.User;
import club.devcord.devmarkt.services.UserService;
import jakarta.inject.Singleton;
import java.util.Optional;
import java.util.regex.Pattern;

@Singleton
public class UserProvider {

  private static final Pattern TOKEN_PATTERN = Pattern.compile("(Self|Foreign) .+");

  private final UserIdValidator userIdValidator;
  private final UserService userService;

  public UserProvider(UserIdValidator userIdValidator,
      UserService userService) {
    this.userIdValidator = userIdValidator;
    this.userService = userService;
  }

  public Optional<User> validate(String token) {
    if (!TOKEN_PATTERN.matcher(token).matches()) {
      return Optional.empty();
    }

    var split = token.split(" ", 2);
    return switch (split[0]) {
      case "Self" -> validateSelf(split[1]);
      case "Foreign" -> validateForeign(split[1]);
      default -> Optional.empty();
    };
  }

  private Optional<User> validateSelf(String token) {
    var id = userIdValidator.validateUserIdFromToken(token);
    return id != null
        ? userService.findDirect(id)
        : Optional.empty();
  }

  private Optional<User> validateForeign(String value) {
    var split = value.split(" ", 2);

    if (split.length != 2) {
      return Optional.empty();
    }

    var sudoer = userIdValidator.validateUserIdFromToken(split[0]);
    if (sudoer == null) {
      return Optional.empty();
    }
    var isAdmin = userService.findDirect(sudoer)
        .filter(user -> user.hasRole(Roles.ADMIN)).isPresent();
    if (isAdmin) {
      var userId = userIdValidator.validateUserId(split[1]);
      if (userId == null) {
        return Optional.empty();
      }
      return userService.findDirect(userId)
          .or(() -> Optional.of(userService.createDefaultUserUnsafe(userId)));
    }
    return Optional.empty();
  }

}
