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

import static club.devcord.devmarkt.graphql.Helpers.assertTemplate;
import static club.devcord.devmarkt.graphql.Helpers.unwrapFailed;
import static club.devcord.devmarkt.graphql.Helpers.unwrapTemplate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import club.devcord.devmarkt.DevmarktTest;
import club.devcord.devmarkt.entities.template.Template;
import club.devcord.devmarkt.graphql.Helpers;
import club.devcord.devmarkt.responses.template.TemplateFailed.TemplateErrors;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

public class TemplateMutationTest extends DevmarktTest {

  @Inject
  TemplateMutation mutation;
  @Inject
  TemplateQuery query;

  @Test
  void createTemplate_success()
      throws JsonProcessingException {
    var response = mutation.createTemplate("test", Helpers.QUESTIONS);
    assertTemplate(mapper, unwrapTemplate(response));

    var verify = query.template("test");
    assertTemplate(mapper, unwrapTemplate(verify));
  }

  @Test
  void createTemplate_duplicate() {
    mutation.createTemplate("test", Helpers.QUESTIONS);
    var response = mutation.createTemplate("test", Helpers.QUESTIONS);
    assertEquals(TemplateErrors.DUPLICATED, unwrapFailed(response).errorCode());
  }

  @Test
  void deleteTemplate_success() {
    mutation.createTemplate("test", Helpers.QUESTIONS);
    var response = mutation.deleteTemplate("test");
    assertTrue(response);

    var verify = query.template("test");
    assertEquals(TemplateErrors.NOT_FOUND, unwrapFailed(verify).errorCode());
  }

  @Test
  void deleteTemplate_notFound() {
    var response = mutation.deleteTemplate("test");
    assertFalse(response);
  }

  @Test
  void updateTemplateName_success() throws JsonProcessingException {
    mutation.createTemplate("test", Helpers.QUESTIONS);
    var response = mutation.updateTemplateName("test", "newTest");
    assertTrue(response);

    var verify = query.template("newTest");
    assertEquals(mapper.writeValueAsString(new Template(-1, "newTest", Helpers.QUESTIONS)),
        mapper.writeValueAsString(unwrapTemplate(verify)));

    var verifyOld = query.template("test");
    assertEquals(TemplateErrors.NOT_FOUND, unwrapFailed(verifyOld).errorCode());
  }

  @Test
  void updateTemplateName_notFound() {
    var response = mutation.updateTemplateName("test", "newTest");
    assertFalse(response);
  }



}
