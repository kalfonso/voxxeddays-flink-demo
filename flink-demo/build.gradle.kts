import flink_demo.Dependencies
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  application
  id("kotlin")
}

dependencies {
  implementation(project(":protos"))
  implementation(Dependencies.protobufJavaUtil)
  // implementation(Dependencies.flinkJava)
  // implementation(Dependencies.flinkStreamingJava)
  implementation(Dependencies.flinkClients)
  implementation(Dependencies.protobuf)
  implementation(Dependencies.flinkConnector)
  implementation(Dependencies.flinkConnectorBase)
  implementation(Dependencies.flinkCore)
  implementation(Dependencies.kafkaTools)
  implementation(Dependencies.flinkProtobufSerializer) {
    exclude(group = "com.esotericsoftware.cryo", module = "kryo")
  }

  testImplementation(Dependencies.junitApi)
  testImplementation(Dependencies.assertk)

  testRuntimeOnly(Dependencies.junitEngine)
}

application {
  mainClass.set("com.demo.flink.FlinkAppKt")
}

tasks {
  compileKotlin {
    kotlinOptions {
      // This is required to access static methods in Java interfaces. For instance:
      // org.apache.flink.api.common.eventtime.WatermarkStrategy.forBoundedOutOfOrderness(...)
      jvmTarget = "1.8"
    }
  }
}


