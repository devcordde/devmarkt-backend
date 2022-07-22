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

const applicationVariables = (tName, lastAnswer) => {
  var app = {
    tName,
    answers: [
      {
        number: 0,
        answer: "Lorem ipsum dolor sit amet, co N0"
      },
      {
        number: 1,
        answer: "Lorem ipsum dolor sit amet, co N1"
      }
    ]
  };
  if (lastAnswer != null) {
    app.answers.push(lastAnswer);
  }
  return app;
}

const unprocessedApplicationVars = ({
  tName: "Empty template",
  answers: []
})

const validLastAnswer = {
  number: 2,
  answer: "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed d"
      + "iam nonumy eirmod tempor invidunt ut laborffet dolore magna aliquyam erat,"
      + " sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet cli"
      + "ta kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit a"
      + "met, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam e"
      + "rat, sed diam voluptua. At vero eos et accusam et justo duo dolores et e"
}

const tests = [
  {
    name: "fetch application",
    query: "application/application.graphql",
    matrix: [
      {
        name: "success - admin",
        variables: {id: 1},
        auth: Authorization.ADMIN,
        response: "application/application.json"
      },
      {
        name: "success - own",
        auth: Authorization.USER,
        variables: {id: 4},
        response: "application/application-user.json"
      },
      {
        name: "notfound",
        auth: Authorization.ADMIN,
        variables: {id: 10},
        response: "application/application-notFound.json"
      },
      {
        name: "not own",
        auth: Authorization.USER,
        variables: {id: 1},
        response: "application/application-notOwn.json"
      }
    ]
  },
  {
    name: "create application",
    auth: Authorization.ADMIN,
    query: "application/create.graphql",
    matrix: [
      {
        name: "success",
        variables: applicationVariables("Dev offered", validLastAnswer),
        response: "application/create-success.json",
        verify: {
          query: "application/application.graphql",
          variables: {id: 7},
          response: "application/verify/create.json"
        }
      },
      {
        name: "template not found",
        variables: applicationVariables("not existing template", validLastAnswer),
        response: "application/create-template-not-found.json",
        verify: {
          query: "application/application.graphql",
          variables: {id: 7},
          response: "application/verify/create-notfound.json"
        }
      },
      {
        name: "user has unprocessed application",
        auth: Authorization.SECOND_USER,
        variables: applicationVariables("Dev offered", validLastAnswer),
        response: "application/create-has-unprocessed-application.json",
        verify: {
          query: "application/application.graphql",
          variables: {id: 7},
          response: "application/verify/create-notfound.json"
        }
      },
      {
        name: "ambiguous number",
        variables: applicationVariables("Dev offered", {number: 0, answer: "fsdfsdf"}),
        response: "application/create-ambiguous-number.json",
        verify: {
          query: "application/application.graphql",
          variables: {id: 7},
          response: "application/verify/create-notfound.json"
        }
      },
      {
        name: "no question",
        variables: applicationVariables("Dev offered", {number: 6, answer: "fsdfsdf"}),
        response: "application/create-no-question.json",
        verify: {
          query: "application/application.graphql",
          variables: {id: 7},
          response: "application/verify/create-notfound.json"
        }
      },
      {
        name: "answer too short",
        variables: applicationVariables("Dev offered", {number: 2, answer: "Lilly the Datawitch"}),
        response: "application/create-answer-to-short.json",
        verify: {
          query: "application/application.graphql",
          variables: {id: 7},
          response: "application/verify/create-notfound.json"
        }
      },
      {
        name: "questions unanswered",
        variables: applicationVariables("Dev offered"),
        response: "application/create-questions-unanswered.json",
        verify: {
          query: "application/application.graphql",
          variables: {id: 7},
          response: "application/verify/create-notfound.json"
        }
      }
    ]
  },
  {
    name: "delete Application",
    query: "application/delete.graphql",
    variables: {id: 7},
    matrix: [
      {
        name: "success",
        auth: Authorization.ADMIN,
        before: {
          query: "application/create.graphql",
          variables: applicationVariables("Dev offered", validLastAnswer)
        },
        response: "application/delete.json",
        verify: {
          query: "application/application.graphql",
          response: "application/verify/delete.json"
        }
      },
      {
        name: "not found",
        auth: Authorization.ADMIN,
        response: "application/delete-false.json"
      },
      {
        name: "not found - not own (no perm)",
        auth: Authorization.USER,
        variables: {id: 1},
        response: "application/delete-false.json",
        verify: {
          query: "application/application.graphql",
          response: "application/application.json"
        }
      }
    ]
  },
  {
    name: "updateApplication",
    auth: Authorization.ADMIN,
    query: "application/update.graphql",
    matrix: [
      {
        name: "already accepted",
        variables: {id: 4, answers: []},
        response: "application/update-already-accepted.json",
        verify: {
          query: "application/application.graphql",
          response: "application/verify/update-not-changed-4.json"
        }
      },
      {
        name: "success",
        auth: Authorization.ADMIN,
        variables: {id: 3, answers: [{number: 0, answer: "updated answer n0"}]},
        response: "application/update.json",
        verify: {
          query: "application/application.graphql",
          response: "application/verify/update.json",
          variables: {id: 3}
        }
      },
      {
        name: "no question",
        variables: {id: 3, answers: [{number: 6, answer: "updated answer n0"}]},
        response: "application/update-no-question.json",
        verify: {
          query: "application/application.graphql",
          response: "application/verify/update-not-changed.json"
        }
      },
      {
        name: "ambiguous number",
        variables: {id: 3, answers: [{number: 0, answer: "updated answer n0"},
            {number: 0, answer: "updated answer n0"}]},
        response: "application/update-ambiguous-number.json",
        verify: {
          query: "application/application.graphql",
          response: "application/verify/update-not-changed.json"
        }
      },
      {
        name: "answer too short",
        variables: {id: 3, answers: [{number: 2, answer: "fsfs"}]},
        response: "application/update-answer-too-short.json",
        verify: {
          query: "application/application.graphql",
          response: "application/verify/update-not-changed.json"
        }
      },
      {
        name: "not found - not own (no perm)",
        auth: Authorization.USER,
        variables: {id: 3, answers: []},
        response: "application/update-not-own.json",
        verify: {
          query: "application/application.graphql",
          response: "application/verify/update-not-changed.json"
        }
      }
    ]
  },
  {
    name: "process application",
    auth: Authorization.ADMIN,
    query: "application/process.graphql",
    matrix: [
      {
        name: "success - accept",
        response: "application/process.json",
        variables: {id: 7, status: 'ACCEPTED'},
        before: {
          query: "application/create.graphql",
          variables: unprocessedApplicationVars
        },
        verify: {
          query: "application/application.graphql",
          variables: {id: 7},
          response: "application/verify/process-accept.json"
        }
      },
      {
        name: "success - rejected",
        response: "application/process.json",
        variables: {id: 7, status: 'REJECTED'},
        before: {
          query: "application/create.graphql",
          variables: unprocessedApplicationVars
        },
        verify: {
          query: "application/application.graphql",
          variables: {id: 7},
          response: "application/verify/process-reject.json"
        }
      },
      {
        name: "status unprocessed",
        response: "application/process-false.json",
        variables: {id: 7, status: 'UNPROCESSED'},
        before: {
          query: "application/create.graphql",
          variables: unprocessedApplicationVars
        },
        verify: {
          query: "application/application.graphql",
          variables: {id: 7},
          response: "application/verify/process-unprocessed.json"
        }
      },
      {
        name: "not found",
        response: "application/process-false.json",
        variables: {id: 12, status: 'REJECTED'}
      }
    ]
  }
]

executeTests("Application", tests);
