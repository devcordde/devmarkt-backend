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

CREATE TYPE application_status AS ENUM ('UNPROCESSED', 'REJECTED', 'ACCEPTED');

CREATE TABLE applications
(
    id SERIAL PRIMARY KEY,
    processTime VARCHAR,
    status application_status NOT NULL,
    user_id INT NOT NULL REFERENCES users (id),
    template_id INT NOT NULL REFERENCES templates (id)
);

CREATE TABLE answers
(
    number INT NOT NULL,
    answer VARCHAR NOT NULL,
    question_id INT NOT NULL REFERENCES questions (id),
    application_id INT NOT NULL REFERENCES applications (id) ON DELETE CASCADE
)