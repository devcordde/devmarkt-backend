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

package club.devcord.devmarkt.event;

import io.micronaut.http.sse.Event;

public class EventBuilder<T> {

  private final EventService<T> service;

  private String name;
  private T data;

  public EventBuilder(EventService<T> service) {
    this.service = service;
  }

  public EventBuilder<T> name(String name) {
    this.name = name;
    return this;
  }

  public EventBuilder<T> data(T data) {
    this.data = data;
    return this;
  }

  public Event<T> build() {
    return Event.of(data)
        .name(name);
  }

  public void fire() {
    service.publish(build());
  }

}
