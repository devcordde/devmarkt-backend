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

import static club.devcord.devmarkt.Helpers.TEMPLATE;
import static club.devcord.devmarkt.Helpers.verify;
import static club.devcord.devmarkt.Seed.TEMPLATE_SEED;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import club.devcord.devmarkt.DevmarktTest;
import club.devcord.devmarkt.Helpers;
import club.devcord.devmarkt.entities.template.Template;
import club.devcord.devmarkt.responses.template.TemplateFailed.TemplateErrors;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

public class TemplateMutationTest extends DevmarktTest {

  @Inject
  TemplateMutation mutation;
  @Inject
  TemplateQuery query;

  @Test
  void createTemplate_success() {
    var response = mutation.createTemplate("test", TEMPLATE.questions());
    verify(Helpers.TEMPLATE, response);
    verify(Helpers.TEMPLATE, query.template(TEMPLATE.name()));
  }

  @Test
  void createTemplate_duplicate() {
    var response = mutation.createTemplate("Dev searched", Helpers.QUESTIONS);
    verify(TemplateErrors.DUPLICATED, response);
  }

  @Test
  void deleteTemplate_success() {
    var response = mutation.deleteTemplate("Dev searched");
    assertTrue(response);
    verify(TemplateErrors.NOT_FOUND, query.template("Dev searched"));
  }

  @Test
  void deleteTemplate_notFound() {
    var response = mutation.deleteTemplate("Sick bill");
    assertFalse(response);
  }

  @Test
  void updateTemplateName_success()  {
    var response = mutation.updateTemplateName("Dev searched", "CIA employment contract");
    assertTrue(response);
    verify(new Template(-1, "CIA employment contract", TEMPLATE_SEED.get("Dev searched").questions()),
        query.template("CIA employment contract"));
    verify(TemplateErrors.NOT_FOUND, query.template("Dev searched"));
  }

  @Test
  void updateTemplateName_notFound() {
    var response = mutation.updateTemplateName("Driving license", "007 License to kill");
    assertFalse(response);
  }
}
