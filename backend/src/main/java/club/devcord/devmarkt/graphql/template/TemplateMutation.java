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
import club.devcord.devmarkt.services.TemplateService;
import graphql.kickstart.tools.GraphQLMutationResolver;
import jakarta.inject.Singleton;
import java.util.List;

@Singleton
public class TemplateMutation implements GraphQLMutationResolver {

  private final TemplateService service;

  public TemplateMutation(TemplateService service) {
    this.service = service;
  }

  public Object createTemplate(String name, List<Question> questions) {
    var template = new Template(-1, name, questions);
    return service.create(template).graphqlUnion();
  }

  public boolean deleteTemplate(String name) {
    return service.delete(name);
  }

  public boolean updateTemplateName(String oldName, String newName) {
    return service.updateName(oldName, newName);
  }
}
