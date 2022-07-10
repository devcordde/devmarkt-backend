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

package club.devcord.devmarkt.entities.application;

import club.devcord.devmarkt.entities.auth.User;
import club.devcord.devmarkt.graphql.GraphQLType;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.Relation;
import io.micronaut.data.annotation.Relation.Cascade;
import io.micronaut.data.annotation.Relation.Kind;
import java.time.OffsetDateTime;
import java.util.List;

@GraphQLType("Application")
@MappedEntity("applications")
public record Application(
    @GeneratedValue @Id
    int id,
    OffsetDateTime processTime,
    ApplicationStatus status,

    @Relation(Kind.MANY_TO_ONE)
    User user,
    int templateId,
    @Relation(value = Kind.ONE_TO_MANY, cascade = Cascade.ALL)
    List<Answer> answers
) {

}
