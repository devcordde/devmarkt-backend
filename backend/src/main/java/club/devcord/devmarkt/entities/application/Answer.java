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

import club.devcord.devmarkt.entities.template.Question;
import club.devcord.devmarkt.graphql.GraphQLType;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.Relation;
import io.micronaut.data.annotation.Relation.Kind;

@GraphQLType("Answer")
@MappedEntity("answers")
public record Answer(
    @GeneratedValue @Id
    Integer id,
    int number,
    String answer,
    @Relation(value = Kind.MANY_TO_ONE)
    Question question,
    @Nullable
    @Relation(value = Kind.MANY_TO_ONE)
    Application application
) {

}
