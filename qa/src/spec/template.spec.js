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

import {Authorization} from "../executor.js";
import {executeTests} from "../spec_executor.js";

const templateCreateVars = (name) => ({
  name,
  questions: [
    {
      number: 1,
      question: "Question"
    }
  ]
});

const tests = [
  {
    name: "Create Template",
    variables: templateCreateVars("PermissionTemplate"),
    query: "template/create-template.graphql",
    auth: Authorization.ADMIN,
    response: "template/create.json",
    after: {
      query: "template/delete-template.graphql",
      variables: {name: "PermissionTemplate"}
    }
  },
  {
    name: "List Templates",
    query: "template/templates.graphql",
    auth: Authorization.ADMIN,
    response: "template/templates.json"
  },
  {
    name: "Delete Template",
    query: "template/delete-template.graphql",
    variables: templateCreateVars("PermissionTemplate"),
    before: {
      query: "template/create-template.graphql",
      variables: templateCreateVars("PermissionTemplate")
    },
    auth: Authorization.ADMIN,
    response: "template/delete.json"
  }
];

executeTests("Template", tests);