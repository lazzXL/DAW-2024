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
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testImplementation(project(":repository-jdbi"))
    testImplementation("org.jdbi:jdbi3-core:3.37.1")
    testImplementation("org.postgresql:postgresql:42.7.2")

    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.0")
}

tasks.test {
    useJUnitPlatform()
    environment("DB_URL", "jdbc:postgresql://localhost:5432/postgres?user=postgres&password=postgres")
}


kotlin {
    jvmToolchain(21)
}