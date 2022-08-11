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

package club.devcord.devmarkt.ws;

import club.devcord.devmarkt.entities.auth.UserId;
import io.micronaut.http.HttpRequest;
import io.micronaut.websocket.WebSocketSession;
import jakarta.inject.Singleton;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class SessionMetaData {
  private final Map<UserId, WebSocketSession> userSessions = new ConcurrentHashMap<>();
  private final Map<HttpRequest<?>, WebSocketSession> httpRequestWebSocketSessionMap
      = new ConcurrentHashMap<>();

  public Map<UserId, WebSocketSession> getUserSessions() {
    return userSessions;
  }

  public Map<HttpRequest<?>, WebSocketSession> getHttpRequestWebSocketSessionMap() {
    return httpRequestWebSocketSessionMap;
  }
}
