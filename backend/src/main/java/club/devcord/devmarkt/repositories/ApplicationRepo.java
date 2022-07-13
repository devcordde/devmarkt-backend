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

import club.devcord.devmarkt.entities.application.Application;
import club.devcord.devmarkt.entities.application.ApplicationStatus;
import club.devcord.devmarkt.entities.auth.User;
import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Join.Type;
import io.micronaut.data.annotation.Where;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import java.util.List;
import java.util.Optional;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface ApplicationRepo extends CrudRepository<Application, Integer> {

  @Where("@.status = 'UNPROCESSED'::application_status")
  boolean existsUnprocessedByUser(User user);

  @Join("user")
  @Join(value = "answers", type = Type.LEFT_FETCH)
  @Join(value = "answers.question", type = Type.LEFT_FETCH)
  @Join("template")
  Optional<Application> findById(int id);

  int deleteById(int id);

  boolean existsByIdAndUser(int id, User user);

  @Where("@.status != :status::application_status")
  int updateById(int id, ApplicationStatus status);

  List<Application> findAllByUser(User user);
}
