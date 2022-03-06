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

package club.devcord.devmarkt.web.template;

import club.devcord.devmarkt.dto.Identified;
import club.devcord.devmarkt.dto.template.Template;
import club.devcord.devmarkt.dto.template.TemplateEvent;
import club.devcord.devmarkt.services.template.TemplateService;
import club.devcord.devmarkt.util.Uris;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.annotation.Status;
import io.micronaut.http.sse.Event;
import java.util.Set;
import org.reactivestreams.Publisher;

@Controller("/template")
public class TemplateController {

  private final TemplateService service;

  public TemplateController(TemplateService templateService) {
    this.service = templateService;
  }

  @Get("/events")
  @EventSwagger
  @Produces(MediaType.TEXT_EVENT_STREAM)
  public Publisher<Event<TemplateEvent>> events() {
    return service.subscribeEvents();
  }

  @Post
  @CreateSwagger
  @Status(HttpStatus.CREATED)
  public HttpResponse<Template> createTemplate(@Body Identified<Template> body) {
    var template = body.value();
    var requesterID = body.requesterID();
    if (service.create(template, requesterID)) {
      return HttpResponse.created(Uris.of("template", template.name()));
    }
    return HttpResponse.status(HttpStatus.CONFLICT);
  }

  @Put
  @ReplaceSwagger
  @Status(HttpStatus.NO_CONTENT)
  public HttpResponse<Template> replaceTemplate(@Body Identified<Template> body) {
    var template = body.value();
    var requesterID = body.requesterID();
    if (service.replace(template, requesterID)) {
      HttpResponse.ok(template)
          .header(HttpHeaders.LOCATION, Uris.of("template", template.name()).toString());
    }
    return HttpResponse.notFound();
  }

  @Get("/{name}")
  @GetSwagger
  public HttpResponse<Template> getTemplate(@PathVariable String name) {
    var result = service.get(name);
    return result.isEmpty()
        ? HttpResponse.notFound()
        : HttpResponse.ok(result.get());
  }


  @Get
  @ListSwagger
  public HttpResponse<Set<String>> getListOfNames() {
    return HttpResponse.ok(service.names());
  }

  @Delete(value = "/{name}")
  @DeleteSwagger
  @Status(HttpStatus.NO_CONTENT)
  public HttpResponse<Void> delete(@PathVariable String name, @Body String requesterID) {
    return service.delete(name, requesterID)
        ? HttpResponse.noContent()
        : HttpResponse.notFound();
  }
}
