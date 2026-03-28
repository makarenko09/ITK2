package tech.itk.task.task.infrastructure.primary;

import tech.itk.task.task.domain.User;

import java.util.UUID;

public record RestUser(
  UUID id,
  String name,
  String email
) {
  public static RestUser from(User user) {
    return new RestUser(user.getId(), user.getName(), user.getEmail());
  }
}
