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
    name: "fetch roles",
    auth: Authorization.ADMIN,
    query: "role/roles.graphql",
    response: "role/roles.json"
  },
  {
    name: "add user roles",
    auth: Authorization.ADMIN,
    query: "role/addUserRoles.graphql",
    matrix: [
      {
        name: "success",
        variables: {
          userId: {type: "testuser", id: 1},
          roles: [
              "user",
              "admin"
          ]
        },
        response: "role/addUserRoles-success.json",
        after: {
          query: "role/removeUserRoles.graphql",
          variables: {
            userId: {type: "testuser", id: 1},
            roles: [
              "user",
              "admin"
            ]
          }
        },
        verify: {
          query: "user/user.graphql",
          variables: {id: {type: "testuser", id: 1}},
          response: "role/verify/addUserRoles.json"
        }
      },
      {
        name: "user not found",
        variables: {
          userId: {type: "testuser", id: 5},
          roles: [
            "user",
            "admin"
          ]
        },
        response: "role/addUserRoles-usernotfound.json"
      },
      {
        name: "role not found",
        variables: {
            userId: {type: "testuser", id: 1},
            roles: [
              "Tabellenschubser"
            ]
        },
        response: "role/addUserRoles-rolenotfound.json"
      }
    ]
  },
  {
    name: "remove user roles",
    auth: Authorization.ADMIN,
    query: "role/removeUserRoles.graphql",
    variables: {
      userId: {type: "testuser", id: 1},
      roles: [
        "user"
      ]
    },
    matrix: [
      {
        name: "success",
        response: "role/removeUserRoles-success.json",
        before: {
          query: "role/addUserRoles.graphql",
          variables: {
            userId: {type: "testuser", id: 1},
            roles: [
              "user",
              "admin"
            ]
          },
        },
        after: {
          query: "role/removeUserRoles.graphql",
          variables: {
            userId: {type: "testuser", id: 1},
            roles: [
              "admin"
            ]
          },
        }
      },
      {
        name: "user not found",
        response: "role/removeUserRoles-usernotfound.json",
        variables: {
          userId: {type: "testuser", id: 5},
          roles: [
            "user"
          ]
        },
      },
      {
        name: "role not found",
        response: "role/removeUserRoles-rolenotfound.json",
        variables: {
          userId: {type: "testuser", id: 1},
          roles: [
            "Tabellenschubser"
          ]
        },
      }
    ]
  }
]

executeTests("Role", tests)