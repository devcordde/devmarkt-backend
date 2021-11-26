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

package club.devcord.devmarkt.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Introspected
@Schema(description = "A wrapper DTO that is used to identify the sender of a request")
public record Identified<T>(
    @Schema(name = "RequesterID", description = "An ID which identifies the sender of this request")
    String requesterID,
    T value
) {
}
