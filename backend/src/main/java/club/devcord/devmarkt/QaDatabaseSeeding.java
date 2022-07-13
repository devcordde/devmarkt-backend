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

import club.devcord.devmarkt.auth.Role;
import club.devcord.devmarkt.entities.auth.User;
import club.devcord.devmarkt.entities.auth.UserId;
import club.devcord.devmarkt.entities.template.Question;
import club.devcord.devmarkt.entities.template.Template;
import club.devcord.devmarkt.repositories.TemplateRepo;
import club.devcord.devmarkt.repositories.UserRepo;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.runtime.event.ApplicationStartupEvent;
import jakarta.inject.Singleton;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@Requires(env = "qa")
public class QaDatabaseSeeding implements ApplicationEventListener<ApplicationStartupEvent> {

  private static final Logger LOGGER = LoggerFactory.getLogger(QaDatabaseSeeding.class);

  private final TemplateRepo templateRepo;
  private final UserRepo userRepo;

  public QaDatabaseSeeding(TemplateRepo templateRepo,
      UserRepo userRepo) {
    this.templateRepo = templateRepo;
    this.userRepo = userRepo;
  }

  @Override
  public void onApplicationEvent(ApplicationStartupEvent event) {
    LOGGER.info("Starting database seeding");
    seedTemplates();
    seedUsers();
  }

  private void seedTemplates() {
    LOGGER.info("Start template seeding");
    var dsQuestions = List.of(
        new Question(0, "Who are we?", false, 1, null),
        new Question(1, "Why should you join us?", true, 100, null),
        new Question(2, "What programming languages should you know?", false, 1, null),
        new Question(3, "Custom text:", true, 30, null)
    );
    templateRepo.save(new Template(-1, "Dev searched", true, dsQuestions));
    var doQuestions = List.of(
        new Question(0, "Who am I?", false, 1, null),
        new Question(1, "What programming language do I know?", false, 1, null),
        new Question(2, "Why should you choose me?", true, 500, null)
    );
    templateRepo.save(new Template(-1, "Dev offered", true, doQuestions));
    templateRepo.save(new Template(-1, "Empty template", true, List.of()));
  }

  private void seedUsers() {
    LOGGER.info("Start user seeding");
    userRepo.save(new User(-1, new UserId("testuser", 1), Role.NONE));
    userRepo.save(new User(-1, new UserId("testuser", 2), Role.USER));
  }
}
