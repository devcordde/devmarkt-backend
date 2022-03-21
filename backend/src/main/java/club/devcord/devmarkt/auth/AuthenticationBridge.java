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

import io.micronaut.security.authentication.Authentication;
import jakarta.inject.Singleton;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class AuthenticationBridge {

  private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationBridge.class);

  private final Map<String, Authentication> authenticationMap = new ConcurrentHashMap<>();

  public void authentication(String token, Authentication authentication) {
    if (authenticationMap.containsKey(token)) {
      LOGGER.warn("Duplicated token in auth map, overriding old");
    }
    authenticationMap.put(token, authentication);
  }

  public Optional<Authentication> authentication(String token) {
    return Optional.ofNullable(authenticationMap.remove(token));
  }

}
