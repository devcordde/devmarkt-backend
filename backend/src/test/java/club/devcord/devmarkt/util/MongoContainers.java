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

package club.devcord.devmarkt.util;

import org.testcontainers.containers.MongoDBContainer;


public class MongoContainers extends MongoDBContainer {

  private MongoContainers(String image, int port) {
    super(image);
    super.addFixedExposedPort(port, 27017);
  }

  public static MongoDBContainer newLatest(int port) {
    return new MongoContainers("mongo:5.0.4", port);
  }

}
