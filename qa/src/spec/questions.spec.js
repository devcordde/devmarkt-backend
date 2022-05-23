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

function addQuestionVars({number, templateName = 'Dev searched'}) {
  return {
    templateName: templateName,
    input: {
      question: "Interesting question, isn't it?",
      number: number,
      minAnswerLength: 10,
      multiline: true
    }
  }
}

const tests = [
  {
    name: 'Fetch question',
    auth: Authorization.USER,
    query: 'question/question.graphql',
    matrix: [
      {
        name: 'success',
        response: 'question/question.json',
        variables: {templateName: 'Dev searched', number: 1}
      },
      {
        name: 'template not found',
        response: 'question/question-templateNotFound.json',
        variables: {templateName: 'Not existing template', number: 1}
      },
      {
        name: 'number not found',
        response: 'question/question-numberNotFound.json',
        variables: {templateName: 'Dev searched', number: 7}
      }
    ]
  },
  {
    name: 'Add question',
    auth: Authorization.ADMIN,
    query: 'question/addQuestion.graphql',
    verify: {
      query: "template/template.graphql",
      variables: {name: "Dev searched"}
    },
    matrix: [
      {
        name: 'success',
        response: 'question/addQuestion.json',
        variables: addQuestionVars({}),
        verify: {
          response: "question/verify/addQuestion.json"
        },
        after: {
          query: 'question/deleteQuestion.graphql',
          variables: {templateName: 'Dev searched', number: 4}
        }
      },
      {
        name: 'success with new number',
        response: 'question/addQuestion.json',
        variables: addQuestionVars({number: 4}),
        after: {
          query: 'question/deleteQuestion.graphql',
          variables: {templateName: 'Dev searched', number: 4}
        },
        verify: {
          response: "question/verify/addQuestion.json"
        }
      },
      {
        name: 'template not found',
        response: 'question/addQuestion-templateNotFound.json',
        variables: addQuestionVars({templateName: 'not existing template'})
      }
    ]
  },
  {
    name: 'Insert question',
    auth: Authorization.ADMIN,
    query: 'question/addQuestion.graphql',
    verify: {
      query: "template/template.graphql",
      variables: {name: "Dev searched"}
    },
    matrix: [
      {
        name: 'success',
        response: 'question/insertQuestion.json',
        variables: addQuestionVars({number: 2}),
        after: {
          query: 'question/deleteQuestion.graphql',
          variables: {templateName: 'Dev searched', number: 2}
        },
        verify: {
          response: "question/verify/insertQuestion.json"
        }
      }
    ]
  }
]

executeTests('Questions', tests)