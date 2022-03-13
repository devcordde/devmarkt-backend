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

package club.devcord.devmarkt.repositories;

import club.devcord.devmarkt.entities.template.RawQuestion;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.repository.CrudRepository;

@JdbcRepository
public interface QuestionRepo extends CrudRepository<RawQuestion, Integer> {
  boolean existsByTemplateIdAndNumber(int templateId, int number);

  int getMaxNumberByTemplateId(int templateId);

  @Query("UPDATE questions SET question = :question WHERE template_id = :templateId AND number = :number")
  void updateQuestionByTemplateIdAndNumber(int templateId, int number, String question);

  void deleteByTemplateIdAndNumber(int templateId, int number);
}
