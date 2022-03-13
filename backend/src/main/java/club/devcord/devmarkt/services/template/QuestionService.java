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
import club.devcord.devmarkt.responses.question.QuestionFailed.QuestionErrors;
import club.devcord.devmarkt.responses.question.QuestionResponse;
import club.devcord.devmarkt.responses.question.QuestionSuccess;
import jakarta.inject.Singleton;

@Singleton
public class QuestionService {

  private final TemplateRepo templateRepo;
  private final QuestionRepo questionRepo;

  public QuestionService(TemplateRepo repo,
      QuestionRepo questionRepo) {
    this.templateRepo = repo;
    this.questionRepo = questionRepo;
  }

  public QuestionResponse addQuestion(String templateName, String question) {
    var id = templateRepo.getIdByName(templateName);
    if(id.isEmpty()) {
      return new QuestionFailed("No template with the given name found",
          templateName, QuestionErrors.TEMPLATE_NOT_FOUND, -1);
    }

    var number = questionRepo.getMaxNumberByTemplateId(id.get()) + 1;
    var questionObj = new RawQuestion(null, id.get(), number, question);
    var questionSaved = questionRepo.save(questionObj);
    return new QuestionSuccess(questionSaved);
  }

  public QuestionResponse updateQuestion(String templateName, int number, String question) {
    var id = templateRepo.getIdByName(templateName);
    if(id.isEmpty()) {
      return new QuestionFailed("No template with the given name found",
          templateName, QuestionErrors.TEMPLATE_NOT_FOUND, number);
    }
    if (!questionRepo.existsByTemplateIdAndNumber(id.get(), number)) {
      return new QuestionFailed("A question with the given templateName and number don't exist.",
          templateName, QuestionErrors.QUESTION_NOT_FOUND, number);
    }
    questionRepo.updateQuestionByTemplateIdAndNumber(id.get(), number, question);
    return new QuestionSuccess(new RawQuestion(null, -1, number, question));
  }

}
