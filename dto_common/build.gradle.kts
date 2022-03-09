plugins {
    java
    id("io.micronaut.library") version "3.3.0"
}

group = "club.devcord.devmarkt"
version = "0.1"

repositories {
    mavenCentral()
}

dependencies {
    annotationProcessor("io.micronaut.openapi:micronaut-openapi")
    compileOnly("io.swagger.core.v3:swagger-annotations")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}
