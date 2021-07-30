import flink_demo.Dependencies
plugins {
  application
  id("kotlin")
}

dependencies {
  implementation(project(":protos"))
  implementation(Dependencies.flinkJava)
  implementation(Dependencies.flinkStreamingJava)
  implementation(Dependencies.protobuf)
  implementation(Dependencies.flinkConnector)
  implementation(Dependencies.flinkConnectorBase)

  testImplementation(Dependencies.junitApi)
  testImplementation(Dependencies.assertk)

  testRuntimeOnly(Dependencies.junitEngine)
}

application {
  mainClassName = "com.demo.flink.FlinkAppKt"
}


