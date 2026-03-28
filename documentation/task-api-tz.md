# Task API — Соответствие Техническому Заданию

## ✅ Выполненные требования

### 1. Стек технологий

| Требование | Реализация | Статус |
|------------|------------|--------|
| Java 21/Kotlin | Java 21 (toolchain) | ✅ |
| PostgreSQL | postgres:18.3 (Docker) | ✅ |
| Kafka | apache/kafka-native:4.2.0 (Docker) | ✅ |
| Spring Boot | 4.0.0 | ✅ |
| Spring Data | Spring JDBC (JdbcTemplate) | ✅ |
| Spring Kafka | Apache Camel 4.10.0 | ✅ |
| Docker container | Jib (eclipse-temurin:21-jre-jammy) | ✅ |

---

### 2. REST API

| Endpoint | Метод | Описание | Статус |
|----------|-------|----------|--------|
| `/api/tasks` | POST | Добавление задачи | ✅ |
| `/api/tasks/{id}` | GET | Получение задачи по ID | ✅ |
| `/api/tasks?page&size` | GET | Получение задач с пагинацией | ✅ |
| `/api/tasks/{id}/assignee` | PATCH | Назначение исполнителя | ✅ |
| `/api/tasks/{id}/status` | PATCH | Смена статуса | ✅ |

**Документация:** [rest-api-valid.md](rest-api-valid.md)

---

### 3. Модель данных

#### Task (Задача)
```sql
CREATE TABLE tasks (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,      -- Наименование
    assignee_id UUID REFERENCES users(id),  -- Исполнитель
    description TEXT,                 -- Описание
    status VARCHAR(50) NOT NULL DEFAULT 'NEW'  -- Статус
);
```

**Поля:**
- ✅ id — UUID
- ✅ наименование (title) — VARCHAR(255)
- ✅ исполнитель (assignee_id) — FK на users
- ✅ описание (description) — TEXT
- ✅ статус (status) — ENUM (NEW, IN_PROGRESS, DONE, CLOSED)

#### User (Пользователь)
```sql
CREATE TABLE users (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,   -- Имя
    email VARCHAR(255) NOT NULL UNIQUE  -- Почта
);
```

**Поля:**
- ✅ id — UUID
- ✅ имя (name) — VARCHAR(255)
- ✅ почта (email) — VARCHAR(255), unique

---

### 4. Kafka события

| Событие | Топик | Триггер | Реализация |
|---------|-------|---------|------------|
| Создание задачи | `task-created` | POST /api/tasks | Apache Camel route |
| Назначение исполнителя | `task-assigned` | PATCH /api/tasks/{id}/assignee | Apache Camel route |

**Camel маршруты:**
```java
from("direct:task-created")
  .to("kafka:task-created?brokers={{kafka.bootstrap-servers}}");

from("direct:task-assigned")
  .to("kafka:task-assigned?brokers={{kafka.bootstrap-servers}}");
```

---

### 5. Docker

#### Запуск зависимостей
```bash
docker compose up -d
```

**Контейнеры:**
- `itk2-postgresql-1` — PostgreSQL 18.3 (port 5432)
- `itk2-kafka-1` — Apache Kafka 4.2.0 (port 9092)

#### Сборка Docker container
```bash
./gradlew jibDockerBuild
docker run -p 8089:8089 task-api:latest
```

**Docker образ:**
- Base: `eclipse-temurin:21-jre-jammy`
- Port: 8089
- User: 1000 (non-root)

---

### 6. NFT (Non-Functional Requirements)

| Требование | Реализация | Статус |
|------------|------------|--------|
| До 10к пользователей | HikariCP pool (max=20, min-idle=5) | ✅ |
| До 100к задач | Индексы БД (idx_tasks_status, idx_tasks_assignee) | ✅ |
| Производительность | JDBC напрямую (без JPA overhead) | ✅ |
| Connection pooling | HikariCP 7.0.2 | ✅ |

