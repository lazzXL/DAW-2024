plugins {
    kotlin("jvm") version "1.9.25"
    //id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
}

group = "pt.isel"
version = "0.0.1-SNAPSHOT"


repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":domain"))
    implementation("jakarta.inject:jakarta.inject-api:2.0.1")
    testImplementation(kotlin("test"))
}


tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}