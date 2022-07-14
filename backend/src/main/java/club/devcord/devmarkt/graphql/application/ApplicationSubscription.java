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

package club.devcord.devmarkt.graphql.application;

import club.devcord.devmarkt.entities.application.Application;
import club.devcord.devmarkt.entities.application.ApplicationProcessEvent;
import club.devcord.devmarkt.services.ApplicationService;
import club.devcord.devmarkt.services.ApplicationService.ApplicationEventType;
import graphql.kickstart.tools.GraphQLSubscriptionResolver;
import jakarta.inject.Singleton;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

@Singleton
public class ApplicationSubscription implements GraphQLSubscriptionResolver {

  private final ApplicationService applicationService;

  public ApplicationSubscription(
      ApplicationService applicationService) {
    this.applicationService = applicationService;
  }

  public Publisher<Application> applicationCreated() {
    return Flux.from(applicationService.eventStream())
        .filter(applicationEvent -> applicationEvent.type() == ApplicationEventType.CREATED)
        .map(event -> (Application) event.data());
  }

  public Publisher<Integer> applicationDeleted() {
    return Flux.from(applicationService.eventStream())
        .filter(applicationEvent -> applicationEvent.type() == ApplicationEventType.DELETED)
        .map(event -> (Integer) event.data());
  }

  public Publisher<ApplicationProcessEvent> applicationProcessed() {
    return Flux.from(applicationService.eventStream())
        .filter(applicationEvent -> applicationEvent.type() == ApplicationEventType.PROCESSED)
        .map(event -> (ApplicationProcessEvent) event.data());
  }

}
