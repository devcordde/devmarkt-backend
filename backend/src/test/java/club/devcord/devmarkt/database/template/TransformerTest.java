package club.devcord.devmarkt.database.template;


import club.devcord.devmarkt.database.template.dto.DBTemplate;
import club.devcord.devmarkt.dto.template.Template;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TransformerTest {

  static final DBTemplate DBTEMPLATE = Constants.DBTEMPLATE;

  static final Template TEMPLATE = Constants.TEMPLATE;

  @Test
  void DBTemplateToTemplate() {
    var transformed = Transformer.transform(DBTEMPLATE);
    Assertions.assertEquals(TEMPLATE, transformed);
  }

  @Test
  void TemplateToDBTemplate() {
    var transformed = Transformer.transform(TEMPLATE, DBTEMPLATE.id());
    Assertions.assertEquals(DBTEMPLATE, transformed);
  }

}
