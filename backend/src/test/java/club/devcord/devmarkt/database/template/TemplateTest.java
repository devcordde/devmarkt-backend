package club.devcord.devmarkt.database.template;

import club.devcord.devmarkt.dto.Identified;
import club.devcord.devmarkt.dto.template.Question;
import club.devcord.devmarkt.dto.template.Template;
import club.devcord.devmarkt.util.Uris;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.flywaydb.core.Flyway;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.testcontainers.containers.PostgreSQLContainer;

@MicronautTest(rollback = false)
@TestInstance(Lifecycle.PER_CLASS)
public class TemplateTest implements TestPropertyProvider {

  private static final PostgreSQLContainer<?> CONTAINER;

  static {
    CONTAINER = new PostgreSQLContainer<>("postgres:14.2")
        .withDatabaseName("devmarkt")
        .withUsername("Johnny")
        .withPassword("12345")
        .withExposedPorts(5432);
    CONTAINER.start();
  }

  BlockingHttpClient client;

  @BeforeEach
  void beforeEach(@Client(value = "/template") HttpClient client, Flyway flyway) {
    this.client = client.toBlocking();
    flyway.clean();
    flyway.migrate();
  }

  void fillDatabase() {
    var response = client.exchange(HttpRequest.POST("/", new Identified<>(
        Constants.REQUESTER_ID,
        Constants.TEMPLATE
    )), Void.class);
    Assertions.assertEquals(HttpStatus.CREATED, response.status());
  }

  @Test
  void testCreateSuccess() {
    var response = client.exchange(HttpRequest.POST("/", new Identified<>(
        Constants.REQUESTER_ID,
        Constants.TEMPLATE
    )), Argument.STRING, Argument.STRING);

    Assertions.assertEquals(HttpStatus.CREATED, response.status());
    Assertions.assertEquals(
        Uris.of("template", Constants.TEMPLATE.name()).toString(),
        response.header(HttpHeaders.LOCATION)
    );

    // verify create
    var verify = client.exchange(HttpRequest.GET(Constants.TEMPLATE.name()), Template.class).body();
    Assertions.assertEquals(Constants.TEMPLATE, verify);
  }

  @Test
  void testCreateFail() {
    fillDatabase();

    var response = client.exchange(HttpRequest.POST("/", new Identified<>(
        Constants.REQUESTER_ID,
        Constants.TEMPLATE
    )), Argument.STRING, Argument.STRING);

    Assertions.assertEquals(HttpStatus.CONFLICT, response.status());
  }

  @Test
  void testReplaceSuccess() {
    fillDatabase();

    var newTemplate = new Template(
        Constants.TEMPLATE.name(),
        List.of(new Question(0, "What's up?"))
    );

    var response = client.exchange(HttpRequest.PUT("/", new Identified<>(
        Constants.REQUESTER_ID,
        newTemplate
    )), Argument.STRING, Argument.STRING);

    Assertions.assertEquals(HttpStatus.NO_CONTENT, response.status());
    Assertions.assertEquals(
        Uris.of("template", Constants.TEMPLATE.name()).toString(),
        response.header(HttpHeaders.LOCATION)
    );

    // verify replacement
    var verify = client.exchange(HttpRequest.GET(Constants.TEMPLATE.name()), Template.class).body();
    Assertions.assertEquals(newTemplate, verify);
  }

  // testing without filling database
  @Test
  void testReplaceFail() {
    var response = client.exchange(HttpRequest.PUT("/", new Identified<>(
        Constants.REQUESTER_ID,
        new Template(
            Constants.TEMPLATE.name(),
            List.of(new Question(0, "What's up?"))
        )
    )), Argument.STRING, Argument.STRING);

    Assertions.assertEquals(HttpStatus.NOT_FOUND, response.status());
  }

  @Test
  void testGetSuccess() {
    fillDatabase();

    var response = client.exchange(HttpRequest.GET(Constants.TEMPLATE.name()),
        Template.class);

    Assertions.assertEquals(HttpStatus.OK, response.status());
    Assertions.assertEquals(Constants.TEMPLATE, response.body());
  }

  @Test
  void testGetFail() {
    var response = client.exchange(HttpRequest.GET("CIA_employment_contract"),
        Argument.STRING, Argument.STRING);

    Assertions.assertEquals(HttpStatus.NOT_FOUND, response.status());
  }

  @Test
  void testAllNamesSuccess() {
    var nameSet = new HashSet<>();
    for (int i = 0; i < 10; i++) {
      var name = "template" + i;
      client.exchange(HttpRequest.POST("", new Identified<>(
          Constants.REQUESTER_ID,
          new Template(
              name,
              Constants.TEMPLATE.questions()
          )
      )));
      nameSet.add(name);
    }

    var response = client.exchange(HttpRequest.GET(""), Set.class);
    Assertions.assertEquals(HttpStatus.OK, response.status());
    Assertions.assertEquals(nameSet, response.body());
  }

  @Test
  void testAllNamesFail() {
    var response = client.exchange(HttpRequest.GET(""), Set.class);
    Assertions.assertEquals(HttpStatus.OK, response.status());
    Assertions.assertEquals(Set.of(), response.body());
  }

  @Test
  void testDeleteSuccess() {
    fillDatabase();

    var response = client.exchange(
        HttpRequest.DELETE(Constants.TEMPLATE.name(), Constants.REQUESTER_ID),
        Argument.STRING, Argument.STRING);
    Assertions.assertEquals(HttpStatus.NO_CONTENT, response.status());

    var verify = client.exchange(HttpRequest.GET(Constants.TEMPLATE.name()),
        Argument.STRING, Argument.STRING);
    Assertions.assertEquals(HttpStatus.NOT_FOUND, verify.status());
  }

  @Test
  void testDeleteFail() {
    var response = client.exchange(
        HttpRequest.DELETE(Constants.TEMPLATE.name(), Constants.REQUESTER_ID),
        Argument.STRING, Argument.STRING);
    Assertions.assertEquals(HttpStatus.NOT_FOUND, response.status());
  }

  @Override
  @NotNull
  public Map<String, String> getProperties() {
    return Map.of(
        "datasources.default.url", CONTAINER.getJdbcUrl(),
        "datasources.default.username", CONTAINER.getUsername(),
        "datasources.default.password", CONTAINER.getPassword()
    );
  }
}
