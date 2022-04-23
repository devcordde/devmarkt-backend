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

package club.devcord.devmarkt.responses.role;

import club.devcord.devmarkt.graphql.GraphQLType;
import club.devcord.devmarkt.responses.Fail;
import club.devcord.devmarkt.util.Admins;

@GraphQLType("RoleFailed")
public record RoleFailed(
    String name,
    String message,
    String errorCode
) implements Fail, RoleResponse {

  public static RoleFailed adminModify() {
    return new RoleFailed(Admins.ADMIN_ROLE_NAME, "Admin role can't be modified",
        RoleErrors.ADMIN_ROLE_CANT_BE_MODIFIED);
  }

  public static class RoleErrors {

    public static final String NOT_FOUND = "NOT_FOUND";
    public static final String DUPLICATED = "DUPLICATED";
    public static final String ADMIN_ROLE_CANT_BE_MODIFIED = "ADMIN_ROLE_CANT_BE_MODIFIED";

    private RoleErrors() {
    }
  }

}
