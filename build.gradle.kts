plugins {
    id("org.springframework.boot") version "3.4.5"
    id("io.spring.dependency-management") version "1.1.6"
    kotlin("jvm") version "2.1.20"
    kotlin("plugin.spring") version "2.1.20"
    kotlin("plugin.jpa") version "2.1.20"
    id("org.jetbrains.kotlin.plugin.noarg") version "2.1.20"
    kotlin("kapt") version "2.1.20"
    id("org.jetbrains.kotlin.plugin.allopen") version "2.1.20"
}

noArg {
    annotation("jakarta.persistence.Entity")
}


allOpen {
    annotation("jakarta.persistence.Entity")
}

group = "ru.bjcreslin"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}

extra["springModulithVersion"] = "1.2.1"

val jsonwebtoken = "0.12.6"
val telegrambots = "6.9.7.1"
val mapstruct = "1.6.3"
val gigachat = "0.1.7"
val postgreSQL = "42.7.3"

dependencies {
    implementation(enforcedPlatform("org.springframework.boot:spring-boot-dependencies:3.4.5"))

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.security:spring-security-crypto")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.modulith:spring-modulith-starter-core")
    implementation("org.springframework.boot:spring-boot-starter-logging")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    implementation("io.jsonwebtoken:jjwt:$jsonwebtoken")
    implementation("io.jsonwebtoken:jjwt-api:$jsonwebtoken")
    implementation("io.jsonwebtoken:jjwt-impl:$jsonwebtoken")
    implementation("io.jsonwebtoken:jjwt-jackson:$jsonwebtoken")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml")
    implementation("org.liquibase:liquibase-core")
    implementation("org.telegram:telegrambots-spring-boot-starter:$telegrambots")
    implementation("org.postgresql:postgresql:${postgreSQL}")
    implementation("com.github.ben-manes.caffeine:caffeine")
    implementation("chat.giga:gigachat-java:${gigachat}")
    
    // QR Code generation
    implementation("com.google.zxing:core:3.5.2")
    implementation("com.google.zxing:javase:3.5.2")

    kapt("org.springframework.boot:spring-boot-configuration-processor")
    implementation("org.mapstruct:mapstruct:${mapstruct}")
    kapt("org.mapstruct:mapstruct-processor:${mapstruct}")

    developmentOnly("org.springframework.boot:spring-boot-devtools")
    implementation("io.micrometer:micrometer-registry-prometheus")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.springframework.modulith:spring-modulith-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.modulith:spring-modulith-bom:${property("springModulithVersion")}")
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
    sourceSets.main {
        kotlin.srcDir("build/generated/ksp/main/kotlin")
        resources.srcDir("build/generated/ksp/main/resources")
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-receivers")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.bootJar {
    archiveBaseName.set("myapp")
    archiveVersion.set("")
}
