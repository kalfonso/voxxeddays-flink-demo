import flink_demo.Dependencies
plugins {
  application
  id("kotlin")
}

dependencies {
  implementation(project(":protos"))

  testImplementation(Dependencies.junitApi)
  testImplementation(Dependencies.assertk)

  testRuntimeOnly(Dependencies.junitEngine)
}

application {
  mainClassName = "com.demo.flink.FlinkAppKt"
}


