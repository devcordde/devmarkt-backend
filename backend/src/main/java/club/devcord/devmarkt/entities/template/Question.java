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
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.core.annotation.Creator;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.data.annotation.Relation;
import io.micronaut.data.annotation.Relation.Kind;
import io.micronaut.data.annotation.Transient;

@GraphQLType("Question")
@MappedEntity("questions")
public record Question(

    @JsonIgnore
    @Nullable
    @GeneratedValue
    @Id
    @MappedProperty("id")
    Integer internalId,

    @Relation(Kind.EMBEDDED)
    QuestionId id,
    String question,
    boolean multiline,
    int minAnswerLength,
    @Transient
    @Nullable
    UpdateAction updateAction
) {

  @Creator
  public Question(Integer internalId,
      QuestionId id,
      String question,
      boolean multiline,
      int minAnswerLength) {
    this(internalId, id, question, multiline, minAnswerLength, null);
  }

  @JsonCreator
  public Question(
      @JsonProperty("number") int number,
      @JsonProperty("question") String question,
      @JsonProperty("multiline") boolean multiline,
      @JsonProperty("minAnswerLength") int minAnswerLength,
      @JsonProperty("updateAction") UpdateAction updateAction) {
    this(null, new QuestionId(null, number), question, multiline, minAnswerLength,
        updateAction);
  }

  public int number() {
    return id.number();
  }

}
