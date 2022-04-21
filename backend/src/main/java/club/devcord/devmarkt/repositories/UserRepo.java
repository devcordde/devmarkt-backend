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

import club.devcord.devmarkt.entities.auth.User;
import club.devcord.devmarkt.entities.auth.UserId;
import graphql.language.OperationDefinition.Operation;
import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Join.Type;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.jdbc.runtime.JdbcOperations;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import javax.transaction.Transactional;

@JdbcRepository(dialect = Dialect.POSTGRES)
public abstract class UserRepo implements CrudRepository<User, Integer> {

  private final JdbcOperations operations;

  protected UserRepo(JdbcOperations operations) {
    this.operations = operations;
  }

  @Join(value = "roles", type = Type.LEFT_FETCH)
  public abstract Optional<User> findByUserId(UserId userId);

  public abstract boolean existsByUserId(UserId userId);

  public abstract int deleteByUserId(UserId id);

  @Transactional
  public void addRoles(UserId id, Collection<String> roleNames) {
    var sql = """
        INSERT INTO user_roles
            SELECT u.id, r.id FROM users u
              JOIN roles r ON r.name = ANY (?)
            WHERE u.id_type = ? AND u.user_id = ?;
        """;
    operations.prepareStatement(sql, statement -> {
      statement.setArray(1, stringSqlArray(roleNames));
      statement.setString(2, id.type());
      statement.setLong(3, id.id());
      return statement.executeUpdate();
    });
  }

  @Transactional
  public void removeRoles(UserId id, Collection<String> roleNames) {
    var sql = """
        WITH user_cte AS (
            SELECT id FROM users WHERE id_type = ? AND user_id = ?
        )
        DELETE FROM user_roles
          USING roles
          WHERE roles.name = ANY (?)
            AND user_roles.user_id = (SELECT id FROM user_cte)
            AND roles.id = user_roles.role_id;
        """;
    operations.prepareStatement(sql, statement -> {
      statement.setString(1, id.type());
      statement.setLong(2, id.id());
      statement.setArray(3, stringSqlArray(roleNames));
      return statement.executeUpdate();
    });
  }

  @Transactional
  public Set<String> checkPermissions(UserId userId, Collection<String> permissions, Operation operation) {
    var sql = """
        WITH user_roles_cte AS (
            SELECT ur.role_id AS id FROM user_roles ur
                JOIN users u ON u.id_type = ? AND u.user_id = ?
            WHERE ur.user_id = u.id
        ),
        permissions_cte AS (
            SELECT p.query AS query FROM permissions p
                JOIN role_permissions rp on rp.role_id IN (SELECT id FROM user_roles_cte)
            WHERE p.id = rp.permission_id AND p.operation = ?::operation
        )
        SELECT * FROM unnest(?) AS query WHERE query NOT IN (SELECT query FROM permissions_cte);
               """;
    return operations.prepareStatement(sql, statement -> {
      statement.setString(1, userId.type());
      statement.setLong(2, userId.id());
      statement.setString(3, operation.toString());
      statement.setArray(4, stringSqlArray(permissions));
      var result = statement.executeQuery();
      return setFromResultSet(result, "query");
    });
  }

  private Set<String> setFromResultSet(ResultSet set, String column) throws SQLException {
    var stringSet = new HashSet<String>(set.getMetaData().getColumnCount());
    while (set.next()) {
      var value = set.getString(column);
      stringSet.add(value);
    }
    return stringSet;
  }

  private Array stringSqlArray(Collection<String> values) throws SQLException {
    return operations.getConnection().createArrayOf("VARCHAR", values.toArray());
  }
}
