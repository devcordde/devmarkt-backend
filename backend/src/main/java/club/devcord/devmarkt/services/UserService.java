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

package club.devcord.devmarkt.services;

import club.devcord.devmarkt.auth.Role;
import club.devcord.devmarkt.entities.auth.User;
import club.devcord.devmarkt.entities.auth.UserId;
import club.devcord.devmarkt.repositories.UserRepo;
import club.devcord.devmarkt.responses.Failure;
import club.devcord.devmarkt.responses.Response;
import club.devcord.devmarkt.responses.Success;
import club.devcord.devmarkt.responses.failure.user.ErrorCode;
import club.devcord.devmarkt.util.Admins;
import jakarta.inject.Singleton;
import java.util.Optional;

@Singleton
public class UserService {

  private final UserRepo repo;

  public UserService(UserRepo repo) {
    this.repo = repo;
  }

  public Optional<User> findDirect(UserId userId) {
    return repo.findById(userId);
  }

  public Response<User> find(UserId userId) {
    return repo.findById(userId)
        .map(Success::response)
        .orElseGet(() -> new Failure<>(ErrorCode.NOT_FOUND));
  }

  public boolean delete(UserId userId) {
    if (Admins.isAdminUserId(userId)) {
      return false;
    }
    return repo.deleteOneById(userId) >= 1;
  }

  public User createDefaultUserUnsafe(UserId userId) {
    var user = new User(-1, userId, Role.USER);
    repo.save(user);
    return user;
  }

  public Response<User> save(UserId userId, Role role) {
    if (repo.existsById(userId)) {
      return new Failure<>(ErrorCode.NOT_FOUND);
    }
    var saved = repo.save(new User(-1, userId, role));
    return new Success<>(saved);
  }

  public Response<User> updateRole(UserId userId, Role role) {
    if (Admins.isAdminUserId(userId)) {
      return new Failure<>(ErrorCode.ADMIN_USER_CANT_BE_MODIFIED);
    }

    var updated = repo.updateById(userId, role);
    return updated != 0
        ? new Success<>(new User(-1, userId, role))
        : new Failure<>(ErrorCode.NOT_FOUND);
  }
}
