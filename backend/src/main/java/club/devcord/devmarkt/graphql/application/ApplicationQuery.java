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

package club.devcord.devmarkt.graphql.application;

import club.devcord.devmarkt.entities.application.Application;
import club.devcord.devmarkt.entities.auth.UserId;
import club.devcord.devmarkt.logging.LoggingUtil;
import club.devcord.devmarkt.services.ApplicationService;
import graphql.kickstart.tools.GraphQLQueryResolver;
import jakarta.inject.Singleton;
import java.time.OffsetDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ApplicationQuery implements GraphQLQueryResolver {

  private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationQuery.class);

  private final ApplicationService service;

  public ApplicationQuery(ApplicationService service) {
    this.service = service;
  }

  public Object application(int id) {
    var response = service.application(id);
    LOGGER.info("Application fetched with id: {} Successful: {}", id, LoggingUtil.responseStatus(response));
    return response.graphQlUnion();
  }

  public List<Application> applications(OffsetDateTime from, OffsetDateTime to, UserId userId) {
    return null;
  }
}
