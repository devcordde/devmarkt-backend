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

INSERT INTO users (id_type, user_id)
VALUES ('testuser', 1); -- no roles user
WITH user_role_user AS (
    INSERT INTO users (id_type, user_id) VALUES ('testuser', 2) RETURNING id
),
     user_role AS (
         SELECT id
         FROM roles
         WHERE name = 'user'
     )
INSERT
INTO user_roles (user_id, role_id)
VALUES ((SELECT id FROM user_role_user), (SELECT id FROM user_role));
