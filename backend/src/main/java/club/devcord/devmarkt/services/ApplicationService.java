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

import club.devcord.devmarkt.entities.application.Answer;
import club.devcord.devmarkt.entities.application.Application;
import club.devcord.devmarkt.entities.application.ApplicationStatus;
import club.devcord.devmarkt.entities.auth.User;
import club.devcord.devmarkt.entities.template.Question;
import club.devcord.devmarkt.repositories.ApplicationRepo;
import club.devcord.devmarkt.repositories.TemplateRepo;
import club.devcord.devmarkt.responses.Applications;
import club.devcord.devmarkt.responses.Response;
import club.devcord.devmarkt.responses.Success;
import jakarta.inject.Singleton;
import java.util.ArrayList;
import java.util.HashSet;

@Singleton
public class ApplicationService {

  private final ApplicationRepo repo;
  private final TemplateRepo templateRepo;

  public ApplicationService(ApplicationRepo repo,
      TemplateRepo templateRepo) {
    this.repo = repo;
    this.templateRepo = templateRepo;
  }

  public Response<Application> application(int id) {
    return repo.findById(id)
        .map(Success::response)
        .orElseGet(() -> Applications.notFound(id));
  }

  public boolean deleteApplication(int id) {
    var deleted = repo.deleteById(id);
    return deleted != 0;
  }

  public Response<Application> createApplication(String templateName, ArrayList<Answer> answers, User user) {
    if (repo.existsUnprocessedByUser(user)) {
      return Applications.hasUnprocessedApplication(user.id());
    }

    var templateOpt = templateRepo.findByName(templateName);
    if (templateOpt.isEmpty()) {
      return Applications.templateNotFound(templateName);
    }
    var template = templateOpt.get();

    var knownNumbers = new HashSet<Integer>(answers.size());
    for (int i = 0; i < answers.size(); i++) {
      var answer = answers.get(i);
      var number = answer.number();
      if (knownNumbers.contains(number)) { // check if number is unique
        return Applications.ambiguousAnswerNumber(number);
      }
      if (number >= template.questions().size()) { // check if a corresponding question exists
        return Applications.noQuestion(number);
      }
      var question = template.questions().get(number);
      if (answer.answer().length() < question.minAnswerLength()) { // check if answer has minimum length
        return Applications.answerTooShort(answer.answer().length(), question.minAnswerLength(), number);
      }
      var preparedAnswer = prepareAnswer(answer, question);
      answers.set(i, preparedAnswer);
      knownNumbers.add(number);
    }

    var application = new Application(-1, null, ApplicationStatus.UNPROCESSED, user, template.id(),
        answers);
    var saved = repo.save(application);
    return new Success<>(saved);
  }

  private Answer prepareAnswer(Answer answer, Question question) {
    return new Answer(null, answer.number(), answer.answer(),
        question, null);
  }
}
