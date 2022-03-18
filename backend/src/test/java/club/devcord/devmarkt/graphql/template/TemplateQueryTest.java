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

package club.devcord.devmarkt.graphql.template;

import static club.devcord.devmarkt.Helpers.verify;
import static club.devcord.devmarkt.Seed.TEMPLATE_SEED;

import club.devcord.devmarkt.DevmarktTest;
import club.devcord.devmarkt.entities.template.Template;
import club.devcord.devmarkt.responses.template.TemplateFailed.TemplateErrors;
import jakarta.inject.Inject;
import java.util.List;
import org.junit.jupiter.api.Test;

public class TemplateQueryTest extends DevmarktTest {

  @Inject
  TemplateMutation mutation;
  @Inject
  TemplateQuery query;

  @Test
  void template_success() {
    var response = query.template("Dev searched");
    verify(TEMPLATE_SEED.get("Dev searched"), response);
  }

  @Test
  void template_notFound() {
    var response = query.template("Lilly's 'Tabellenschubsergang' Membership request");
    verify(TemplateErrors.NOT_FOUND, response);
  }

  @Test
  void templates_success() {
    var response = query.templates(new DataFetchingEnviromentStub("name", "questions"));
    verify(TEMPLATE_SEED.values(), response);
  }

  @Test
  void templates_onlyNames_success() {
    var onlyNameList = TEMPLATE_SEED.values()
        .stream()
        .map(template -> new Template(-1, template.name(), List.of()))
        .toList();

    var response = query.templates(new DataFetchingEnviromentStub("name"));
    verify(onlyNameList, response);
  }

}
