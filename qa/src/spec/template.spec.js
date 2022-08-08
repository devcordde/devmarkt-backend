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
    name: "Fetch Template",
    auth: Authorization.USER,
    query: "template/template.graphql",
    matrix: [
      {
        name: "success",
        variables: {
          name: "Dev searched"
        },
        response: "template/template.json"
      },
      {
        name: "not found",
        variables: {
          name: "Not existing template"
        },
        response: "template/template-notfound.json"
      }
    ]
  },
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
  },
  {
    name: "Update template name",
    auth: Authorization.ADMIN,
    query: "template/update-template.graphql",
    verify: {
      query: "template/template-names.graphql"
    },
    matrix: [
      {
        name: "success",
        variables: {
          name: "Dev searched",
          updated: {
            name: "Updated name",
            questions: []
          }
        },
        response: "template/update-name.json",
        verify: {
          response: "template/verify/update-name.json"
        }
      },
      {
        name: "template not found",
        variables: {
          name: "Not existing template",
          updated: {
            name: "Updated name",
            questions: []
          }
        },
        response: "template/update-notfound.json",
        verify: {
          response: "template/names.json"
        }
      }
    ]
  },
  {
    name: "Append question",
    auth: Authorization.ADMIN,
    query: "template/update-template.graphql",
    verify: {
      query: "template/template.graphql"
    },
    matrix: [
      {
        name: "success",
        variables: {
          name: "Dev searched",
          updated: {
            questions: [
              {
                number: 123,
                question: "Appended Question",
                multiline: false,
                minAnswerLength: 10,
                updateAction: "APPEND"
              }
            ]
          }
        },
        response: "template/update-append-question.json",
        verify: {
          response: "template/verify/update-append-question.json"
        }
      },
      {
        name: "template not found",
        variables: {
          name: "Not existing template",
          updated: {
            questions: [
              {
                number: 10,
                question: "Appended Question",
                multiline: false,
                minAnswerLength: 10,
                updateAction: "APPEND"
              }
            ]
          }
        },
        response: "template/update-notfound.json"
      }
    ]
  },
  {
    name: "Insert Question",
    query: "template/update-template.graphql",
    auth: Authorization.ADMIN,
    verify: {
      query: "template/template.graphql"
    },
    matrix: [
      {
        name: "success",
        variables: {
          name: "Dev searched",
          updated: {
            questions: [
              {
                number: 2,
                question: "Inserted Question",
                multiline: false,
                minAnswerLength: 10,
                updateAction: "INSERT"
              }
            ]
          }
        },
        response: "template/update-insert.json",
        verify: {
          response: "template/verify/update-insert.json"
        }
      },
      {
        name: "out of index -> do nothing",
        variables: {
          name: "Dev searched",
          updated: {
            questions: [
              {
                number: 4,
                question: "Inserted Question",
                multiline: false,
                minAnswerLength: 10,
                updateAction: "INSERT"
              }
            ]
          }
        },
        response: "template/verify/update-nothing.json",
        verify: {
          response: "template/template.json"
        }
      }
    ]
  },
  {
    name: "replace question",
    auth: Authorization.ADMIN,
    query: "template/update-template.graphql",
    verify: {
      query: "template/template.graphql"
    },
    matrix: [
      {
        name: "success",
        variables: {
          name: "Dev searched",
          updated: {
            questions: [
              {
                number: 2,
                question: "Replaced Question",
                multiline: false,
                minAnswerLength: 10,
                updateAction: "REPLACE"
              }
            ]
          }
        },
        response: "template/update-replace.json",
        verify: {
          response: "template/verify/update-replace.json"
        }
      },
      {
        name: "out of index -> do nothing",
        variables: {
          name: "Dev searched",
          updated: {
            questions: [
              {
                number: 4,
                question: "Replaced Question",
                multiline: false,
                minAnswerLength: 10,
                updateAction: "INSERT"
              }
            ]
          }
        },
        response: "template/verify/update-nothing.json",
        verify: {
          response: "template/template.json"
        }
      }
    ]
  },
  {
    name: "delete question",
    auth: Authorization.ADMIN,
    query: "template/update-template.graphql",
    verify: {
      query: "template/template.graphql"
    },
    matrix: [
      {
        name: "success",
        variables: {
          name: "Dev searched",
          updated: {
            questions: [
              {
                number: 2,
                question: "Deleted Question",
                multiline: false,
                minAnswerLength: 10,
                updateAction: "DELETE"
              }
            ]
          }
        },
        response: "template/update-delete.json",
        verify: {
          response: "template/verify/update-delete.json"
        }
      },
      {
        name: "out of index -> do nothing",
        variables: {
          name: "Dev searched",
          updated: {
            questions: [
              {
                number: 4,
                question: "Replaced Question",
                multiline: false,
                minAnswerLength: 10,
                updateAction: "DELETE"
              }
            ]
          }
        },
        response: "template/verify/update-nothing.json",
        verify: {
          response: "template/template.json"
        }
      }
    ]
  },
  {
    name: "all actions together",
    auth: Authorization.ADMIN,
    query: "template/update-template.graphql",
    verify: {
      query: "template/template.graphql",
      response: "template/verify/update-all.json",
      variables: {
        name: "All updated"
      }
    },
    response: "template/update-all.json",
    variables: {
      name: "Dev searched",
      updated: {
        name: "All updated",
        questions: [
          {
            question: "Inserted Question NUM: 1",
            number: 2,
            minAnswerLength: 10,
            multiline: false,
            updateAction: "INSERT"
          },
          {
            question: "Appened Question NUM: 4",
            number: 10,
            minAnswerLength: 100,
            multiline: true,
            updateAction: "APPEND"
          },
          {
            question: "Deleted Question NUM: 0",
            number: 0,
            minAnswerLength: 10,
            multiline: false,
            updateAction: "DELETE"
          },
          {
            question: "Replaced Question NUM: 2",
            number: 2,
            minAnswerLength: 44,
            multiline: false,
            updateAction: "REPLACE"
          }
        ]
      }
    }
  }
];

executeTests("Template", tests);
