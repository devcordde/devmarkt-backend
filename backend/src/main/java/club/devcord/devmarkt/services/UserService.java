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

import club.devcord.devmarkt.entities.auth.Role;
import club.devcord.devmarkt.entities.auth.User;
import club.devcord.devmarkt.entities.auth.UserId;
import club.devcord.devmarkt.repositories.UserRepo;
import club.devcord.devmarkt.responses.user.UserFailed;
import club.devcord.devmarkt.responses.user.UserFailed.UserErrors;
import club.devcord.devmarkt.responses.user.UserResponse;
import club.devcord.devmarkt.responses.user.UserSuccess;
import graphql.language.OperationDefinition.Operation;
import jakarta.inject.Singleton;
import java.util.Collection;
import java.util.Set;

@Singleton
public class UserService {

  private final UserRepo repo;

  public UserService(UserRepo repo) {
    this.repo = repo;
  }

  public Set<String> checkPermissions(Operation operation, Collection<String> permissions, UserId userId) {
    return repo.checkPermissions(userId, permissions, operation);
  }

  public UserResponse find(UserId userId) {
    return repo.findByUserId(userId)
        .map(user -> (UserResponse) new UserSuccess(user))
        .orElseGet(
            () -> new UserFailed(UserErrors.NOT_FOUND, "No user with the given id was found"));
  }

  public boolean delete(UserId userId) {
    return repo.deleteByUserId(userId) >= 1;
  }

  public UserResponse save(UserId userId, Collection<String> roles) {
    if (repo.existsByUserId(userId)) {
      return new UserFailed(UserErrors.DUPLICATED, "A user with the same id already exists");
    }
    var mappedRoles = roles
        .stream()
        .map(s -> new Role(-1, s, null))
        .toList();
    repo.save(new User(-1, userId, mappedRoles));
    return addUserRoles(userId, roles);
  }

  public UserResponse addUserRoles(UserId userId, Collection<String> roles) {
    repo.addRoles(userId, roles);
    return find(userId);
  }

  public UserResponse removeUserRoles(UserId userId, Collection<String> roles) {
    repo.removeRoles(userId, roles);
    return find(userId);
  }


}
