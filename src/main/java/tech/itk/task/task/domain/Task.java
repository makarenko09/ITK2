package tech.itk.task.task.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Objects;
import java.util.UUID;

/**
 * Агрегат Task в доменной модели.
 * Использует Factory Method для создания сущностей.
 * JPA Entity для автоматического создания таблиц.
 */
@Entity
@Table(name = "tasks")
public class Task {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private String title;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TaskStatus status = TaskStatus.NEW;

  @ManyToOne(optional = true)
  @JoinColumn(name = "assignee_id")
  private User assignee;

  /**
   * Конструктор по умолчанию (требуется для JPA).
   */
  protected Task() {
  }

  /**
   * Factory Method для создания новой задачи.
   * @param title заголовок задачи
   * @param description описание задачи
   * @return новая задача со статусом NEW
   */
  public static Task create(String title, String description) {
    Task task = new Task();
    task.title = title;
    task.description = description;
    task.status = TaskStatus.NEW;
    return task;
  }

  /**
   * Бизнес-метод: назначить исполнителя.
   * @param user исполнитель
   */
  public void assignTo(User user) {
    if (user == null) {
      throw new IllegalArgumentException("Assignee cannot be null");
    }
    this.assignee = user;
  }

  /**
   * Бизнес-метод: изменить статус.
   * @param status новый статус
   */
  public void changeStatus(TaskStatus status) {
    if (status == null) {
      throw new IllegalArgumentException("Status cannot be null");
    }
    this.status = status;
  }

  // Getters
  public UUID getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public TaskStatus getStatus() {
    return status;
  }

  public User getAssignee() {
    return assignee;
  }

  // Setters (требуются для JPA/Hibernate)
  public void setId(UUID id) {
    this.id = id;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setStatus(TaskStatus status) {
    this.status = status;
  }

  public void setAssignee(User assignee) {
    this.assignee = assignee;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Task task = (Task) o;
    return Objects.equals(id, task.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }

  @Override
  public String toString() {
    return "Task{" +
      "id=" + id +
      ", title='" + title + '\'' +
      ", status=" + status +
      ", assignee=" + (assignee != null ? assignee.getEmail() : "null") +
      '}';
  }
}
