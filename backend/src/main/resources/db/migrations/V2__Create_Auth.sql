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

CREATE TABLE users
(
    id      SERIAL PRIMARY KEY,
    id_type VARCHAR NOT NULL,
    user_id BIGINT  NOT NULL,
    UNIQUE (id_type, user_id)
);

CREATE TABLE roles
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR NOT NULL UNIQUE
);

CREATE TABLE user_roles
(
    user_id INT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    role_id INT NOT NULL REFERENCES roles (id) ON DELETE CASCADE,
    UNIQUE (user_id, role_id)
);
