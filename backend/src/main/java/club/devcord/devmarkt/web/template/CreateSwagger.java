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

package club.devcord.devmarkt.web.template;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Operation(
    summary = "Creates a template.",
    description = "Saves the given template if no other template with exactly the same name exists."
)
@ApiResponse(
    responseCode = "201",
    description = "The template was successfully created",
    headers = @Header(name = "location",
        description = "A URI pointing to the created resource",
        schema = @Schema(
            type = "string"
        )
    )
)
@ApiResponse(
    responseCode = "409",
    description = "A template with the same name exists"
)
@Retention(RetentionPolicy.RUNTIME)
@interface CreateSwagger {
}
