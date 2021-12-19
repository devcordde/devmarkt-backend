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

package club.devcord.devmarkt.util.base;

import io.micronaut.context.ApplicationContext;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.netty.DefaultHttpClient;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.runtime.server.EmbeddedServer;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;

public abstract class RestAPITestBase {
  public static final HttpClient CLIENT = new DefaultHttpClient();
  public static final BlockingHttpClient BLOCKING_CLIENT = CLIENT.toBlocking();
  protected BlockingHttpClient client;

  @Inject
  ApplicationContext context;

  @BeforeEach
  public void injectClient(EmbeddedServer server){
    server.start(); // the server doesn't restart automatically
    var client = context.createBean(HttpClient.class, UriBuilder
        .of(server.getURI())
        .path(clientPath())
        .build());
    this.client = client.toBlocking();
  }

  protected abstract String clientPath();
}
