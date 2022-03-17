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

import club.devcord.devmarkt.DevmarktTest;
import club.devcord.devmarkt.graphql.Helpers;
import club.devcord.devmarkt.graphql.template.TemplateMutation;
import club.devcord.devmarkt.responses.question.QuestionFailed.QuestionErrors;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.inject.Inject;
import java.util.List;
import org.junit.jupiter.api.Test;

public class QuestionQueryTest extends DevmarktTest {

  @Inject
  QuestionQuery questionQuery;
  @Inject
  TemplateMutation templateMutation;

  @Test
  void question_success() throws JsonProcessingException {
    templateMutation.createTemplate("test", Helpers.QUESTIONS);

    var response = questionQuery.question("test", 0);
    assertJson(Helpers.QUESTIONS.get(0), unwrapQuestion(response));
  }

  @Test
  void question_templateNotFound() {
    var response = questionQuery.question("test", 0);
    assertEquals(QuestionErrors.TEMPLATE_NOT_FOUND, unwrapQuestionFailed(response).errorCode());
  }

  @Test
  void question_questionNotFound() {
    templateMutation.createTemplate("test", List.of());

    var response = questionQuery.question("test", 0);
    assertEquals(QuestionErrors.QUESTION_NOT_FOUND, unwrapQuestionFailed(response).errorCode());
  }

}
