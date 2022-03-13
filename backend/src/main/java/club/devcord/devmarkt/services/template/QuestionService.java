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

package club.devcord.devmarkt.services.template;

import club.devcord.devmarkt.entities.template.RawQuestion;
import club.devcord.devmarkt.repositories.QuestionRepo;
import club.devcord.devmarkt.repositories.TemplateRepo;
import club.devcord.devmarkt.responses.question.QuestionFailed;
import club.devcord.devmarkt.responses.question.QuestionResponse;
import club.devcord.devmarkt.responses.question.QuestionSuccess;
import jakarta.inject.Singleton;
import java.util.HashSet;

@Singleton
public class QuestionService {

  private final TemplateRepo templateRepo;
  private final QuestionRepo questionRepo;

  public QuestionService(TemplateRepo repo,
      QuestionRepo questionRepo) {
    this.templateRepo = repo;
    this.questionRepo = questionRepo;
  }


  public QuestionResponse question(String templateName, int number) {
    var templateIdOpt = templateRepo.getIdByName(templateName);
    if (templateIdOpt.isEmpty()) {
      return QuestionFailed.templateNotFound(templateName, number);
    }

    return questionRepo.findByTemplateIdAndNumber(templateIdOpt.get(), number)
        .map(rawQuestion -> (QuestionResponse) new QuestionSuccess(rawQuestion))
        .orElseGet(() -> QuestionFailed.questionNotFound(templateName, number));
  }

  /*
  If a number is provided (higher than -1), than the question will be inserted.
  In detail all numbers of the questions from the given number on
  are increased by 1 and the question is added on the given number.

  Examples:
  addQuestion(..., ..., -1): old, old, old -> old, old, old, new
  assQuestion(..., ..., 2) old, old, old -> old, old, new, old
   */
  public QuestionResponse addQuestion(String templateName, String question, int number) {
    var templateIdOpt = templateRepo.getIdByName(templateName);
    if (templateIdOpt.isEmpty()) {
      return QuestionFailed.templateNotFound(templateName, number);
    }

    int templateId = templateIdOpt.get();
    if (number == -1) {
      number = questionRepo.getMaxNumberByTemplateId(templateId)
          .map(i -> i.intValue() + 1)
          .orElse(0);
    } else {
      reorderQuestions(templateId, number, 1);
    }

    var questionObj = new RawQuestion(null, templateId, number, question);
    var questionSaved = questionRepo.save(questionObj);
    return new QuestionSuccess(questionSaved);
  }

  public QuestionResponse updateQuestion(String templateName, int number, String question) {
    var templateIdOpt = templateRepo.getIdByName(templateName);
    if (templateIdOpt.isEmpty()) {
      return QuestionFailed.templateNotFound(templateName, number);
    }

    var updated = questionRepo.updateByTemplateIdAndNumber(templateIdOpt.get(), number, question);
    return updated != 0
        ? new QuestionSuccess(new RawQuestion(null, -1, number, question))
        : QuestionFailed.questionNotFound(templateName, number);
  }

  public boolean deleteQuestion(String templateName, int number) {
    var templateIdOpt = templateRepo.getIdByName(templateName);
    if (templateIdOpt.isEmpty()) {
      return false;
    }

    int templateId = templateIdOpt.get();
    var deleted = questionRepo.deleteByTemplateIdAndNumber(templateId, number);
    reorderQuestions(templateId, number, 0);
    return deleted != 0;
  }

  /*
  Reorders (changes the number) of all questions with the given templateId, which
  numbers are out of the line to it's right order, optionally with an offset.
  Eg. 1, 2, 4 -> 1, 2, 3 (offset 0)
      1, 2, 4 -> 1, 2, 5 (offset 2)
   */
  private void reorderQuestions(int templateId, int from, int offset) {
    var questions = questionRepo.findByTemplateIdAndNumberGreaterThanEqualsOrderByNumber(templateId,
        from);
    var updatedQuestions = new HashSet<RawQuestion>(questions.size() - from);
    for (int i = 0; i < questions.size(); i++) {
      var question = questions.get(i);
      int rightNum = i + offset;
      if (question.number() != rightNum) {
        updatedQuestions.add(
            new RawQuestion(question.id(), templateId, rightNum, question.question()));
      }
    }

    questionRepo.deleteAll(updatedQuestions);
    questionRepo.saveAll(updatedQuestions);
  }

}
