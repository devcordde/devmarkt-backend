plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.micronaut.application") version "4.0.0"
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
    // When.MAYBE warning fix
    annotationProcessor("com.google.code.findbugs:jsr305:3.0.2")
    implementation("ch.qos.logback:logback-classic:1.4.8")
    implementation("com.google.code.findbugs:jsr305:3.0.2")
    implementation("io.micronaut.security:micronaut-security-jwt")
    implementation("io.micronaut.reactor:micronaut-reactor")
    implementation("io.micronaut:micronaut-runtime")
    implementation("io.micronaut.data:micronaut-data-jdbc")
    implementation("io.micronaut.flyway:micronaut-flyway")
    implementation("io.micronaut.graphql:micronaut-graphql")
    implementation("com.graphql-java-kickstart:graphql-java-tools:13.0.3")
    implementation("com.graphql-java:graphql-java-extended-validation:20.0-validator-6.2.0.Final")
    implementation("io.micronaut:micronaut-validation")
    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("io.micronaut.sql:micronaut-jdbc-hikari")

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


