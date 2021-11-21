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
import club.devcord.devmarkt.mongodb.Collection;
import club.devcord.devmarkt.mongodb.Collections;
import club.devcord.devmarkt.util.BaseUriBuilder;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller("/template")
public class TemplateController {
  private final MongoCollection<Template> collection;

  public TemplateController(@Collection(Template.class) MongoCollection<Template> collection) {
    this.collection = collection;
  }

  @Post
  public HttpResponse<URI> createTemplate(@Body Identified<Template> body) {
    var template = body.value();

    try {
      var result = collection.insertOne(template);
      return !result.wasAcknowledged()
          ? HttpResponse.serverError()
          : HttpResponse.created(BaseUriBuilder.of("template", template.name()));
    } catch (MongoWriteException e) {
      if(e.getCode() == 11000) { // 11000 = duplicated primary key: template exists
        return HttpResponse.status(HttpStatus.CONFLICT);
      }
      throw e;
    }
  }

  @Put
  public HttpResponse<URI> replaceTemplate(@Body Identified<Template> body) {
    var template = body.value();

    var result = collection.replaceOne(Collections.eqID(template.name()), template);
    if(!result.wasAcknowledged()) {
      return HttpResponse.serverError();
    }
    if(result.getMatchedCount() == 0) {
      return HttpResponse.notFound();
    }
    if(result.getModifiedCount() == 0) {
      return HttpResponse.notModified();
    }
    return HttpResponse.ok(BaseUriBuilder.of("template", template.name()));
  }

  @Get("/{name}")
  public HttpResponse<Template> getTemplate(@PathVariable String name) {
    var result = collection.find(Collections.eqID(name))
        .first();

    return Objects.isNull(result)
        ? HttpResponse.notFound()
        : HttpResponse.ok(result);
  }


  @SuppressWarnings("NullableProblems") // fix later this shit
  @Get
  public HttpResponse<List<String>> getListOfNames() {
    var names = collection.find()
        .map(Template::name)
        .into(new ArrayList<>());

    return HttpResponse.ok(names);
  }

  @Delete(value = "/{name}", consumes = "text/plain")
  public HttpResponse<Void> delete(@PathVariable String name, @Body String requesterID) {
    var result = collection.deleteOne(Collections.eqID(name));

    if(!result.wasAcknowledged()) {
      return HttpResponse.serverError();
    }

    return result.getDeletedCount() == 1
        ? HttpResponse.ok()
        : HttpResponse.notFound();
  }
}
