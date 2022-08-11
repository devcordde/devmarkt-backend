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

package club.devcord.devmarkt.graphql;

import club.devcord.devmarkt.responses.Failure;
import club.devcord.devmarkt.responses.FailureException;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

public class ErrorHandlingDataFetcher<T> implements DataFetcher<Object> {

  private final DataFetcher<T> originalDataFetcher;

  public ErrorHandlingDataFetcher(DataFetcher<T> originalDataFetcher) {
    this.originalDataFetcher = originalDataFetcher;
  }

  @Override
  public Object get(DataFetchingEnvironment environment) throws Exception {
    try {
      return originalDataFetcher.get(environment);
    } catch (FailureException exception) {
      return DataFetcherResult.newResult()
          .data(new Failure(exception.errors()))
          .build();
    }
  }
}
