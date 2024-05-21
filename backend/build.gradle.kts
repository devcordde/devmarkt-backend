plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.micronaut.application") version "4.4.0"
}

version = "2.0"
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
    annotationProcessor("io.micronaut.security:micronaut-security-annotations")
    annotationProcessor("io.micronaut.data:micronaut-data-processor")
    annotationProcessor("io.micronaut:micronaut-inject-java")
    annotationProcessor("io.micronaut.validation:micronaut-validation-processor")
    // When.MAYBE warning fix
    annotationProcessor("com.google.code.findbugs:jsr305:3.0.2")
    implementation("ch.qos.logback:logback-classic:1.5.6")
    implementation("org.slf4j:slf4j-api:2.0.13")
    implementation("com.google.code.findbugs:jsr305:3.0.2")
    implementation("io.micronaut.security:micronaut-security-jwt")
    implementation("io.micronaut.reactor:micronaut-reactor")
    implementation("io.micronaut:micronaut-runtime")
    implementation("io.micronaut.data:micronaut-data-jdbc")
    implementation("io.micronaut.flyway:micronaut-flyway")
    // https://github.com/flyway/flyway/issues/3651
    implementation("org.flywaydb:flyway-core") {
        version {
            strictly("9.16.1")
        }
    }
    implementation("io.micronaut.graphql:micronaut-graphql")
    implementation("com.graphql-java-kickstart:graphql-java-tools:13.1.1")
    implementation("com.graphql-java:graphql-java-extended-validation:22.0")
    implementation("io.micronaut.validation:micronaut-validation")
    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("io.micronaut.sql:micronaut-jdbc-hikari")
    runtimeOnly("org.yaml:snakeyaml")

}


application {
    mainClass.set("club.devcord.devmarkt.App")
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


