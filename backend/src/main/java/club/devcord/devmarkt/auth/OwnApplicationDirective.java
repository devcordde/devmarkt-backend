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

package club.devcord.devmarkt.auth;

import club.devcord.devmarkt.auth.error.UnauthorizedError;
import club.devcord.devmarkt.entities.auth.User;
import club.devcord.devmarkt.responses.Failure;
import club.devcord.devmarkt.responses.failure.application.ErrorCode;
import club.devcord.devmarkt.services.ApplicationService;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLScalarType;
import graphql.schema.idl.SchemaDirectiveWiring;
import graphql.schema.idl.SchemaDirectiveWiringEnvironment;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class OwnApplicationDirective implements SchemaDirectiveWiring {

  private static final Logger LOGGER = LoggerFactory.getLogger(OwnApplicationDirective.class);

  private final ApplicationService applicationService;

  public OwnApplicationDirective(
      ApplicationService applicationService) {
    this.applicationService = applicationService;
  }

  @Override
  public GraphQLFieldDefinition onField(
      SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition> environment) {
    var field = environment.getFieldDefinition();
    var idField = (String) environment.getDirective().toAppliedDirective().getArgument("idField")
        .getValue();
    assert idField != null;
    var originalDataFetcher = environment.getFieldDataFetcher();
    var fieldBooleanReturn = environment.getFieldDefinition().getType() instanceof GraphQLScalarType scalarType
        && scalarType.getName().equals("Boolean");
    DataFetcher<?> authDataFetcher = env -> {
      var user = (User) env.getGraphQlContext().get("user");

      if (user == null) {
        LOGGER.info("Rejecting unauthorized request");
        return DataFetcherResult.newResult()
            .error(new UnauthorizedError())
            .build();
      }

      var value = (int) env.getArgument(idField);
      if (user.role() == Role.ADMIN || applicationService.isOwnApplication(value, user)) {
        return originalDataFetcher.get(env);
      }
      LOGGER.info("Rejecting request because user {} doesn't own application id: {}",
          user.id(), value);
      return fieldBooleanReturn
          ? false
          : DataFetcherResult.newResult()
          .data(new Failure<>(ErrorCode.NOT_FOUND))
          .build();
    };

    environment.setFieldDataFetcher(authDataFetcher);
    return field;
  }
}
