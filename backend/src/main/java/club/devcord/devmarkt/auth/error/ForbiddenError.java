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

package club.devcord.devmarkt.auth.error;

import graphql.ErrorClassification;
import graphql.language.SourceLocation;
import java.util.List;

public record ForbiddenError(
    String operation,
    String query,
    SourceLocation location
) implements GraphQlErrorResult {

  @Override
  public String getMessage() {
    return "You're forbidden to use '%s' @ '%s'.".formatted(query, operation);
  }

  @Override
  public List<SourceLocation> getLocations() {
    return List.of(location);
  }

  @Override
  public ErrorClassification getErrorType() {
    return Error.FORBIDDEN;
  }

  private enum Error implements ErrorClassification {
    FORBIDDEN
  }
}
