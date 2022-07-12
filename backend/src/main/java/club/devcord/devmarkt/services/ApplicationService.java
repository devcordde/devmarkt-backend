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
import club.devcord.devmarkt.entities.template.Template;
import club.devcord.devmarkt.repositories.AnswerRepo;
import club.devcord.devmarkt.repositories.ApplicationRepo;
import club.devcord.devmarkt.repositories.TemplateRepo;
import club.devcord.devmarkt.responses.Applications;
import club.devcord.devmarkt.responses.Response;
import club.devcord.devmarkt.responses.Success;
import jakarta.inject.Singleton;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.function.Function;

@Singleton
public class ApplicationService {

  private final ApplicationRepo applicationRepo;
  private final TemplateRepo templateRepo;
  private final AnswerRepo answerRepo;

  public ApplicationService(ApplicationRepo repo,
      TemplateRepo templateRepo, AnswerRepo answerRepo) {
    this.applicationRepo = repo;
    this.templateRepo = templateRepo;
    this.answerRepo = answerRepo;
  }

  public Response<Application> application(int id) {
    return applicationRepo.findById(id)
        .map(Success::response)
        .orElseGet(() -> Applications.notFound(id));
  }

  public boolean isOwnApplication(int applicationId, User user) {
    return applicationRepo.existsByIdAndUser(applicationId, user);
  }

  public boolean deleteApplication(int id) {
    var deleted = applicationRepo.deleteById(id);
    return deleted != 0;
  }

  public boolean processApplication(int id, ApplicationStatus status) {
    if (status == ApplicationStatus.UNPROCESSED) {
      return false;
    }

    var updated = applicationRepo.updateById(id, status);
    return updated != 0;
  }

  public Response<Application> createApplication(String templateName, ArrayList<Answer> answers,
      User user) {
    if (applicationRepo.existsUnprocessedByUser(user)) {
      return Applications.hasUnprocessedApplication(user.id());
    }

    var templateOpt = templateRepo.findByName(templateName);
    if (templateOpt.isEmpty()) {
      return Applications.templateNotFound(templateName);
    }
    var template = templateOpt.get();
    var validationResponse = validateAndPrepareAnswers(answers, template, answer -> null, null);
    if (validationResponse != null) {
      return validationResponse;
    }

    if (template.questions().size() != answers.size()) {
      return Applications.questionsUnanswered();
    }

    var application = new Application(-1, null, ApplicationStatus.UNPROCESSED, user, template,
        answers);
    var saved = applicationRepo.save(application);
    return new Success<>(saved);
  }

  private Response<Application> validateAndPrepareAnswers(ArrayList<Answer> answers,
      Template template, Function<Answer, Integer> numberFunc, Application application) {
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
      if (answer.answer().length()
          < question.minAnswerLength()) { // check if answer has minimum length
        return Applications.answerTooShort(answer.answer().length(), question.minAnswerLength(),
            number);
      }
      var preparedAnswer = prepareAnswer(answer, question, numberFunc.apply(answer), application);
      answers.set(i, preparedAnswer);
      knownNumbers.add(number);
    }
    return null;
  }


  private Answer prepareAnswer(Answer answer, Question question, Integer number,
      Application application) {
    return new Answer(number, answer.number(), answer.answer(),
        question, application);
  }

  public Response<Application> updateApplication(int id, ArrayList<Answer> newAnswers) {
    var infoOpt = applicationRepo.findById(
        id); // since relations aren't supported in dto projections yet, it's the easiest to fetch the whole application
    if (infoOpt.isEmpty()) {
      return Applications.notFound(id);
    }
    var applicationInfo = infoOpt.get();

    if (applicationInfo.status() == ApplicationStatus.ACCEPTED) {
      return Applications.alreadyAccepted(id);
    }

    var template = templateRepo.findById(applicationInfo.template().id());
    if (template.isEmpty()) {
      return Applications.templateNotFound(applicationInfo.template().name());
    }
    var validationResponse = validateAndPrepareAnswers(newAnswers, template.get(),
        answer -> applicationInfo.answers().get(answer.number()).id(), applicationInfo);
    if (validationResponse != null) {
      return validationResponse;
    }
    answerRepo.updateAll(newAnswers);
    var updated = applicationRepo.findById(id);
    if (updated.isEmpty()) {
      return Applications.notFound(id);
    }
    return new Success<>(updated.get());
  }
}
