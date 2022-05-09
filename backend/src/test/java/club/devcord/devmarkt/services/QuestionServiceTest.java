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

package club.devcord.devmarkt.services;

import static club.devcord.devmarkt.Helpers.verify;
import static club.devcord.devmarkt.Seed.TEMPLATE_SEED;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import club.devcord.devmarkt.DevmarktTest;
import club.devcord.devmarkt.entities.template.Question;
import club.devcord.devmarkt.repositories.TemplateRepo;
import club.devcord.devmarkt.responses.question.QuestionFailed;
import club.devcord.devmarkt.responses.question.QuestionFailed.QuestionErrors;
import club.devcord.devmarkt.responses.question.QuestionSuccess;
import jakarta.inject.Inject;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

public class QuestionServiceTest extends DevmarktTest {

  @Inject
  QuestionService service;
  @Inject
  TemplateRepo templateRepo;

  @Test
  void addQuestion_success() {
    var response = service.addQuestion("Dev offered", "What do I expect from you?", -1);
    assertTrue(response instanceof QuestionSuccess);
    verify(new RawQuestion(-1, -1, 3, "What do I expect from you?"),
        ((QuestionSuccess) response).question());
  }

  @Test
  void addQuestion_templateNotFound() {
    var response = service.addQuestion("Willi Wonka's chocolate recipe",
        "How much sugar is int it?", -1);
    assertTrue(response instanceof QuestionFailed);
    verify(QuestionErrors.TEMPLATE_NOT_FOUND, response);
  }

  @Test
  void addQuestion__withNumber_success() {
    var response = service.addQuestion("Dev searched", "What do we expect from you?", 2);
    assertTrue(response instanceof QuestionSuccess);
    verify(new RawQuestion(-1, -1, 2, "What do we expect from you?"),
        ((QuestionSuccess) response).question());
  }

  @Test
  void updateQuestion_success() {
    var response = service.updateQuestion("Dev offered", 1, "Is 42 the answer of all?");
    assertTrue(response instanceof QuestionSuccess);
    verify(new RawQuestion(-1, -1, 1, "Is 42 the answer of all?"),
        ((QuestionSuccess) response).question());
  }

  @Test
  void updateQuestion_templateNotFound() {
    var response = service.updateQuestion("Nick's Self-talk", 2,
        "What should I eat today's night?");
    verify(QuestionErrors.TEMPLATE_NOT_FOUND, response);
  }

  @Test
  void updateQuestion_questionNotFound() {
    var response = service.updateQuestion("Empty template", 1, "Is water blue or colorless?");
    verify(QuestionErrors.QUESTION_NOT_FOUND, response);
  }

  @Test
  void deleteQuestion_success() {
    var deleted = service.deleteQuestion("Dev searched", 1);
    assertTrue(deleted);
  }

  @Test
  void deleteQuestion__noTemplate_fail() {
    var deleted = service.deleteQuestion("Illuminati's plans", 1);
    assertFalse(deleted);
  }

  @Test
  void deleteQuestion__noQuestion_fail() {
    var deleted = service.deleteQuestion("Empty template", 1);
    assertFalse(deleted);
  }

  @Test
  void reorderQuestions__allIncrementByOne() {
    var reorderedQuestions = TEMPLATE_SEED
        .get("Dev offered")
        .questions()
        .stream()
        .map(question -> new Question(-1, null, question.number() + 1, question.question()))
        .toList();

    service.reorderQuestions(templateRepo.findInternalIdByName("Dev offered").orElseThrow(), 0, 1);

    var actual = templateRepo.findByName("Dev offered")
        .orElseThrow()
        .questions();

    verify(reorderedQuestions, actual);
  }

  @Test
  void reorderQuestions__from2IncrementByThree() {
    var reorderedQuestions = TEMPLATE_SEED
        .get("Dev offered")
        .questions()
        .stream()
        .map(question -> {
          if (question.number() >= 2) {
            return new Question(-1, null, question.number() + 3, question.question());
          }
          return question;
        })
        .toList();

    service.reorderQuestions(templateRepo.findInternalIdByName("Dev offered").orElseThrow(), 2, 3);

    var actual = templateRepo.findByName("Dev offered")
        .orElseThrow()
        .questions();

    verify(reorderedQuestions, actual);
  }

  @Test
  void reorderQuestions__allToRightOrderNumbersNotSequential() {
    var questions = new ArrayList<>(TEMPLATE_SEED.get("Dev offered").questions());
    questions.add(new Question(-1, null, 3, "Outlined question"));

    service.addQuestion("Dev offered", "Outlined question", 6);
    service.reorderQuestions(templateRepo.findInternalIdByName("Dev offered").orElseThrow(), 0, 0);
    verify(questions, templateRepo.findByName("Dev offered").orElseThrow().questions());
  }

}
