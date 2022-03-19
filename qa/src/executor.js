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

import fetch from "node-fetch";

const endpoint = `${process.env.BACKEND_HOST}/graphql`;

export function execute(graphql, variables = {}) {
  return fetch(endpoint, {
    method: "post",
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      query: graphql.loc.source.body,
      variables
    })
  }).then(response => response.json());
}

export default async function test(graphql, expectedResponse, variables = {}) {
  const actualResponse = await execute(graphql, variables);
  expect(actualResponse).toEqual(expectedResponse);
}