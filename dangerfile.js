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

import {danger, message, warn} from 'danger';

const fs = require('fs');
const path = require('path');

function isCollaborator() {
  if (!danger.github) {
    return false;
  }
  const relation = danger.github.pr.author_association;
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

const modifiedFiles = danger.git.created_files
.concat(danger.git.deleted_files)
.concat(danger.git.modified_files);

const notAddedFiles = danger.git.deleted_files.concat(
    danger.git.modified_files);

const failed = danger.github.pr?.labels.some(
    label => label.name === 'danger-ignore-fail')
    ? warn
    : fail;

// PR checks (assignee, labels, milestone)
if (danger.github) {
  if (
      !danger.github.pr?.assignees.length
      && !danger.github.pr?.assignee
  ) {
    warn("No assignee has been set");
  }

  if (!danger.github.pr?.labels.length) {
    warn("No labels have been set");
  }

  if (danger.github.pr?.labels.some(
      label => label.name === 'better description')) {
    warn(
        "This PR has the `better description` label, consider editing the description before merging");
  }

  if (!danger.github.pr?.milestone) {
    warn("No milestone has been set");
  }
}

// File checks
if (
    modifiedFiles.some(
        file => TOOLING_FILES.some(toolingFile => file.includes(toolingFile)))
    && !isCollaborator()
) {
  message("This PR modifies the tooling of the project");
}

if (modifiedFiles.some(file => file.includes(".idea"))) {
  const fn = isCollaborator() ? message : warn;
  fn("This PR modifies the IntelliJ IDEA setting files");
}

if (modifiedFiles.some(file => file.includes("src/main/resources/db/seeder"))
    !== modifiedFiles.some(file => file.includes(
        "src/test/java/club/devcord/devmarkt/Seed.java"))) {
  warn("The `Seed` class needs to be checked if it is up to date");
}

if (notAddedFiles.some(
        file => file.includes("src/main/resources/db/migrations"))
    && danger.github?.pr?.base?.ref === 'main'
) {
  failed(`Migrations can't be changed (\`${
      notAddedFiles
      .filter(file => file.includes("src/main/resources/db/migrations"))
      .join('`, `')
  }\`)`);
}

// Linter

function readFileContent(fileName) {
  return fs.readFileSync(fileName).toString("utf-8");
}

function readDirectoryRecursive(
    directory,
    excluded = [],
    currentPath = __dirname
) {

  const isExcluded = (file) => excluded.some(exclude => file.includes(exclude));

  const dirents = fs.readdirSync(directory, {withFileTypes: true});

  const files = dirents.filter(dirent => dirent.isFile())
  .filter(dirent => !isExcluded(dirent.name))
  .map(dirent => `${dirent.name}`);

  const directories = dirents.filter(dirent => dirent.isDirectory())
  .filter(dirent => !isExcluded(`${dirent.name}`));

  directories.forEach(dir => {
    const recursedFiles = readDirectoryRecursive(
        path.join(currentPath, dir.name),
        excluded,
        `${currentPath}/${dir.name}`)
    .map(filename => `${dir.name}/${filename}`);
    files.push(...recursedFiles);
  });
  return files;
}

const LINTER_EXCLUDED = [
  ".git",
  "gradle",
  ".idea",
  "node_modules",
  "build",
  "LICENSE"
];

readDirectoryRecursive(".", LINTER_EXCLUDED)
.map(file => ({filename: file, content: readFileContent(file)}))
.forEach(lintFile)

function lintFile(file) {
  if (!file.content.endsWith("\n")) {
    failed(`\`${file.filename}\` is missing a new line at the end`);
  }

  if (/import [^*]+\.\*;/.test(file.content)) {
    failed(`\`${file.filename}\` is using a wildcard import`);
  }

  if ((/System\.out/.test(file.content) || /System\.err/.test(file.content))
      && file.filename !== 'dangerfile.js') {
    failed(
        `\`${file.filename}\` is using the \`System.out\` or \`System.err\` print stream`);
  }

  if (!/^[a-z0-9-_/.]+\.[a-z0-9]+$/i.test(file.filename)) {
    failed(
        `\`${file.filename}\` does not follow the file naming convention`);
  }
}
