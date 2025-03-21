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

    implementation("jakarta.annotation:jakarta.annotation-api:2.1.1")
    implementation("jakarta.inject:jakarta.inject-api:2.0.1")

    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")

    implementation("org.slf4j:slf4j-api:2.0.16")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testImplementation(project(":repository-jdbi"))
    testImplementation("org.jdbi:jdbi3-core:3.37.1")
    testImplementation("org.postgresql:postgresql:42.7.2")

    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.0")
}

tasks.test {
    useJUnitPlatform()
    environment("DB_URL", "jdbc:postgresql://localhost:5431/postgres?user=postgres&password=postgres")
    dependsOn(":repository-jdbi:dbTestsWait")
    finalizedBy(":repository-jdbi:dbTestsDown")
}


kotlin {
    jvmToolchain(21)
}
