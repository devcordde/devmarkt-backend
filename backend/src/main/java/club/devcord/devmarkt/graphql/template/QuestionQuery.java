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

package club.devcord.devmarkt.graphql.template;

import club.devcord.devmarkt.services.QuestionService;
import graphql.kickstart.tools.GraphQLQueryResolver;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class QuestionQuery implements GraphQLQueryResolver {

  private static final Logger LOGGER = LoggerFactory.getLogger(QuestionQuery.class);

  private final QuestionService service;

  public QuestionQuery(QuestionService service) {
    this.service = service;
  }

  public Object question(String templateName, int number) {
    var response = service.question(templateName, number).graphQlUnion();
    LOGGER.info("Question fetch. TemplateName: {}, Number: {}", templateName, number);
    System.out.println(response);
    return response;
  }
}
