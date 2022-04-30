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

import club.devcord.devmarkt.logging.LoggingUtil;
import club.devcord.devmarkt.services.QuestionService;
import graphql.kickstart.tools.GraphQLMutationResolver;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class QuestionMutation implements GraphQLMutationResolver {

  private static final Logger LOGGER = LoggerFactory.getLogger(QuestionMutation.class);

  private final QuestionService service;

  public QuestionMutation(QuestionService service) {
    this.service = service;
  }

  public Object addQuestion(String templateName, String question, int number) {
    var response = service.addQuestion(templateName, question, number);
    LOGGER.info("Question addition. Response: {}, TemplateName: {}, Number: {}, Question: {}",
        LoggingUtil.responseStatus(response), templateName, number, question);
    return response.graphQlUnion();
  }

  public Object updateQuestion(String templateName, int number, String question) {
    var response = service.updateQuestion(templateName, number, question);
    LOGGER.info("Question update, Response: {}, TemplateName: {}, Number: {}, Question: {}",
        LoggingUtil.responseStatus(response), templateName, number, question);
    return response.graphQlUnion();
  }

  public boolean deleteQuestion(String templateName, int number) {
    var response = service.deleteQuestion(templateName, number);
    LOGGER.info("Question deletion. Successful: {}, TemplateName: {}, Number: {}", response,
        templateName, number);
    return response;
  }
}
