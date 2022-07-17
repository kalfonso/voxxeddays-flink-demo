import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc
import flink_demo.Dependencies

plugins {
  application
  id("kotlin")
  id("com.google.protobuf")
  id("com.github.johnrengelman.shadow")
}

dependencies {
  implementation(Dependencies.protobufJava)
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
  implementation(Dependencies.slf4jApi)
  implementation(Dependencies.slf4jLog4j12)

  testImplementation(Dependencies.junitApi)
  testImplementation(Dependencies.assertk)

  testRuntimeOnly(Dependencies.junitEngine)
}

application {
  mainClass.set("com.demo.flink.FraudDetectionAppKt")
}

java {
  sourceCompatibility = JavaVersion.VERSION_11
  targetCompatibility = JavaVersion.VERSION_11
}

sourceSets {
  main {
    java {
      srcDirs("build/generated/source/proto/main/java")
    }
  }
}

protobuf {
  protoc {
    artifact = flink_demo.Dependencies.protoc
  }
}

tasks {
  compileKotlin {
    kotlinOptions {
      // This is required to access static methods in Java interfaces. For instance:
      // org.apache.flink.api.common.eventtime.WatermarkStrategy.forBoundedOutOfOrderness(...)
      jvmTarget = "11"
    }
  }
}

val shadowJar by tasks.getting(com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar::class) {
  exclude("module-info.class") // https://github.com/johnrengelman/shadow/issues/352
  mergeServiceFiles()
  archiveClassifier.set(null as String?)

  // Need to fix mixing conf files when using ShadowJar and Scala dependencies
  // https://stackoverflow.com/questions/34326168/how-can-i-fix-missing-conf-files-when-using-shadowjar-and-scala-dependencies/34326169
  transform(com.github.jengelman.gradle.plugins.shadow.transformers.AppendingTransformer::class.java) {
    resource = "reference.conf"
  }
}


