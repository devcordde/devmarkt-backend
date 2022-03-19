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

package club.devcord.devmarkt.graphql;

import io.micronaut.core.io.ResourceResolver;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchemaResolver {

  private static final Logger LOGGER = LoggerFactory.getLogger(SchemaResolver.class);

  public Set<String> resolveSchemas(String location, ResourceResolver resolver) throws IOException, URISyntaxException {
    var url = resolver.getResources(location)
        .findFirst();
    if (url.isPresent()) {
      try (var pathStream = Files.walk(Paths.get(url.get().toURI()))) {
        var shortedLocation = location.substring("classpath:".length());
        return pathStream
            .filter(Files::isRegularFile)
            .map(path -> Path.of(shortedLocation, path.getFileName().toString()))
            .map(Path::toString)
            .collect(Collectors.toSet());
      }
    } else {
      LOGGER.error("No schema files found");
      return null;
    }
  }

}
