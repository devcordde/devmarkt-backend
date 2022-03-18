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
import static org.junit.jupiter.api.Assertions.assertTrue;

import club.devcord.devmarkt.DevmarktTest;
import club.devcord.devmarkt.Helpers;
import club.devcord.devmarkt.Seed;
import club.devcord.devmarkt.entities.template.Template;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

public class TemplateRepoTest extends DevmarktTest {

  @Inject
  TemplateRepo repo;

  @Test
  void existByName_true() {
    var exists = repo.existsByName("Dev searched");
    assertTrue(exists);
  }

  @Test
  void existByName_false() {
    var exists = repo.existsByName("Devcord Admin Rank Buy Offer");
    assertFalse(exists);
  }

  @Test
  void findByName_present() {
    var opt = repo.findByName("Dev offered");
    assertPresent(opt);
    verify(TEMPLATE_SEED.get("Dev offered"), opt.get());
  }

  @Test
  void findByName_empty() {
    var opt = repo.findByName("Testament");
    assertEmpty(opt);
  }

  @Test
  void deleteByName_success() {
    var deleted = repo.deleteByName("Dev offered");
    assertEquals(1, deleted);
  }

  @Test
  void deleteByName_fail() {
    var deleted = repo.deleteByName("Taucher's diving equipment sale offer");
    assertEquals(0, deleted);
  }

  @Test
  void updateByName__updateName_success() {
    var updated = repo.updateByName("Dev searched", "I nEeD A dEv!");
    assertEquals(1, updated);
  }

   @Test
  void updateByName__updateName_fail() {
    var updated = repo.updateByName("Pool building permit", "i wAnT tO bUiLd A pOoL!?");
    assertEquals(0, updated);
  }

  @Test
  void getIdByName__noVerify_success() {
   var opt = repo.getIdByName("Dev offered");
   assertPresent(opt);
  }

  @Test
  void getIdByName__noVerify_fail() {
   var opt = repo.getIdByName("White house sale contract");
   assertEmpty(opt);
  }

  @Test
  void findAll_success() {
    var all = repo.findAll();
    verify(TEMPLATE_SEED.values(), all);
  }

  @Test
  void findNames_success() {
    var names = repo.findName();
    verify(Seed.templateNames(), names);
  }

  @Test
  void save_success() {
    var template = new Template(-1, "500 liters of whiskey order", Helpers.QUESTIONS);
    var saved = repo.save(template);
    verify(template, saved);
  }

}
