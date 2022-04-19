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

package club.devcord.devmarkt.graphql.user;

import club.devcord.devmarkt.entities.auth.UserId;
import club.devcord.devmarkt.services.UserService;
import graphql.kickstart.tools.GraphQLMutationResolver;
import jakarta.inject.Singleton;
import java.util.Collection;
import java.util.Set;

@Singleton
public class UserMutation implements GraphQLMutationResolver {

  public final UserService service;

  public UserMutation(UserService service) {
    this.service = service;
  }

  public Object createUser(UserId userId, Set<String> roles) {
    return service.save(userId, roles).graphQlUnion();
  }

  public boolean deleteUser(UserId userId) {
    return service.delete(userId);
  }

  public Object addUserRoles(UserId id, Collection<String> roles) {
    return service.addUserRoles(id, roles).graphQlUnion();
  }

  public Object removeUserRoles(UserId id, Collection<String> roles) {
    return service.removeUserRoles(id, roles).graphQlUnion();
  }

}
