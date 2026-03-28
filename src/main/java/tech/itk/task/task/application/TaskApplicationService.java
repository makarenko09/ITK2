package tech.itk.task.task.application;

import org.apache.camel.ProducerTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.itk.task.shared.error.domain.Assert;
import tech.itk.task.shared.pagination.domain.Seed4jSampleApplicationPage;
import tech.itk.task.shared.pagination.domain.Seed4jSampleApplicationPageable;
import tech.itk.task.task.domain.Task;
import tech.itk.task.task.domain.User;
import tech.itk.task.task.domain.repository.TaskRepository;
import tech.itk.task.task.domain.repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class TaskApplicationService {

  private final TaskRepository taskRepository;
  private final UserRepository userRepository;
  private final ProducerTemplate producerTemplate;

  public TaskApplicationService(
    TaskRepository taskRepository,
    UserRepository userRepository,
    ProducerTemplate producerTemplate
  ) {
    this.taskRepository = taskRepository;
    this.userRepository = userRepository;
    this.producerTemplate = producerTemplate;
  }

  public Task createTask(String title, String description) {
    Assert.field("title", title).notBlank();
    
    Task task = new Task(title, description);
    Task saved = taskRepository.save(task);

    // Отправка события в Kafka через Camel
    producerTemplate.sendBody("direct:task-created", Map.of(
      "taskId", saved.getId(),
      "title", saved.getTitle(),
      "status", saved.getStatus().name()
    ));

    return saved;
  }

  @Transactional(readOnly = true)
  public Task getTaskById(UUID id) {
    Assert.notNull("id", id);
    
    return taskRepository.findById(id)
      .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + id));
  }

  @Transactional(readOnly = true)
  public Seed4jSampleApplicationPage<Task> getTasks(Seed4jSampleApplicationPageable pageable) {
    Assert.notNull("pageable", pageable);
    
    List<Task> tasks = taskRepository.findAll(pageable.offset(), pageable.pageSize());
    long total = taskRepository.count();
    return Seed4jSampleApplicationPage.builder(tasks)
      .currentPage(pageable.page())
      .pageSize(pageable.pageSize())
      .totalElementsCount(total)
      .build();
  }

  public Task assignTask(UUID taskId, UUID assigneeId) {
    Assert.notNull("taskId", taskId);
    Assert.notNull("assigneeId", assigneeId);
    
    Task task = taskRepository.findById(taskId)
      .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + taskId));

    User assignee = userRepository.findById(assigneeId)
      .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + assigneeId));

    task.setAssignee(assignee);
    Task saved = taskRepository.save(task);

    // Отправка события в Kafka через Camel
    producerTemplate.sendBody("direct:task-assigned", Map.of(
      "taskId", saved.getId(),
      "assigneeId", assignee.getId(),
      "assigneeEmail", assignee.getEmail()
    ));

    return saved;
  }

  public Task updateTaskStatus(UUID taskId, tech.itk.task.task.domain.TaskStatus status) {
    Assert.notNull("taskId", taskId);
    Assert.notNull("status", status);
    
    Task task = taskRepository.findById(taskId)
      .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + taskId));

    task.setStatus(status);
    return taskRepository.save(task);
  }
}
