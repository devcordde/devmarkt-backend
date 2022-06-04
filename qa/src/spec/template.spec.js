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
      question: "Question",
      multiline: false,
      minAnswerLength: 10
    }
  ]
});

const tests = [
  {
    name: "Create Template",
    variables: templateCreateVars("PermissionTemplate"),
    auth: Authorization.ADMIN,
    matrix: [
      {
        name: 'success',
        query: "template/create-template.graphql",
        response: "template/create.json",
        verify: {
          query: "template/template.graphql",
          response: "template/verify/create.json"
        },
        after: {
          query: "template/delete-template.graphql",
          variables: {name: "PermissionTemplate"}
        }
      },
      {
        name: 'duplicated',
        query: 'template/create-template.graphql',
        response: 'template/create-duplicated.json',
        variables: {name: 'Dev searched', questions: []},
        verify: {
          query: "template/templates.graphql",
          response: "template/templates.json"
        }
      }
    ]
  },
  {
    name: "List Templates",
    query: "template/templates.graphql",
    auth: Authorization.ADMIN,
    response: "template/templates.json"
  },
  {
    name: "Delete Template",
    variables: templateCreateVars("PermissionTemplate"),
    auth: Authorization.ADMIN,
    query: "template/delete-template.graphql",
    verify: {
      query: "template/template-names.graphql"
    },
    matrix: [
      {
        name: 'success',
        before: {
          query: "template/create-template.graphql",
          variables: templateCreateVars("PermissionTemplate")
        },
        response: "template/delete.json",
        verify: {
          response: "template/names.json"
        }
      },
      {
        name: 'notfound',
        response: 'template/delete-notfound.json',
        verify: {
          response: "template/names.json"
        }
      }
    ]
  }
];

executeTests("Template", tests);
