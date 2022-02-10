import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc
import flink_demo.Dependencies

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("com.google.protobuf")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(flink_demo.Dependencies.protobufJavaUtil)
}

protobuf {
    protoc {
        artifact = flink_demo.Dependencies.protoc
    }
}

