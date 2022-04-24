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
import club.devcord.devmarkt.logging.LoggingUtil;
import club.devcord.devmarkt.services.RoleService;
import graphql.kickstart.tools.GraphQLMutationResolver;
import jakarta.inject.Singleton;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class PermissionMutation implements GraphQLMutationResolver {

  private static final Logger LOGGER = LoggerFactory.getLogger(PermissionMutation.class);

  private final RoleService roleService;

  public PermissionMutation(RoleService roleService) {
    this.roleService = roleService;
  }

  public Object addPermissions(String roleName, Set<Permission> permissions) {
    var response = roleService.addPermissions(roleName, permissions);
    LOGGER.info("Permission addition, Response: {}, RoleName: {}, Permissions: {}",
        LoggingUtil.responseStatus(response), roleName, permissions);
    return response.graphQlUnion();
  }

  public Object removePermissions(String roleName, Set<Permission> permissions) {
    var response = roleService.removePermissions(roleName, permissions);
    LOGGER.info("Permission removal, Response: {}, RoleName: {}, Permissions: {}",
        LoggingUtil.responseStatus(response), roleName, permissions);
    return response.graphQlUnion();
  }
}
