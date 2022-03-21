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

import io.micronaut.context.annotation.Context;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Requires;
import io.micronaut.security.token.jwt.generator.JwtTokenGenerator;
import io.micronaut.security.token.jwt.signature.secret.SecretSignatureConfiguration;
import java.util.List;
import java.util.Map;

// used to create a valid jwt for development purposes
@Factory
@Requires(beans = {SecretSignatureConfiguration.class, JwtTokenGenerator.class})
public class TestFactory {

  @Context
  public String testLol(JwtTokenGenerator generator) {
    System.out.println(generator.generateToken(Map.of(
        "sub", "abc:1234567890",
        "exp", 1647727093194L,
        "roles", List.of("USER"),
        "iat", 1516239022
    )).get());
    return "";
  }

}
