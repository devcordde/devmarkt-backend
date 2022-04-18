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

package club.devcord.devmarkt.misc;

import club.devcord.devmarkt.entities.EntityID;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.inject.annotation.TypedAnnotationTransformer;
import io.micronaut.inject.visitor.VisitorContext;
import java.util.List;

public class EntityIdAnnotationTransformer implements TypedAnnotationTransformer<EntityID> {

  @Override
  public List<AnnotationValue<?>> transform(AnnotationValue<EntityID> annotation,
      VisitorContext visitorContext) {
    visitorContext.fail("test", null);
    return null;
    /*return List.of(
        AnnotationValue.builder(Id.class).build(),
        AnnotationValue.builder(GeneratedValue.class).build(),
        AnnotationValue.builder(MappedProperty.class)
            .value(annotation.getRequiredValue("value", String.class)).build()
    );*/
  }

  @Override
  public Class<EntityID> annotationType() {
    return EntityID.class;
  }
}
