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

DELETE
FROM templates
WHERE name IN ('Dev searched', 'Dev offered', 'Empty template');

WITH templateId AS (INSERT INTO templates (name) VALUES ('Dev searched') RETURNING id)
INSERT
INTO questions(template_id, number, question, multiline, min_answer_length)
VALUES ((SELECT id FROM templateId), 0, 'Who are we?', false, 0),
       ((SELECT id FROM templateId), 1, 'Why should you join us?', true, 100),
       ((SELECT id FROM templateId), 2, 'What programming languages should you know?', false, 0),
       ((SELECT id FROM templateId), 3, 'Custom text:', true, 30);

WITH templateId AS (INSERT INTO templates (name) VALUES ('Dev offered') RETURNING id)
INSERT
INTO questions(template_id, number, question, multiline, min_answer_length)
VALUES ((SELECT id FROM templateId), 0, 'Who am I?', false, 0),
       ((SELECT id FROM templateId), 1, 'What programming language do I know?', false, 0),
       ((SELECT id FROM templateId), 2, 'Why should you choose me?', true, 500);

INSERT INTO templates(name)
VALUES ('Empty template');
