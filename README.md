# Task API Service

Сервис управления задачами с интеграцией Apache Kafka через Apache Camel.

## Prerequisites

### SDKMAN

Для управления версиями JDK и Gradle используйте [SDKMAN](https://sdkman.io/):

```bash
curl -s "https://get.sdkman.io" | bash
```

После установки SDKMAN, проект автоматически использует указанные версии из `.sdkmanrc`:

```bash
sdk env
```

### Node.js and NPM

Перед сборкой проекта установите Node.js и npm:

[Node.js](https://nodejs.org/): Используется для запуска dev-сервера и сборки проекта.

После установки Node.js выполните команду для установки зависимостей разработки:

```bash
npm install
```

## Local environment

- [Local server](http://localhost:8089)
- [Local API doc](http://localhost:8089/swagger-ui.html)

<!-- seed4j-needle-localEnvironment -->

## Start up

### 1. Запуск зависимостей (PostgreSQL + Kafka)

```bash
docker compose up -d
```

Или по отдельности:

```bash
docker compose -f src/main/docker/postgresql.yml up -d
docker compose -f src/main/docker/kafka.yml up -d
```

### 2. Запуск приложения

```bash
# Разработка с hot-reload
./gradlew bootRun

# Или сборка и запуск JAR
./gradlew build
java -jar build/libs/task-api-0.0.1-SNAPSHOT.jar
```

### 3. Docker сборка

```bash
# Сборка Docker образа
./gradlew jibDockerBuild

# Запуск контейнера
docker run -p 8089:8089 task-api:latest
```

<!-- seed4j-needle-startupCommand -->

## Documentation

- [Task API Implementation](documentation/task-api-implementation.md)
- [Package types](documentation/package-types.md)
- [Assertions](documentation/assertions.md)
- [PostgreSQL](documentation/postgresql.md)
- [Logs Spy](documentation/logs-spy.md)
- [Apache Kafka](documentation/apache-kafka.md)
- [CORS configuration](documentation/cors-configuration.md)

<!-- seed4j-needle-documentation -->
