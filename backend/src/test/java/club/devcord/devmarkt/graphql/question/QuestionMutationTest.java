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

package club.devcord.devmarkt.graphql.question;

import static club.devcord.devmarkt.Helpers.verify;
import static club.devcord.devmarkt.Seed.TEMPLATE_SEED;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import club.devcord.devmarkt.DevmarktTest;
import club.devcord.devmarkt.entities.template.Question;
import club.devcord.devmarkt.entities.template.Template;
import club.devcord.devmarkt.graphql.template.QuestionMutation;
import club.devcord.devmarkt.graphql.template.QuestionQuery;
import club.devcord.devmarkt.graphql.template.TemplateMutation;
import club.devcord.devmarkt.graphql.template.TemplateQuery;
import club.devcord.devmarkt.responses.question.QuestionFailed.QuestionErrors;
import jakarta.inject.Inject;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

public class QuestionMutationTest extends DevmarktTest {

  @Inject
  TemplateMutation templateMutation;
  @Inject
  QuestionMutation questionMutation;
  @Inject
  QuestionQuery questionQuery;
  @Inject
  TemplateQuery templateQuery;

  @Test
  void addQuestion_success() {
    var question = new RawQuestion(-1, -1, 4, "How old are you?");
    var response = questionMutation.addQuestion("Dev searched", question.question(), -1);
    verify(question, response);
    verify(question, questionQuery.question("Dev searched", 4));
  }

  @Test
  void addQuestion_templateNotFound() {
    var response = questionMutation.addQuestion("Secret Information", "Where does Santa live?", -1);
    verify(QuestionErrors.TEMPLATE_NOT_FOUND, response);
  }

  @Test
  void addQuestion__withNumber_success() {
    var question = new Question(null, null, 0, "Where are Johnny's underpants?");
    var response = questionMutation.addQuestion("Dev offered", question.question(), 0);
    verify(question, response);

    var reorderedQuestions = TEMPLATE_SEED.get("Dev offered")
        .questions()
        .stream()
        .map(question1 -> new Question(-1, null, question1.number() + 1, question1.question()))
        .collect(Collectors.toList());
    reorderedQuestions.add(question);

    verify(reorderedQuestions, ((Template) templateQuery.template("Dev offered")).questions());


  }

  @Test
  void addQuestion__withNumber_templateNotFound() {
    var response = questionMutation.addQuestion("NASA secret file ", "How many aliens where there?",
        1);
    verify(QuestionErrors.TEMPLATE_NOT_FOUND, response);
  }

  @Test
  void updateQuestion_success() {
    var question = new RawQuestion(-1, -1, 1, "Is SpongeBob blue?");

    var response = questionMutation.updateQuestion("Dev searched", 1, question.question());
    verify(question, response);

    var verify = questionQuery.question("Dev searched", 1);
    verify(question, verify);
  }

  @Test
  void updateQuestion_templateNotFound() {
    var response = questionMutation.updateQuestion("ANTI-PHP-Petition", 1,
        "How bad is php really?");
    verify(QuestionErrors.TEMPLATE_NOT_FOUND, response);
  }

  @Test
  void updateQuestion_questionNotFound() {
    var response = questionMutation.updateQuestion("Empty template", 0, "How was your day?");
    verify(QuestionErrors.QUESTION_NOT_FOUND, response);
  }

  @Test
  void deleteQuestion_success() {
    var response = questionMutation.deleteQuestion("Dev offered", 0);
    assertTrue(response);

    var reorderedQuestions = TEMPLATE_SEED.get("Dev offered")
        .questions()
        .stream()
        .map(question1 -> new Question(-1, null, question1.number() - 1, question1.question()))
        .filter(question -> question.number() >= 0)
        .collect(Collectors.toList());

    verify(reorderedQuestions, ((Template) templateQuery.template("Dev offered")).questions());
  }

  @Test
  void deleteQuestion_failed() {
    var response = questionMutation.deleteQuestion("Empty template", 0);
    assertFalse(response);
  }

}
