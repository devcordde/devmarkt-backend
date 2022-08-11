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

package club.devcord.devmarkt.logging;

import club.devcord.devmarkt.entities.auth.User;
import club.devcord.devmarkt.entities.auth.UserId;
import club.devcord.devmarkt.responses.Failure;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import java.util.Optional;

public class LogMsgGenerator {

  private LogMsgGenerator() {

  }

  public static String generateMsg(DataFetchingEnvironment environment, Object result) {
    StringBuilder msg = new StringBuilder(
        "Request!");
    msg.append(" User: ")
        .append(Optional.ofNullable((User) environment.
            getGraphQlContext().
            get("user"))
            .map(User::id)
            .map(UserId::toString)
            .orElse("none")
        )
        .append("; Operation: ")
        .append(environment.getOperationDefinition().getOperation())
        .append(" -> ")
        .append(environment.getFieldDefinition().getName());
    if (!environment.getArguments().isEmpty()) {
      msg.append("; Arg: ");
      for (var arg : environment.getArguments().entrySet()) {
        msg.append(arg.getKey())
            .append(" -> ")
            .append(arg.getValue());
      }
    }
    msg.append("; Result: ");
    if (result instanceof DataFetcherResult<?> dataFetcherResult) {
      if (dataFetcherResult.getData() instanceof Failure failure) {
        msg.append("Failure: ");
        for (var error : failure.errors()) {
          msg.append(error.code())
            .append(" -> ")
            .append(error.data())
            .append(", ");
        }
      }
      if (!dataFetcherResult.getErrors().isEmpty()) {
        msg.append("; Errors: ");
        for (var error : dataFetcherResult.getErrors()) {
          msg.append(error.getErrorType())
            .append(" -> ")
            .append(error.getMessage())
            .append(", ");
        }
      }
    } else {
      msg.append(result);
    }
    return msg.toString();
  }

}
