# Task API Implementation Report (ITK2)

Сервис управления задачами с интеграцией Apache Kafka через Apache Camel.

**Последнее обновление:** 28.03.2026

## Навигация по документации

- [README](../README.md) — основной README проекта
- [Task API ТЗ](task-api-tz.md) — соответствие техническому заданию
- [REST API Valid](rest-api-valid.md) — валидные примеры запросов/ответов
- [Applied Patterns](applied-patterns.md) — применённые паттерны
- [PostgreSQL](postgresql.md) — настройка и использование БД
- [Apache Kafka](apache-kafka.md) — настройка и использование Kafka
- [Assertions](assertions.md) — утилиты валидации
- [Package types](package-types.md) — аннотации пакетов
- [Logs Spy](logs-spy.md) — тестирование логов
- [CORS configuration](cors-configuration.md) — CORS настройка
- [Hexagonal Architecture](origin/hexagonal-architecture.md) — архитектура проекта

---

## Обзор реализации

Реализован **Task API Service** — сервис управления задачами с интеграцией Apache Kafka через Apache Camel.

**Ключевые изменения (28.03.2026):**
- ✅ Spring Data JPA для репозиториев (автоматическая генерация CRUD)
- ✅ Hibernate auto DDL (таблицы создаются автоматически)
- ✅ Интеграционные тесты для REST API (11 тестов)
- ✅ Factory Method паттерн для создания сущностей
- ✅ Domain Model с бизнес-методами
- ✅ Hexagonal Architecture

## Технические характеристики

### Стек технологий
| Компонент | Версия | Описание |
|-----------|--------|----------|
| Java | 21 (toolchain) | Компиляция под Java 21, локально Java 25 |
| Spring Boot | 4.0.0 | Фреймворк для приложения |
| Apache Camel | 4.10.0 | Интеграция с Kafka |
| Spring Data JPA | 4.0.0 | Автоматическая генерация репозиториев |
| Hibernate | 6.6.13.Final | ORM для auto DDL |
| HikariCP | 7.0.2 | Connection pooling |
| PostgreSQL | 18.3 | Реляционная БД |
| Apache Kafka | 4.2.0 | Брокер сообщений |
| Gradle | 9.4.1 | Система сборки |
| SDKMAN | - | Управление версиями JDK/Gradle |

### Примечание по версии Java
Проект использует **Java toolchain** для компиляции под Java 21, при этом локальная разработка ведётся на Java 25:
- `build.gradle.kts`: `languageVersion = JavaLanguageVersion.of(21)`
- `.sdkmanrc`: `java=25.0.2-open` (локальная среда разработки)
- JIB Docker image: `eclipse-temurin:21-jre-jammy`

### Архитектурные решения

#### 1. Spring Data JPA + Hibernate
Использован подход **Spring Data JPA** для автоматической генерации репозиториев и **Hibernate** для auto DDL:
- ✅ JPA Entity с аннотациями (@Entity, @Table)
- ✅ Spring Data JPA репозитории (extends JpaRepository)
- ✅ hibernate.hbm2ddl.auto=update (автоматическое создание таблиц)
- ✅ Автоматическая генерация CRUD операций

**Обоснование:**
- Минимум кода для репозиториев
- Автоматическое создание таблиц при запуске
- Прозрачное управление транзакциями
- Легкая миграция на production с Flyway/Liquibase

**Пример Spring Data JPA репозитория:**
```java
@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
  // CRUD операции генерируются автоматически
}
```

**Конфигурация Hibernate:**
```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
    show-sql: true
```

#### 2. Apache Camel для Kafka
Использован Apache Camel для автоматизации отправки событий в Kafka:
```java
@Component
public class KafkaTaskRoutes extends RouteBuilder {

  @Override
  public void configure() {
    from("direct:task-created")
      .to("kafka:task-created?brokers={{kafka.bootstrap-servers}}");

    from("direct:task-assigned")
      .to("kafka:task-assigned?brokers={{kafka.bootstrap-servers}}");
  }
}
```

**Преимущества:**
- Декларативная конфигурация маршрутов
- Встроенная поддержка error handling
- Упрощённое тестирование
- Автоматическое создание топиков

#### 3. Factory Method паттерн
Создание сущностей через Factory Method:
```java
public class Task {

  public static Task create(String title, String description) {
    Task task = new Task();
    task.title = title;
    task.description = description;
    task.status = TaskStatus.NEW;  // Гарантированный начальный статус
    return task;
  }

  public void assignTo(User user) {
    if (user == null) {
      throw new IllegalArgumentException("Assignee cannot be null");
    }
    this.assignee = user;
  }

  public void changeStatus(TaskStatus status) {
    if (status == null) {
      throw new IllegalArgumentException("Status cannot be null");
    }
    this.status = status;
  }
}
```

