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

package club.devcord.devmarkt.responses.failure;

import club.devcord.devmarkt.graphql.GraphQLType;

@GraphQLType("Error")
public record Error<T>(
    String code,
    // We're not able to use the ErrorData interface here because of a weird graphql error,
    // nevertheless type safety is guaranteed by the constructor
    Object data
) {

  public Error(ErrorCode<T> code, ErrorData<T> data) {
    this(code.name(), data);
  }
}
