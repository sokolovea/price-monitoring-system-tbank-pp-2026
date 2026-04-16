plugins {
    java
    id("org.springframework.boot") version "4.0.3"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "ru.tbank"
version = "0.0.1-SNAPSHOT"
description = "Project development workshop. Tbank, winter 2026. Integration microservice"

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
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.mockito:mockito-junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    
    //kafka
    implementation("org.springframework.kafka:spring-kafka")
    implementation(project(":core"))
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.1")
}

tasks.test {
    useJUnitPlatform()
}