plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("io.micronaut.application") version "3.2.1"
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
    compileOnly("io.micronaut.reactor:micronaut-reactor")

    implementation("io.swagger.core.v3:swagger-annotations")

    implementation(project(":dto_common"))
    implementation(project(":micronaut-mongojack"))

    testImplementation("org.testcontainers:mongodb:1.16.3")
    testCompileOnly("io.micronaut.reactor:micronaut-reactor")
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


