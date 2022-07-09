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
import club.devcord.devmarkt.entities.template.Template;
import club.devcord.devmarkt.entities.template.UpdateAction;
import club.devcord.devmarkt.repositories.TemplateRepo;
import club.devcord.devmarkt.responses.Response;
import club.devcord.devmarkt.responses.Success;
import club.devcord.devmarkt.responses.Templates;
import jakarta.inject.Singleton;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Singleton
public class TemplateService {

  private final TemplateRepo templateRepo;

  public TemplateService(TemplateRepo repo) {
    this.templateRepo = repo;
  }

  public Response<Template> create(String name, List<Question> questions) {
    if (templateRepo.existsByName(name)) {
      return Templates.duplicated(name);
    }
    var savedTemplate = templateRepo.save(new Template(-1, name, true, questions));
    return new Success<>(savedTemplate);
  }

  public Response<Template> find(String name) {
    return templateRepo.findByName(name)
        .map(Success::response)
        .orElseGet(() -> Templates.notFound(name));
  }

  public boolean delete(String name) {
    return templateRepo.deleteByName(name) != 0;
  }

  /*
  Updates an existing template based on its current values.
   */
  public Response<Template> update(String templateName, Template updated) {
    var currentTemplateOpt = templateRepo.findByName(templateName);
    if (currentTemplateOpt.isEmpty()) {
      return Templates.notFound(templateName);
    }
    var currentTemplate = currentTemplateOpt.get();
    var questions = currentTemplate.questions();
    for (var question : updated.questions()) {
      if (question.updateAction() == null
          || (question.updateAction() != UpdateAction.APPEND
          && question.number() >= questions.size())) {
        continue;
      }
      switch (question.updateAction()) {
        case APPEND -> questions.add(mutateQuestion(question, questions.size()));
        case REPLACE -> questions.set(question.number(), question);
        case DELETE -> questions.remove(question.number());
        case INSERT -> {
          recorderQuestion(questions, 1, question.number());
          questions.set(question.number(), question);
        }
      }
    }
    recorderQuestion(questions, 0, 0);
    templateRepo.deleteByName(templateName);
    var name = updated.name() != null ? updated.name() : templateName;
    var saved = templateRepo.save(new Template(-1, name, true, questions));
    return new Success<>(saved);
  }

  private Question mutateQuestion(Question question, int number) {
    return new Question(question.internalId(), new QuestionId(question.id().template(), number),
        question.question(), question.multiline(), question.minAnswerLength(),
        question.updateAction());
  }

  private void recorderQuestion(List<Question> questions, int offset, int start) {
    questions.removeAll(Collections.singletonList(null));
    questions.sort(Comparator.comparingInt(Question::number));
    int i = start;
    for (var question : List.copyOf(questions)) {
      if (question.number() != i && question.number() >= start) {
        var updated = mutateQuestion(question, i + offset);
        if (i >= questions.size()) {
          questions.add(updated);
        } else {
          questions.set(i, updated);
        }
      }
      i++;
    }
  }

  public List<Template> all() {
    return templateRepo.findAll();
  }

  public List<String> allNames() {
    return templateRepo.findName();
  }
}
