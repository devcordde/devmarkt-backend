package club.devcord.devmarkt.database.template;

import club.devcord.devmarkt.database.template.dto.DBQuestion;
import club.devcord.devmarkt.database.template.dto.DBTemplate;
import club.devcord.devmarkt.dto.template.Question;
import club.devcord.devmarkt.dto.template.Template;
import java.util.List;

public class Constants {

  public static final DBTemplate DBTEMPLATE = new DBTemplate(
      5,
      "template",
      List.of(new DBQuestion(null, null, 0, "How are you?"),
          new DBQuestion(null, null, 1, "What's your name?"),
          new DBQuestion(null, null, 2, "Where do you live?"))
  );

  public static final Template TEMPLATE = new Template(
      "template",
      List.of(new Question(0, "How are you?"),
          new Question(1, "What's your name?"),
          new Question(2, "Where do you live?"))
  );

  public static final String REQUESTER_ID = "007";

}
