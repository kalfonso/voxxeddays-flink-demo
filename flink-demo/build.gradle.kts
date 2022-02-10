import flink_demo.Dependencies
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

  testImplementation(Dependencies.junitApi)
  testImplementation(Dependencies.assertk)

  testRuntimeOnly(Dependencies.junitEngine)
}

application {
  mainClass.set("com.demo.flink.FlinkAppKt")
}


