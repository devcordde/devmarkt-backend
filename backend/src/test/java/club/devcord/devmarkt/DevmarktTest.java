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

package club.devcord.devmarkt;

import club.devcord.devmarkt.graphql.Helpers;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;
import java.util.Map;
import org.flywaydb.core.Flyway;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.testcontainers.containers.PostgreSQLContainer;

@MicronautTest(rollback = false)
@TestInstance(Lifecycle.PER_CLASS)
public abstract class DevmarktTest implements TestPropertyProvider {

  private static final PostgreSQLContainer<?> CONTAINER;

  static {
    CONTAINER = new PostgreSQLContainer<>("postgres:14.2")
        .withDatabaseName("devmarkt")
        .withUsername("Johnny")
        .withPassword("12345")
        .withExposedPorts(5432);
    CONTAINER.start();
  }

  @BeforeEach
  void beforeEach(Flyway flyway, ObjectMapper mapper) {
    flyway.clean();
    flyway.migrate();
    Helpers.initMapper(mapper);
  }

  @Override
  @NotNull
  public Map<String, String> getProperties() {
    return Map.of(
        "datasources.default.url", CONTAINER.getJdbcUrl(),
        "datasources.default.username", CONTAINER.getUsername(),
        "datasources.default.password", CONTAINER.getPassword()
    );
  }

}