#### 4. Валидация через Assert
Вместо отдельных исключений используется `Assert` из `shared.error.domain`:
```java
Assert.field("title", title).notBlank();
Assert.notNull("id", id);
```

#### 5. Пагинация
Использована существующая пагинация проекта:
- `Seed4jSampleApplicationPage<T>` — domain
- `Seed4jSampleApplicationPageable` — domain
- `RestSeed4jSampleApplicationPage<T>` — REST wrapper
- `RestSeed4jSampleApplicationPageable` — REST DTO с валидацией

#### 6. Профиль local
Конфигурация по умолчанию использует профиль `local`:
```yaml
spring:
  profiles:
    active: local
```

## Модель данных

### Task (Задача)
```sql
CREATE TABLE tasks (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'NEW',
    assignee_id UUID REFERENCES users(id)
);
```

**Поля:**
- `id` — UUID, первичный ключ
- `title` — наименование задачи
- `description` — описание
- `status` — статус (NEW, IN_PROGRESS, DONE, CLOSED)
- `assignee_id` — ссылка на исполнителя

### User (Пользователь)
```sql
CREATE TABLE users (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE
);
```

**Поля:**
- `id` — UUID, первичный ключ
- `name` — имя пользователя
- `email` — уникальный email

## REST API

### Endpoints

| Метод | Endpoint | Описание | Статус |
|-------|----------|----------|--------|
| POST | `/api/tasks` | Создание задачи | ✅ |
| GET | `/api/tasks/{id}` | Получение задачи по ID | ✅ |
| GET | `/api/tasks?page=0&size=20` | Получение задач с пагинацией | ✅ |
| PATCH | `/api/tasks/{id}/assignee` | Назначение исполнителя | ✅ |
| PATCH | `/api/tasks/{id}/status` | Смена статуса | ✅ |

### Примеры запросов

#### Создание задачи
```bash
curl -X POST http://localhost:8089/api/tasks \
  -H "Content-Type: application/json" \
  -d '{"title": "Новая задача", "description": "Описание задачи"}'
```

#### Получение задач с пагинацией
```bash
curl "http://localhost:8089/api/tasks?page=0&size=10"
```

#### Назначение исполнителя
```bash
curl -X PATCH http://localhost:8089/api/tasks/{id}/assignee \
  -H "Content-Type: application/json" \
  -d '{"assigneeId": "uuid-исполнителя"}'
```

#### Смена статуса
```bash
curl -X PATCH http://localhost:8089/api/tasks/{id}/status \
  -H "Content-Type: application/json" \
  -d '{"status": "IN_PROGRESS"}'
```

## Kafka события

### Топики
| Топик | Описание | Триггер |
|-------|----------|---------|
| `task-created` | Создание новой задачи | POST /api/tasks |
| `task-assigned` | Назначение исполнителя | PATCH /api/tasks/{id}/assignee |

### Формат сообщений

**task-created:**
```json
{
  "taskId": "uuid",
  "title": "Наименование задачи",
  "status": "NEW"
}
```

**task-assigned:**
```json
{
  "taskId": "uuid",
  "assigneeId": "uuid",
  "assigneeEmail": "email@example.com"
}
```

## Структура проекта

```
src/main/java/tech/itk/task/
├── TaskApiApp.java                    # Главный класс
├── ApplicationStartupTraces.java      # Трейсы запуска
├── task/
│   ├── domain/
│   │   ├── Task.java                  # Entity
│   │   ├── User.java                  # Entity
│   │   ├── TaskStatus.java            # Enum
│   │   └── repository/
│   │       ├── TaskRepository.java    # Hibernate SessionFactory
│   │       └── UserRepository.java    # Hibernate SessionFactory
│   ├── application/
│   │   └── TaskApplicationService.java  # Бизнес-логика
│   └── infrastructure/primary/
│       ├── TaskResource.java          # REST Controller
│       ├── RestTask.java              # DTO
│       ├── RestUser.java              # DTO
│       ├── CreateTaskRequest.java     # Request DTO
│       ├── AssignTaskRequest.java     # Request DTO
│       └── UpdateTaskStatusRequest.java # Request DTO
├── shared/
│   ├── pagination/
│   │   ├── domain/
│   │   │   ├── Seed4jSampleApplicationPage.java
│   │   │   └── Seed4jSampleApplicationPageable.java
│   │   └── infrastructure/primary/
│   │       ├── RestSeed4jSampleApplicationPage.java
│   │       └── RestSeed4jSampleApplicationPageable.java
│   ├── error/domain/
│   │   └── Assert.java
│   ├── collection/domain/
│   └── generation/domain/
└── wire/
    └── camel/
        └── KafkaTaskRoutes.java       # Apache Camel routes
```

