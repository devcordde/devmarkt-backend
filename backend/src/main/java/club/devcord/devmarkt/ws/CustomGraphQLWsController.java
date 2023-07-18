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

import io.micronaut.configuration.graphql.GraphQLConfiguration;
import io.micronaut.configuration.graphql.GraphQLJsonSerializer;
import io.micronaut.configuration.graphql.ws.apollo.GraphQLApolloWsConfiguration;
import io.micronaut.configuration.graphql.ws.apollo.GraphQLApolloWsController;
import io.micronaut.configuration.graphql.ws.apollo.GraphQLApolloWsRequest.ClientType;
import io.micronaut.configuration.graphql.ws.apollo.GraphQLApolloWsResponse;
import io.micronaut.http.HttpRequest;
import io.micronaut.websocket.CloseReason;
import io.micronaut.websocket.WebSocketSession;
import io.micronaut.websocket.annotation.OnClose;
import io.micronaut.websocket.annotation.OnError;
import io.micronaut.websocket.annotation.OnMessage;
import io.micronaut.websocket.annotation.OnOpen;
import io.micronaut.websocket.annotation.ServerWebSocket;
import java.util.Map;
import org.reactivestreams.Publisher;

@ServerWebSocket(value = "${" + GraphQLConfiguration.PREFIX + "." + GraphQLApolloWsConfiguration.PATH_CONFIG + ":"
    + GraphQLApolloWsConfiguration.DEFAULT_PATH + "}", subprotocols = "graphql-ws")
public class CustomGraphQLWsController {

  private final GraphQLApolloWsController controller;
  private final GraphQLJsonSerializer serializer;
  private final SessionMetaData sessionMetaData;

  public CustomGraphQLWsController(GraphQLApolloWsController controller, GraphQLJsonSerializer serializer,
      SessionMetaData sessionMetaData) {
    this.controller = controller;
    this.serializer = serializer;
    this.sessionMetaData = sessionMetaData;
  }

  @OnOpen
  public void onOpen(WebSocketSession session, HttpRequest<?> request) {
    controller.onOpen(session, request);
  }

  @OnMessage
  public Publisher<GraphQLApolloWsResponse> onMessage(String message, WebSocketSession session) {
    var type = serializer.deserialize(message, MessageType.class);
    var request = session.get("httpRequest", HttpRequest.class).get();
    if (ClientType.GQL_CONNECTION_INIT.getType().equals(type.type())) {
      var auth = (String) type.payload().get("Authorization");
      request.mutate()
          .header("Authorization", auth);
    }
    sessionMetaData.getHttpRequestWebSocketSessionMap().put(request, session);
    return controller.onMessage(message, session);
  }

  @OnClose
  public Publisher<GraphQLApolloWsResponse> onClose(WebSocketSession session, CloseReason closeReason) {
    return controller.onClose(session, closeReason);
  }

  @OnError
  public Publisher<GraphQLApolloWsResponse> onError(WebSocketSession session, Throwable t) {
    return controller.onError(session, t);
  }

  private record MessageType(String type, Map<String, Object> payload) {}
}
