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

package club.devcord.devmarkt.util.base;

import club.devcord.devmarkt.util.MongoContainers;
import com.mongodb.client.MongoClient;
import io.micronaut.context.annotation.Value;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.containers.MongoDBContainer;

public class MongoTestBase {
  @Value("${test.mongo-port}")
  private int port;

  private MongoDBContainer container;

  @BeforeEach
  public void startContainer() {
    container = MongoContainers.new5_0_4(port);
    container.start();
  }

  @AfterEach
  public void removeContainer(MongoClient client) {
    client.close();
    container.close();
  }
}
