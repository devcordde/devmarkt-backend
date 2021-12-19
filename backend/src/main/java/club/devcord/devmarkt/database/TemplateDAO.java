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
import java.util.List;
import java.util.Optional;

public interface TemplateDAO {

  InsertResult insert(Template template);

  ReplaceResult replace(Template template);

  DeleteResult delete(String name);

  Optional<Template> find(String name);

  List<String> allNames();

}
