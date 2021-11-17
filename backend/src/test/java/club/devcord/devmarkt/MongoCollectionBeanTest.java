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

package club.devcord.devmarkt;

import club.devcord.devmarkt.mongodb.Collection;
import com.mongodb.client.MongoCollection;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mongojack.ObjectId;

@TestInstance(Lifecycle.PER_CLASS)
@MicronautTest
public class MongoCollectionBeanTest {

  @Inject
  @Collection(TestDTO.class)
  MongoCollection<TestDTO> collection;

  @Test
  public void testCollection() {
    collection.insertOne(new TestDTO(null, "Test"));
  }

  @org.mongojack.MongoCollection(name = "testDTOs")
  public static record TestDTO(
      @ObjectId ObjectId id,
      String data
  ){}
}
