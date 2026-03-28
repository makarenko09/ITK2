package tech.itk.task.task.infrastructure.primary;

import jakarta.validation.constraints.NotBlank;

public record CreateTaskRequest(
  @NotBlank(message = "Title is required")
  String title,
  String description
) {
}
