package club.devcord.devmarkt;

import com.mongodb.client.MongoClient;
import io.micronaut.context.ApplicationContext;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@Introspected(packages = {
    "club.devcord.devmarkt.dto.template"
}, includedAnnotations = club.devcord.devmarkt.dto.Introspected.class)
@OpenAPIDefinition(
    info = @Info(
        title = "Devmarkt-Backend",
        version = "0.1",
        description = "Rest API Specification for the Devmarkt-Backend",
        license = @License(name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0")
    )
)
public class Application {

  public static void main(String[] args) {
    var context = Micronaut.run(Application.class, args);
    setup(context);
  }

  public static void setup(ApplicationContext context) {
    context.createBean(MongoClient.class); // start mongo client at startup
  }
}
