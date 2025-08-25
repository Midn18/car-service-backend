import com.google.protobuf.gradle.*

plugins {
    kotlin("jvm")
    application
    id("com.google.protobuf") version "0.9.4"
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation("io.grpc:grpc-netty-shaded:1.62.2")
    implementation("io.grpc:grpc-protobuf:1.62.2")
    implementation("io.grpc:grpc-stub:1.62.2")
    implementation("com.google.protobuf:protobuf-java:3.25.3")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.25.3"
    }
    plugins {
        create("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.62.2"
        }
    }
    generateProtoTasks {
        all().forEach { task ->
            task.plugins {
                id("grpc")
            }
        }
    }
}

application {
    mainClass.set("com.carservice.appointments.MainKt")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}