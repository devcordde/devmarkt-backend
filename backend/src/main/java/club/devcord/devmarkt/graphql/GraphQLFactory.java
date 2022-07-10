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

import club.devcord.devmarkt.auth.RoleDirective;
import graphql.GraphQL;
import graphql.kickstart.tools.GraphQLResolver;
import graphql.kickstart.tools.SchemaParserBuilder;
import graphql.scalars.ExtendedScalars;
import graphql.scalars.java.JavaPrimitives;
import graphql.validation.rules.OnValidationErrorStrategy;
import graphql.validation.rules.ValidationRules;
import graphql.validation.schemawiring.ValidationSchemaWiring;
import io.micronaut.aop.Intercepted;
import io.micronaut.context.BeanContext;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Context;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Value;
import io.micronaut.core.io.ResourceResolver;
import io.micronaut.inject.qualifiers.Qualifiers;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.net.URISyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Factory
public class GraphQLFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(GraphQLFactory.class);

  @Bean
  @Singleton
  @Context
  public GraphQL graphQL(
      @Value("${graphql.schemas}") String location,
      GraphQLResolver<?>[] resolver,
      ResourceResolver resourceResolver,
      BeanContext context,
      RoleDirective roleDirective) {
    var builder = new SchemaParserBuilder()
        .scalars(JavaPrimitives.GraphQLLong, TemplateNameScalar.TEMPLATE_NAME,
            ExtendedScalars.NonNegativeInt, ExtendedScalars.PositiveInt, ExtendedScalars.DateTime,
            ExtendedScalars.newAliasedScalar("ApplicationId").aliasedScalar(ExtendedScalars.NonNegativeInt).build())
        .directive("Auth", roleDirective);

    registerTypes(builder, context);
    readSchemas(builder, location, resourceResolver);
    addResolvers(resolver, builder);
    initValidation(builder);

    var schema = builder.build()
        .makeExecutableSchema();

    return GraphQL.newGraphQL(schema)
        .build();
  }

  private void addResolvers(GraphQLResolver<?>[] resolver, SchemaParserBuilder builder) {
    for (var r : resolver) {
      builder.resolvers(r);
      LOGGER.info("SchemaResolver added: {}", resolverClass(r));
    }
  }

  private void readSchemas(SchemaParserBuilder builder, String location,
      ResourceResolver resolver) {
    var reader = new SchemaResolver();
    try {
      reader.resolveSchemas(location, resolver)
          .stream()
          .peek(s -> LOGGER.info("Reading schema: {}", s))
          .forEach(builder::file);
    } catch (URISyntaxException | IOException e) {
      LOGGER.error("An error occurred during schema loading", e);
    }
  }

  private Class<?> resolverClass(GraphQLResolver<?> resolver) {
    if (resolver instanceof Intercepted) {
      return resolver.getClass().getSuperclass();
    }
    return resolver.getClass();
  }

  private void registerTypes(SchemaParserBuilder builder, BeanContext context) {
    context.getBeanDefinitions(Qualifiers.byStereotype(GraphQLType.class))
        .forEach(beanDefinition -> {
          var type = beanDefinition.stringValue(GraphQLType.class).orElseThrow();
          var beanType = beanDefinition.getBeanType();
          builder.dictionary(type, beanType);
          LOGGER.info("GraphQL Type {} mapped to class {}.", type, beanType);
        });
  }

  private void initValidation(SchemaParserBuilder builder) {
    var rules = ValidationRules.newValidationRules()
        .onValidationErrorStrategy(OnValidationErrorStrategy.RETURN_NULL)
        .build();

    var schemaWiring = new ValidationSchemaWiring(rules);
    builder.directiveWiring(schemaWiring);
    LOGGER.info("GraphQL validation initialized");
  }
}
