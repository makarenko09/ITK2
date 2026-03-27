// seed4j-needle-gradle-imports

plugins {
  java
  alias(libs.plugins.jib)
  // seed4j-needle-gradle-plugins
}

// seed4j-needle-gradle-properties

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(25)
  }
}

jib {
  from {
    image = "eclipse-temurin:25-jre-jammy"
    platforms {
      platform {
        architecture = "amd64"
        os = "linux"
      }
    }
  }
  to {
    image = "seed4jSampleApplication:latest"
  }
  container {
    entrypoint = listOf("bash", "-c", "/entrypoint.sh")
    ports = listOf("8080")
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
// seed4j-needle-gradle-plugins-configurations

repositories {
  mavenCentral()
  // seed4j-needle-gradle-repositories
}

group = "com.mycompany.myapp"
version = "0.0.1-SNAPSHOT"

val profiles = (project.findProperty("profiles") as String? ?: "")
  .split(",")
  .map { it.trim() }
  .filter { it.isNotEmpty() }
// seed4j-needle-profile-activation

dependencies {
  // seed4j-needle-gradle-implementation-dependencies
  // seed4j-needle-gradle-compile-dependencies
  // seed4j-needle-gradle-runtime-dependencies
  testImplementation(libs.junit.engine)
  testImplementation(libs.junit.params)
  testImplementation(libs.assertj)
  testImplementation(libs.mockito)
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
