/*
 * Copyright 2021 Contributors to the Devmarkt-Backend project
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

package club.devcord.devmarkt;

import club.devcord.devmarkt.dto.Identified;
import club.devcord.devmarkt.dto.template.Question;
import club.devcord.devmarkt.dto.template.Template;
import club.devcord.devmarkt.util.base.RestAPITestBase;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@MicronautTest(rebuildContext = true, application = Application.class)
public class TemplateTest extends RestAPITestBase {
  private static final Template TEST_TEMPLATE = new Template(
      "test",
      List.of(new Question("How are you?"), new Question("How old are you?"))
  );

  @Override
  protected String clientPath() {
    return "/template";
  }

  public void verifyTemplate(String url, Template template) {
    var fetchedTemplate = RestAPITestBase.BLOCKING_CLIENT.exchange(HttpRequest.GET(
        url
    ), Template.class).body();

    Assertions.assertEquals(template, fetchedTemplate);
  }

  @Test
  public void createTemplate() {
    var result = client.exchange(HttpRequest.POST(
        "",
        new Identified<>(
            "1234",
            TEST_TEMPLATE
        )
    ));
    Assertions.assertEquals(HttpStatus.CREATED, result.getStatus());
    verifyTemplate(result.header("location"), TEST_TEMPLATE);
  }

  @Test
  public void updateTemplate() {
    createTemplate();
    var testTemplate = new Template(
        "test",
        List.of(new Question("Whats your name?"))
    );
    var result = client.exchange(HttpRequest.PUT(
        "",
        new Identified<>(
            "1234",
            testTemplate
        )
    ));

    Assertions.assertEquals(HttpStatus.NO_CONTENT, result.getStatus());
    verifyTemplate(String.valueOf(result.header("location")), testTemplate);
  }

  @Test
  public void getTemplate() {
    createTemplate();
    var result = client.retrieve(HttpRequest.GET(
        "/test"
    ), Template.class);

    Assertions.assertEquals(TEST_TEMPLATE, result);
  }

  @Test
  public void listTemplates() {
    createTemplate();
    var result = client.retrieve(HttpRequest.GET(
        ""
    ), List.class);

    Assertions.assertEquals(List.of("test"), result);
  }

  @Test
  public void deleteTemplate() {
    createTemplate();
    var result = client.exchange(HttpRequest.DELETE(
        "/test",
        new Identified<Void>(
            "1234",
            null
        )
    ), Void.class);

    Assertions.assertEquals(HttpStatus.OK, result.getStatus());
  }
}