## Конфигурация

### application.yml
```yaml
spring:
  application:
    name: task-api
  profiles:
    active: local
  datasource:
    driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://localhost:5432/task-api
    username: task-api
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
    show-sql: true

kafka:
  bootstrap-servers: localhost:9092

camel:
  springboot:
    main-run-controller: true
    name: task-api

server:
  port: 8089
```

### application-local.yml
```yaml
logging:
  level:
    tech:
      itk:
        task: DEBUG
```

### kafka.yml (Docker)
```yaml
KAFKA_AUTO_CREATE_TOPICS_ENABLE: 'true'
```

## Быстрый запуск

### 1. Запуск зависимостей
```bash
# PostgreSQL и Kafka
docker compose up -d
```

### 2. Запуск приложения
```bash
# Разработка
./gradlew bootRun

# Или сборка и запуск
./gradlew build
java -jar build/libs/*.jar
```

### 3. Docker сборка
```bash
# Сборка образа
./gradlew jibDockerBuild

# Запуск контейнера
docker run -p 8089:8089 task-api:latest
```

### 4. Проверка работы
```bash
# Swagger UI
http://localhost:8089/swagger-ui.html

# Health check
curl http://localhost:8089/management/health
```

## Производительность (NFT)

### Целевые показатели
| Параметр | Значение |
|----------|----------|
| Пользователи | до 10 000 |
| Задачи | до 100 000 |

### Рекомендации для продакшена
1. **Индексы БД:**
   ```sql
   CREATE INDEX idx_tasks_status ON tasks(status);
   CREATE INDEX idx_tasks_assignee ON tasks(assignee_id);
   CREATE INDEX idx_users_email ON users(email);
   ```

2. **Пул соединений HikariCP:**
   - Увеличить `maximum-pool-size` до 20-50
   - Настроить `connection-timeout`

3. **Kafka:**
   - Увеличить репликацию для продакшена
   - Настроить retention policy

## Отличия от seed4j генерации

| Компонент | seed4j | Реализация ITK2 |
|-----------|--------|-----------------|
| ORM | Spring Data JPA | Hibernate напрямую (SessionFactory) |
| Репозитории | JpaRepository | SessionFactory |
| Kafka | Spring Kafka | Apache Camel |
| Java версия | 25 | 21 (toolchain) |
| Порт | 8080 | 8089 |
| Пакет | com.mycompany.myapp | tech.itk.task |
| Пагинация | Seed4jSampleApplicationPage | Seed4jSampleApplicationPage |
| Исключения | Отдельные классы | Assert.field() |

## Конфигурация сборки

### .sdkmanrc
```properties
java=25.0.2-open
gradle=/home/DEV/.sdkman/candidates/gradle/9.3.0
```

### build.gradle.kts (фрагменты)
```kotlin
java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(21)
  }
}

jib {
  from {
    image = "eclipse-temurin:21-jre-jammy"
  }
  to {
    image = "task-api:latest"
  }
  container {
    ports = listOf("8089")
  }
}

springBoot {
  mainClass = "tech.itk.task.TaskApiApp"
}

dependencies {
  implementation(libs.hibernate.core)
  implementation(libs.camel.spring.boot.starter)
  implementation(libs.camel.kafka)
  implementation(libs.camel.jdbc)
  // ...
}
```

### libs.versions.toml
```toml
[versions]
  hibernate = "6.6.13.Final"
  camel = "4.10.0"
```

## Заключение

Реализация соответствует требованиям ТЗ с следующими особенностями:

### Выполнено ✅
- ✅ Java 21 (toolchain), локально Java 25
- ✅ PostgreSQL + Hibernate (auto DDL)
- ✅ REST API с пагинацией (5 endpoints)
- ✅ Модель данных (Task, User)
- ✅ Репозитории на основе SessionFactory
- ✅ Apache Camel маршруты для Kafka
- ✅ Отправка событий `task-created` и `task-assigned`
- ✅ Docker конфигурация (docker-compose)
- ✅ Валидация через Assert

### Требует реализации ⏳
- 🔲 Сборка Docker образа через Jib
- 🔲 Тесты (Unit, Integration, Cucumber)

### Рекомендации
1. Добавить индексы БД для производительности
2. Настроить connection pool для продакшена
3. Добавить тесты с Testcontainers
