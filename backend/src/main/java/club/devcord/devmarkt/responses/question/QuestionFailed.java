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

package club.devcord.devmarkt.responses.question;

import club.devcord.devmarkt.GraphQLType;
import club.devcord.devmarkt.responses.Fail;

@GraphQLType("QuestionFailed")
public record QuestionFailed(
    String message,
    String templateName,
    String errorCode,
    int number
) implements QuestionResponse, Fail {

  public static QuestionFailed templateNotFound(String templateName, int number) {
    return new QuestionFailed("No template with the given name found.",
        templateName, QuestionErrors.TEMPLATE_NOT_FOUND, number);
  }

  public static QuestionFailed questionNotFound(String templateName, int number) {
    return new QuestionFailed("No question with the given template name and number found.",
        templateName, QuestionErrors.QUESTION_NOT_FOUND, number);
  }

  public static class QuestionErrors {

    public static final String TEMPLATE_NOT_FOUND = "TEMPLATE_NOT_FOUND";
    public static final String QUESTION_NOT_FOUND = "QUESTION_NOT_FOUND";

    private QuestionErrors() {

    }
  }

}
