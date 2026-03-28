# LogsSpy

LogsSpy — JUnit5 расширение для тестирования логов.

## Быстрый старт

### Использование в тестах

```java
@UnitTest
@ExtendWith(LogsSpyExtension.class)
class MyTest {

  @Logs
  private LogsSpy logs;

  @Test
  void shouldDoStuff() {
    // Тестирование логов
    logs.shouldHave(Level.INFO, "Message");
  }
}
```

## Документация

- [Task API Implementation](task-api-implementation.md) — полная документация
- [README](../README.md) — основной README проекта

---

## Оригинальная документация

LogsSpy is a JUnit5 extension used to assert logs.

## Usage

```java
@UnitTest
@ExtendWith(LogsSpyExtension.class)
class MyTest {

  @Logs
  private LogsSpy logs;

  @Test
  void shouldDoStuff() {
    logs.shouldHave(Level.INFO, "Message");
  }
}
```
    doingStuff();

    logs.shouldHave(Level.INFO, "some stuff");
    logs.shouldHave(Level.DEBUG, "repeated stuff", 5);
    logs.shouldNotHave(Level.ERROR, "this is an error");
  }
}
```
