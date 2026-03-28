# PostgreSQL

Реляционная база данных для хранения задач и пользователей.

## Быстрый старт

### 1. Запуск PostgreSQL

Перед запуском приложения выполните:

```bash
docker compose -f src/main/docker/postgresql.yml up -d
```

Или запустите все зависимости сразу:

```bash
docker compose up -d
```

### 2. Подключение к базе

```bash
docker exec -it postgresql psql -U task-api -d task-api
```

## Конфигурация

| Параметр | Значение |
|----------|----------|
| Host | localhost |
| Port | 5432 |
| Database | task-api |
| Username | task-api |
| Password | (пустой) |

## Документация

- [Task API Implementation](task-api-implementation.md) — модель данных и схема БД
- [README](../README.md) — основной README проекта
