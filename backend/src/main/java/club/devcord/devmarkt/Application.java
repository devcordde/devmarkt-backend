package club.devcord.devmarkt;

import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

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
    Micronaut.run(Application.class, args);
  }
}
