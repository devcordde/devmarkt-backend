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

import club.devcord.devmarkt.dto.template.Template;
import jakarta.inject.Singleton;
import java.util.Optional;
import java.util.Set;

@Singleton
public class TemplateImpl implements TemplateDAO {

  private final TemplateRepo repo;

  public TemplateImpl(TemplateRepo repo) {
    this.repo = repo;
  }

  @Override
  public InsertResult insert(Template template) {
    if (repo.existsByName(template.name())) {
      return InsertResult.DUPLICATED;
    }
    repo.save(Transformer.transform(template));
    return InsertResult.INSERTED;
  }

  @Override
  public ReplaceResult replace(Template template) {
    var opt = repo.findByName(template.name());
    if (opt.isEmpty()) {
      return ReplaceResult.NOT_FOUND;
    }
    var found = opt.get();
    repo.delete(found);
    repo.save(Transformer.transform(template, found.id()));
    return ReplaceResult.REPLACED;
  }

  @Override
  public DeleteResult delete(String name) {
    if (!repo.existsByName(name)) {
      return DeleteResult.NOT_FOUND;
    }
    repo.deleteByName(name);
    return DeleteResult.DELETED;
  }

  @Override
  public Optional<Template> find(String name) {
    return repo.findByName(name)
        .map(Transformer::transform);
  }

  @Override
  public Set<String> allNames() {
    return repo.findName();
  }
}
