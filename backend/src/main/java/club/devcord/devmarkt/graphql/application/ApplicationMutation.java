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

package club.devcord.devmarkt.graphql.application;

import club.devcord.devmarkt.entities.application.Answer;
import club.devcord.devmarkt.entities.application.ApplicationStatus;
import graphql.kickstart.tools.GraphQLMutationResolver;
import jakarta.inject.Singleton;
import java.util.List;

@Singleton
public class ApplicationMutation implements GraphQLMutationResolver {

  public Object createApplication(String templateName, List<Answer> answers) {
    return null;
  }

  public boolean deleteApplication(int id) {
    return false;
  }

  public Object updateApplication(int id, List<Answer> updatedAnswers) {
    return null;
  }

  public Object processApplication(int id, ApplicationStatus status) {
    return null;
  }

}
