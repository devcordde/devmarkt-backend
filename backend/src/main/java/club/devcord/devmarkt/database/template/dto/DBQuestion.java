package club.devcord.devmarkt.database.template.dto;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.Relation;
import io.micronaut.data.annotation.Relation.Kind;

@MappedEntity("questions")
public record DBQuestion(
    @Id @GeneratedValue
    Integer id,
    @Nullable @Relation(Kind.MANY_TO_ONE)
    DBTemplate template,
    int digit,
    String question
) {

}
