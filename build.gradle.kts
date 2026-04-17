import org.gradle.api.tasks.SourceSetContainer
import org.gradle.testing.jacoco.tasks.JacocoReport
import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification

plugins {
    jacoco
}

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "jacoco")


    jacoco {
        toolVersion = "0.8.11"
    }

    tasks.withType<JacocoCoverageVerification>().configureEach {
        isEnabled = false
    }

    tasks.withType<JacocoReport>().configureEach {
        reports {
            xml.required.set(true)
            html.required.set(true)
        }
    }
}

tasks.register<JacocoReport>("jacocoRootReport") {

    group = "verification"
    description = "Generates aggregated JaCoCo coverage report"

    dependsOn(subprojects.map { it.tasks.named("test") })

    executionData.setFrom(
        fileTree(rootDir) {
            include("**/build/jacoco/*.exec")
        }
    )

    val sourceSets = subprojects.map {
        it.extensions.getByType<SourceSetContainer>()["main"]
    }

    classDirectories.setFrom(
        subprojects.map {
            fileTree("${it.buildDir}/classes/java/main") {
                exclude(
                    "**/config/**",
                    "**/*Application*",
                    "**/dto/**",
                    "**/generated/**"
                )
            }
        }
    )

    // ВСЕ исходники
    sourceDirectories.setFrom(sourceSets.map { it.allSource.srcDirs })

    additionalSourceDirs.setFrom(sourceSets.map { it.allSource.srcDirs })

    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

tasks.register<JacocoCoverageVerification>("jacocoRootVerification") {

    group = "verification"
    description = "Verifies aggregated code coverage"

    dependsOn("jacocoRootReport")

    executionData.setFrom(
        fileTree(rootDir) {
            include("**/build/jacoco/*.exec")
        }
    )

    val sourceSets = subprojects.map {
        it.extensions.getByType<SourceSetContainer>()["main"]
    }

    classDirectories.setFrom(sourceSets.map { it.output })

    violationRules {
        rule {
            limit {
                minimum = "0.5".toBigDecimal()
            }
        }
    }
}