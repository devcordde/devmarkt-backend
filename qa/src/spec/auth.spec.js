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
    name: 'test admin role only',
    query: 'auth/roles.graphql',
    matrix: [
      {
        auth: Authorization.NONE,
        response: 'auth/unauthorized/roles.json'
      },
      {
        auth: Authorization.INVALID_METHOD,
        response: 'auth/unauthorized/roles.json'
      },
      {
        auth: Authorization.NO_METHOD,
        response: 'auth/unauthorized/roles.json'
      },
      {
        auth: Authorization.WRONG_FORMAT_USERID,
        response: 'auth/unauthorized/roles.json'
      },
      {
        auth: Authorization.WRONG_FORMAT_FOREIGN,
        response: 'auth/unauthorized/roles.json'
      },
      {
        auth: Authorization.WRONG_FORMAT_SELF,
        response: 'auth/unauthorized/roles.json'
      },
      {
        auth: Authorization.FOREIGN_UNAUTHORIZED_SUDOER,
        response: 'auth/unauthorized/roles.json'
      },
      {
        auth: Authorization.USER,
        response: 'auth/permission/roles.json'
      },
      {
        auth: Authorization.FOREIGN_USER_ROLE,
        response: 'auth/permission/roles.json'
      },
      {
        auth: Authorization.FOREIGN_NOT_KNOWN_USER,
        response: 'auth/permission/roles.json'
      },
      {
        auth: Authorization.FOREIGN_NO_ROLE,
        response: 'auth/permission/roles.json'
      },
      {
        auth: Authorization.ADMIN,
        response: 'auth/roles.json'
      },
      {
        auth: Authorization.FOREIGN_ADMIN,
        response: 'auth/roles.json'
      }
    ]
  },
  {
    name: 'test user role',
    query: 'auth/templates.graphql',
    matrix: [
      {
        auth: Authorization.NONE,
        response: 'auth/unauthorized/templates.json'
      },
      {
        auth: Authorization.INVALID_METHOD,
        response: 'auth/unauthorized/templates.json'
      },
      {
        auth: Authorization.NO_METHOD,
        response: 'auth/unauthorized/templates.json'
      },
      {
        auth: Authorization.WRONG_FORMAT_USERID,
        response: 'auth/unauthorized/templates.json'
      },
      {
        auth: Authorization.WRONG_FORMAT_FOREIGN,
        response: 'auth/unauthorized/templates.json'
      },
      {
        auth: Authorization.WRONG_FORMAT_SELF,
        response: 'auth/unauthorized/templates.json'
      },
      {
        auth: Authorization.FOREIGN_UNAUTHORIZED_SUDOER,
        response: 'auth/unauthorized/templates.json'
      },
      {
        auth: Authorization.USER,
        response: 'auth/templates.json'
      },
      {
        auth: Authorization.FOREIGN_USER_ROLE,
        response: 'auth/templates.json'
      },
      {
        auth: Authorization.FOREIGN_NOT_KNOWN_USER,
        response: 'auth/templates.json'
      },
      {
        auth: Authorization.FOREIGN_NO_ROLE,
        response: 'auth/permission/templates.json'
      },
      {
        auth: Authorization.ADMIN,
        response: 'auth/templates.json'
      },
      {
        auth: Authorization.FOREIGN_ADMIN,
        response: 'auth/templates.json'
      }
    ]
  }
]

executeTests('Authorization', tests)
