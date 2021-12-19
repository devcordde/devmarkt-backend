/*
 * Copyright 2021 Contributors to the Devmarkt-Backend project
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

package club.devcord.devmarkt.database;

import club.devcord.devmarkt.dto.template.Template;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class EmptTemplateImpl implements TemplateDAO {

  @Override
  public InsertResult insert(Template template) {
    return InsertResult.INSERTED;
  }

  @Override
  public ReplaceResult replace(Template template) {
    return ReplaceResult.REPLACED;
  }

  @Override
  public DeleteResult delete(String name) {
    return DeleteResult.DELETED;
  }

  @Override
  public Optional<Template> find(String name) {
    return Optional.empty();
  }

  @Override
  public List<String> allNames() {
    return Collections.emptyList();
  }
}
