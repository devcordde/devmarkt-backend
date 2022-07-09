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

import club.devcord.devmarkt.entities.template.Question;
import club.devcord.devmarkt.entities.template.QuestionId;
import club.devcord.devmarkt.repositories.QuestionRepo;
import club.devcord.devmarkt.repositories.TemplateRepo;
import club.devcord.devmarkt.responses.Questions;
import club.devcord.devmarkt.responses.Response;
import club.devcord.devmarkt.responses.Success;
import jakarta.inject.Singleton;
import java.util.Comparator;

@Singleton
public class QuestionService {

  private final TemplateRepo templateRepo;
  private final QuestionRepo questionRepo;

  public QuestionService(TemplateRepo repo, QuestionRepo questionRepo) {
    this.templateRepo = repo;
    this.questionRepo = questionRepo;
  }


  public Response<Question> question(String templateName, int number) {
    var templateIdOpt = templateRepo.findByName(templateName);
    if (templateIdOpt.isEmpty()) {
      return Questions.templateNotFound(templateName);
    }

    return questionRepo.findById(new QuestionId(templateIdOpt.get(), number))
        .map(Success::response)
        .orElseGet(() -> Questions.questionNotFound(templateName, number));
  }

  /*
  If a number is provided (higher than -1), than the question will be inserted.
  In detail all numbers of the questions from the given number on
  are increased by 1 and the question is added on the given number.

  Examples:
  addQuestion(..., ..., -1): old, old, old -> old, old, old, new
  assQuestion(..., ..., 2) old, old, old -> old, old, new, old
   */
  public Response<Question> addQuestion(String templateName, Question question) {
    var templateIdOpt = templateRepo.findIdByName(templateName);
    var number = question.number();
    if (templateIdOpt.isEmpty()) {
      return Questions.templateNotFound(templateName);
    }

    int templateId = templateIdOpt.get();
    if (number < 0) {
      number = questionRepo.findMaxIdNumberByIdTemplateId(templateId)
          .map(i -> i + 1)
          .orElse(0);
    } else {
      reorderQuestions(templateId, number, 1, true);
    }
    var questionObj = new Question(templateId, number,
        question.question(), question.multiline(), question.minAnswerLength());
    var questionSaved = questionRepo.save(questionObj);
    return new Success<>(questionSaved);
  }

  public Response<Question> updateQuestion(String templateName, Question question) {
    var templateIdOpt = templateRepo.findIdByName(templateName);
    var number = question.number();
    if (templateIdOpt.isEmpty()) {
      return Questions.templateNotFound(templateName);
    }

    var newQuestion = new Question(templateIdOpt.get(), number,
        question.question(), question.multiline(), question.minAnswerLength());
    var updated = questionRepo.updateOne(newQuestion);
    return updated == 1
        ? new Success<>(newQuestion)
        : Questions.questionNotFound(templateName, number);
  }

  public boolean deleteQuestion(String templateName, int number) {
    var templateIdOpt = templateRepo.findIdByName(templateName);
    if (templateIdOpt.isEmpty()) {
      return false;
    }

    int templateId = templateIdOpt.get();
    var deleted = questionRepo.delete(new QuestionId(templateId, number));
    reorderQuestions(templateId, number + 1, -1, false);
    return deleted != 0;
  }

  /*
  Reorders (changes the number) of all questions with the given templateId, which
  numbers are out of the line to it's right order, optionally with an offset.
  Eg. 1, 2, 4 -> 1, 2, 3 (offset 0)
      1, 2, 4 -> 1, 2, 5 (offset 2)
   */
  public void reorderQuestions(int templateId, int from, int offset, boolean order) {
    var questions = questionRepo.findByIdTemplateIdAndIdNumberGreaterThanEquals(templateId,
        from);
    questions.sort(Comparator.comparingInt(Question::number));

    for (int i = 0; i < questions.size(); i++) {
      var question = questions.get(i);

      int rightNum = i + offset + from;
      if (question.number() != rightNum) {
        var updatedQuestion = new Question(
            question.internalId(), new QuestionId(templateId, rightNum),
            question.question(), question.multiline(), question.minAnswerLength()
        );
        questions.set(i, updatedQuestion);
      }
    }

    if (!questions.isEmpty()) {
      if (order) {
        questions.sort((o1, o2) -> Integer.compare(o2.number(), o1.number()));
      }
      questionRepo.updateNumbers(questions);
    }
  }
}
