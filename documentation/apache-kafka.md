# Apache Kafka

Интеграция с Apache Kafka через Apache Camel для отправки событий о задачах.

## Быстрый старт

### 1. Запуск Kafka

Перед запуском приложения выполните:

```bash
docker compose -f src/main/docker/kafka.yml up -d
```

Или запустите все зависимости сразу:

```bash
docker compose up -d
```

### 2. Проверка работы

После создания задачи через REST API, событие отправляется в топик `task-created`:

```bash
curl -X POST http://localhost:8089/api/tasks \
  -H "Content-Type: application/json" \
  -d '{"title": "Тест", "description": "Описание"}'
```

## Документация

- [Task API Implementation](task-api-implementation.md) — полная документация реализации
- [README](../README.md) — основной README проекта
