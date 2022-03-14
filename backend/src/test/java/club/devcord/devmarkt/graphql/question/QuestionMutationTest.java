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

import static club.devcord.devmarkt.graphql.Helpers.assertJson;
import static club.devcord.devmarkt.graphql.Helpers.unwrapQuestion;
import static club.devcord.devmarkt.graphql.Helpers.unwrapQuestionFailed;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import club.devcord.devmarkt.DevmarktTest;
import club.devcord.devmarkt.entities.template.Question;
import club.devcord.devmarkt.entities.template.RawQuestion;
import club.devcord.devmarkt.graphql.Helpers;
import club.devcord.devmarkt.graphql.template.TemplateMutation;
import club.devcord.devmarkt.responses.question.QuestionFailed.QuestionErrors;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.inject.Inject;
import java.util.List;
import org.junit.jupiter.api.Test;

public class QuestionMutationTest extends DevmarktTest {

  @Inject
  TemplateMutation templateMutation;
  @Inject
  QuestionMutation questionMutation;
  @Inject
  QuestionQuery questionQuery;

  @Test
  void addQuestion_success() throws JsonProcessingException {
    var question = new RawQuestion(-1, -1, 1, "How old are you?");
    templateMutation.createTemplate("test",
        List.of(new Question(null, null, 0, "What's your name?")));

    var response = questionMutation.addQuestion("test", question.question(), -1);
    assertJson(question, unwrapQuestion(response));

    var verify = questionQuery.question("test", 1);
    assertJson(question, unwrapQuestion(verify));
  }

  @Test
  void addQuestion_templateNotFound() {
    var response = questionMutation.addQuestion("test", "Where does Santa live?", -1);
    assertEquals(QuestionErrors.TEMPLATE_NOT_FOUND, unwrapQuestionFailed(response).errorCode());
  }

  @Test
  void addQuestion__withNumber_success() throws JsonProcessingException {
    var question = new RawQuestion(-1, -1, 0, "Where are Johnny's underpants?");
    templateMutation.createTemplate("test",
        List.of(new Question(null, null, 0, "What's your name?")));

    var response = questionMutation.addQuestion("test", question.question(), 0);
    assertJson(question, unwrapQuestion(response));

    var verifyFirst = questionQuery.question("test", 0);
    assertJson(question, unwrapQuestion(verifyFirst));

    var verifySecond = questionQuery.question("test", 1);
    assertJson(new RawQuestion(-1, -1, 1, "What's your name?"), unwrapQuestion(verifySecond));
  }

  @Test
  void addQuestion__withNumber_templateNotFound() {
    var response = questionMutation.addQuestion("test", "Am I cool?", 1);
    assertEquals(QuestionErrors.TEMPLATE_NOT_FOUND, unwrapQuestionFailed(response).errorCode());
  }

  @Test
  void updateQuestion_success() throws JsonProcessingException {
    var question = new RawQuestion(-1, -1, 1, "Is SpongeBob blue?");
    templateMutation.createTemplate("test", Helpers.QUESTIONS);

    var response = questionMutation.updateQuestion("test", 1, question.question());
    assertJson(question, unwrapQuestion(response));

    var verify = questionQuery.question("test", 1);
    assertJson(question, unwrapQuestion(verify));
  }

  @Test
  void updateQuestion_templateNotFound() {
    var response = questionMutation.updateQuestion("test", 1, "How was your day?");
    assertEquals(QuestionErrors.TEMPLATE_NOT_FOUND, unwrapQuestionFailed(response).errorCode());
  }

  @Test
  void updateQuestion_questionNotFond() {
    templateMutation.createTemplate("test", List.of());
    var response = questionMutation.updateQuestion("test", 0, "How was your day?");
    assertEquals(QuestionErrors.QUESTION_NOT_FOUND, unwrapQuestionFailed(response).errorCode());
  }

  @Test
  void deleteQuestion_success() throws JsonProcessingException {
    templateMutation.createTemplate("test", Helpers.QUESTIONS);

    var response = questionMutation.deleteQuestion("test", 0);
    assertTrue(response);

    var verifyFirst = unwrapQuestion(questionQuery.question("test", 0));
    assertJson(verifyFirst, new RawQuestion(-1, -1, 0, verifyFirst.question()));

    var verifySecond = unwrapQuestion(questionQuery.question("test", 1));
    assertJson(verifySecond, new RawQuestion(-1, -1, 1, verifySecond.question()));
  }

  @Test
  void deleteQuestion_failed() {
    templateMutation.createTemplate("test", List.of());

    var response = questionMutation.deleteQuestion("test", 0);
    assertFalse(response);
  }

}
