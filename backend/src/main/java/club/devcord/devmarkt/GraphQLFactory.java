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

package club.devcord.devmarkt;

import graphql.GraphQL;
import graphql.kickstart.tools.GraphQLResolver;
import graphql.kickstart.tools.SchemaParserBuilder;
import io.micronaut.context.BeanContext;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Context;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Value;
import io.micronaut.inject.qualifiers.Qualifiers;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

@Factory
public class GraphQLFactory {

  @Bean
  @Singleton
  @Context
  public GraphQL graphQL(
      @Value("${graphql.schemas}") String location,
      GraphQLResolver<?>[] resolver,
      BeanContext context) throws URISyntaxException, IOException {
    var builder = new SchemaParserBuilder();
    var base = Path.of(this.getClass().getResource(location).toURI());

    Files.walk(base)
        .map(path -> Path.of(location).resolve(path.getFileName()))
        .map(Path::toString)
        .filter(path -> path.endsWith(".graphql"))
        .map(s -> s.substring(1))
        .forEach(builder::file);
    builder.resolvers(resolver);

    context.getBeanDefinitions(Qualifiers.byStereotype(GraphQLType.class))
        .forEach(beanDefinition -> {
          var type = beanDefinition.stringValue(GraphQLType.class).orElseThrow();
          builder.dictionary(type, beanDefinition.getBeanType());
        });

    var schema = builder.build().makeExecutableSchema();
    return GraphQL.newGraphQL(schema)
        .build();
  }

}
