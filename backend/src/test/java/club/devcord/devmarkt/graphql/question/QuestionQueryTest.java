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

import club.devcord.devmarkt.DevmarktTest;
import club.devcord.devmarkt.graphql.template.TemplateMutation;
import club.devcord.devmarkt.responses.question.QuestionFailed.QuestionErrors;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

public class QuestionQueryTest extends DevmarktTest {

  @Inject
  QuestionQuery questionQuery;
  @Inject
  TemplateMutation templateMutation;

  @Test
  void question_success() {
    var response = questionQuery.question("Dev searched", 0);
    verify(TEMPLATE_SEED.get("Dev searched").questions().get(0), response);
  }

  @Test
  void question_templateNotFound() {
    var response = questionQuery.question("Krusty Crab Guestbook", 0);
    verify(QuestionErrors.TEMPLATE_NOT_FOUND, response);
  }

  @Test
  void question_questionNotFound() {
    var response = questionQuery.question("Dev searched", 10);
    verify(QuestionErrors.QUESTION_NOT_FOUND, response);
  }

}
