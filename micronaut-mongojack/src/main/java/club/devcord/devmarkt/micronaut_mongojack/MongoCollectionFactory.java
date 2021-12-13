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
package club.devcord.devmarkt.micronaut_mongojack;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Prototype;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.annotation.Value;
import io.micronaut.core.type.ArgumentCoercible;
import io.micronaut.inject.InjectionPoint;
import org.bson.UuidRepresentation;
import org.mongojack.JacksonMongoCollection;

@Factory
@Requires(property = "mongodb.database", beans = {MongoClient.class, ObjectMapper.class})
@SuppressWarnings("unchecked")
public class MongoCollectionFactory {

  @Prototype
  public <T> MongoCollection<T> mongoCollection(MongoClient client,
      @Value("${mongodb.database}") String database, ObjectMapper mapper,
      InjectionPoint<?> injectionPoint) {

    if(injectionPoint instanceof ArgumentCoercible) {
      var argument = ((ArgumentCoercible<T>) injectionPoint)
          .asArgument();
      var collectionType = (Class<T>) argument
          .getFirstTypeVariable()
          .orElseThrow(NoCollectionTypeProvided::new)
          .getType();
      return JacksonMongoCollection.builder()
          .withObjectMapper(mapper)
          .build(client, database, collectionType, UuidRepresentation.STANDARD);
    }
    throw new NoCollectionTypeProvided();
  }
}
