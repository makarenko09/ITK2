# Task API Service

Сервис управления задачами с интеграцией Apache Kafka через Apache Camel.

## Quick Start

```bash
# Запуск зависимостей
docker compose up -d

# Запуск приложения
./gradlew bootRun

# Проверка API
curl http://localhost:8089/api/tasks
```

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

## Local environment

- [Local server](http://localhost:8089)
- [Local API doc](http://localhost:8089/swagger-ui.html)
- [Health check](http://localhost:8089/management/health)

<!-- seed4j-needle-localEnvironment -->

## API Endpoints

| Метод | Endpoint | Описание |
|-------|----------|----------|
| POST | `/api/tasks` | Создание задачи |
| GET | `/api/tasks/{id}` | Получение задачи по ID |
| GET | `/api/tasks?page=0&size=20` | Получение задач с пагинацией |
| PATCH | `/api/tasks/{id}/assignee` | Назначение исполнителя |
| PATCH | `/api/tasks/{id}/status` | Смена статуса |

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

- [Task API Implementation](documentation/task-api-implementation.md) — основная документация
- [PostgreSQL](documentation/postgresql.md)
- [Apache Kafka](documentation/apache-kafka.md)
- [Assertions](documentation/assertions.md)
- [Package types](documentation/package-types.md)
- [Logs Spy](documentation/logs-spy.md)
- [CORS configuration](documentation/cors-configuration.md)

<!-- seed4j-needle-documentation -->

## Project Status

### Реализовано ✅
- ✅ Модель данных (Task, User, TaskStatus)
- ✅ REST API (5 endpoints)
- ✅ Репозитории на Hibernate SessionFactory (без Spring Data JPA)
- ✅ Сервисный слой с Assert валидацией
- ✅ Apache Camel маршруты для Kafka
- ✅ Docker конфигурация (PostgreSQL + Kafka)
- ✅ Java 21 toolchain (локально Java 25)

### Тесты ⚠️
- ✅ Тестовые классы перенесены в `tech.itk.task`
- ✅ LogsSpy для тестирования логов
- ⚠️ Требуется создание тестов для Task API

<!-- seed4j-needle-projectStatus -->

## Known Issues

### Тесты
Некоторые тесты требуют обновления для новой архитектуры:
- Тесты для несуществующих классов удалены
- Требуется написание интеграционных тестов для REST API

**Workaround:** Запускать сборку без тестов:
```bash
./gradlew build -x test
```
