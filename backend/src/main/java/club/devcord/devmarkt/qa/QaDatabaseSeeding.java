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

package club.devcord.devmarkt.qa;

import club.devcord.devmarkt.auth.Role;
import club.devcord.devmarkt.entities.application.Answer;
import club.devcord.devmarkt.entities.application.Application;
import club.devcord.devmarkt.entities.application.ApplicationStatus;
import club.devcord.devmarkt.entities.auth.User;
import club.devcord.devmarkt.entities.auth.UserId;
import club.devcord.devmarkt.entities.template.Question;
import club.devcord.devmarkt.entities.template.Template;
import club.devcord.devmarkt.repositories.ApplicationRepo;
import club.devcord.devmarkt.repositories.TemplateRepo;
import club.devcord.devmarkt.repositories.UserRepo;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.data.jdbc.runtime.JdbcOperations;
import io.micronaut.runtime.event.ApplicationStartupEvent;
import io.micronaut.transaction.exceptions.TransactionSystemException;
import jakarta.inject.Singleton;
import java.sql.PreparedStatement;
import java.util.List;
import jakarta.transaction.Transactional;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@Requires(env = "qa")
public class QaDatabaseSeeding implements ApplicationEventListener<ApplicationStartupEvent> {

  private static final Logger LOGGER = LoggerFactory.getLogger(QaDatabaseSeeding.class);

  private final TemplateRepo templateRepo;
  private final UserRepo userRepo;
  private final ApplicationRepo applicationRepo;

  private final JdbcOperations jdbcOperations;

  private final Flyway flyway;

  private Template devSearched;
  private Template devOffered;
  private Template emptyTemplate;

  private User userUser;
  private User noneUser;
  private User secondUserUser;

  public QaDatabaseSeeding(TemplateRepo templateRepo,
      UserRepo userRepo, ApplicationRepo applicationRepo,
      JdbcOperations jdbcOperations, Flyway flyway) {
    this.templateRepo = templateRepo;
    this.userRepo = userRepo;
    this.applicationRepo = applicationRepo;
    this.jdbcOperations = jdbcOperations;
    this.flyway = flyway;
  }

  @Override
  public void onApplicationEvent(ApplicationStartupEvent event) {
    seed();
  }

  @Transactional
  public void reseedDatabase() {
    LOGGER.info("Reseeding database");
    try {
      flyway.clean();
      flyway.migrate();
      jdbcOperations.prepareStatement("DEALLOCATE ALL", PreparedStatement::execute);
      seed();
    } catch (TransactionSystemException e) {
      LOGGER.info("TransactionSystemException occurred, might be have something to do with prepared statement caching."
          + "Retrying...");
      // As this is only used for qa tests, this should be fine to avoid errors with caching (retrying solves them usually here)
      reseedDatabase();
    }
  }

  private void seed() {
    LOGGER.info("Starting database seeding");
    seedTemplates();
    seedUsers();
    seedApplications();
  }

  private void seedTemplates() {
    LOGGER.info("Start template seeding");
    var questions = List.of(
        new Question(0, "Who are we?", false, 1, null),
        new Question(1, "Why should you join us?", true, 100, null),
        new Question(2, "What programming languages should you know?", false, 1, null),
        new Question(3, "Custom text:", true, 30, null)
    );
    devSearched = templateRepo.save(new Template(-1, "Dev searched", true, questions));
    questions = List.of(
        new Question(0, "Who am I?", false, 1, null),
        new Question(1, "What programming language do I know?", false, 1, null),
        new Question(2, "Why should you choose me?", true, 500, null)
    );
    devOffered = templateRepo.save(new Template(-1, "Dev offered", true, questions));
    emptyTemplate = templateRepo.save(new Template(-1, "Empty template", true, List.of()));
  }

  private void seedUsers() {
    LOGGER.info("Start user seeding");
    noneUser = userRepo.save(new User(-1, new UserId("testuser", 1), Role.NONE));
    userUser = userRepo.save(new User(-1, new UserId("testuser", 2), Role.USER));
    secondUserUser = userRepo.save(new User(-1, new UserId("testuser", 3), Role.USER));
  }

  private final String lorem500 = """
        Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore 
        et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet c
        lita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, se
        d diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et e
        """;

  private void seedApplications() {
    LOGGER.info("Start application seeding");
    var answers = List.of(
        new Answer(null, 0, "Lorem ipsum dolor sit amet, co N0", devOffered.questions().get(0), null),
        new Answer(null, 1, "Lorem ipsum dolor sit amet, co N1", devOffered.questions().get(1), null),
        new Answer(null, 2, lorem500 + " N2", devOffered.questions().get(2),null)
    );
    applicationRepo.save(new Application(-1, null, ApplicationStatus.UNPROCESSED, noneUser, devOffered, answers));
    applicationRepo.save(new Application(-1, null, ApplicationStatus.ACCEPTED, noneUser, emptyTemplate, List.of()));
    applicationRepo.save(new Application(-1, null, ApplicationStatus.REJECTED, noneUser, devOffered, answers));

    applicationRepo.save(new Application(-1, null, ApplicationStatus.ACCEPTED, userUser, devOffered, answers));
    applicationRepo.save(new Application(-1, null, ApplicationStatus.REJECTED, userUser, emptyTemplate, List.of()));

    applicationRepo.save(new Application(-1, null, ApplicationStatus.UNPROCESSED, secondUserUser, emptyTemplate, List.of()));
  }
}
