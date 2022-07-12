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

import club.devcord.devmarkt.auth.Role;
import club.devcord.devmarkt.entities.auth.UserId;
import club.devcord.devmarkt.logging.LoggingUtil;
import club.devcord.devmarkt.services.UserService;
import graphql.kickstart.tools.GraphQLMutationResolver;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class UserMutation implements GraphQLMutationResolver {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserMutation.class);

  public final UserService service;

  public UserMutation(UserService service) {
    this.service = service;
  }

  public Object createUser(UserId userId, Role role) {
    var response = service.save(userId, role);
    LOGGER.info("User creation. Response: {}, UserId: {}, Role: {}",
        LoggingUtil.responseStatus(response), userId, role);
    return response.graphQlUnion();
  }

  public boolean deleteUser(UserId userId) {
    var response = service.delete(userId);
    LOGGER.info("User deletion, Successful: {}, UserId: {}", response, userId);
    return response;
  }

  public Object updateUserRole(UserId userId, Role role) {
    var response = service.updateRole(userId, role);
    LOGGER.info("User role update. Response: {}, UserId: {}, Role: {}",
        LoggingUtil.responseStatus(response), userId, role);
    return response.graphQlUnion();
  }

}
