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
import club.devcord.devmarkt.repositories.ApplicationRepo;
import club.devcord.devmarkt.repositories.TemplateRepo;
import club.devcord.devmarkt.responses.Applications;
import club.devcord.devmarkt.responses.Response;
import club.devcord.devmarkt.responses.Success;
import club.devcord.devmarkt.util.Collections;
import jakarta.inject.Singleton;
import java.util.ArrayList;
import java.util.Comparator;

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

  public Response<Application> createApplication(String templateName, ArrayList<Answer> answers, User user) {
    if (repo.existsUnprocessedByUser(user)) {
      return Applications.hasUnprocessedApplication(user.id());
    }

    var templateOpt = templateRepo.findByName(templateName);
    if (templateOpt.isEmpty()) {
      return Applications.templateNotFound(templateName);
    }
    var template = templateOpt.get();

    if (Collections.hasAmbiguousEntry(answers, Answer::number)) {
      return Applications.answersHaveSameNumber();
    }

    answers.sort(Comparator.comparingInt(Answer::number));
    for (int i = 0; i < answers.size(); i++) {
      var originalAnswer = answers.get(i);
      var updatedAnswer = new Answer(null, originalAnswer.number(), originalAnswer.answer(),
          template.questions().get(i), null);
      answers.set(i, updatedAnswer);
    }

    var application = new Application(-1, null, ApplicationStatus.UNPROCESSED, user,
        template.id(), answers);
    System.out.println(application);
    var saved = repo.save(application);
    return new Success<>(saved);
  }
}
