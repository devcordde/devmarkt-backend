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

import club.devcord.devmarkt.entities.template.Question;

public interface Questions {

  static Failure<Question> templateNotFound(String templateName) {
    return new Failure<>(Errors.TEMPLATE_NOT_FOUND.name(),
        "No template called '%s' was found.".formatted(templateName));
  }

  static Failure<Question> questionNotFound(String templateName, int number) {
    return new Failure<>(Errors.QUESTION_NOT_FOUND.name(),
        "Template '%s' has no question with number %s".formatted(templateName, number));
  }

  enum Errors {
    TEMPLATE_NOT_FOUND,
    QUESTION_NOT_FOUND
  }

}

