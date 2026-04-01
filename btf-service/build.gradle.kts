plugins {
	java
	id("org.springframework.boot") version "4.0.3"
	id("io.spring.dependency-management") version "1.1.7"
    id ("org.openapi.generator") version "7.21.0"
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
	implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-liquibase")
	implementation("io.jsonwebtoken:jjwt:0.13.0")
	compileOnly("org.projectlombok:lombok")
	runtimeOnly("org.postgresql:postgresql")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
	testImplementation("org.springframework.boot:spring-boot-starter-jdbc-test")
  implementation("jakarta.validation:jakarta.validation-api:4.0.0-M1")
  implementation("jakarta.annotation:jakarta.annotation-api:3.0.0")
  implementation("io.swagger.core.v3:swagger-annotations:2.2.45")
	testImplementation("org.springframework.boot:spring-boot-starter-web-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

openApiGenerate {
    generatorName.set("spring")
    inputSpec.set("$rootDir/btf-service/src/main/resources/openapi.yaml")
    outputDir.set("$buildDir/generated")


    apiPackage.set("ru.tbank.pp.api")
    modelPackage.set("ru.tbank.pp.model")
    invokerPackage.set("ru.tbank.pp.invoker")

    configOptions.set(
        mapOf(
            "useSpringBoot4" to "true",
            "openApiNullable" to "false",
            "interfaceOnly" to "true",
        )
    )
}

tasks.test {
	useJUnitPlatform()
}

sourceSets {
    main {
        java {
            srcDir("$buildDir/generated/src/main/java")
        }
    }
}

tasks.named("compileJava") {
    dependsOn(tasks.openApiGenerate)
}