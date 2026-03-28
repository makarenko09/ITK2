package tech.itk.task.task.application.exception;

import java.util.UUID;

/**
 * Исключение выбрасывается когда задача не найдена.
 */
public class TaskNotFoundException extends RuntimeException {

  private static final String MESSAGE = "Task not found with id: %s";

  public TaskNotFoundException(UUID id) {
    super(MESSAGE.formatted(id));
  }

  /**
   * @return Код ошибки для клиентского ответа.
   */
  public String getErrorCode() {
    return "task_not_found";
  }
}
