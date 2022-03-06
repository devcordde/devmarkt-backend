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

package club.devcord.devmarkt.database.template;

import club.devcord.devmarkt.database.Transformer;
import club.devcord.devmarkt.dto.template.Template;
import jakarta.inject.Singleton;
import java.util.Optional;
import java.util.Set;

@Singleton
public class TemplateDao {

  private final TemplateRepo repo;

  public TemplateDao(TemplateRepo repo) {
    this.repo = repo;
  }

  public boolean insert(Template template) {
    if (repo.existsByName(template.name())) {
      return false;
    }
    repo.save(Transformer.transform(template));
    return true;
  }

  public boolean replace(Template template) {
    var opt = repo.findByName(template.name());
    if (opt.isEmpty()) {
      return false;
    }
    var found = opt.get();
    repo.delete(found);
    repo.save(Transformer.transform(template, found.id()));
    return true;
  }

  public boolean delete(String name) {
    if (!repo.existsByName(name)) {
      return false;
    }
    repo.deleteByName(name);
    return true;
  }

  public Optional<Template> find(String name) {
    return repo.findByName(name)
        .map(Transformer::transform);
  }

  public Set<String> allNames() {
    return repo.findName();
  }
}
