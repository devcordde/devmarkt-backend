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

import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;
import org.jetbrains.annotations.NotNull;

public class TemplateNameScalar {

  public static final GraphQLScalarType TEMPLATE_NAME = GraphQLScalarType.newScalar()
      .name("TemplateName")
      .description("The name of a template, represented by a string")
      .coercing(new Coercing<String, String>() {
        @Override
        public String serialize(@NotNull Object dataFetcherResult)
            throws CoercingSerializeException {
          if (dataFetcherResult instanceof String name && isValid(name)) {
            return name;
          }
          throw new CoercingSerializeException(
              "Unable to serialize %s as an TemplateName aka String"
                  .formatted(dataFetcherResult));
        }

        @NotNull
        @Override
        public String parseValue(@NotNull Object input) throws CoercingParseValueException {
          return serialize(input);
        }

        @NotNull
        @Override
        public String parseLiteral(@NotNull Object input) throws CoercingParseLiteralException {
          if (input instanceof StringValue stringValue) {
            var name = stringValue.getValue();
            if (isValid(name)) {
              return name;
            }
          }
          throw new CoercingParseLiteralException("Value is not a valid TemplateName: %s"
              .formatted(input));
        }
      })
      .build();

  private static boolean isValid(String name) {
    return name.length() <= 40
        && !name.isBlank();
  }

}
