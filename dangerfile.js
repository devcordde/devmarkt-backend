/*
 * Copyright 2021 Contributors to the Devmarkt-Backend project
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

import {danger, warn} from 'danger';

// for debugging
console.info(danger.github.pr);

function isCollaborator(relation) {
  return relation === "COLLABORATOR"
      || relation === "MEMBER"
      || relation === "OWNER";
}

const TOOLING_FILES = [
    ".github",
    "build.gradle.kts",
    "settings.gradle.kts",
    "gradlew",
    "gradle/wrapper",
    "dangerfile.js",
    "package.json",
    "package-lock.json"
];

if (
    !danger.github.pr.assignees.length
    && !danger.github.pr.assignee
) {
  warn("No assignee has been set");
}

if (!danger.github.pr.labels.length) {
  warn("No lables have been set");
}

if (danger.github.pr.labels.some(
    label => label.name === 'better description')) {
  warn("This PR has the `better description` label, consider editing the description before merging");
}

if (!danger.github.pr.milestone) {
  warn("No milestone has been set");
}

const modifiedFiles = danger.git.created_files.concat(danger.git.deleted_files).concat(danger.git.modified_files);

if(modifiedFiles.some(file => TOOLING_FILES.some(toolingFile => file.includes(toolingFile)))) {
  warn("This PR modifies the tooling of the project");
}