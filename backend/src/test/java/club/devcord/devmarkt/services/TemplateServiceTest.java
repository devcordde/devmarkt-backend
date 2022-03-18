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

import static club.devcord.devmarkt.Helpers.verify;
import static club.devcord.devmarkt.Seed.TEMPLATE_SEED;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import club.devcord.devmarkt.DevmarktTest;
import club.devcord.devmarkt.Helpers;
import club.devcord.devmarkt.Seed;
import club.devcord.devmarkt.entities.template.Template;
import club.devcord.devmarkt.responses.template.TemplateFailed.TemplateErrors;
import club.devcord.devmarkt.responses.template.TemplateSuccess;
import jakarta.inject.Inject;
import java.util.List;
import org.junit.jupiter.api.Test;

public class TemplateServiceTest extends DevmarktTest {

  @Inject
  TemplateService service;

  @Test
  void create_success() {
    var response = service.create("Toilets blasting request", Helpers.QUESTIONS);
    assertTrue(response instanceof TemplateSuccess);
    verify(new Template(-1, "Toilets blasting request", Helpers.QUESTIONS),
        ((TemplateSuccess) response).template());
  }

  @Test
  void create_templateDuplicated() {
    var response = service.create("Dev offered", List.of());
    verify(TemplateErrors.DUPLICATED, response);
  }

  @Test
  void find_success() {
    var response = service.find("Dev searched");
    assertTrue(response instanceof TemplateSuccess);
    verify(TEMPLATE_SEED.get("Dev searched"), ((TemplateSuccess) response).template());
  }

  @Test
  void find_notFound() {
    var response = service.find("Grinch wishlist");
    verify(TemplateErrors.NOT_FOUND, response);
  }

  @Test
  void delete_success() {
    var deleted = service.delete("Dev offered");
    assertTrue(deleted);
  }

  @Test
  void delete_fail() {
    var deleted = service.delete("Not existing template");
    assertFalse(deleted);
  }

  @Test
  void updateName_success() {
    var updated = service.updateName("Dev offered", "I'm here to do stuff for money :)");
    assertTrue(updated);
  }

  @Test
  void updateName_fail() {
    var updated = service.updateName("Template that disappeared", "Template suddenly back");
    assertFalse(updated);
  }

  @Test
  void all_success() {
    var all = service.all();
    verify(TEMPLATE_SEED.values(), all);
  }

  @Test
  void allNames_success() {
    var names = service.allNames();
    verify(Seed.templateNames(), names);
  }

}
