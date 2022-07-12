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
import club.devcord.devmarkt.entities.template.Template;
import club.devcord.devmarkt.graphql.GraphQLType;
import io.micronaut.core.annotation.Creator;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.Relation;
import io.micronaut.data.annotation.Relation.Cascade;
import io.micronaut.data.annotation.Relation.Kind;
import io.micronaut.data.jdbc.annotation.ColumnTransformer;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@GraphQLType("Application")
@MappedEntity("applications")
public record Application(
    @GeneratedValue @Id
    int id,
    @Nullable
    OffsetDateTime processTime,
    @ColumnTransformer(write = "?::application_status")
    ApplicationStatus status,

    @Relation(value = Kind.MANY_TO_ONE)
    User user,
    @Relation(value = Kind.MANY_TO_ONE)
    Template template,
    @Relation(value = Kind.ONE_TO_MANY, cascade = Cascade.ALL, mappedBy = "application")
    List<Answer> answers
) {

    @Creator
    public static Application newSorted(int id, @Nullable OffsetDateTime processTime, ApplicationStatus status,
        User user, Template template, List<Answer> answers) {
        var list = new ArrayList<>(answers != null ? answers : List.of());
        list.sort(Comparator.comparingInt(Answer::number));
        return new Application(id, processTime, status, user, template, answers);
    }
}
