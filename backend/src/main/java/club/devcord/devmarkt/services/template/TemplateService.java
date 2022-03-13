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

package club.devcord.devmarkt.services.template;

import club.devcord.devmarkt.entities.Template;
import club.devcord.devmarkt.repositories.TemplateRepo;
import club.devcord.devmarkt.responses.template.TemplateFailed;
import club.devcord.devmarkt.responses.template.TemplateResponse;
import club.devcord.devmarkt.responses.template.TemplateSuccess;
import club.devcord.devmarkt.responses.template.TemplateFailed.Codes;
import jakarta.inject.Singleton;

@Singleton
public class TemplateService {

  private final TemplateRepo repo;

  public TemplateService(TemplateRepo repo) {
    this.repo = repo;
  }

  public TemplateResponse create(Template template) {
    if (repo.existsByName(template.name())) {
      return new TemplateFailed(template.name(), Codes.DUPLICATED,"A template with the same name exists");
    }
    var savedTemplate = repo.save(template);
    return new TemplateSuccess(savedTemplate);
  }

  public TemplateResponse find(String name) {
    var optional = repo.findByName(name);
    if (optional.isPresent()) {
      return new TemplateSuccess(optional.get());
    }
    return new TemplateFailed(name, Codes.NOT_FOUND, "No template with the given name found.");
  }

  public boolean delete(String name) {
    if (!repo.existsByName(name)) {
      return false;
    }
    repo.deleteByName(name);
    return true;
  }
}
