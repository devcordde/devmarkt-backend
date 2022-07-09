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

import club.devcord.devmarkt.auth.Roles;
import club.devcord.devmarkt.entities.auth.Role;
import club.devcord.devmarkt.entities.auth.User;
import club.devcord.devmarkt.entities.auth.UserId;
import club.devcord.devmarkt.repositories.UserRepo;
import club.devcord.devmarkt.responses.Response;
import club.devcord.devmarkt.responses.Success;
import club.devcord.devmarkt.responses.Users;
import club.devcord.devmarkt.util.Admins;
import jakarta.inject.Singleton;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Singleton
public class UserService {

  private final UserRepo repo;

  public UserService(UserRepo repo) {
    this.repo = repo;
  }

  public Optional<User> findDirect(UserId userId) {
    return repo.findByUserId(userId);
  }

  public Response<User> find(UserId userId) {
    return repo.findByUserId(userId)
        .map(Success::response)
        .orElseGet(() -> Users.notFound(userId.merged()));
  }

  public boolean delete(UserId userId) {
    if (Admins.isAdminUserId(userId)) {
      return false;
    }
    return repo.deleteByUserId(userId) >= 1;
  }

  public User createDefaultUserUnsafe(UserId userId) {
    var user = new User(-1, userId, Set.of(new Role(-1, Roles.USER.toString())));
    repo.save(user);
    repo.addRoles(userId, Set.of(Roles.USER.toString()));
    return user;
  }

  public Response<User> save(UserId userId, Collection<String> roles) {
    if (repo.existsByUserId(userId)) {
      return Users.duplicated(userId.merged());
    }
    repo.save(new User(-1, userId, null));
    return addUserRoles(userId, roles);
  }

  public Response<User> addUserRoles(UserId userId, Collection<String> roles) {
    if (Admins.isAdminUserId(userId)) {
      return Users.adminUserModify();
    }
    repo.addRoles(userId, roles);
    return find(userId);
  }

  public Response<User> removeUserRoles(UserId userId, Collection<String> roles) {
    if (Admins.isAdminUserId(userId)) {
      return Users.adminUserModify();
    }
    repo.removeRoles(userId, roles);
    return find(userId);
  }


}
