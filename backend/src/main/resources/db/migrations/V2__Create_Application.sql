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

CREATE TABLE applications
(
    id SERIAL PRIMARY KEY,
    user_id VARCHAR NOT NULL,
    template_id INT NOT NULL,
    FOREIGN KEY (template_id) REFERENCES templates (id)
);

CREATE TABLE answers
(
    id SERIAL PRIMARY KEY,
    application_id INT NOT NULL,
    question_id INT NOT NULL,
    answer VARCHAR NOT NULL,
    FOREIGN KEY (application_id) REFERENCES applications (id),
    FOREIGN KEY (question_id) REFERENCES questions (id)
);

CREATE TYPE application_status AS ENUM('UNFINISHED', 'PENDING_REVIEWS', 'REWORK');

CREATE TABLE current_application_status
(
    application_id INT NOT NULL,
    status application_status NOT NULL,
    FOREIGN KEY (application_id) REFERENCES applications (id)
);
