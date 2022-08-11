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

import club.devcord.devmarkt.entities.template.Template;
import club.devcord.devmarkt.services.TemplateService;
import graphql.kickstart.tools.GraphQLQueryResolver;
import graphql.schema.DataFetchingEnvironment;
import jakarta.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class TemplateQuery implements GraphQLQueryResolver {

  private static final Logger LOGGER = LoggerFactory.getLogger(TemplateQuery.class);

  private final TemplateService service;

  public TemplateQuery(TemplateService service) {
    this.service = service;
  }

  public Template template(String name) {
    return service.find(name);
  }

  public List<Template> templates(DataFetchingEnvironment environment) {
    var fields = environment.getSelectionSet().getFields();
    if (fields.size() == 1 && fields.get(0).getName().equals("name")) {
      var names = service.allNames()
          .stream()
          .map(name -> new Template(-1, name, true, List.of()))
          .collect(Collectors.toList());
      LOGGER.info("All template names fetched.");
      return names;
    }
    LOGGER.info("All templates fetched.");
    return service.all();
  }
}
