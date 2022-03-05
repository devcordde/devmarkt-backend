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

package club.devcord.devmarkt.util;

import io.micronaut.context.annotation.Context;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.server.util.HttpHostResolver;
import io.micronaut.http.uri.UriBuilder;
import jakarta.inject.Singleton;
import java.net.URI;

@Singleton
@Context
public class Uris {

  private static String base;

  public Uris(HttpHostResolver resolver) {
    Uris.base = resolver.resolve(HttpRequest.GET(""));
  }

  public static UriBuilder create() {
    return UriBuilder.of(base);
  }

  public static URI of(String... paths) {
    var builder = create();
    for (var path : paths) {
      builder.path(path);
    }
    return builder.build();
  }
}
