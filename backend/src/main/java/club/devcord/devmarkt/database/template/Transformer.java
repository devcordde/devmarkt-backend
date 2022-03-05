package club.devcord.devmarkt.database.template;

import club.devcord.devmarkt.database.template.dto.DBQuestion;
import club.devcord.devmarkt.database.template.dto.DBTemplate;
import club.devcord.devmarkt.dto.template.Question;
import club.devcord.devmarkt.dto.template.Template;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Transformer {

  static DBTemplate transform(Template template) {
    return transform(template, -1);
  }

  static DBTemplate transform(Template template, int id) {
    var questions = IntStream.range(0, template.questions().size())
        .mapToObj(
            digit -> new DBQuestion(null, null, digit, template.questions().get(digit).question()))
        .toList();
    return new DBTemplate(id, template.name(), questions);
  }

  static Template transform(DBTemplate template) {
    var questions = template.questions()
        .stream()
        .map(question -> new Question(question.digit(), question.question()))
        .collect(Collectors.toList());
    return new Template(template.name(), questions);
  }

}
