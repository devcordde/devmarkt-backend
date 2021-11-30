plugins {
    id("com.github.johnrengelman.shadow") version "7.1.0"
    id("io.micronaut.application") version "3.0.0"
}

version = "0.1"
group = "club.devcord.devmarkt"

repositories {
    mavenCentral()
}

micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("club.devcord.devmarkt.*")
    }
}

dependencies {
    annotationProcessor("io.micronaut.openapi:micronaut-openapi")
    annotationProcessor("io.micronaut:micronaut-http-validation")
    annotationProcessor("io.micronaut:micronaut-inject-java")
    implementation("io.micronaut:micronaut-http-client")
    implementation("io.micronaut:micronaut-runtime")
    implementation("javax.annotation:javax.annotation-api")

    implementation("org.mongojack:mongojack:4.3.0")
    implementation("io.micronaut.mongodb:micronaut-mongo-sync")

    implementation("io.swagger.core.v3:swagger-annotations")

    implementation(project(":dto_common"))

    testImplementation("org.testcontainers:mongodb:1.16.2")
}


application {
    mainClass.set("club.devcord.devmarkt.Application")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
    this.testLogging {
        this.showStandardStreams = true
    }
}


