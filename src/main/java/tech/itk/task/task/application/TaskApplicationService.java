package tech.itk.task.task.application;

import org.apache.camel.ProducerTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.itk.task.shared.error.domain.Assert;
import tech.itk.task.shared.pagination.domain.Seed4jSampleApplicationPage;
import tech.itk.task.shared.pagination.domain.Seed4jSampleApplicationPageable;
import tech.itk.task.task.application.exception.TaskNotFoundException;
import tech.itk.task.task.application.exception.UserNotFoundException;
import tech.itk.task.task.domain.Task;
import tech.itk.task.task.domain.User;
import tech.itk.task.task.domain.repository.TaskRepository;
import tech.itk.task.task.domain.repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Application Service для оркестрации операций с задачами.
 * Не содержит бизнес-логики, только координацию между портами.
 */
@Service
@Transactional
public class TaskApplicationService {

  private final TaskRepository taskRepository;
  private final UserRepository userRepository;
  private final ProducerTemplate producerTemplate;

  public TaskApplicationService(
      TaskRepository taskRepository,
      UserRepository userRepository,
      ProducerTemplate producerTemplate) {
    this.taskRepository = taskRepository;
    this.userRepository = userRepository;
    this.producerTemplate = producerTemplate;
  }

  /**
   * Создать новую задачу.
   */
  public Task createTask(String title, String description) {
    Assert.field("title", title).notBlank();

    // Factory Method для создания задачи
    Task task = Task.create(title, description);
    Task saved = taskRepository.save(task);

    // Dispatch domain event через порт
    producerTemplate.sendBody("direct:task-created", Map.of(
        "taskId", saved.getId(),
        "title", saved.getTitle(),
        "status", saved.getStatus().name()));

    return saved;
  }

  /**
   * Получить задачу по ID.
   */
  @Transactional(readOnly = true)
  public Task getTaskById(UUID id) {
    Assert.notNull("id", id);

    return taskRepository.findById(id)
        .orElseThrow(() -> new TaskNotFoundException(id));
  }

  /**
   * Получить список задач с пагинацией.
   */
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

  /**
   * Назначить исполнителя на задачу (бизнес-операция в domain).
   */
  public Task assignTask(UUID taskId, UUID assigneeId) {
    Assert.notNull("taskId", taskId);
    Assert.notNull("assigneeId", assigneeId);

    Task task = taskRepository.findById(taskId)
        .orElseThrow(() -> new TaskNotFoundException(taskId));

    User assignee = userRepository.findById(assigneeId)
        .orElseThrow(() -> new UserNotFoundException(assigneeId));

    // Бизнес-метод доменной модели
    task.assignTo(assignee);
    Task saved = taskRepository.save(task);

    // Dispatch domain event через порт
    producerTemplate.sendBody("direct:task-assigned", Map.of(
        "taskId", saved.getId(),
        "assigneeId", assignee.getId(),
        "assigneeEmail", assignee.getEmail()));

    return saved;
  }

  /**
   * Изменить статус задачи (бизнес-операция в domain).
   */
  public Task updateTaskStatus(UUID taskId, tech.itk.task.task.domain.TaskStatus status) {
    Assert.notNull("taskId", taskId);
    Assert.notNull("status", status);

    Task task = taskRepository.findById(taskId)
        .orElseThrow(() -> new TaskNotFoundException(taskId));

    // Бизнес-метод доменной модели
    task.changeStatus(status);
    return taskRepository.save(task);
  }
}
