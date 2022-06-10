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

import club.devcord.devmarkt.entities.template.Question;
import club.devcord.devmarkt.entities.template.QuestionId;
import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.repository.CrudRepository;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;

@JdbcRepository
public interface QuestionRepo extends CrudRepository<Question, QuestionId> {

  @Query("SELECT MAX (number) FROM questions WHERE template_id = :templateId")
  Optional<Integer> findMaxIdNumberByIdTemplateId(int templateId);

  @Join("id.template")
  List<Question> findByIdTemplateIdAndIdNumberGreaterThanEquals(int internalId,
      int number);

  @NotNull
  @Join("id.template")
  Optional<Question> findById(@NotNull QuestionId id);

  @Query("UPDATE questions SET number = :number WHERE id = :internalId")
  void updateNumbers(Iterable<Question> questions);

  int updateOne(Question question);

  int delete(QuestionId id);
}
