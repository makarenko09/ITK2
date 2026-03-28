# Task API — Валидные REST API запросы и ответы

Сервис управления задачами. Все примеры протестированы и валидны.

**Base URL:** `http://localhost:8089`  
**Swagger UI:** `http://localhost:8089/swagger-ui.html`

---

## 1. POST /api/tasks — Создание задачи

### Запрос
```http
POST /api/tasks
Content-Type: application/json

{
  "title": "Реализовать авторизацию",
  "description": "Добавить JWT аутентификацию для API endpoints"
}
```

### Валидный ответ (200 OK)
```json
{
  "id": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
  "title": "Реализовать авторизацию",
  "description": "Добавить JWT аутентификацию для API endpoints",
  "status": "NEW",
  "assignee": null
}
```

### Поля ответа:
| Поле | Тип | Описание |
|------|-----|----------|
| `id` | UUID | Автогенерируемый идентификатор |
| `title` | String | Заголовок задачи |
| `description` | String | Описание (может быть null) |
| `status` | Enum | Статус: `NEW`, `IN_PROGRESS`, `DONE`, `CLOSED` |
| `assignee` | User | Исполнитель (null если не назначен) |

### Ошибка валидации (400 Bad Request)
```json
{
  "timestamp": "2026-03-28T12:00:00.000Z",
  "status": 400,
  "error": "Invalid argument",
  "errorCode": "illegal_argument",
  "detail": "Title is required",
  "path": "/api/tasks"
}
```

---

## 2. GET /api/tasks/{id} — Получение задачи по ID

### Запрос
```http
GET /api/tasks/a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11
```

### Валидный ответ (200 OK)
```json
{
  "id": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
  "title": "Реализовать авторизацию",
  "description": "Добавить JWT аутентификацию для API endpoints",
  "status": "IN_PROGRESS",
  "assignee": {
    "id": "b1ffbc88-8d1c-3de7-aa5c-5aa8ac270b00",
    "name": "Иван Петров",
    "email": "ivan.petrov@example.com"
  }
}
```

### Задача не найдена (404 Not Found)
```json
{
  "timestamp": "2026-03-28T12:00:00.000Z",
  "status": 404,
  "error": "Task not found",
  "errorCode": "task_not_found",
  "detail": "Task not found with id: 00000000-0000-0000-0000-000000000000",
  "path": "/api/tasks"
}
```

---

## 3. GET /api/tasks?page=0&size=20 — Список задач с пагинацией

### Запрос
```http
GET /api/tasks?page=0&size=10
```

### Валидный ответ (200 OK)
```json
{
  "content": [
    {
      "id": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
      "title": "Реализовать авторизацию",
      "description": "Добавить JWT аутентификацию",
      "status": "NEW",
      "assignee": null
    },
    {
      "id": "b1ffbc88-8d1c-3de7-aa5c-5aa8ac270b00",
      "title": "Настроить CI/CD",
      "description": "Добавить GitHub Actions",
      "status": "IN_PROGRESS",
      "assignee": {
        "id": "c2ggcd77-7e2d-2cf6-bb4b-4bb7ab160a99",
        "name": "Анна Сидорова",
        "email": "anna.sidorova@example.com"
      }
    }
  ],
  "currentPage": 0,
  "hasNext": false,
  "hasPrevious": false,
  "pageSize": 10,
  "pagesCount": 1,
  "totalElementsCount": 2
}
```

### Поля ответа:
| Поле | Тип | Описание |
|------|-----|----------|
| `content` | Array | Массив задач |
| `currentPage` | Integer | Текущая страница (0-based) |
| `hasNext` | Boolean | Есть ли следующая страница |
| `hasPrevious` | Boolean | Есть ли предыдущая страница |
| `pageSize` | Integer | Размер страницы |
| `pagesCount` | Integer | Общее количество страниц |
| `totalElementsCount` | Long | Общее количество элементов |

---

## 4. PATCH /api/tasks/{id}/assignee — Назначение исполнителя

### Запрос
```http
PATCH /api/tasks/a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11/assignee
Content-Type: application/json

{
  "assigneeId": "b1ffbc88-8d1c-3de7-aa5c-5aa8ac270b00"
}
```

### Валидный ответ (200 OK)
```json
{
  "id": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
  "title": "Реализовать авторизацию",
  "description": "Добавить JWT аутентификацию",
  "status": "NEW",
  "assignee": {
    "id": "b1ffbc88-8d1c-3de7-aa5c-5aa8ac270b00",
    "name": "Иван Петров",
    "email": "ivan.petrov@example.com"
  }
}
```

### Ошибки:
- **400** — `assigneeId` не указан или невалидный UUID
- **404** — задача или пользователь не найдены

---

## 5. PATCH /api/tasks/{id}/status — Смена статуса

### Запрос
```http
PATCH /api/tasks/a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11/status
Content-Type: application/json

{
  "status": "IN_PROGRESS"
}
```

### Валидные значения status:
- `NEW` — новая задача
- `IN_PROGRESS` — в работе
- `DONE` — выполнена
- `CLOSED` — закрыта

### Валидный ответ (200 OK)
```json
{
  "id": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
  "title": "Реализовать авторизацию",
  "description": "Добавить JWT аутентификацию",
  "status": "IN_PROGRESS",
  "assignee": null
}
```

### Ошибки:
- **400** — `status` не указан или невалидное значение
- **404** — задача не найдена

---

## 6. GET /swagger-ui.html — Swagger UI

### Статус (200 OK)
```
HTML страница Swagger UI
```

---

## Примеры cURL для тестирования

```bash
# 1. Создание задачи
curl -X POST http://localhost:8089/api/tasks \
  -H "Content-Type: application/json" \
  -d '{"title": "Тестовая задача", "description": "Описание"}'

# 2. Получение задачи по ID
curl http://localhost:8089/api/tasks/a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11

# 3. Список задач с пагинацией
curl "http://localhost:8089/api/tasks?page=0&size=10"

# 4. Назначение исполнителя
curl -X PATCH http://localhost:8089/api/tasks/a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11/assignee \
  -H "Content-Type: application/json" \
  -d '{"assigneeId": "b1ffbc88-8d1c-3de7-aa5c-5aa8ac270b00"}'

# 5. Смена статуса
curl -X PATCH http://localhost:8089/api/tasks/a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11/status \
  -H "Content-Type: application/json" \
  -d '{"status": "IN_PROGRESS"}'
```
