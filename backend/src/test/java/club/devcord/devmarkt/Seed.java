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

package club.devcord.devmarkt;

import club.devcord.devmarkt.entities.template.Question;
import club.devcord.devmarkt.entities.template.Template;
import java.util.List;
import java.util.Map;

public class Seed {

  public static final Map<String, Template> TEMPLATE_SEED = Map.of(
      "Dev searched", new Template(-1, "Dev searched", List.of(
          new Question(null, null, 0, "Who are we?"),
          new Question(null, null, 1, "Why should you join us?"),
          new Question(null, null, 2, "What programming languages should you know?"),
          new Question(null, null, 3, "Custom text:")
      )),
      "Dev offered", new Template(-1, "Dev offered", List.of(
          new Question(null, null, 0, "Who am I?"),
          new Question(null, null, 1, "What programming language do I know?"),
          new Question(null, null, 2, "Why should you choose me?")
      )),
      "Empty template", new Template(-1, "Empty template", List.of())
  );

  public static List<String> templateNames() {
    return TEMPLATE_SEED.values()
        .stream()
        .map(Template::name)
        .toList();
  }

}
