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

package club.devcord.devmarkt.graphql.question;

import club.devcord.devmarkt.services.template.QuestionService;
import graphql.kickstart.tools.GraphQLMutationResolver;
import jakarta.inject.Singleton;

@Singleton
public class QuestionMutation implements GraphQLMutationResolver {

  private final QuestionService service;

  public QuestionMutation(
      QuestionService service) {
    this.service = service;
  }

  public Object addQuestion(String templateName, String question, int number) {
    return service.addQuestion(templateName, question, number).graphqlUnion();
  }

  public Object updateQuestion(String templateName, int number, String question) {
    return service.updateQuestion(templateName, number, question).graphqlUnion();
  }

  public boolean deleteQuestion(String templateName, int number) {
    return service.deleteQuestion(templateName, number);
  }
}
