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


import club.devcord.devmarkt.database.template.entities.DBQuestion;
import club.devcord.devmarkt.database.template.entities.DBTemplate;
import club.devcord.devmarkt.dto.template.Question;
import club.devcord.devmarkt.dto.template.Template;
import java.util.List;

public class Constants {

  public static final DBTemplate DBTEMPLATE = new DBTemplate(
      5,
      "template",
      List.of(new DBQuestion(null, null, 0, "How are you?"),
          new DBQuestion(null, null, 1, "What's your name?"),
          new DBQuestion(null, null, 2, "Where do you live?"))
  );

  public static final Template TEMPLATE = new Template(
      "template",
      List.of(new Question(0, "How are you?"),
          new Question(1, "What's your name?"),
          new Question(2, "Where do you live?"))
  );

  public static final String REQUESTER_ID = "007";

}
