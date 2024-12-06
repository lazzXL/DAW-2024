plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.3.3"
	id("io.spring.dependency-management") version "1.1.6"
	//id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
}

group = "pt.isel"
version = "0.0.1-SNAPSHOT"

kotlin {
	jvmToolchain(21)
}

repositories {
	mavenCentral()
}

dependencies {
	implementation(project(":services"))
	implementation("org.springframework:spring-webmvc:6.1.13")
	implementation("org.slf4j:slf4j-api:2.0.16")

	/*
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")*/
	testImplementation(project(":repository-jdbi"))
	testImplementation("org.jdbi:jdbi3-core:3.37.1")
	testImplementation("org.postgresql:postgresql:42.7.2")
	testImplementation(kotlin("test"))
	testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
	testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.0")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
	testImplementation(project(mapOf("path" to ":host")))
}

tasks.test {
	useJUnitPlatform()
	environment("DB_URL", "jdbc:postgresql://localhost:5431/postgres?user=postgres&password=postgres")
	dependsOn(":repository-jdbi:dbTestsWait")
	finalizedBy(":repository-jdbi:dbTestsDown")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
	//jvmToolchain(21)
}
