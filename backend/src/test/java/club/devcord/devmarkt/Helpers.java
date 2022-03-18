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

package club.devcord.devmarkt;

import static org.junit.jupiter.api.Assertions.assertEquals;

import club.devcord.devmarkt.entities.template.Question;
import club.devcord.devmarkt.entities.template.Template;
import club.devcord.devmarkt.responses.Fail;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;

public class Helpers {

  public static final List<Question> QUESTIONS = List.of(
      new Question(null, null, 0, "Where's the policeman who stole my newspaper?"),
      new Question(null, null, 1, "Should I really try to eat uranium?"),
      new Question(null, null, 2, "Oh no, is my table really pregnant?"));

  public static final Template TEMPLATE = new Template(-1, "test", QUESTIONS);

  private static ObjectMapper mapper;

  public static void initMapper(ObjectMapper mapper) {
    Helpers.mapper = mapper;
  }

  public static void assertJson(Object expected, Object value) {
    try {
      assertEquals(mapper.writeValueAsString(expected), mapper.writeValueAsString(value));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

  public static void verify(String expectedErrorCode, Object response) {
    if (response instanceof Fail fail) {
      Assertions.assertEquals(expectedErrorCode, fail.errorCode());
    }
  }

  public static <T> void verify(T expected, Object actual) {
    assertJson(expected, actual);
  }

  public static <T> void verify(Collection<T> expected, Collection<T> actual) {
    assertEquals(jsonSet(expected), jsonSet(actual));
  }

  private static Set<String> jsonSet(Collection<?> objects) {
    return objects
        .stream()
        .map(value -> {
          try {
            return mapper.writeValueAsString(value);
          } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
          }
        })
        .collect(Collectors.toSet());
  }

}
