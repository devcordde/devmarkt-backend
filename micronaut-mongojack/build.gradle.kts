plugins {
    id("io.micronaut.library") version "3.1.1"
}

version = "0.1"
group = "club.devcord.devmarkt"

repositories {
    mavenCentral()
}

dependencies {
    annotationProcessor("io.micronaut:micronaut-inject-java")
    api("org.mongojack:mongojack:4.3.0")
    api("io.micronaut.mongodb:micronaut-mongo-sync")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}
