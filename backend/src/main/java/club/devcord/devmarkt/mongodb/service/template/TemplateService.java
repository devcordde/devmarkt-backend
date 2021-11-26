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

package club.devcord.devmarkt.mongodb.service.template;

import club.devcord.devmarkt.dto.template.Template;
import club.devcord.devmarkt.mongodb.Collection;
import club.devcord.devmarkt.mongodb.Collections;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import jakarta.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Singleton
public class TemplateService {

  private final MongoCollection<Template> collection;

  public TemplateService(@Collection(Template.class) MongoCollection<Template> collection) {
    this.collection = collection;
  }

  public InsertResult insert(Template template) {
    try {
      var result = collection.insertOne(template);
      return !result.wasAcknowledged()
          ? InsertResult.REJECTED
          : InsertResult.INSERTED;
    } catch (MongoWriteException e) {
      if(e.getCode() == 11000) { // 11000 = duplicated primary key: template exists
        return InsertResult.DUPLICATED;
      }
      throw e;
    }
  }

  public ReplaceResult replace(Template template) {
    var result = collection.replaceOne(Collections.eqID(template.name()), template);
    if(!result.wasAcknowledged()) {
      return ReplaceResult.REJECTED;
    }
    if(result.getMatchedCount() == 0) {
      return ReplaceResult.NOT_FOUND;
    }
    if(result.getModifiedCount() == 0) {
      return ReplaceResult.NOT_MODIFIED;
    }
    return ReplaceResult.REPLACED;
  }

  public DeleteResult delete(String name) {
    var result = collection.deleteOne(Collections.eqID(name));
    if(!result.wasAcknowledged()) {
      return DeleteResult.REJECTED;
    }
    return result.getDeletedCount() == 0
        ? DeleteResult.NOT_FOUND
        : DeleteResult.DELETED;
  }

  public Optional<Template> find(String name) {
    var result = collection.find(Collections.eqID(name));
    return Optional.ofNullable(result.first());
  }

  public List<String> allNames() {
    var names = collection.distinct(Collections.ID, String.class)
        .into(new ArrayList<>());
    return java.util.Collections.unmodifiableList(names);
  }
}
