import org.gradle.api.tasks.SourceSetContainer
import org.gradle.testing.jacoco.tasks.JacocoReport
import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification

plugins {
    jacoco
}

jacoco {
    toolVersion = "0.8.11"
}

allprojects {
    group = "ru.tbank"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "jacoco")

    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    configure<JacocoPluginExtension> {
        toolVersion = rootProject.extensions.getByType<JacocoPluginExtension>().toolVersion
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        finalizedBy(tasks.named("jacocoTestReport"))
    }

    tasks.withType<JacocoReport> {
        reports {
            xml.required.set(true)
            html.required.set(true)
        }
    }

    tasks.withType<JacocoCoverageVerification> {
        isEnabled = false
    }
}

tasks.register<JacocoReport>("jacocoRootReport") {
    group = "verification"
    description = "Generates aggregated JaCoCo coverage report for all subprojects"

    dependsOn(subprojects.map { it.tasks.named("test") })
    dependsOn(subprojects.map { it.tasks.named("jacocoTestReport") }) // 🔥 ВАЖНО

    executionData.setFrom(
        fileTree(rootDir) {
            include("**/build/jacoco/*.exec")
        }
    )

    subprojects.forEach { subproject ->
        if (subproject.plugins.hasPlugin("java")) {

            val sourceSet = subproject.extensions.getByType<SourceSetContainer>()["main"]

            sourceDirectories.from(sourceSet.allSource.srcDirs)

            classDirectories.from(
                fileTree("${subproject.layout.buildDirectory.get()}/classes/java/main") {
                    exclude(
                        "**/config/**",
                        "**/*Application*",
                        "**/dto/**",
                        "**/entity/**",
                        "**/model/**",
                        "**/exception/**",
                        "**/generated/**"
                    )
                }
            )
        }
    }

    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

tasks.register<JacocoCoverageVerification>("jacocoRootVerification") {
    group = "verification"
    description = "Verifies aggregated code coverage against rules"

    dependsOn("jacocoRootReport")

    executionData.setFrom(
        fileTree(rootDir) {
            include("**/build/jacoco/*.exec")
        }
    )

    subprojects.forEach { subproject ->
        if (subproject.plugins.hasPlugin("java")) {
            val sourceSet = subproject.extensions.getByType<SourceSetContainer>()["main"]

            classDirectories.from(
                fileTree("${subproject.buildDir}/classes/java/main") {
                    exclude(
                        "**/config/**",
                        "**/*Application*",
                        "**/dto/**",
                        "**/entity/**",
                        "**/model/**",
                        "**/exception/**",
                        "**/generated/**"
                    )
                }
            )
        }
    }

    violationRules {
        rule {
            limit {
                minimum = "0.5".toBigDecimal()
                counter = "LINE"
            }
        }
    }
}