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

package club.devcord.devmarkt.qa;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller("qa")
@Requires(env = "qa")
public class DatabaseSeedingController {

  private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseSeedingController.class);
  private final QaDatabaseSeeding qaDatabaseSeeding;

  public DatabaseSeedingController(QaDatabaseSeeding qaDatabaseSeeding) {
    this.qaDatabaseSeeding = qaDatabaseSeeding;
  }

  @Post("seedDatabase")
  public void seedDatabase() {
    LOGGER.info("requested database seeding");
    qaDatabaseSeeding.reseedDatabase();
  }

}
