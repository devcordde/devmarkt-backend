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

package club.devcord.devmarkt.responses;

import club.devcord.devmarkt.graphql.GraphQLType;
import club.devcord.devmarkt.responses.failure.Error;
import club.devcord.devmarkt.responses.failure.ErrorCode;
import club.devcord.devmarkt.responses.failure.ErrorData;
import java.util.Collection;
import java.util.Collections;

@GraphQLType
public record Failure<T>(
    Collection<Error<T>> errors
) implements Response<T> {

  public Failure(ErrorCode<T> code) {
    this(code, (ErrorData<T>) null);
  }
  public Failure(ErrorCode<T> code, ErrorData<T> data) {
    this(Collections.singletonList(new Error<>(code, data)));
  }

  public Failure(ErrorCode<T> code, Collection<ErrorData<T>> data) {
    this(data.stream().map(eD -> new Error<>(code, eD)).toList());
  }

}
