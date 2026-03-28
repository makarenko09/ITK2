package tech.itk.task.task.infrastructure.primary;

import tech.itk.task.task.domain.Task;
import tech.itk.task.task.domain.TaskStatus;

import java.util.UUID;

public record RestTask(
  UUID id,
  String title,
  String description,
  TaskStatus status,
  RestUser assignee
) {
  public static RestTask from(Task task) {
    return new RestTask(
      task.getId(),
      task.getTitle(),
      task.getDescription(),
      task.getStatus(),
      task.getAssignee() != null ? RestUser.from(task.getAssignee()) : null
    );
  }
}
