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

import club.devcord.devmarkt.event.EventService;
import io.micronaut.http.sse.Event;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EventServiceTest {

  @Test
  public void testEvents() {
    var service = new EventService<Person>();

    AtomicInteger counter = new AtomicInteger();

    service.subscribe()
        .map(Event::getData)
        .map(Person::name)
        .filter("Robert"::equals)
        .doOnNext(s -> counter.incrementAndGet())
        .subscribe();

    service.subscribe()
        .map(Event::getData)
        .map(Person::name)
        .filter("Detlef"::equals)
        .doOnNext(s -> counter.incrementAndGet())
        .subscribe();

    service.publish(Event.of(new Person("Robert", 49)));
    service.publish(Event.of(new Person("Detlef", 77)));
    Assertions.assertEquals(2, counter.get());
  }

  private static record Person(String name, int age) {}

}
