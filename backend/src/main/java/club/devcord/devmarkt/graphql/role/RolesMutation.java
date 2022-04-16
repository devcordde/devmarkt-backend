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

package club.devcord.devmarkt.graphql.role;

import club.devcord.devmarkt.entities.auth.Permission;
import club.devcord.devmarkt.services.RoleService;
import graphql.kickstart.tools.GraphQLMutationResolver;
import jakarta.inject.Singleton;
import java.util.Set;

@Singleton
public class RolesMutation implements GraphQLMutationResolver {

  private final RoleService roleService;

  public RolesMutation(RoleService roleService) {
    this.roleService = roleService;
  }

  public Object createRole(String name, Set<Permission> permissions) {
    return roleService.create(name, permissions).graphQlUnion();
  }

  public boolean deleteRole(String name) {
    return roleService.delete(name);
  }
}