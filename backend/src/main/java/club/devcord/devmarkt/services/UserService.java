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

import club.devcord.devmarkt.auth.SchemaPermissionGenerator;
import club.devcord.devmarkt.entities.auth.Permission;
import club.devcord.devmarkt.entities.auth.User;
import club.devcord.devmarkt.entities.auth.UserId;
import club.devcord.devmarkt.repositories.UserRepo;
import club.devcord.devmarkt.responses.user.UserFailed;
import club.devcord.devmarkt.responses.user.UserFailed.UserErrors;
import club.devcord.devmarkt.responses.user.UserResponse;
import club.devcord.devmarkt.responses.user.UserSuccess;
import club.devcord.devmarkt.util.Admins;
import graphql.language.OperationDefinition.Operation;
import jakarta.inject.Singleton;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Singleton
public class UserService {

  private final UserRepo repo;

  public UserService(UserRepo repo) {
    this.repo = repo;
  }

  public Stream<String> checkPermissions(Operation operation, Stream<String> permissions,
      UserId userId) {
    var userOpt = repo.findByUserId(userId);
    if (userOpt.isEmpty()) {
      return permissions;
    }
    var user = userOpt.get();
    var userPermissions = user.roles()
        .stream()
        .flatMap(role -> role.permissions().stream())
        .filter(permission -> permission.operation() == operation)
        .map(Permission::query)
        .collect(Collectors.toSet());

    return permissions
        .filter(s -> {
          if (isIntrospectionField(s)) {
            var subPerm = s.substring(0, s.lastIndexOf(SchemaPermissionGenerator.PERMISSION_SEPARATOR));
            return userPermissions
                .stream()
                .noneMatch(perm -> perm.startsWith(subPerm));
          }
          return !userPermissions.contains(s);
        });
  }

  public boolean exists(UserId userId) {
    return repo.existsByUserId(userId);
  }

  private boolean isIntrospectionField(String perm) {
    return perm.startsWith("__", perm.lastIndexOf(SchemaPermissionGenerator.PERMISSION_SEPARATOR) + 1);
  }

  public UserResponse find(UserId userId) {
    return repo.findByUserId(userId)
        .map(user -> (UserResponse) new UserSuccess(user))
        .orElseGet(
            () -> new UserFailed(UserErrors.NOT_FOUND, "No user with the given id was found"));
  }

  public boolean delete(UserId userId) {
    if (Admins.isAdminUserId(userId)) {
      return false;
    }
    return repo.deleteByUserId(userId) >= 1;
  }

  public void saveUnsafe(UserId userId, Collection<String> roles) {
    repo.save(new User(-1, userId, null));
    addUserRolesUnsafe(userId, roles);
  }

  public UserResponse save(UserId userId, Collection<String> roles) {
    if (repo.existsByUserId(userId)) {
      return new UserFailed(UserErrors.DUPLICATED, "A user with the same id already exists");
    }
    repo.save(new User(-1, userId, null));
    return addUserRoles(userId, roles);
  }

  public void addUserRolesUnsafe(UserId userId, Collection<String> roles) {
    repo.addRoles(userId, roles);
  }

  public UserResponse addUserRoles(UserId userId, Collection<String> roles) {
    if (Admins.isAdminUserId(userId)) {
      return UserFailed.adminUserModify();
    }
    repo.addRoles(userId, roles);
    return find(userId);
  }

  public UserResponse removeUserRoles(UserId userId, Collection<String> roles) {
    if (Admins.isAdminUserId(userId)) {
      return UserFailed.adminUserModify();
    }
    repo.removeRoles(userId, roles);
    return find(userId);
  }


}
