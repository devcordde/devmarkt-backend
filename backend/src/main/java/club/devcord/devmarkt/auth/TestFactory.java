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

package club.devcord.devmarkt.auth;

import club.devcord.devmarkt.repositories.UserRepo;
import io.micronaut.context.BeanContext;
import io.micronaut.context.annotation.Context;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Requires;
import io.micronaut.security.token.jwt.generator.JwtTokenGenerator;
import io.micronaut.security.token.jwt.signature.secret.SecretSignatureConfiguration;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// used to create a valid jwt for development purposes
@Factory
@Requires(beans = {SecretSignatureConfiguration.class, JwtTokenGenerator.class})
public class TestFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(TestFactory.class);

  @Context
  public String testLol(JwtTokenGenerator generator, BeanContext context, UserRepo repo) {

    LOGGER.info("testuser:2 user: " + generator.generateToken(Map.of(
        "sub", "testuser:2",
        "iat", 1516239022
    )).get());

    LOGGER.info("internal:1 user: " + generator.generateToken(Map.of(
        "sub", "internal:1",
        "iat", 1516239022
    )).get());

    LOGGER.info("testuser:1 user: " + generator.generateToken(Map.of(
        "sub", "testuser:1",
        "iat", 1516239022
    )).get());
    return "";
  }

}
