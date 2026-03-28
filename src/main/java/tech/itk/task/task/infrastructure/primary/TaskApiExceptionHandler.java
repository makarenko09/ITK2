package tech.itk.task.task.infrastructure.primary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tech.itk.task.task.application.exception.TaskNotFoundException;
import tech.itk.task.task.application.exception.UserNotFoundException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Глобальный обработчик исключений для REST API.
 * Обрабатывает бизнес-исключения и возвращает понятные ответы клиенту.
 */
@RestControllerAdvice(basePackages = "tech.itk.task.task.infrastructure.primary")
public class TaskApiExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(TaskApiExceptionHandler.class);

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

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<Map<String, Object>> handleUserNotFound(UserNotFoundException ex) {
    log.warn("User not found: {}", ex.getMessage());
    return buildErrorResponse(
      HttpStatus.NOT_FOUND,
      "User not found",
      ex.getMessage(),
      "user_not_found"
    );
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
    log.warn("Invalid argument: {}", ex.getMessage());
    return buildErrorResponse(
      HttpStatus.BAD_REQUEST,
      "Invalid argument",
      ex.getMessage(),
      "illegal_argument"
    );
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
    log.error("Unexpected error", ex);
    return buildErrorResponse(
      HttpStatus.INTERNAL_SERVER_ERROR,
      "Internal server error",
      "An unexpected error occurred. Please check logs for details.",
      "internal_error"
    );
  }

  private ResponseEntity<Map<String, Object>> buildErrorResponse(
    HttpStatus status,
    String title,
    String detail,
    String errorCode
  ) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("timestamp", Instant.now());
    body.put("status", status.value());
    body.put("error", title);
    body.put("errorCode", errorCode);
    body.put("detail", detail);
    body.put("path", getCurrentRequestPath());
    return ResponseEntity.status(status).body(body);
  }

  private String getCurrentRequestPath() {
    // Упрощенная реализация - в продакшене использовать RequestAttributes
    return "/api/tasks";
  }
}
