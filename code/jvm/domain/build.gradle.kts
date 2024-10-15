plugins {
    kotlin("jvm") version "1.9.25"
}

group = "pt.isel"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    implementation("jakarta.inject:jakarta.inject-api:2.0.1")

    api("org.springframework.security:spring-security-core:6.3.0")

    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

tasks.withType<Test> {
    useJUnitPlatform()
}