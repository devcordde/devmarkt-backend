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
import io.swagger.v3.oas.annotations.media.Schema;

@Introspected
@Schema(name = "TemplateEvent", description = "A universal template event")
public record TemplateEvent(
    @Schema(description = "The templates name")
    String name,
    @Schema(description = "The requesters ID")
    String requesterID,
    @Schema(description = "The type of this Event")
    EventType type,
    @Schema(description = "The actual template data of this event. Null if the type is DELETED")
    Template templateData){

  @Schema(name = "EventType", description = "The type of this template event")
  public enum EventType {
    CREATED,
    REPLACED,
    DELETED
  }

}
