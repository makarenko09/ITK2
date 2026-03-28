# Применённые паттерны проектирования

Согласовано с [hexagonal architecture](origin/hexagonal-architecture.md) и практиками seed4j.

---

## 📋 Обзор

| Паттерн | Статус | Файлы |
|---------|--------|-------|
| Factory Method | ✅ | `Task.java`, `User.java` |
| Domain Model | ✅ | `Task.java`, `User.java` |
| Repository | ✅ | `TaskRepository.java`, `UserRepository.java` |
| Service Layer | ✅ | `TaskApplicationService.java` |
| Dependency Injection | ✅ | Все сервисы через конструктор |
| Exception Handler | ✅ | `TaskApiExceptionHandler.java` |
| DTO | ✅ | `RestTask.java`, `CreateTaskRequest.java`, etc. |

---

## 1. Factory Method

**Цель:** Инкапсуляция создания сущностей, гарантия корректного начального состояния.

### Task.java
```java
/**
 * Factory Method для создания новой задачи.
 */
public static Task create(String title, String description) {
  Task task = new Task();
  task.title = title;
  task.description = description;
  task.status = TaskStatus.NEW;  // Гарантированный начальный статус
  return task;
}
```

### User.java
```java
/**
 * Factory Method для создания нового пользователя.
 */
public static User create(String name, String email) {
  User user = new User();
  user.name = name;
  user.email = email;
  return user;
}
```

**Преимущества:**
- ✅ Гарантированное начальное состояние (`status = NEW`)
- ✅ Скрытая логика инициализации
- ✅ Упрощённый код в `TaskApplicationService`

---

## 2. Domain Model

**Цель:** Бизнес-логика в доменной модели, а не в сервисах.

### Task.java — Бизнес-методы
```java
/**
 * Бизнес-метод: назначить исполнителя.
 */
public void assignTo(User user) {
  if (user == null) {
    throw new IllegalArgumentException("Assignee cannot be null");
  }
  this.assignee = user;
}

/**
 * Бизнес-метод: изменить статус.
 */
public void changeStatus(TaskStatus status) {
  if (status == null) {
    throw new IllegalArgumentException("Status cannot be null");
  }
  this.status = status;
}
```

**Преимущества:**
- ✅ Бизнес-правила в domain (не в application)
- ✅ Самодостаточная модель
- ✅ Легко тестировать

---

## 3. Repository (Порт)

**Цель:** Абстракция доступа к данным, инверсия зависимостей.

### domain/repository/TaskRepository.java
```java
public interface TaskRepository {
  Task save(Task task);
  Optional<Task> findById(UUID id);
  List<Task> findAll(int offset, int limit);
  long count();
  void deleteById(UUID id);
}
```

**Преимущества:**
- ✅ Domain не зависит от инфраструктуры
- ✅ Легко заменить реализацию (Hibernate → JDBC → In-Memory)
- ✅ Упрощённое тестирование (mock)

---

## 4. Service Layer (Оркестрация)

**Цель:** Координация между портами, без бизнес-логики.

### TaskApplicationService.java
```java
@Service
@Transactional
public class TaskApplicationService {

  public Task createTask(String title, String description) {
    Assert.field("title", title).notBlank();
    
    // Factory Method
    Task task = Task.create(title, description);
    Task saved = taskRepository.save(task);
    
    // Dispatch event через порт
    producerTemplate.sendBody("direct:task-created", Map.of(
      "taskId", saved.getId(),
      "title", saved.getTitle(),
      "status", saved.getStatus().name()
    ));
    
    return saved;
  }
}
```

**Преимущества:**
- ✅ Только оркестрация (нет бизнес-логики)
- ✅ Транзакции на уровне сервиса
- ✅ Отправка событий через порты

---

## 5. Dependency Injection

**Цель:** Явные зависимости, инверсия управления.

### Конструкторная инъекция (все классы)
```java
@Service
public class TaskApplicationService {

  private final TaskRepository taskRepository;
  private final UserRepository userRepository;
  private final ProducerTemplate producerTemplate;

  public TaskApplicationService(
    TaskRepository taskRepository,
    UserRepository userRepository,
    ProducerTemplate producerTemplate
  ) {
    this.taskRepository = taskRepository;
    this.userRepository = userRepository;
    this.producerTemplate = producerTemplate;
  }
}
```

