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

import club.devcord.devmarkt.entities.auth.Operation;
import club.devcord.devmarkt.entities.auth.Permission;
import club.devcord.devmarkt.entities.auth.Role;
import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Join.Type;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.jdbc.runtime.JdbcOperations;
import io.micronaut.data.repository.CrudRepository;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;
import javax.transaction.Transactional;
import org.jetbrains.annotations.NotNull;

@JdbcRepository
public abstract class RoleRepo implements CrudRepository<Role, Integer> {

  private final JdbcOperations jdbcOperations;

  public RoleRepo(JdbcOperations operations) {
    this.jdbcOperations = operations;
  }

  public abstract boolean existsByName(String name);

  public abstract int deleteByName(String name);

  @Join(value = "permissions", type = Type.LEFT_FETCH)
  public abstract Optional<Role> findByName(String name);

  @NotNull
  @Override
  @Transactional
  public Role save(@NotNull Role role) {
    var query = """
        WITH inserted_role AS (
          INSERT INTO roles (name) VALUES (?) RETURNING id
        )
        INSERT INTO role_permissions (role_id, permission_id)
          SELECT r.id, p.id FROM inserted_role r
            JOIN permissions p ON
                (p.operation = 'QUERY'::operation AND p.query = ANY (?)) OR
                (p.operation = 'MUTATION'::operation AND p.query = ANY (?)) OR
                (p.operation = 'SUBSCRIPTION'::operation AND p.query = ANY (?))
        """;

    return jdbcOperations.prepareStatement(query, statement -> {
      statement.setString(1, role.name());
      setPermissions(statement, role.permissions(), 2);
      statement.executeUpdate();
      return role;
    });
  }

  @Transactional
  public int removePermissions(String roleName, Collection<Permission> permissions) {
    var query = """
        WITH to_delete_role AS (
          SELECT id FROM roles WHERE name = ? LIMIT 1
        ),
        to_delete_perm AS (
          SELECT p.id, p.operation, p.query FROM permissions p
          WHERE
            (p.operation = 'QUERY'::operation AND p.query = ANY (?)) OR
            (p.operation = 'MUTATION'::operation AND p.query = ANY (?)) OR
            (p.operation = 'SUBSCRIPTION'::operation AND p.query = ANY (?))
        )
        DELETE FROM role_permissions WHERE role_id = (SELECT id FROM to_delete_role) AND permission_id IN (SELECT id FROM to_delete_perm);
        """;

    return jdbcOperations.prepareStatement(query, statement -> {
      statement.setString(1, roleName);
      setPermissions(statement, permissions, 2);

      return statement.executeUpdate();
    });
  }

  @Transactional
  public int addPermissions(String roleName, Collection<Permission> permissions) {
    var sql = """
        INSERT INTO role_permissions (role_id, permission_id)
          SELECT r.id, p.id FROM roles r
            JOIN permissions p ON
                (p.operation = 'QUERY'::operation AND p.query = ANY (?)) OR
                (p.operation = 'MUTATION'::operation AND p.query = ANY (?)) OR
                (p.operation = 'SUBSCRIPTION'::operation AND p.query = ANY (?))
           WHERE r.name = ?
           ON CONFLICT DO NOTHING;
                """;

    return jdbcOperations.prepareStatement(sql, statement -> {
      setPermissions(statement, permissions, 1);
      statement.setString(4, roleName);
      return statement.executeUpdate();
    });
  }

  private void setPermissions(PreparedStatement statement, Collection<Permission> permissions,
      int start)
      throws SQLException {
    statement.setArray(start, varcharSqlArray(permissions, Operation.QUERY));
    statement.setArray(start + 1, varcharSqlArray(permissions, Operation.MUTATION));
    statement.setArray(start + 2, varcharSqlArray(permissions, Operation.SUBSCRIPTION));
  }

  private Array varcharSqlArray(Collection<Permission> permissions, Operation operation)
      throws SQLException {
    var queries = permissions
        .stream()
        .filter(p -> p.operation() == operation)
        .map(Permission::query)
        .toArray(String[]::new);
    return jdbcOperations.getConnection().createArrayOf("VARCHAR", queries);
  }

}
