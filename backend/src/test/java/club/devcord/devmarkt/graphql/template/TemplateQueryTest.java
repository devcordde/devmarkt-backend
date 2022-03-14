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

import static club.devcord.devmarkt.graphql.Helpers.assertJson;
import static club.devcord.devmarkt.graphql.Helpers.unwrapTemplate;
import static org.junit.jupiter.api.Assertions.assertEquals;

import club.devcord.devmarkt.DevmarktTest;
import club.devcord.devmarkt.entities.template.Template;
import club.devcord.devmarkt.graphql.Helpers;
import club.devcord.devmarkt.responses.template.TemplateFailed.TemplateErrors;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.inject.Inject;
import java.util.List;
import org.junit.jupiter.api.Test;

public class TemplateQueryTest extends DevmarktTest {

  @Inject
  TemplateMutation mutation;
  @Inject
  TemplateQuery query;

  @Test
  void template_success() throws JsonProcessingException {
    mutation.createTemplate("test", Helpers.QUESTIONS);
    var response = query.template("test");

    assertJson(Helpers.TEMPLATE, unwrapTemplate(response));
  }

  @Test
  void template_notFound() {
    var response = query.template("test");
    assertEquals(TemplateErrors.NOT_FOUND, Helpers.unwrapTemplateFailed(response).errorCode());
  }

  @Test
  void templates_success() throws JsonProcessingException {
    mutation.createTemplate("test1", Helpers.QUESTIONS);
    mutation.createTemplate("test2", Helpers.QUESTIONS);
    mutation.createTemplate("test3", Helpers.QUESTIONS);

    var response = query.templates();

    var templateList = List.of(new Template(-1, "test1", Helpers.QUESTIONS),
        new Template(-1, "test2", Helpers.QUESTIONS),
        new Template(-1, "test3", Helpers.QUESTIONS));
    assertJson(templateList, response);
  }

}
