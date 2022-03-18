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

package club.devcord.devmarkt.logging;

import static org.junit.jupiter.api.Assertions.assertEquals;

import club.devcord.devmarkt.responses.Fail;
import club.devcord.devmarkt.responses.Response;
import club.devcord.devmarkt.responses.Success;
import org.junit.jupiter.api.Test;

public class LoggingUtilTest {

  @Test
  void testResponseStatus_fail() {
    var response = new FailResponse("SOME_FANCY_ERROR_CODE");
    var status = LoggingUtil.responseStatus(response);
    assertEquals("SOME_FANCY_ERROR_CODE", status);
  }

  @Test
  void testResponseStatus_success() {
    var response = new SuccessResponse();
    var status = LoggingUtil.responseStatus(response);
    assertEquals("success", status);
  }

  @Test
  void testResponseStatus_unexpected() {
    var response = new UnexpectedResponse();
    var status = LoggingUtil.responseStatus(response);
    assertEquals("unexpected response", status);
  }

  private record UnexpectedResponse() implements Response {

  }

  private record FailResponse(String errorCode) implements Response, Fail {

  }

  private record SuccessResponse() implements Response, Success {

  }

}
