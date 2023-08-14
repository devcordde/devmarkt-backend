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

package club.devcord.devmarkt.entities.auth;

import club.devcord.devmarkt.auth.Role;
import club.devcord.devmarkt.graphql.GraphQLType;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.data.annotation.Relation;
import io.micronaut.data.annotation.Relation.Kind;
import io.micronaut.data.annotation.sql.ColumnTransformer;

@GraphQLType("User")
@MappedEntity("users")
public record User(
    @MappedProperty("id")
    @Id @GeneratedValue
    int internalId,
    @Relation(Kind.EMBEDDED)
    UserId id,
    @ColumnTransformer(write = "?::role")
    Role role
) {

}