**Преимущества:**
- ✅ Явные зависимости
- ✅ Immutable поля (`final`)
- ✅ Легко тестировать (конструктор)

---

## 6. Exception Handler

**Цель:** Централизованная обработка ошибок REST API.

### TaskApiExceptionHandler.java
```java
@RestControllerAdvice(basePackages = "tech.itk.task.task.infrastructure.primary")
public class TaskApiExceptionHandler {

  @ExceptionHandler(TaskNotFoundException.class)
  public ResponseEntity<Map<String, Object>> handleTaskNotFound(TaskNotFoundException ex) {
    log.warn("Task not found: {}", ex.getMessage());
    return buildErrorResponse(
      HttpStatus.NOT_FOUND,
      "Task not found",
      ex.getMessage(),
      "task_not_found"
    );
  }
}
```

**Преимущества:**
- ✅ Единый формат ошибок
- ✅ Логирование в одном месте
- ✅ Понятные ответы клиенту

---

## 7. DTO (Data Transfer Object)

**Цель:** Разделение domain модели и API контракта.

### infrastructure/primary/RestTask.java
```java
public record RestTask(
  UUID id,
  String title,
  String description,
  TaskStatus status,
  RestUser assignee
) {
  public static RestTask from(Task task) {
    return new RestTask(
      task.getId(),
      task.getTitle(),
      task.getDescription(),
      task.getStatus(),
      task.getAssignee() != null ? RestUser.from(task.getAssignee()) : null
    );
  }
}
```

**Преимущества:**
- ✅ Domain модель скрыта от клиента
- ✅ Контроль над API контрактом
- ✅ Record для immutability

---

## 8. Hexagonal Architecture

**Структура проекта:**

```
task/
├── domain/
│   ├── Task.java                 # Агрегат + Factory Method
│   ├── User.java                 # Агрегат + Factory Method
│   ├── TaskStatus.java           # Enum
│   └── repository/               # Порты (интерфейсы)
│       ├── TaskRepository.java
│       └── UserRepository.java
│
├── application/
│   ├── TaskApplicationService.java  # Оркестрация
│   └── exception/                   # Бизнес-исключения
│       ├── TaskNotFoundException.java
│       └── UserNotFoundException.java
│
└── infrastructure/
    ├── primary/                    # REST адаптеры
    │   ├── TaskResource.java
    │   ├── RestTask.java
    │   ├── CreateTaskRequest.java
    │   └── TaskApiExceptionHandler.java
    │
    └── secondary/                  # Реализации портов
        ├── HibernateTaskRepository.java
        └── HibernateUserRepository.java
```

---

## ❌ Отклонённые паттерны

### Lombok
**Решение:** Не используется

**Причины:**
- Seed4j/JHipster предпочитают чистый Java
- Лучшая читаемость кода
- Меньше проблем с IDE
- Явный код вместо магических аннотаций
- Сеттеры требуются для JPA/Hibernate

### Builder для DTO
**Решение:** Record вместо Builder

**Причины:**
- Record — нативный Java 16+
- Immutability из коробки
- Меньше кода
- Достаточно для простых DTO

---

## 📊 Итоговая статистика

| Компонент | Количество | Описание |
|-----------|------------|----------|
| Domain агрегаты | 2 | Task, User |
| Repository порты | 2 | TaskRepository, UserRepository |
| Application сервисы | 1 | TaskApplicationService |
| REST контроллеры | 1 | TaskResource |
| DTO | 5 | RestTask, RestUser, CreateTaskRequest, AssignTaskRequest, UpdateTaskStatusRequest |
| Exception handlers | 1 | TaskApiExceptionHandler |
| Интеграционные тесты | 11 | TaskResourceIT |

---

## ✅ Валидные REST API

Все endpoints протестированы в `TaskResourceIT.java`:

| Endpoint | Метод | Статус |
|----------|-------|--------|
| `/api/tasks` | POST | ✅ Создание задачи |
| `/api/tasks/{id}` | GET | ✅ Получение по ID |
| `/api/tasks?page&size` | GET | ✅ Пагинация |
| `/api/tasks/{id}/assignee` | PATCH | ✅ Назначение исполнителя |
| `/api/tasks/{id}/status` | PATCH | ✅ Смена статуса |

**Документация:** [rest-api-valid.md](rest-api-valid.md)
