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
import club.devcord.devmarkt.responses.FailureException;
import club.devcord.devmarkt.responses.failure.user.ErrorCode;
import club.devcord.devmarkt.util.Admins;
import club.devcord.devmarkt.ws.ReflectiveUnsubscriber;
import jakarta.inject.Singleton;
import java.util.Optional;

@Singleton
public class UserService {

  private final UserRepo repo;
  private final ReflectiveUnsubscriber reflectiveUnsubscriber;

  public UserService(UserRepo repo, ReflectiveUnsubscriber reflectiveUnsubscriber) {
    this.repo = repo;
    this.reflectiveUnsubscriber = reflectiveUnsubscriber;
  }

  public Optional<User> findDirect(UserId userId) {
    return repo.findById(userId);
  }

  public User find(UserId userId) {
    return repo.findById(userId)
        .orElseThrow(() -> new FailureException(ErrorCode.NOT_FOUND));
  }

  public boolean delete(UserId userId) {
    if (Admins.isAdminUserId(userId)) {
      return false;
    }
    reflectiveUnsubscriber.unsubscribeSubscriptions(userId);
    return repo.deleteOneById(userId) >= 1;
  }

  public User createDefaultUserUnsafe(UserId userId) {
    var user = new User(-1, userId, Role.USER);
    repo.save(user);
    return user;
  }

  public User save(UserId userId, Role role) {
    if (repo.existsById(userId)) {
      throw new FailureException(ErrorCode.DUPLICATED);
    }
    return repo.save(new User(-1, userId, role));
  }

  public User updateRole(UserId userId, Role role) {
    if (Admins.isAdminUserId(userId)) {
      throw new FailureException(ErrorCode.ADMIN_USER_CANT_BE_MODIFIED);
    }
    reflectiveUnsubscriber.unsubscribeSubscriptions(userId);
    var updated = repo.updateById(userId, role);
    if (updated == 0) {
      throw new FailureException(ErrorCode.NOT_FOUND);
    }
    return new User(-1, userId, role);
  }
}
