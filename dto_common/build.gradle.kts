plugins {
    id("io.micronaut.library") version "3.1.0"
}

group = "club.devcord.devmarkt"
version = "0.1"

repositories {
    mavenCentral()
}

dependencies {
    annotationProcessor("io.micronaut:micronaut-inject-java")
    compileOnly("io.swagger.core.v3:swagger-annotations:2.1.11")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}
