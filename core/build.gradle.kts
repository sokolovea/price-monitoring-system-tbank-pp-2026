plugins {
    id("java")
    id("org.springframework.boot") version "4.0.3"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.openapi.generator") version "7.21.0"
}

group = "ru.tbank"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("jakarta.validation:jakarta.validation-api:4.0.0-M1")
    implementation("jakarta.annotation:jakarta.annotation-api:3.0.0")
    implementation("io.swagger.core.v3:swagger-annotations:2.2.45")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
}

openApiGenerate {
    generatorName.set("spring")
    inputSpec.set("$rootDir/core/src/main/resources/openapi.yaml")
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