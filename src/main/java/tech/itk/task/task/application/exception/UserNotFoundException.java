package tech.itk.task.task.application.exception;

import java.util.UUID;

/**
 * Исключение выбрасывается когда пользователь не найден.
 */
public class UserNotFoundException extends RuntimeException {

  private static final String MESSAGE = "User not found with id: %s";

  public UserNotFoundException(UUID id) {
    super(MESSAGE.formatted(id));
  }

  /**
   * @return Код ошибки для клиентского ответа.
   */
  public String getErrorCode() {
    return "user_not_found";
  }
}
