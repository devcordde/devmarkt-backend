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

import club.devcord.devmarkt.services.PermissionService;
import club.devcord.devmarkt.services.RoleService;
import club.devcord.devmarkt.services.UserService;
import club.devcord.devmarkt.util.Admins;
import graphql.GraphQL;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.context.event.StartupEvent;
import jakarta.inject.Singleton;
import java.util.Set;

@Singleton
public class AuthSetup implements ApplicationEventListener<StartupEvent> {

  private final PermissionService permissionService;
  private final UserService userService;
  private final RoleService roleService;

  private final GraphQL graphQL;
  private final SchemaPermissionGenerator schemaPermissionGenerator;

  public AuthSetup(PermissionService service, GraphQL graphQL,
      SchemaPermissionGenerator schemaPermissionGenerator,
      UserService userService, RoleService roleService) {
    this.permissionService = service;
    this.graphQL = graphQL;
    this.schemaPermissionGenerator = schemaPermissionGenerator;
    this.userService = userService;
    this.roleService = roleService;
  }

  @Override
  public void onApplicationEvent(StartupEvent event) {
    updatePermissions();
    setupAdminRole();
    setupAdminUser();
  }

  private void updatePermissions() {
    var permission = schemaPermissionGenerator.generate(graphQL.getGraphQLSchema());
    permissionService.updatePermissions(permission);
  }

  private void setupAdminRole() {
    var permissions = permissionService.permissions();
    boolean exists = roleService.exist(Admins.ADMIN_ROLE_NAME);
    if (exists) {
      roleService.addPermissionsUnsafe(Admins.ADMIN_ROLE_NAME, permissions);
    } else {
      roleService.createUnsafe(Admins.ADMIN_ROLE_NAME, permissions);
    }
  }

  private void setupAdminUser() {
    boolean exists = userService.exists(Admins.ADMIN_USERID);
    if (exists) {
      userService.addUserRolesUnsafe(Admins.ADMIN_USERID, Set.of(Admins.ADMIN_ROLE_NAME));
    } else {
      userService.saveUnsafe(Admins.ADMIN_USERID, Set.of(Admins.ADMIN_ROLE_NAME));
    }
  }
}
