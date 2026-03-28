# Assertions

Утилиты для проверки входных данных через класс `Assert`.

## Быстрый старт

### Простые проверки

```java
Assert.notNull("field", value);
```

### Fluent API для специфичных проверок

```java
Assert.field("name", name)
  .notBlank()
  .maxLength(150);

Assert.field("age", age)
  .min(0)
  .max(150);
```

## Примеры использования

Смотрите [TaskApplicationService](../src/main/java/tech/itk/task/task/application/TaskApplicationService.java):

```java
public Task createTask(String title, String description) {
  Assert.field("title", title).notBlank();
  // ...
}
```

## Документация

- [Task API Implementation](task-api-implementation.md) — полная документация
- [README](../README.md) — основной README проекта

---

## Оригинальная документация

The `Assert` class contains utilities for input checks. Assertions are done at runtime and result in an exception if the condition is not met.

These assertions are designed for basic, technical related checks. For business-facing checks, create your own exceptions.

## Usage

### Simple checks

```java
Assert.notNull("field", value);
```

### Fluent API

```java
Assert.field("name", name)
  .notBlank()
  .maxLength(150);
```
