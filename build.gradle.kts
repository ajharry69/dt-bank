import org.springframework.boot.gradle.tasks.bundling.BootBuildImage
import org.springframework.boot.gradle.tasks.run.BootRun

plugins {
    java
    jacoco
    id("org.springframework.boot") version "3.4.5" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
}

extra["springCloudVersion"] = "2024.0.1"
extra["mapstructVersion"] = "1.6.3"
extra["springDocVersion"] = "2.7.0"
extra["keycloakVersion"] = "3.7.0"
extra["restAssuredVersion"] = "5.3.2"
extra["datafakerVersion"] = "2.4.3"

allprojects {
    group = "com.github.ajharry69"
    version = "1.0.3"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "jacoco")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(24)
        }
    }

    configurations {
        compileOnly {
            extendsFrom(configurations.annotationProcessor.get())
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        finalizedBy(tasks.jacocoTestReport)
    }

    tasks.withType<JacocoReport> {
        reports {
            csv.required.set(true)
            csv.outputLocation.set(File(rootDir, "build/reports/jacoco/${project.name}/results.csv"))
            xml.required.set(true)
            xml.outputLocation.set(File(rootDir, "build/reports/jacoco/${project.name}/results.xml"))
            html.outputLocation.set(File(rootDir, "build/reports/jacoco/${project.name}/html"))
        }
    }

    tasks.withType<BootRun> {
        jvmArgs = listOf("--enable-native-access=ALL-UNNAMED")
    }

    tasks.withType<BootBuildImage> {
        builder.set("paketobuildpacks/builder-jammy-full")

        val username = System.getenv("GITHUB_ACTOR") ?: "ajharry69"
        val version = System.getenv("IMAGE_VERSION") ?: project.version
        imageName.set("ghcr.io/$username/${project.name}:$version")

        environment.putAll(
            mapOf(
                "BP_JVM_VERSION" to java.toolchain.languageVersion.get().toString(),
                "BP_OCI_LICENSES" to "Apache-2.0",
                "BP_OCI_SOURCE" to "https://github.com/$username/dt-bank",
            ),
        )

        docker {
            publishRegistry {
                this.username.set(username)
                System.getenv("GITHUB_TOKEN")?.let(password::set)
            }
        }
    }
}