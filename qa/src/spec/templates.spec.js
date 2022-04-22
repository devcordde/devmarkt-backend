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

import test, {
  Authorization, execute,
  prefixedLoad,
  prefixedTestNamed
} from "../executor.js";

const load = prefixedLoad("template");
const testNamed = prefixedTestNamed("template");

describe("Template Query", () => {
  it("Lists all template names", async () => {
    await testNamed("template-names.graphql", "template-names.json", {}, Authorization.ADMIN);
  })

  it("Lists all templates with questions", async () => {
    await testNamed("templates.graphql", "templates.json", {}, Authorization.ADMIN);
  })

  it("Lists all templates with questions and no name", async () => {
    await testNamed("templates-only-questions.graphql",
        "templates-only-questions.json", {}, Authorization.ADMIN);
  })
})

describe("Template Mutation", () => {
  const templateCreateVars = (name) => ({
    name,
    questions: [
      {
        number: 1,
        question: "Question"
      }
    ]
  });

  const createTemplate = load("create-template.graphql");
  const createTemplateSuccessResponse = load("create-template-success.json");
  const createTemplateDuplicatedResponse = load(
      "create-template-duplicated.json");

  it("Creates a template", async () => {
    await test(createTemplate, createTemplateSuccessResponse,
        templateCreateVars("Template"), Authorization.ADMIN);
  })

  it("Does not create duplicate templates", async () => {
    await test(createTemplate, createTemplateSuccessResponse,
        templateCreateVars("DuplicatedTemplate"), Authorization.ADMIN);
    await test(createTemplate, createTemplateDuplicatedResponse,
        templateCreateVars("DuplicatedTemplate"), Authorization.ADMIN);
  })

  afterEach(async () => {
    const removeQuery = await load("delete-template.graphql");
    await execute(removeQuery, { name: "Template" }, Authorization.ADMIN);
    await execute(removeQuery, { name: "DuplicatedTemplate" }, Authorization.ADMIN);
  })
})