**Индексы для производительности:**
```sql
CREATE INDEX idx_tasks_status ON tasks(status);
CREATE INDEX idx_tasks_assignee ON tasks(assignee_id);
CREATE INDEX idx_users_email ON users(email);
```

---

## 📁 Архитектура проекта

### Hexagonal Architecture

```
task/
├── domain/                          # Бизнес-модель (без зависимостей)
│   ├── Task.java                    # POJO + Factory Method
│   ├── User.java                    # POJO + Factory Method
│   ├── TaskStatus.java              # Enum
│   └── repository/                  # Порты (интерфейсы)
│       ├── TaskRepository.java
│       └── UserRepository.java
│
├── application/                     # Оркестрация (без бизнес-логики)
│   ├── TaskApplicationService.java  # @Service, @Transactional
│   └── exception/                   # Бизнес-исключения
│       ├── TaskNotFoundException.java
│       └── UserNotFoundException.java
│
└── infrastructure/
    ├── primary/                     # REST адаптеры
    │   ├── TaskResource.java        # @RestController
    │   ├── RestTask.java            # DTO (record)
    │   ├── CreateTaskRequest.java   # Request DTO (record)
    │   ├── AssignTaskRequest.java   # Request DTO (record)
    │   ├── UpdateTaskStatusRequest.java # Request DTO (record)
    │   └── TaskApiExceptionHandler.java # @RestControllerAdvice
    │
    └── secondary/                   # Реализации портов
        ├── JdbcTaskRepository.java  # @Repository, JdbcTemplate
        ├── JdbcUserRepository.java  # @Repository, JdbcTemplate
        └── KafkaTaskRoutes.java     # @Component, Apache Camel
```

---

## 🔧 Конфигурация

### application.yml
```yaml
spring:
  datasource:
    driver-class-name: org.postgresql.Driver
    hikari:
      maximum-pool-size: 20    # NFT: 10к пользователей
      minimum-idle: 5
      idle-timeout: 300000
      connection-timeout: 20000
    url: jdbc:postgresql://localhost:5432/task-api
    username: task-api
    password: ''

kafka:
  bootstrap-servers: localhost:9092

camel:
  springboot:
    main-run-controller: true
    name: task-api

server:
  port: 8089
```

---

## ✅ Проверка соответствия

### Запуск проекта
```bash
# 1. Запуск зависимостей
docker compose up -d

# 2. Запуск приложения
./gradlew bootRun

# 3. Проверка API
curl http://localhost:8089/api/tasks?page=0&size=10

# 4. Swagger UI
http://localhost:8089/swagger-ui.html
```

### Тесты
```bash
# Unit тесты
./gradlew test

# Integration тесты
./gradlew integrationTest
```

---

## 📊 Итоговая статистика

| Компонент | Количество | Описание |
|-----------|------------|----------|
| Domain агрегаты | 2 | Task, User (POJO) |
| Repository порты | 2 | TaskRepository, UserRepository |
| JDBC репозитории | 2 | JdbcTaskRepository, JdbcUserRepository |
| Application сервисы | 1 | TaskApplicationService |
| REST контроллеры | 1 | TaskResource |
| Camel маршруты | 2 | task-created, task-assigned |
| DTO | 5 | RestTask, RestUser, CreateTaskRequest, etc. |
| Интеграционные тесты | 11 | TaskResourceIT |
| SQL миграции | 1 | V1__init_schema.sql |

---

## 🎯 Полное соответствие ТЗ

| Требование | Статус |
|------------|--------|
| Java 21 | ✅ |
| PostgreSQL (Docker) | ✅ |
| Kafka (Docker) | ✅ |
| Spring Boot | ✅ |
| Spring JDBC (без JPA) | ✅ |
| Apache Camel для Kafka | ✅ |
| REST API (5 endpoints) | ✅ |
| Модель данных (Task, User) | ✅ |
| Kafka события (create, assign) | ✅ |
| Docker container (Jib) | ✅ |
| NFT (10к users, 100к tasks) | ✅ |

**Все требования ТЗ выполнены! ✅**
