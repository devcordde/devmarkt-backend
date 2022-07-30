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

const tests = [
  {
    name: "User fetch",
    query: "user/user.graphql",
    auth: Authorization.ADMIN,
    matrix: [
      {
        name: "success",
        variables: {id: {type: "testuser", number: 1}},
        response: "user/user.json"
      },
      {
        name: "success - user with no applications",
        variables: {id: {type: "internal", number: 1}},
        response: "user/user-noapp.json"
      },
      {
        name: "notfound",
        variables: {id: {type: "not_known", number: 1}},
        response: "user/user-notfound.json"
      }
    ]
  },
  {
    name: "user create",
    query: "user/create.graphql",
    auth: Authorization.ADMIN,
    verify: {
      query: "user/user.graphql"
    },
    matrix: [
      {
        name: "success",
        variables: {
          id: {type: "the_answer_of_all", number: 49},
          role: "USER"
        },
        response: "user/create-success.json",
        verify: {
          response: "user/verify/create.json"
        }
      },
      {
        name: "duplicated",
        variables: {
          id: {type: "testuser", number: 1},
          role: "USER"
        },
        response: "user/create-duplicated.json"
      }
    ]
  },
  {
    name: "user delete",
    auth: Authorization.ADMIN,
    verify: {
      query: "user/user.graphql",
    },
    query: "user/delete.graphql",
    variables: {id: {type: "Database_Witch", number: 1}},
    matrix: [
      {
        name: "success",
        before: {
          query: "user/create.graphql",
          variables: {
            id: {type: "Database_Witch", number: 1},
            role: "ADMIN"
          }
        },
        response: "user/delete-success.json",
        verify: {
          response: "user/verify/delete.json"
        }
      },
      {
        name: "notfound",
        response: "user/delete-notfound.json"
      }
    ]
  }
]

executeTests("User", tests);
