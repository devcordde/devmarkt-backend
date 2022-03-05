package club.devcord.devmarkt.database.template.dto;

import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.Relation;
import io.micronaut.data.annotation.Relation.Cascade;
import java.util.List;

@MappedEntity("templates")
public record DBTemplate(
    @Id @GeneratedValue
    int id,
    String name,
    @Relation(value = Relation.Kind.ONE_TO_MANY, mappedBy = "template", cascade = Cascade.ALL)
    List<DBQuestion> questions
) {

}
