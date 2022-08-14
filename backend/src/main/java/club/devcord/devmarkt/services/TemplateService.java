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
import club.devcord.devmarkt.entities.template.TemplateUpdateInput;
import club.devcord.devmarkt.entities.template.UpdateAction;
import club.devcord.devmarkt.repositories.TemplateRepo;
import club.devcord.devmarkt.responses.FailureException;
import club.devcord.devmarkt.responses.failure.template.ErrorCode;
import club.devcord.devmarkt.responses.failure.template.NumberTemplateErrorData;
import club.devcord.devmarkt.util.Collections;
import jakarta.inject.Singleton;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Singleton
public class TemplateService {

  private final TemplateRepo templateRepo;

  public TemplateService(TemplateRepo repo) {
    this.templateRepo = repo;
  }

  public Template create(String name, List<Question> questions) {
    if (templateRepo.existsByName(name)) {
      throw new FailureException(ErrorCode.DUPLICATED);
    }
    var ambiguousEntries = Collections.ambiguousEntries(questions, Question::number);
    if (!ambiguousEntries.isEmpty()) {
      var errors = ambiguousEntries
          .stream()
          .map(NumberTemplateErrorData::new)
          .map(NumberTemplateErrorData::data)
          .toList();
      throw new FailureException(ErrorCode.AMBIGUOUS_NUMBER, errors);
    }
    return templateRepo.save(new Template(-1, name, true, questions));
  }

  public Template find(String name) {
    return templateRepo.findByName(name)
        .orElseThrow(() -> new FailureException(ErrorCode.NOT_FOUND));
  }

  public boolean delete(String name) {
    return templateRepo.deleteByName(name) != 0;
  }

  /*
  Updates an existing template based on its current values.
   */
  public Template update(String templateName, TemplateUpdateInput updated) {
    var currentTemplateOpt = templateRepo.findByName(templateName);
    if (currentTemplateOpt.isEmpty()) {
      throw new FailureException(ErrorCode.NOT_FOUND);
    }
    var currentTemplate = currentTemplateOpt.get();
    var questions = new ArrayList<>(currentTemplate.questions());

    // remove old internalIds so that the questions are inserted (cascade)
    questions.replaceAll(this::removeInternalId);

    for (var question : updated.questions()) {
      if (question.updateAction() == null
          || (question.updateAction() != UpdateAction.APPEND
          && question.number() >= questions.size())) {
        continue;
      }
      switch (question.updateAction()) {
        case APPEND -> questions.add(mutateQuestion(question, questions.size()));
        case REPLACE -> questions.set(question.number(), question);
        case DELETE -> {
          questions.remove(question.number());
          recorderQuestion(questions, 0, 0);
        }
        case INSERT -> {
          recorderQuestion(questions, 1, question.number());
          questions.set(question.number(), question);
        }
      }
    }
    templateRepo.deleteByName(templateName);
    var name = updated.name() != null ? updated.name() : templateName;
    return templateRepo.save(new Template(-1, name, true, questions));
  }

  private Question mutateQuestion(Question question, int number) {
    return new Question(question.internalId(), new QuestionId(null, number),
        question.question(), question.multiline(), question.minAnswerLength(),
        question.updateAction());
  }

  private Question removeInternalId(Question question) {
    return new Question(null, question.id(),
        question.question(), question.multiline(), question.minAnswerLength(),
        question.updateAction());
  }

  private void recorderQuestion(List<Question> questions, int offset, int start) {
    questions.removeAll(java.util.Collections.singletonList(null));
    questions.sort(Comparator.comparingInt(Question::number));

    var original = List.copyOf(questions);
    for (int i = start; i < original.size(); i++) {
      var question = original.get(i);
      var newNum = i + offset;
      var updated = mutateQuestion(question, newNum);
      if (newNum >= original.size()) {
        questions.add(updated);
      } else {
        questions.set(newNum, updated);
      }
    }
  }

  public List<Template> all() {
    var templates = templateRepo.findAll();
    templates.sort(Comparator.comparing(Template::name));
    return templates;
  }

  public List<String> allNames() {
    var names = templateRepo.findName();
    names.sort(String::compareTo);
    return names;
  }

  public Optional<Template> findDirect(int id) {
    return templateRepo.findById(id);
  }
}
