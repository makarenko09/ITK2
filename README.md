# Task API Service

Сервис управления задачами с интеграцией Apache Kafka через Apache Camel.

**Стек:** Java 21, Spring Boot 4.0, PostgreSQL, Kafka, Spring JDBC, Apache Camel

## Quick Start

```bash
# Запуск зависимостей
docker compose up -d

# Запуск приложения
./gradlew bootRun

# Проверка API
curl http://localhost:8089/api/tasks

# Swagger UI
open http://localhost:8089/swagger-ui.html
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
- [Health check](http://localhost:8089/actuator/health)

<!-- seed4j-needle-localEnvironment -->

## API Endpoints

| Метод | Endpoint | Описание |
|-------|----------|----------|
| POST | `/api/tasks` | Создание задачи |
| GET | `/api/tasks/{id}` | Получение задачи по ID |
| GET | `/api/tasks?page=0&size=20` | Получение задач с пагинацией |
| PATCH | `/api/tasks/{id}/assignee` | Назначение исполнителя |
| PATCH | `/api/tasks/{id}/status` | Смена статуса |

**Документация API:** [documentation/rest-api-valid.md](documentation/rest-api-valid.md)

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
- [Task API ТЗ](documentation/task-api-tz.md) — соответствие техническому заданию
- [REST API Valid](documentation/rest-api-valid.md) — валидные примеры запросов/ответов
- [Applied Patterns](documentation/applied-patterns.md) — применённые паттерны
- [PostgreSQL](documentation/postgresql.md)
- [Apache Kafka](documentation/apache-kafka.md)
- [Assertions](documentation/assertions.md)
- [Package types](documentation/package-types.md)
- [Logs Spy](documentation/logs-spy.md)
- [CORS configuration](documentation/cors-configuration.md)

<!-- seed4j-needle-documentation -->

## Project Status

### Реализовано ✅
- ✅ Модель данных (Task, User, TaskStatus) — POJO без JPA
- ✅ REST API (5 endpoints)
- ✅ Репозитории на Spring JDBC (JdbcTemplate) — напрямую без JPA
- ✅ Сервисный слой с Assert валидацией
- ✅ Apache Camel маршруты для Kafka
- ✅ Docker конфигурация (PostgreSQL + Kafka)
- ✅ Java 21 toolchain (локально Java 25)
- ✅ Интеграционные тесты (TaskResourceIT)
- ✅ NFT: HikariCP pool (max=20), индексы БД для 100к задач

### Архитектура
- ✅ Hexagonal Architecture (domain/application/infrastructure)
- ✅ Factory Method для создания сущностей
- ✅ Domain Model с бизнес-методами
- ✅ Repository порты в domain

<!-- seed4j-needle-projectStatus -->

## Known Issues

### Тесты
- ✅ Integration тесты для REST API созданы (TaskResourceIT.java)
- ⚠️ Требуется покрытие Cucumber сценариями

**Workaround:** Запускать сборку без тестов:
```bash
./gradlew build -x test
```

## Performance (NFT)

### Целевые показатели
| Параметр | Значение | Реализация |
|----------|----------|------------|
| Пользователи | до 10 000 | HikariCP pool (max=20, min-idle=5) |
| Задачи | до 100 000 | Индексы БД (idx_tasks_status, idx_tasks_assignee) |
| Connection pool | 20 соединений | HikariCP 7.0.2 |

### Индексы БД
```sql
CREATE INDEX idx_tasks_status ON tasks(status);
CREATE INDEX idx_tasks_assignee ON tasks(assignee_id);
CREATE INDEX idx_users_email ON users(email);
```
