package club.devcord.devmarkt.database.template;

import club.devcord.devmarkt.database.template.dto.DBTemplate;
import io.micronaut.data.annotation.Join;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.repository.CrudRepository;
import java.util.Optional;
import java.util.Set;

@JdbcRepository
public interface TemplateRepo extends CrudRepository<DBTemplate, Integer> {

  boolean existsByName(String name);

  @Join(value = "questions")
  Optional<DBTemplate> findByName(String name);

  void deleteByName(String name);

  Set<String> findName();
}
