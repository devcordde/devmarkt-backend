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

package club.devcord.devmarkt.repositories;

import static club.devcord.devmarkt.Helpers.assertEmpty;
import static club.devcord.devmarkt.Helpers.assertPresent;
import static club.devcord.devmarkt.Helpers.verify;
import static club.devcord.devmarkt.Seed.TEMPLATE_SEED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import club.devcord.devmarkt.DevmarktTest;
import jakarta.inject.Inject;
import java.util.List;
import org.junit.jupiter.api.Test;

public class QuestionRepoTest extends DevmarktTest {

  @Inject
  QuestionRepo questionRepo;

  @Inject
  TemplateRepo templateRepo;

  private int templateId(String name) {
    var opt = templateRepo.findInternalIdByName(name);
    assertPresent(opt);
    return opt.get();
  }

  private int freeTemplateID() {
    var id = Integer.MAX_VALUE;
    assertFalse(templateRepo.existsById(id));
    return id;
  }

  @Test
  void getMaxNumberByTemplateId_success() {
    var opt = questionRepo.getMaxNumberByTemplateId(templateId("Dev searched"));
    assertPresent(opt);
    assertEquals(3, opt.get());
  }

  @Test
  void getMaxNumberByTemplateID_fail() {
    var opt = questionRepo.getMaxNumberByTemplateId(freeTemplateID());
    assertEmpty(opt);
  }

  @Test
  void updateQuestion_success() {
    var updated = questionRepo.updateByTemplateIdAndNumber(templateId("Dev offered"),
        1, "Are we living in a simulation?");
    assertEquals(1, updated);
  }

  @Test
  void updateQuestion__noQuestion_fail() {
    var updated = questionRepo.updateByTemplateIdAndNumber(templateId("Empty template"),
        6, "Are you stupid?");
    assertEquals(0, updated);
  }

  @Test
  void updateQuestion__noTemplate_fail() {
    var updated = questionRepo.updateByTemplateIdAndNumber(freeTemplateID(),
        1, "Is Santa real?");
    assertEquals(0, updated);
  }

  @Test
  void deleteQuestion_success() {
    var deleted = questionRepo.deleteByTemplateIdAndNumber(templateId("Dev offered"), 0);
    assertEquals(1, deleted);
  }

  @Test
  void deleteQuestion__noQuestion_fail() {
    var deleted = questionRepo.deleteByTemplateIdAndNumber(templateId("Empty template"), 0);
    assertEquals(0, deleted);
  }

  @Test
  void deleteQuestion__noTempalte_fail() {
    var deleted = questionRepo.deleteByTemplateIdAndNumber(freeTemplateID(), 0);
    assertEquals(0, deleted);
  }

  @Test
  void findByNumberGreaterOrEquals_success() {
    var numbers = TEMPLATE_SEED
        .get("Dev offered")
        .questions()
        .stream()
        .filter(question -> question.number() >= 2)
        .toList();

    var list = questionRepo.findByTemplateIdAndNumberGreaterThanEqualsOrderByNumber(
        templateId("Dev offered"), 2);
    verify(numbers, list);
  }

  @Test
  void findByNumberGreaterOrEquals__noTemplate_fail() {
    var list = questionRepo.findByTemplateIdAndNumberGreaterThanEqualsOrderByNumber(
        freeTemplateID(), 1);
    verify(List.of(), list);
  }

  @Test
  void findByTemplateIdAndNumber_success() {
    var opt = questionRepo.findByTemplateIdAndNumber(templateId("Dev offered"), 1);
    assertPresent(opt);
    verify(TEMPLATE_SEED.get("Dev offered").questions().get(1), opt.get());
  }

  @Test
  void findByTemplateIdAndNumber__noTemplate_fail() {
    var opt = questionRepo.findByTemplateIdAndNumber(freeTemplateID(), 0);
    assertEmpty(opt);
  }

  @Test
  void findByTemplateIdAndNumber__noQuestion_fail() {
    var opt = questionRepo.findByTemplateIdAndNumber(templateId("Empty template"), 0);
    assertEmpty(opt);
  }

  @Test
  void save_success() {
    var question = new RawQuestion(-1, templateId("Empty template"),
        0, "Why is Donald Duck throwing a party in my garden?");
    var saved = questionRepo.save(question);
    verify(question, saved);
  }

}
