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

import test, {Authorization, execute, load} from "./executor.js";

export function executeTests(suiteName, tests = []) {
  tests.forEach(({
    name,
    auth = Authorization.NONE,
    variables = {},
    query,
    response,
    before = () => {
    },
    after = () => {
    },
    verify,
    matrix = [],
  }) => {
    describe(suiteName, () => {
      if (matrix.length) {
        matrix.forEach(entry =>
            runTest({
              itName: entry.name ?? "default",
              name: name,
              auth: entry.auth ?? auth,
              variables: entry.variables ?? variables,
              query: entry.query ?? query,
              response: entry.response ?? response,
              before: entry.before ?? before,
              after: entry.after ?? after,
              verify: entry.verify != null
                  ? {
                    query: entry.verify.query ?? verify?.query,
                    variables: entry.verify.variables ?? verify?.variables
                        ?? entry.variables ?? variables,
                    response: entry.verify.response
                  } : verify
            }));
        return;
      }

      runTest({
        itName: "default",
        name,
        auth,
        variables,
        query,
        response,
        before,
        after,
        verify
      });
    })
  })
}

function runTest({
  name,
  auth,
  variables,
  query,
  response,
  before,
  after,
  verify,
  itName
}) {
  describe(name, () => {
    describe(itName, () => {
      beforeAll(wrapHookIfNeeded(before));
      afterAll(wrapHookIfNeeded(after));

      describe(`As ${Authorization.nameFor(auth)}`, () => {
        it("Test", async () => {
          const graphQl = load(query);
          const expectedResponse = load(response);

          await test(graphQl, expectedResponse, variables, auth);
        })
        if (verify != null && verify.response != null) {
          it("Verify", async () => {
            await test(load(verify.query), load(verify.response),
                verify.variables, Authorization.ADMIN)
          })
        }
      })
    })
  })

}

function wrapHookIfNeeded(data) {
  if (typeof data === 'function') {
    return data;
  }

  if (Array.isArray(data)) {
    return async () => await Promise.all(data.map(convertToJestHook));
  }

  return convertToJestHook(data);
}

function convertToJestHook(hook) {
  return async () => {
    const query = await load(hook.query);
    await execute(query, hook.variables, Authorization.ADMIN);
  }
}
