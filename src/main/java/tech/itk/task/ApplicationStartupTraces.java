package tech.itk.task;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import tech.itk.task.shared.error.domain.Assert;
import tech.itk.task.shared.generation.domain.ExcludeFromGeneratedCodeCoverage;

final class ApplicationStartupTraces {

  private static final String SEPARATOR = "-".repeat(58);
  private static final String BREAK = "\n";

  private static final Logger log = LoggerFactory.getLogger(ApplicationStartupTraces.class);

  private ApplicationStartupTraces() {}

  static String of(Environment environment) {
    Assert.notNull("environment", environment);

    return new ApplicationStartupTracesBuilder()
      .append(BREAK)
      .appendSeparator()
      .append(applicationRunningTrace(environment))
      .append(localUrl(environment))
      .append(externalUrl(environment))
      .append(profilesTrace(environment))
      .appendSeparator()
      .append(configServer(environment))
      .build();
  }

  private static String applicationRunningTrace(Environment environment) {
    String applicationName = environment.getProperty("spring.application.name");

    if (StringUtils.isBlank(applicationName)) {
      return "Application is running!";
    }

    return new StringBuilder().append("Application '").append(applicationName).append("' is running!").toString();
  }

  private static String localUrl(Environment environment) {
    return url("Local", "localhost", environment);
  }

  private static String externalUrl(Environment environment) {
    try {
      return url("External", InetAddress.getLocalHost().getHostAddress(), environment);
    } catch (UnknownHostException e) {
      log.warn("The host name could not be determined, using `localhost` as fallback");
      return url("External", "localhost", environment);
    }
  }

  private static String url(String type, String address, Environment environment) {
    String serverPort = environment.getProperty("server.port");
    String contextPath = contextPath(environment);

    if (StringUtils.isBlank(serverPort)) {
      return "";
    }

    return new StringBuilder()
      .append(BREAK)
      .append(type)
      .append(": \t")
      .append(protocol(environment))
      .append("://")
      .append(address)
      .append(":")
      .append(serverPort)
      .append(contextPath)
      .toString();
  }

  private static String protocol(Environment environment) {
    if (ArrayUtils.contains(environment.getActiveProfiles(), "ssl")) {
      return "https";
    }

    return "http";
  }

  private static String configServer(Environment environment) {
    String configServerStatus = environment.getProperty("config.client.status");

    if (StringUtils.isNotBlank(configServerStatus)) {
      return new StringBuilder().append(BREAK).append("Config Server: \t").append(configServerStatus).toString();
    }

    return "";
  }

  private static String profilesTrace(Environment environment) {
    String[] profiles = environment.getActiveProfiles();

    if (profiles.length == 0) {
      return "";
    }

    return new StringBuilder().append(BREAK).append("Profile: \t").append(String.join(", ", profiles)).toString();
  }

  private static String contextPath(Environment environment) {
    String contextPath = environment.getProperty("server.servlet.context-path");

    if (StringUtils.isBlank(contextPath)) {
      return "";
    }

    return contextPath;
  }

  @ExcludeFromGeneratedCodeCoverage(reason = "Hard to test implement detail error management")
  private static String hostAddress() {
    try {
      return InetAddress.getLocalHost().getHostAddress();
    } catch (UnknownHostException e) {
      log.warn("The host name could not be determined, using `localhost` as fallback");
    }

    return "localhost";
  }

  private static final class ApplicationStartupTracesBuilder {

    private final StringBuilder builder = new StringBuilder();

    ApplicationStartupTracesBuilder append(String trace) {
      if (StringUtils.isNotBlank(trace)) {
        builder.append(trace);
      }

      return this;
    }

    ApplicationStartupTracesBuilder appendSeparator() {
      builder.append(SEPARATOR).append(BREAK);

      return this;
    }

    String build() {
      return builder.toString();
    }
  }
}
