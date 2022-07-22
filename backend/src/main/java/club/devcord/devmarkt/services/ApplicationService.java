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
import club.devcord.devmarkt.responses.Failure;
import club.devcord.devmarkt.responses.Response;
import club.devcord.devmarkt.responses.Success;
import club.devcord.devmarkt.responses.failure.Error;
import club.devcord.devmarkt.responses.failure.application.AnswerTooShortApplicationErrorData;
import club.devcord.devmarkt.responses.failure.application.ErrorCode;
import club.devcord.devmarkt.responses.failure.application.NumberApplicationErrorData;
import jakarta.inject.Singleton;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
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
        .orElseGet(() -> new Failure<>(ErrorCode.NOT_FOUND));
  }

  public boolean isOwnApplication(int applicationId, User user) {
    return applicationRepo.existsByIdAndUser(applicationId, user);
  }

  public Application currentApplication(User user) {
    return applicationRepo.findOneByUser(user).orElse(null);
  }

  public List<Application> applicationsForUser(User user) {
    return applicationRepo.findAllByUser(user);
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
      return new Failure<>(ErrorCode.HAS_UNPROCESSED_APPLICATION);
    }

    var templateOpt = templateRepo.findByName(templateName);
    if (templateOpt.isEmpty()) {
      return new Failure<>(ErrorCode.TEMPLATE_NOT_FOUND);
    }

    var errors = new ArrayList<Error<Application>>();
    var template = templateOpt.get();
    validateAndPrepareAnswers(answers, template, answer -> null, null, errors);

    if (!errors.isEmpty()) {
      return new Failure<>(errors);
    }

    var application = new Application(-1, null, ApplicationStatus.UNPROCESSED, user, template,
        answers);
    var saved = applicationRepo.save(application);
    return new Success<>(saved);
  }

  private boolean containsNumber(Collection<Answer> answers, int number) {
    return answers.stream().anyMatch(answer -> answer.number() == number);
  }

  private void validateAndPrepareAnswers(ArrayList<Answer> answers,
      Template template, Function<Answer, Integer> numberFunc, Application application,
      Collection<Error<Application>> errors) {
    var knownNumbers = new HashSet<Integer>(answers.size());
    var unansweredQuestions = new ArrayList<>(template.questions());
    for (int i = 0; i < answers.size(); i++) {
      var answer = answers.get(i);
      var number = answer.number();
      if (knownNumbers.contains(number)) { // check if number is unique
        var error = new Error<>(ErrorCode.AMBIGUOUS_ANSWER_NUMBER,
            new NumberApplicationErrorData(number));
        if (!errors.contains(error)) {
          errors.add(error); // prevent duplicated errors
          continue;
        }
      }
      if (number >= template.questions().size()) { // check if a corresponding question exists
        errors.add(new Error<>(ErrorCode.NO_QUESTION, new NumberApplicationErrorData(number)));
        continue;
      }
      var question = template.questions().get(number);
      if (answer.answer().length()
          < question.minAnswerLength()) { // check if answer has minimum length
        errors.add(new Error<>(ErrorCode.ANSWER_TOO_SHORT,
            new AnswerTooShortApplicationErrorData(answer.answer().length(),
                question.minAnswerLength(), number)));
        continue;
      }
      var preparedAnswer = prepareAnswer(answer, question, numberFunc.apply(answer), application);
      answers.set(i, preparedAnswer);
      knownNumbers.add(number);
      unansweredQuestions.removeIf(question1 -> question1.number() == number);
    }
    for (var question : unansweredQuestions) {
      errors.add(new Error<>(ErrorCode.QUESTION_UNANSWERED,
          new NumberApplicationErrorData(question.number())));
    }
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
      return new Failure<>(ErrorCode.NOT_FOUND);
    }
    var applicationInfo = infoOpt.get();

    if (applicationInfo.status() == ApplicationStatus.ACCEPTED) {
      return new Failure<>(ErrorCode.ALREADY_ACCEPTED);
    }

    var template = templateRepo.findById(applicationInfo.template().id());
    if (template.isEmpty()) {
      return new Failure<>(ErrorCode.TEMPLATE_NOT_FOUND);
    }
    var errors = new ArrayList<Error<Application>>();
    validateAndPrepareAnswers(newAnswers, template.get(),
        answer -> applicationInfo.answers().get(answer.number()).id(), applicationInfo, errors);
    if (!errors.isEmpty()) {
      return new Failure<>(errors);
    }
    answerRepo.updateAll(newAnswers);
    var updated = applicationRepo.findById(id);
    if (updated.isEmpty()) {
      return new Failure<>(ErrorCode.NOT_FOUND);
    }
    return new Success<>(updated.get());
  }
}
