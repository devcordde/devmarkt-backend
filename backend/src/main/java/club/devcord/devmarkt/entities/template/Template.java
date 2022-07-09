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

package club.devcord.devmarkt.entities.template;

import club.devcord.devmarkt.graphql.GraphQLType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.micronaut.core.annotation.Creator;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.Relation;
import io.micronaut.data.annotation.Relation.Cascade;
import io.micronaut.data.annotation.Relation.Kind;
import io.micronaut.data.annotation.Where;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@GraphQLType("Template")
@MappedEntity("templates")
@Where("@.enabled = true")
public record Template(
    @JsonIgnore
    @Id @GeneratedValue
    int id,

    String name,
    @JsonIgnore
    boolean enabled,
    @Relation(value = Kind.ONE_TO_MANY, mappedBy = "template", cascade = Cascade.ALL)
    List<Question> questions
) {

  @Creator
  public static Template newSorted(int id, String name, boolean enabled, List<Question> questions) {
    var list = new ArrayList<>(questions != null ? questions : List.of());
    list.sort(Comparator.comparingInt(Question::number));
    return new Template(id, name, enabled, list);
  }
}
