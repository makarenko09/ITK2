// seed4j-needle-gradle-imports

plugins {
  java
  alias(libs.plugins.jib)
  alias(libs.plugins.spring.boot)
  // seed4j-needle-gradle-plugins
}

// seed4j-needle-gradle-properties

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(21)
  }
}

jib {
  from {
    image = "eclipse-temurin:21-jre-jammy"
    platforms {
      platform {
        architecture = "amd64"
        os = "linux"
      }
    }
  }
  to {
    image = "task-api:latest"
  }
  container {
    entrypoint = listOf("bash", "-c", "/entrypoint.sh")
    ports = listOf("8089")
    environment = mapOf(
     "SPRING_OUTPUT_ANSI_ENABLED" to "ALWAYS",
     "SEED4J_SLEEP" to "0"
    )
    creationTime = "USE_CURRENT_TIMESTAMP"
    user = "1000"
  }
  extraDirectories {
    paths {
      path {
        setFrom("src/main/docker/jib")
      }
    }
    permissions = mapOf("/entrypoint.sh" to "755")
  }
}
defaultTasks("bootRun")

springBoot {
  mainClass = "tech.itk.task.TaskApiApp"
}

// seed4j-needle-gradle-plugins-configurations

repositories {
  mavenCentral()
  maven { url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/") }
  // seed4j-needle-gradle-repositories
}

group = "tech.itk.task"
version = "0.0.1-SNAPSHOT"

val profiles = (project.findProperty("profiles") as String? ?: "")
  .split(",")
  .map { it.trim() }
  .filter { it.isNotEmpty() }
// seed4j-needle-profile-activation

dependencies {
  implementation(libs.commons.lang3)
  implementation(platform(libs.spring.boot.dependencies))
  implementation(libs.spring.boot.starter)
  implementation(libs.spring.boot.configuration.processor)
  implementation(libs.hikariCP)
  implementation(libs.kafka.clients)
  implementation(libs.testcontainers.kafka)
  implementation(libs.spring.boot.starter.validation)
  implementation(libs.spring.boot.starter.webmvc)
  implementation(libs.springdoc.openapi.starter.webmvc.ui)
  implementation(libs.springdoc.openapi.starter.webmvc.api)
  implementation(libs.camel.spring.boot.starter)
  implementation(libs.camel.kafka)
  implementation(libs.camel.jdbc)
  implementation(libs.spring.boot.starter.jdbc)
  implementation(libs.spring.boot.starter.data.jpa)
  // seed4j-needle-gradle-implementation-dependencies
  // seed4j-needle-gradle-compile-dependencies
  runtimeOnly(libs.postgresql)
  // seed4j-needle-gradle-runtime-dependencies
  testImplementation(libs.spring.boot.starter.test)
  testImplementation(libs.testcontainers.testcontainers.postgresql)
  testImplementation(libs.reflections)
  testImplementation(libs.spring.boot.starter.webmvc.test)

  // seed4j-needle-gradle-test-dependencies
}

// seed4j-needle-gradle-free-configuration-blocks

tasks.test {
  filter {
    includeTestsMatching("**Test*")
    excludeTestsMatching("**IT*")
    excludeTestsMatching("**CucumberTest*")
  }
  useJUnitPlatform()
  // seed4j-needle-gradle-tasks-test
}

val test by testing.suites.existing(JvmTestSuite::class)
tasks.register<Test>("integrationTest") {
  description = "Runs integration tests."
  group = "verification"
  shouldRunAfter("test")

  testClassesDirs = files(test.map { it.sources.output.classesDirs })
  classpath = files(test.map { it.sources.runtimeClasspath })

  filter {
    includeTestsMatching("**IT*")
    includeTestsMatching("**CucumberTest*")
  }
  useJUnitPlatform()
}
