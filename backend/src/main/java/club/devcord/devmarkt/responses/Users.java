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

package club.devcord.devmarkt.responses;

import club.devcord.devmarkt.entities.auth.User;

public interface Users {

  static Failure<User> adminUserModify() {
    return new Failure<>(Errors.ADMIN_USER_CANT_BE_MODIFIED.name(), "Admin user can't be modified,");
  }

  static Failure<User> notFound(String id) {
    return new Failure<>(Errors.DUPLICATED.name(), "A user with the id '%s' wasn't found.".formatted(id));
  }

  static Failure<User> duplicated(String id) {
    return new Failure<>(Errors.DUPLICATED.name(), "A user with the id '%s' already exists.".formatted(id));
  }

  enum Errors {
    NOT_FOUND,
    DUPLICATED,
    ADMIN_USER_CANT_BE_MODIFIED
  }
}
