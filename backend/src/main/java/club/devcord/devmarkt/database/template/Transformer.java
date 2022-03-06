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

import club.devcord.devmarkt.database.template.dto.DBQuestion;
import club.devcord.devmarkt.database.template.dto.DBTemplate;
import club.devcord.devmarkt.dto.template.Question;
import club.devcord.devmarkt.dto.template.Template;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Transformer {

  static DBTemplate transform(Template template) {
    return transform(template, -1);
  }

  static DBTemplate transform(Template template, int id) {
    var questions = IntStream.range(0, template.questions().size())
        .mapToObj(
            digit -> new DBQuestion(null, null, digit, template.questions().get(digit).question()))
        .toList();
    return new DBTemplate(id, template.name(), questions);
  }

  static Template transform(DBTemplate template) {
    var questions = template.questions()
        .stream()
        .map(question -> new Question(question.digit(), question.question()))
        .collect(Collectors.toList());
    return new Template(template.name(), questions);
  }

}
