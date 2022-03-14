plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("io.micronaut.application") version "3.3.0"
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
    annotationProcessor("io.micronaut.data:micronaut-data-processor")
    annotationProcessor("io.micronaut.openapi:micronaut-openapi")
    annotationProcessor("io.micronaut:micronaut-http-validation")
    annotationProcessor("io.micronaut:micronaut-inject-java")
    implementation("io.micronaut:micronaut-http-client")
    implementation("io.micronaut:micronaut-runtime")
    implementation("javax.annotation:javax.annotation-api")
    implementation("io.micronaut.data:micronaut-data-jdbc")
    implementation("io.micronaut.reactor:micronaut-reactor")
    implementation("io.micronaut.flyway:micronaut-flyway")
    implementation("io.micronaut.graphql:micronaut-graphql")
    implementation("com.graphql-java-kickstart:graphql-java-tools:12.0.1")
    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("io.micronaut.sql:micronaut-jdbc-hikari")
    compileOnly("io.swagger.core.v3:swagger-annotations")

    implementation(project(":dto_common"))

    testImplementation("io.micronaut:micronaut-http-client")
    testImplementation("org.testcontainers:testcontainers:1.16.3")
    testImplementation("org.testcontainers:postgresql:1.16.3")
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


