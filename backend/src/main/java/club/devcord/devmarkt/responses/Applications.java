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

package club.devcord.devmarkt.responses;

import club.devcord.devmarkt.entities.application.Application;
import club.devcord.devmarkt.entities.auth.UserId;

public interface Applications {

  static Failure<Application> notFound(int id) {
    return new Failure<>(Errors.NOT_FOUND.name(), "An application with the ID '%s' was not found."
        .formatted(id));
  }

  static Failure<Application> hasUnprocessedApplication(UserId userId) {
    return new Failure<>(Errors.HAS_UNPROCESSED_APPLICATION.name(), "The user '%s' has already one unprocessed application."
        .formatted(userId));
  }

  static Failure<Application> templateNotFound(String name) {
    return new Failure<>(Errors.TEMPLATE_NOT_FOUND.name(), "A template with the name '%s' wasn't found."
        .formatted(name));
  }

  static Failure<Application> ambiguousAnswerNumber(int number) {
    return new Failure<>(Errors.AMBIGUOUS_ANSWER_NUMBER.name(), "The number '%s' is ambiguous."
        .formatted(number));
  }

  static Failure<Application> noQuestion(int number)  {
    return new Failure<>(Errors.NO_QUESTION.name(), "There's no question with number '%s'."
        .formatted(number));
  }

  static Failure<Application> answerTooShort(int length, int expectedLength, int number) {
    return new Failure<>(Errors.ANSWER_TOO_SHORT.name(), "The answer with number %s has %s characters but needs minimum %s"
        .formatted(number, length, expectedLength));
  }

  static Failure<Application> alreadyAccepted(int id) {
    return new Failure<>(Errors.ALREADY_ACCEPTED.name(), "The application with id %s is already accepted."
        .formatted(id));
  }

  static Failure<Application> questionsUnanswered() {
    return new Failure<>(Errors.QUESTION_UNANSWERED.name(), "You have to answer all questions.");
  }

  enum Errors {
    NOT_FOUND,
    HAS_UNPROCESSED_APPLICATION,
    TEMPLATE_NOT_FOUND,
    AMBIGUOUS_ANSWER_NUMBER,
    NO_QUESTION,
    ANSWER_TOO_SHORT,
    ALREADY_ACCEPTED,
    QUESTION_UNANSWERED
  }

}
