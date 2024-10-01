plugins {
    kotlin("jvm") version "1.9.25"
}

group = "pt.isel"
version = "0.0.1-SNAPSHOT"

kotlin {
    jvmToolchain(17)
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation(project(":repository"))
    implementation(project(":domain"))

}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}