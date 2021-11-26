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

package club.devcord.devmarkt.mongodb;

import club.devcord.devmarkt.dto.Introspected;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Prototype;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.annotation.Value;
import io.micronaut.inject.InjectionPoint;
import org.bson.UuidRepresentation;
import org.mongojack.JacksonMongoCollection;

@Factory
@Requires(property = "devmarkt.mongodb.database", classes = {MongoClient.class, ObjectMapper.class})
@Introspected
@SuppressWarnings("unchecked")
public class MongoCollectionFactory {

  @Prototype
  public <T> MongoCollection<T> mongoCollection(MongoClient client,
      @Value("${devmarkt.mongodb.database}") String database, ObjectMapper mapper,
      InjectionPoint<?> injectionPoint) {

    Class<T> clazz = injectionPoint
        .getAnnotationMetadata()
        .classValue(Collection.class)
        .map(aClass -> (Class<T>) aClass)
        .orElseThrow(NoCollectionTypeProvided::new);

    return JacksonMongoCollection.builder()
        .withObjectMapper(mapper)
        .build(client, database, clazz, UuidRepresentation.STANDARD);
  }
}
