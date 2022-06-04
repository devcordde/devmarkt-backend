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

import club.devcord.devmarkt.entities.auth.Role;
import club.devcord.devmarkt.entities.auth.User;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.json.JsonMapper;
import io.micronaut.security.token.jwt.generator.JwtTokenGenerator;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// this file contains development oriented code and is not intended for production use
@Singleton
@Requires(property = "ENABLE_DEVELOPMENT_FEATURES")
public class DevelopmentStartup implements ApplicationEventListener<StartupEvent> {

  private static final Logger LOGGER = LoggerFactory.getLogger(DevelopmentStartup.class);

  private final JwtTokenGenerator generator;
  private final JsonMapper mapper;

  public DevelopmentStartup(
      JwtTokenGenerator jwtTokenGenerator, JsonMapper mapper) {
    this.generator = jwtTokenGenerator;
    this.mapper = mapper;
  }

  @Override
  public void onApplicationEvent(StartupEvent event) {
    LOGGER.info("testuser:2 user: {}",generator.generateToken(Map.of(
        "sub", "testuser:2",
        "iat", 1516239022
    )).get());

    LOGGER.info("internal:1 user: {}",generator.generateToken(Map.of(
        "sub", "internal:1",
        "iat", 1516239022
    )).get());

    LOGGER.info("testuser:1 user: {}", generator.generateToken(Map.of(
        "sub", "testuser:1",
        "iat", 1516239022
    )).get());
    LOGGER.info("wrong_format userid: {}", generator.generateToken(Map.of(
        "sub", "wrong_format",
        "iat", 1516239022
    )).get());
    LOGGER.info("notKnown_user 'not_known:1' userid: {}", generator.generateToken(Map.of(
        "sub", "notKnown:1",
        "iat", 1516239022
    )).get());

    var code = System.identityHashCode(mapper);
    LOGGER.info("{}", code);

    try {
      var role = new Role(-1, "test");
      var user = new String(mapper.writeValueAsBytes(new User(-1, null, List.of(role))), StandardCharsets.UTF_8);
      LOGGER.info(user);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
