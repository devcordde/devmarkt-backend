package club.devcord.devmarkt;

import com.mongodb.client.MongoClient;
import io.micronaut.context.ApplicationContext;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.runtime.Micronaut;

@Introspected(packages = {
    "club.devcord.devmarkt.dto.template"
}, includedAnnotations = club.devcord.devmarkt.dto.Introspected.class)
public class Application {

  public static void main(String[] args) {
    var context = Micronaut.run(Application.class, args);
    setup(context);
  }

  public static void setup(ApplicationContext context) {
    context.createBean(MongoClient.class); // start mongo client at startup
  }
}
