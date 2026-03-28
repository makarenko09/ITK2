package tech.itk.task.task.infrastructure.primary;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tech.itk.task.shared.pagination.domain.Seed4jSampleApplicationPageable;
import tech.itk.task.shared.pagination.infrastructure.primary.RestSeed4jSampleApplicationPage;
import tech.itk.task.shared.pagination.infrastructure.primary.RestSeed4jSampleApplicationPageable;
import tech.itk.task.task.application.TaskApplicationService;
import tech.itk.task.task.domain.Task;

import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
public class TaskResource {

  private final TaskApplicationService taskService;

  public TaskResource(TaskApplicationService taskService) {
    this.taskService = taskService;
  }

  @PostMapping
  public ResponseEntity<RestTask> createTask(@Valid @RequestBody CreateTaskRequest request) {
    Task task = taskService.createTask(request.title(), request.description());
    return ResponseEntity.ok(RestTask.from(task));
  }

  @GetMapping("/{id}")
  public ResponseEntity<RestTask> getTaskById(@PathVariable UUID id) {
    Task task = taskService.getTaskById(id);
    return ResponseEntity.ok(RestTask.from(task));
  }

  @GetMapping
  public ResponseEntity<RestSeed4jSampleApplicationPage<RestTask>> getTasks(
    @Validated RestSeed4jSampleApplicationPageable pageable
  ) {
    var page = taskService.getTasks(pageable.toPageable());
    return ResponseEntity.ok(RestSeed4jSampleApplicationPage.from(page, RestTask::from));
  }

  @PatchMapping("/{id}/assignee")
  public ResponseEntity<RestTask> assignTask(
    @PathVariable UUID id,
    @Valid @RequestBody AssignTaskRequest request
  ) {
    Task task = taskService.assignTask(id, request.assigneeId());
    return ResponseEntity.ok(RestTask.from(task));
  }

  @PatchMapping("/{id}/status")
  public ResponseEntity<RestTask> updateTaskStatus(
    @PathVariable UUID id,
    @Valid @RequestBody UpdateTaskStatusRequest request
  ) {
    Task task = taskService.updateTaskStatus(id, request.status());
    return ResponseEntity.ok(RestTask.from(task));
  }
}
