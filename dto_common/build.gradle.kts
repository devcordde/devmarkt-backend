plugins {
    java
    id("io.micronaut.library") version "3.1.0"
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

dependencies {
    annotationProcessor("io.micronaut:micronaut-inject-java")

    compileOnly("io.swagger.core.v3:swagger-annotations:2.1.11")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}
