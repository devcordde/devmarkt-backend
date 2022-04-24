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

import club.devcord.devmarkt.graphql.GraphQLType;
import io.micronaut.data.annotation.Embeddable;
import io.micronaut.data.annotation.MappedProperty;

@GraphQLType("UserId")
@Embeddable
public record UserId(
    @MappedProperty("id_type")
    String type,
    @MappedProperty("user_id")
    long id) {

    public String merged() {
        return type + ":" + id;
    }
}
