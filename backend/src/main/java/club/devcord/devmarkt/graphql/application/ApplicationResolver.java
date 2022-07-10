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
import club.devcord.devmarkt.entities.auth.User;
import club.devcord.devmarkt.entities.template.Template;
import club.devcord.devmarkt.services.TemplateService;
import club.devcord.devmarkt.services.UserService;
import graphql.kickstart.tools.GraphQLResolver;
import jakarta.inject.Singleton;

@Singleton
public class ApplicationResolver implements GraphQLResolver<Application> {

  private final UserService userService;
  private final TemplateService templateService;

  public ApplicationResolver(UserService userService,
      TemplateService templateService) {
    this.userService = userService;
    this.templateService = templateService;
  }

  // TODO: Enhance error handling
  public User user(Application application) {
    return userService.findDirect(application.userId()).orElseThrow();
  }

  public Template template(Application application) {
    return templateService.findDirect(application.templateId()).orElseThrow();
  }

}
