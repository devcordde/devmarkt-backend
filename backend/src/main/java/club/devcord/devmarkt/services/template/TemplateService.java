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

package club.devcord.devmarkt.services.template;

import club.devcord.devmarkt.database.template.TemplateDao;
import club.devcord.devmarkt.dto.template.Template;
import club.devcord.devmarkt.dto.template.TemplateEvent;
import club.devcord.devmarkt.dto.template.TemplateEvent.EventType;
import club.devcord.devmarkt.event.EventBuilder;
import club.devcord.devmarkt.event.EventService;
import io.micronaut.http.sse.Event;
import jakarta.inject.Singleton;
import java.util.Optional;
import java.util.Set;
import reactor.core.publisher.Flux;

@Singleton
public class TemplateService {

  private final EventService<TemplateEvent> eventService = new EventService<>();
  private final TemplateDao dao;

  public TemplateService(TemplateDao dataService) {
    this.dao = dataService;
  }

  private EventBuilder<TemplateEvent> event() {
    return new EventBuilder<>(eventService);
  }

  public Flux<Event<TemplateEvent>> subscribeEvents() {
    return eventService.subscribe();
  }

  public boolean create(Template template, String requesterID) {
    var inserted = dao.insert(template);
    if (inserted) {
      event()
          .name("TemplateEvent")
          .data(new TemplateEvent(template.name(), requesterID, EventType.CREATED, template))
          .fire();
    }
    return inserted;
  }

  public boolean replace(Template template, String requesterID) {
    var replaced = dao.replace(template);
    if (replaced) {
      event()
          .name("TemplateReplaced")
          .data(new TemplateEvent(template.name(), requesterID, EventType.REPLACED, template))
          .fire();
    }
    return replaced;
  }

  public boolean delete(String name, String requesterID) {
    var deleted = dao.delete(name);
    if (deleted) {
      event()
          .name("TemplateDeleted")
          .data(new TemplateEvent(name, requesterID, EventType.DELETED, null))
          .fire();
    }
    return deleted;
  }

  public Optional<Template> get(String name) {
    return dao.find(name);
  }

  public Set<String> names() {
    return dao.allNames();
  }

}
