/*
 * Copyright 2021 Contributors to the Devmarkt-Backend project
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

package club.devcord.devmarkt.dto.template;

import club.devcord.devmarkt.dto.Introspected;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.mongojack.Id;
import org.mongojack.MongoCollection;

@Introspected
@MongoCollection(name = "templates")
@Schema(name = "Template", description = "A template")
public record Template(
    @Schema(description = "The template's name")
    @Id @JsonProperty("_id") String name,

    @Schema(description = "The questions that this template should contain")
    List<Question> questions
) {
}
