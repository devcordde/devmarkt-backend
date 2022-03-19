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

import test from "../executor.js";
import createTemplate from "../graphql/template/create-template.graphql";
import listTemplateNames from "../graphql/template/template-names.graphql";
import listTemplatesWithQuestions from "../graphql/template/templates.graphql";
import listTemplatesWithoutNames from "../graphql/template/templates-only-questions.graphql";
import createTemplateDuplicatedResponse from "../fixtures/template/create-template-duplicated.json";
import createTemplateSuccessResponse from "../fixtures/template/create-template-success.json";
import listTemplateNamesResponse from "../fixtures/template/template-names.json";
import listTemplatesWithQuestionsResponse from "../fixtures/template/templates.json";
import listTemplatesWithoutNamesResponse from "../fixtures/template/templates-only-questions.json";

describe("Template Query", () => {
  it("Lists all template names", async () => {
    await test(listTemplateNames, listTemplateNamesResponse);
  })

  it("Lists all templates with questions", async () => {
    await test(listTemplatesWithQuestions, listTemplatesWithQuestionsResponse);
  })

  it("Lists all templates with questions and no name", async () => {
    await test(listTemplatesWithoutNames, listTemplatesWithoutNamesResponse);
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

  it("Creates a template", async () => {
    await test(createTemplate, createTemplateSuccessResponse, templateCreateVars("Template"));
  })

  it("Does not create duplicate templates", async () => {
    await test(createTemplate, createTemplateSuccessResponse, templateCreateVars("DuplicatedTemplate"));
    await test(createTemplate, createTemplateDuplicatedResponse, templateCreateVars("DuplicatedTemplate"))
  })
})