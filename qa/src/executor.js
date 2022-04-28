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
import {readFileSync} from "fs";
import {resolve} from "path";

const endpoint = `${process.env.BACKEND_HOST}/graphql`;

export const Authorization = {
  NONE: "",
  // testuser:1
  NO_ROLES: readFile(resolve("./src/fixtures/no_roles.key")),
  // internal:1
  ADMIN: readFile(resolve("./src/fixtures/admin.key")),
  // testuser:2
  USER: readFile(resolve("./src/fixtures/user.key")),
};

Authorization.nameFor = token => {
  for(const key of Object.keys(Authorization)) {
    if(Authorization[key] === token) {
      return key;
    }
  }
  return "";
}

export function execute(graphql, variables = {}, authorization = Authorization.NONE) {
  return fetch(endpoint, {
    method: "post",
    headers: {
      'Content-Type': 'application/json',
      'Authorization': authorization
    },
    body: JSON.stringify({
      query: graphql,
      variables
    })
  }).then(response => response.json());
}

export default async function test(graphql, expectedResponse, variables = {}, authorization = Authorization.NONE) {
  const loadedGraphql = await Promise.resolve(graphql);
  const loadedExpectedResponse = await Promise.resolve(expectedResponse);
  const actualResponse = await execute(loadedGraphql, variables, authorization);
  expect(actualResponse).toEqual(loadedExpectedResponse);
}

export function testNamed(graphql, response, variables = {}, authorization = Authorization.NONE) {
  return test(load(graphql), load(response), variables, authorization);
}

export function prefixedTestNamed(prefix) {
  return (graphql, response, variables = {}, authorization = Authorization.NONE) => testNamed(
      `${prefix}/${graphql}`, `${prefix}/${response}`, variables, authorization);
}

export async function load(file) {
  if (file.endsWith(".graphql")) {
    return Promise.resolve(readFile(resolve(`./src/graphql/${file}`)));
  }
  if (file.endsWith(".json")) {
    return Promise.resolve((await import(`./fixtures/${file}`)).default);
  }
  return Promise.reject("Unknown File Type");
}

export function prefixedLoad(prefix) {
  return (name) => load(`${prefix}/${name}`);
}

function readFile(file) {
  return readFileSync(file, "utf-8");
}
