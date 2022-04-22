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

import club.devcord.devmarkt.entities.auth.Permission;
import club.devcord.devmarkt.entities.auth.Role;
import club.devcord.devmarkt.repositories.RoleRepo;
import club.devcord.devmarkt.responses.role.RoleFailed;
import club.devcord.devmarkt.responses.role.RoleFailed.RoleErrors;
import club.devcord.devmarkt.responses.role.RoleResponse;
import club.devcord.devmarkt.responses.role.RoleSuccess;
import jakarta.inject.Singleton;
import java.util.Set;

@Singleton
public class RoleService {

  private final RoleRepo roleRepo;

  public RoleService(RoleRepo roleRepo) {
    this.roleRepo = roleRepo;
  }

  public boolean exist(String name) {
    return roleRepo.existsByName(name);
  }

  public RoleResponse find(String name) {
    return roleRepo.findByName(name)
        .map(role -> (RoleResponse) new RoleSuccess(role))
        .orElseGet(() -> new RoleFailed(name, "No role with name %s found".formatted(name),
            RoleErrors.NOT_FOUND));
  }

  public RoleResponse create(String name, Set<Permission> permissions) {
    if (roleRepo.existsByName(name)) {
      return new RoleFailed(name, "A role with the same name already exists.",
          RoleErrors.DUPLICATED);
    }
    var saved = roleRepo.save(new Role(-1, name, permissions));
    return new RoleSuccess(saved);
  }

  public boolean delete(String name) {
    return roleRepo.deleteByName(name) == 1;
  }

  public RoleResponse removePermissions(String roleName, Set<Permission> permissions) {
    roleRepo.removePermissions(roleName, permissions);
    return find(roleName);
  }

  public RoleResponse addPermissions(String name, Set<Permission> permissions) {
    roleRepo.addPermissions(name, permissions);
    return find(name);
  }
}
