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

package club.devcord.devmarkt.web;

import club.devcord.devmarkt.dto.Identified;
import club.devcord.devmarkt.dto.template.Template;
import club.devcord.devmarkt.mongodb.service.template.TemplateService;
import club.devcord.devmarkt.util.BaseUriBuilder;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import java.net.URI;
import java.util.List;

@Controller("/template")
public class TemplateController {
  private final TemplateService templateService;

  public TemplateController(TemplateService templateService) {
    this.templateService = templateService;
  }

  @Post
  public HttpResponse<URI> createTemplate(@Body Identified<Template> body) {
    var template = body.value();
    return switch (templateService.insert(template)) {
      case REJECTED -> HttpResponse.serverError();
      case DUPLICATED -> HttpResponse.status(HttpStatus.CONFLICT);
      case INSERTED -> HttpResponse.created(BaseUriBuilder.of("template", template.name()));
    };
  }

  @Put
  public HttpResponse<Object> replaceTemplate(@Body Identified<Template> body) {
    var template = body.value();
    return switch (templateService.replace(template)) {
      case REJECTED -> HttpResponse.serverError();
      case NOT_MODIFIED -> HttpResponse.notModified();
      case NOT_FOUND -> HttpResponse.notFound();
      case REPLACED -> HttpResponse.noContent()
          .header("location", BaseUriBuilder.of("template", template.name()).toString());
    };
  }

  @Get("/{name}")
  public HttpResponse<Template> getTemplate(@PathVariable String name) {
    var result = templateService.find(name);
    return result.isEmpty()
        ? HttpResponse.notFound()
        : HttpResponse.ok(result.get());
  }


  @Get
  public HttpResponse<List<String>> getListOfNames() {
    return HttpResponse.ok(templateService.allNames());
  }

  @Delete(value = "/{name}")
  public HttpResponse<Void> delete(@PathVariable String name, @Body String requesterID) {
    return switch (templateService.delete(name)) {
      case REJECTED -> HttpResponse.serverError();
      case NOT_FOUND -> HttpResponse.notFound();
      case DELETED -> HttpResponse.ok();
    };
  }
}
