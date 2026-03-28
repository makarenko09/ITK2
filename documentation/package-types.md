# Package types

Приложение использует две аннотации для маркировки пакетов:

- `@SharedKernel` — для пакетов с классами, общими между несколькими контекстами
- `@BusinessContext` — для пакетов с классами, отвечающими конкретным бизнес-требованиям

## Быстрый старт

### Marking packages

Добавьте файл `package-info.java` в корень пакета:

```java
@tech.itk.task.SharedKernel
package tech.itk.task.shared.error;
```

Или для бизнес-контекста:

```java
@tech.itk.task.BusinessContext(name = "Task", description = "Управление задачами")
package tech.itk.task.task;
```

## Примеры в проекте

| Пакет | Аннотация | Описание |
|-------|-----------|----------|
| `tech.itk.task.task` | `@BusinessContext` | Управление задачами |
| `tech.itk.task.task.domain` | `@BusinessContext` | Доменная модель задач |
| `tech.itk.task.task.application` | `@BusinessContext` | Сервисный слой |
| `tech.itk.task.shared.error` | `@SharedKernel` | Общие утилиты ошибок |
| `tech.itk.task.shared.pagination` | `@SharedKernel` | Общие утилиты пагинации |

## Документация

- [Task API Implementation](task-api-implementation.md) — структура проекта
- [README](../README.md) — основной README проекта

---

## Оригинальная документация

This application comes with two package level annotations:

- `SharedKernel` used to mark packages containing classes shared between multiple contexts;
- `BusinessContext` used to mark packages containing classes to answer a specific business need.

To mark a package, add a `package-info.java` file:

```java
@tech.itk.task.SharedKernel
package tech.itk.task;
```
