plugins {
    kotlin("jvm") version "1.9.25"
    id("org.springframework.boot") apply false
    id("io.spring.dependency-management") apply false
}

group = "com.carservice"
version = "0.0.1-SNAPSHOT"

dependencies {
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.18.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
}