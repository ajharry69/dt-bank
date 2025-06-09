import org.springframework.boot.gradle.tasks.bundling.BootBuildImage
import org.springframework.boot.gradle.tasks.bundling.BootJar

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-hateoas")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${property("springDocVersion")}")
}

tasks.withType<BootJar> {
    enabled = false
}

tasks.withType<BootBuildImage> {
    enabled = false
}