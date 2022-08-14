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
import io.micronaut.configuration.graphql.ws.GraphQLWsRequest;
import io.micronaut.configuration.graphql.ws.GraphQLWsResponse;
import io.micronaut.context.BeanContext;
import io.micronaut.websocket.WebSocketSession;
import jakarta.inject.Singleton;
import java.util.Map;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

// Dirty solution to unsubscribe a user's subscriptions (uses reflections)
@Singleton
public class ReflectiveUnsubscriber{

  private final Object wsState;
  private final SessionMetaData metaData;

  public ReflectiveUnsubscriber(BeanContext beanContext, SessionMetaData metaData) {
    this.metaData = metaData;
    try {
      var stateClazz = Class.forName("io.micronaut.configuration.graphql.ws.GraphQLWsState");
      this.wsState = beanContext.getBean(stateClazz);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  public void unsubscribeSubscriptions(UserId userId) {
    var session = metaData.getUserSessions().get(userId);
    if (session == null) return;
    try {
      var activeOperationsField = wsState.getClass().getDeclaredField("activeOperations");
      activeOperationsField.setAccessible(true);
      var activeOperations = ((Map<String, ?>) activeOperationsField.get(wsState)).get(session.getId());
      var operationIdsField = activeOperations.getClass().getDeclaredField("activeOperations");
      operationIdsField.setAccessible(true);
      var operationIds = ((Map<String, ?>) operationIdsField.get(activeOperations)).keySet();
      var method = wsState.getClass().getDeclaredMethod("stopOperation", GraphQLWsRequest.class,
          WebSocketSession.class);
      method.setAccessible(true);
      for (var id : operationIds) {
        var request = new GraphQLWsRequest();
        request.setId(id);
        Flux.from((Publisher<GraphQLWsResponse>) method.invoke(wsState, request, session))
            .subscribe(session::sendSync);
      }
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
  }

}
