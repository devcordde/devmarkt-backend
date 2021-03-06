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

package club.devcord.devmarkt.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.function.Function;

public final class Collections {

  private Collections() {
  }

  public static <T, R> Collection<R> ambiguousEntries(Collection<T> collection,
      Function<T, R> identity) {
    var knownKeys = new HashSet<R>(collection.size());
    var ambiguousEntries = new HashSet<R>();
    for (var entry : collection) {
      var id = identity.apply(entry);
      if (!knownKeys.add(id)) {
        ambiguousEntries.add(id);
      }
    }
    return ambiguousEntries;
  }

}
