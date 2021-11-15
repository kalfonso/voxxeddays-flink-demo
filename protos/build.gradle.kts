import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("com.google.protobuf")
}

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.toVersion("11")
}

dependencies {
    implementation(flink_demo.Dependencies.protobufJavaUtil)
}

tasks {
    compileJava {
        options.compilerArgs.add("-parameters")
    }

    compileKotlin {
        kotlinOptions {
            jvmTarget = "11"
        }
    }
    compileTestKotlin {
        kotlinOptions {
            jvmTarget = "11"
            javaParameters = true
        }
    }
}

tasks.withType<Test>().configureEach {
    environment("ENVIRONMENT", "test")
    useJUnitPlatform()
}

protobuf {
    protoc {
        artifact = flink_demo.Dependencies.protoc
    }
}

