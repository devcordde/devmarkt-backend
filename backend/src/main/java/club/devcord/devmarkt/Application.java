package club.devcord.devmarkt;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.runtime.Micronaut;

@Introspected(packages = {
    "club.devcord.devmarkt.dto.template"
}, includedAnnotations = club.devcord.devmarkt.dto.Introspected.class)
public class Application {

  public static void main(String[] args) {
    Micronaut.run(Application.class, args);
  }
}
