import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

plugins {
    java
    jacoco
    id("org.springframework.boot") version "3.4.5" apply false
    id("io.spring.dependency-management") version "1.1.5" apply false
}

allprojects {
    group = "com.github.ajharry69"
    version = "0.0.1-SNAPSHOT"

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

    tasks.withType<BootBuildImage> {
        val username = System.getenv("GITHUB_ACTOR") ?: "ajharry69"
        imageName.set("ghcr.io/$username/${project.name}:${project.version}")
        docker {
            publishRegistry {
                this.username.set(username)
                System.getenv("GITHUB_TOKEN")?.let(password::set)
            }
        }
    }
}