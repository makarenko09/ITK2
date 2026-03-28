package tech.itk.task.task.domain.repository;

import tech.itk.task.task.domain.Task;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Порт для доступа к данным задач.
 * Интерфейс находится в domain, реализация - в infrastructure/secondary.
 */
public interface TaskRepository {

  Task save(Task task);

  Optional<Task> findById(UUID id);

  List<Task> findAll(int offset, int limit);

  long count();

  void deleteById(UUID id);
}
