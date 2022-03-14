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

package club.devcord.devmarkt.graphql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import club.devcord.devmarkt.entities.template.Question;
import club.devcord.devmarkt.entities.template.Template;
import club.devcord.devmarkt.responses.template.TemplateFailed;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

public class Helpers {

  public static final List<Question> QUESTIONS = List.of(
      new Question(null, null, 0, "How are you?"),
      new Question(null, null, 1, "Who are you?"));

  public static final Template TEMPLATE = new Template(-1, "test", QUESTIONS);

  public static void assertTemplate(ObjectMapper mapper, Template template)
      throws JsonProcessingException {
    assertEquals(mapper.writeValueAsString(TEMPLATE), mapper.writeValueAsString(template));
  }

  public static Template unwrapTemplate(Object response) {
    assertTrue(response instanceof Template, "Response isn't a Template");
    return (Template) response;
  }

  public static TemplateFailed unwrapFailed(Object response) {
    assertTrue(response instanceof TemplateFailed, "Response isn't a TemplateFailed");
    return (TemplateFailed) response;
  }

}
