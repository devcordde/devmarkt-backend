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

package club.devcord.devmarkt.graphql.template;

import club.devcord.devmarkt.entities.template.Question;
import club.devcord.devmarkt.entities.template.Template;
import club.devcord.devmarkt.entities.template.TemplateUpdateInput;
import club.devcord.devmarkt.services.TemplateService;
import graphql.kickstart.tools.GraphQLMutationResolver;
import jakarta.inject.Singleton;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class TemplateMutation implements GraphQLMutationResolver {

  private static final Logger LOGGER = LoggerFactory.getLogger(TemplateMutation.class);

  private final TemplateService service;

  public TemplateMutation(TemplateService service) {
    this.service = service;
  }

  public Template createTemplate(String name, List<Question> questions) {
    return service.create(name, questions);
  }

  public boolean deleteTemplate(String name) {
    var response = service.delete(name);
    LOGGER.info("Template deletion. Successful: {}, Name: {}", response, name);
    return response;
  }

  public Template updateTemplate(String templateName, TemplateUpdateInput updatedTemplate) {
    return service.update(templateName, updatedTemplate);
  }
}
