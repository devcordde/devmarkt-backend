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

import club.devcord.devmarkt.entities.template.Template;

public interface Templates {

  static Failure<Template> notFound(String name) {
    return new Failure<>(Errors.NOT_FOUND.name(),
        "No template called '%s' was found.".formatted(name));
  }

  static Failure<Template> duplicated(String name) {
    return new Failure<>(Errors.DUPLICATED.name(),
        "A template with the name '%s' already exists.".formatted(name));
  }

  static Failure<Template> ambiguousNumber() {
    return new Failure<>(Errors.AMBIGUOUS_NUMBER.name(), "This template has at least one ambiguous number.");
  }

  enum Errors {
    NOT_FOUND,
    DUPLICATED,
    AMBIGUOUS_NUMBER
  }
}
