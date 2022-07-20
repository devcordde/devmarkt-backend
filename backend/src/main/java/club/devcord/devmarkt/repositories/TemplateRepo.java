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

import club.devcord.devmarkt.entities.template.Template;
import io.micronaut.context.annotation.Executable;
import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Join.Type;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Where;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.repository.CrudRepository;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;

@JdbcRepository
public interface TemplateRepo extends CrudRepository<Template, Integer> {

  boolean existsByName(String name);

  @Override
  @Where("true")
  @Join(value = "questions", type = Type.LEFT_FETCH)
  Optional<Template> findById(@javax.validation.constraints.NotNull Integer integer);

  @Executable
  @Join(value = "questions", type = Type.LEFT_FETCH)
  Optional<Template> findByName(String name);

  @Query("UPDATE templates SET enabled = false WHERE name = :name AND enabled = true")
  int deleteByName(String name);

  @NotNull
  @Join(value = "questions", type = Type.LEFT_FETCH)
  List<Template> findAll();

  List<String> findName();

  @Where("true")
  // replaced the @Where conditions of the entity, so that we're able to find soft deleted templates.
  @Join(value = "questions", type = Type.LEFT_FETCH)
  Optional<Template> findById(int id);
}
