import flink_demo.Dependencies
import com.diffplug.gradle.spotless.SpotlessExtension
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
  repositories {
    mavenCentral()
  }

  dependencies {
    classpath(flink_demo.Dependencies.junitGradlePlugin)
    classpath(flink_demo.Dependencies.kotlinGradlePlugin)
    classpath(flink_demo.Dependencies.spotlessPlugin)
    classpath(flink_demo.Dependencies.protobufGradlePlugin)
  }
}

subprojects {
  apply(plugin = "java")
  apply(plugin = "kotlin")
  apply(plugin = "com.diffplug.gradle.spotless")

  buildscript {
    repositories {
      mavenCentral()
    }
  }

  repositories {
    mavenCentral()
  }

  val compileKotlin by tasks.getting(KotlinCompile::class) {
    kotlinOptions {
      jvmTarget = JavaVersion.VERSION_11.toString()
    }
    dependsOn("spotlessKotlinApply")
  }

  val compileTestKotlin by tasks.getting(KotlinCompile::class) {
    kotlinOptions {
      jvmTarget = JavaVersion.VERSION_11.toString()
    }
  }

  tasks.withType<JavaCompile> {
    sourceCompatibility = JavaVersion.VERSION_11.toString()
    targetCompatibility = JavaVersion.VERSION_11.toString()
  }

  configure<SpotlessExtension> {
    kotlin {
      target("**/*.kt")
      ktlint(flink_demo.Dependencies.ktlintVersion).userData(mapOf(
        "indent_size"              to "2",
        "continuation_indent_size" to "2",
        "max_line_length"          to "100",
        "disabled_rules"           to "import-ordering"
      ))
    }
  }

  tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
      events("started", "passed", "skipped", "failed")
      exceptionFormat = TestExceptionFormat.FULL
      showExceptions = true
      showStackTraces = true
    }
  }

  configurations.all {
    exclude(group = "org.apache.logging.log4j", module = "log4j-slf4j-impl")
  }
}
