plugins {
    java
    id("io.micronaut.library") version "3.1.1"
}

group = "club.devcord.devmarkt"
version = "0.1"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}
