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
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Operation(
    summary = "Deletes a template by the name"
)
@ApiResponse(
    responseCode = "204",
    description = "The template was successfully deleted"
)
@ApiResponse(
    responseCode = "404",
    description = "No template was found with this name. Use POST instead to create a new one"
)
@Retention(RetentionPolicy.RUNTIME)
@interface DeleteSwagger {
}
