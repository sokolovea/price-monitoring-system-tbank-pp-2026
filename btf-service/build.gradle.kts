plugins {
	java
	id("org.springframework.boot") version "4.0.3"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "ru.tbank"
version = "0.0.1-SNAPSHOT"
description = "Project development workshop. Tbank, winter 2026. BTF microservice"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(25)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
    implementation("jakarta.validation:jakarta.validation-api:4.0.0-M1")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-jdbc")
	implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("io.jsonwebtoken:jjwt:0.13.0")
	compileOnly("org.projectlombok:lombok")
	runtimeOnly("org.postgresql:postgresql")
	annotationProcessor("org.projectlombok:lombok")
    testImplementation("jakarta.validation:jakarta.validation-api:4.0.0-M1-test")
	testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
	testImplementation("org.springframework.boot:spring-boot-starter-jdbc-test")
	testImplementation("org.springframework.boot:spring-boot-starter-web-test")
    testImplementation("org.springframework.boot:spring-boot-starter-security-test")
    testImplementation("io.jsonwebtoken:jjwt:0.13.0-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher-test")
}

tasks.test {
	useJUnitPlatform()
}