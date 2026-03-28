# CORS configuration

Настройка Cross-Origin Resource Sharing (CORS) для приложения.

## Конфигурация

Добавьте в `application-local.yml`:

```yaml
application:
  cors:
    allowed-origins: http://localhost:8100,http://localhost:9000
    allowed-methods: "*"
    allowed-headers: "*"
    exposed-headers: Authorization,Link,X-Total-Count
    allow-credentials: true
    max-age: 1800
    allowed-origin-patterns: https://*.githubpreview.dev
```

## Документация

- [Task API Implementation](task-api-implementation.md) — полная документация
- [README](../README.md) — основной README проекта

---

## Оригинальная документация

You might want to configure Cross Origin Resources Sharing, here is a configuration example:

```properties
# CORS configuration
application.cors.allowed-origins=http://localhost:8100,http://localhost:9000
application.cors.allowed-methods=*
application.cors.allowed-headers=*
application.cors.exposed-headers=Authorization,Link,X-Total-Count
application.cors.allow-credentials=true
application.cors.max-age=1800
application.cors.allowed-origin-patterns=https://*.githubpreview.dev
```
