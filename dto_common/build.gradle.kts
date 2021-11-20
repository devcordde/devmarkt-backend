plugins {
    java
}

group = "club.devcord.devmarkt"
version = "0.1"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.mongojack:mongojack:4.3.0")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}