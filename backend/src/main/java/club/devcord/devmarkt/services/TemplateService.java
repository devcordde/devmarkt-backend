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

package club.devcord.devmarkt.services;

import club.devcord.devmarkt.entities.template.Template;
import club.devcord.devmarkt.repositories.TemplateRepo;
import club.devcord.devmarkt.responses.template.TemplateFailed;
import club.devcord.devmarkt.responses.template.TemplateResponse;
import club.devcord.devmarkt.responses.template.TemplateSuccess;
import jakarta.inject.Singleton;
import java.util.List;

@Singleton
public class TemplateService {

  private final TemplateRepo templateRepo;

  public TemplateService(TemplateRepo repo) {
    this.templateRepo = repo;
  }

  public TemplateResponse create(Template template) {
    var name = template.name();
    if (templateRepo.existsByName(name)) {
      return TemplateFailed.duplicated(name);
    }
    var savedTemplate = templateRepo.save(template);
    return new TemplateSuccess(savedTemplate);
  }

  public TemplateResponse find(String name) {
    return templateRepo.findByName(name)
        .map(tem -> (TemplateResponse) new TemplateSuccess(tem))
        .orElseGet(() -> TemplateFailed.notFound(name));
  }

  public boolean delete(String name) {
    return templateRepo.deleteByName(name) != 0;
  }

  public boolean updateName(String oldName, String newName) {
    return templateRepo.updateByName(oldName, newName) != 0;
  }

  public List<Template> all() {
    return templateRepo.findAll();
  }

  public List<String> allNames() {
    return templateRepo.findName();
  }
}
