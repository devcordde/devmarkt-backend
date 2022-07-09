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

import club.devcord.devmarkt.auth.error.ForbiddenError;
import club.devcord.devmarkt.auth.error.UnauthorizedError;
import club.devcord.devmarkt.entities.auth.User;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.idl.SchemaDirectiveWiring;
import graphql.schema.idl.SchemaDirectiveWiringEnvironment;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class RoleDirective implements SchemaDirectiveWiring {

  private static final Logger LOGGER = LoggerFactory.getLogger(RoleDirective.class);

  @Override
  public GraphQLFieldDefinition onField(
      SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition> environment) {
    var field = environment.getFieldDefinition();
    var roleName = (String) environment.getDirective().getArgument("role")
        .toAppliedArgument().getValue();
    assert roleName != null;
    var expectedRole = Role.valueOf(roleName.toUpperCase());
    var originalDataFetcher = environment.getFieldDataFetcher();
    DataFetcher<?> authDataFetcher = env -> {
      var user = (User) env.getGraphQlContext().get("user");

      if (user == null) {
        LOGGER.debug("Rejecting unauthorized request");
        return DataFetcherResult.newResult()
            .error(new UnauthorizedError())
            .build();
      }

      if (user.role() == expectedRole || user.role() == Role.ADMIN) {
        return originalDataFetcher.get(env);
      }
      LOGGER.debug("Rejecting forbidden request for user {}", user.id());
      return DataFetcherResult.newResult()
          .error(new ForbiddenError(environment.getFieldsContainer().getName(), field.getName()))
          .build();
    };

    environment.setFieldDataFetcher(authDataFetcher);
    return field;
  }
}
