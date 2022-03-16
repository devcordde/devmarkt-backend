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

package club.devcord.devmarkt.responses.template;

import club.devcord.devmarkt.graphql.GraphQLType;
import club.devcord.devmarkt.responses.Fail;

@GraphQLType("TemplateFailed")
public record TemplateFailed(
    String name,
    String errorCode,
    String message
) implements TemplateResponse, Fail {

  public static TemplateFailed notFound(String name) {
    return new TemplateFailed(name, TemplateErrors.NOT_FOUND,
        "No template with the given name found.");
  }

  public static TemplateFailed duplicated(String name) {
    return new TemplateFailed(name, TemplateErrors.DUPLICATED,
        "A template with the same name exists.");
  }

  public static class TemplateErrors {

    public static final String NOT_FOUND = "NOT_FOUND";
    public static final String DUPLICATED = "DUPLICATED";

    private TemplateErrors() {

    }
  }

}
