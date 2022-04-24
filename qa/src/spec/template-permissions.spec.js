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

import test, {Authorization, execute, load, prefixedLoad} from "../executor.js";

const loadTemplate = prefixedLoad("template");

describe("Template Permissions", () => {
  const templateCreateVars = (name) => ({
    name,
    questions: [
      {
        number: 1,
        question: "Question"
      }
    ]
  });

  describe("Create Template", () => {
    it.each`
        authorization          | expected
        ${Authorization.NONE}  | ${'errors/unauthorized.json'}
        ${Authorization.TEST}  | ${'template/permission/create-permission-denied.json'}
        ${Authorization.ADMIN} | ${'template/create-template-success.json'}
    `("Denies access if permission is not granted", async ({authorization, expected}) => {
      const query = loadTemplate("create-template.graphql");
      const expectedResponse = load(expected);
      await test(query, expectedResponse, templateCreateVars("PermissionTemplate"), authorization);
    })

    afterEach(async () => {
      const removeQuery = await loadTemplate("delete-template.graphql");
      await execute(removeQuery, { name: "PermissionTemplate" }, Authorization.ADMIN);
    })
  })

  describe("List Templates", () => {
    it.each`
        authorization          | expected
        ${Authorization.NONE}  | ${'errors/unauthorized.json'}
        ${Authorization.TEST}  | ${'template/permission/list-permission-denied.json'}
        ${Authorization.ADMIN} | ${'template/templates.json'}
    `("Denies access if permission is not granted", async({autorization, expected}) => {
      const query = loadTemplate("templates.graphql");
      const expectedResponse = load(expected);
      await test(query, expectedResponse, {}, autorization);
    })
  })

  describe("Delete Template", () => {
    beforeEach(async () => {
      const query = loadTemplate("create-template.graphql");
      await execute(query, templateCreateVars("PermissionTemplate"), Authorization.ADMIN);
    })

    it.each`
        authorization          | expected
        ${Authorization.NONE}  | ${'errors/unauthorized.json'}
        ${Authorization.TEST}  | ${'template/permission/delete-permission-denied.json'}
        ${Authorization.ADMIN} | ${'template/delete-template.json'}
    `("Denies access if permission is not granted", async ({autorization, expected}) => {
      const query = loadTemplate("delete-template.graphql");
      const expectedResponse = load(expected);
      await test(query, expectedResponse, {name: "PermissionTemplate"}, autorization);
    })
  })
})
