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
import club.devcord.devmarkt.logging.LoggingUtil;
import club.devcord.devmarkt.services.UserService;
import graphql.kickstart.tools.GraphQLQueryResolver;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class UserQuery implements GraphQLQueryResolver {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserQuery.class);

  public final UserService service;

  public UserQuery(UserService service) {
    this.service = service;
  }

  public Object user(UserId userId) {
    var response = service.find(userId);
    LOGGER.info("User fetch, Response: {}, UserId: {}", LoggingUtil.responseStatus(response),
        userId.merged());
    return response.graphQlUnion();
  }

}
