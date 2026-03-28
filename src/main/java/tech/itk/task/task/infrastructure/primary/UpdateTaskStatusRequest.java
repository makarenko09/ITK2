package tech.itk.task.task.infrastructure.primary;

import jakarta.validation.constraints.NotNull;
import tech.itk.task.task.domain.TaskStatus;

public record UpdateTaskStatusRequest(
  @NotNull(message = "Status is required")
  TaskStatus status
) {
}
