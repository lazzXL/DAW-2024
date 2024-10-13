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
    api(project(":repository"))
    api(project(":domain"))
    implementation("jakarta.inject:jakarta.inject-api:2.0.1")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}