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

package club.devcord.devmarkt.service;

import club.devcord.devmarkt.TemplateTest;
import club.devcord.devmarkt.dto.template.Template;
import club.devcord.devmarkt.dto.template.TemplateEvent;
import club.devcord.devmarkt.services.template.CreateResult;
import club.devcord.devmarkt.services.template.TemplateService;
import club.devcord.devmarkt.util.base.MongoTestBase;
import io.micronaut.http.sse.Event;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.Disposable;

@MicronautTest
public class TemplateServiceTest extends MongoTestBase {

  @Inject
  private TemplateService service;

  @Test
  public void testService() {
    var atomicTemplate = new AtomicReference<Template>();

    service.subscribeEvents()
        .map(Event::getData)
        .map(TemplateEvent::templateData)
        .doOnNext(atomicTemplate::set)
        .subscribe();

    var result = service.create(TemplateTest.TEST_TEMPLATE, "12345");

    Assertions.assertEquals(TemplateTest.TEST_TEMPLATE, atomicTemplate.get());
    Assertions.assertEquals(CreateResult.CREATED, result);
  }

}
