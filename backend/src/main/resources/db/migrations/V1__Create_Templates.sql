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

CREATE TABLE templates
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR UNIQUE
);

CREATE TABLE questions
(
    id          SERIAL PRIMARY KEY,
    template_id INT NOT NULL,
    number      INT NOT NULL,
    question    VARCHAR NOT NULL,
    multiline   BOOLEAN NOT NULL,
    min_answer_length INT NOT NULL,
    UNIQUE (template_id, number),
    FOREIGN KEY (template_id) REFERENCES templates (id) ON DELETE CASCADE
);
