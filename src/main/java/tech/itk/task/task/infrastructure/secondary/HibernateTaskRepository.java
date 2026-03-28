package tech.itk.task.task.infrastructure.secondary;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import tech.itk.task.task.domain.Task;
import tech.itk.task.task.domain.repository.TaskRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Реализация порта TaskRepository через Hibernate.
 * Находится в infrastructure/secondary согласно hexagonal architecture.
 */
@Repository
public class HibernateTaskRepository implements TaskRepository {

  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public Task save(Task task) {
    if (task.getId() == null) {
      entityManager.persist(task);
    } else {
      entityManager.merge(task);
    }
    return task;
  }

  @Override
  public Optional<Task> findById(UUID id) {
    Task task = entityManager.find(Task.class, id);
    return Optional.ofNullable(task);
  }

  @Override
  public List<Task> findAll(int offset, int limit) {
    return entityManager
      .createQuery("FROM Task", Task.class)
      .setFirstResult(offset)
      .setMaxResults(limit)
      .getResultList();
  }

  @Override
  public long count() {
    return entityManager
      .createQuery("SELECT COUNT(t) FROM Task t", Long.class)
      .getSingleResult();
  }

  @Override
  public void deleteById(UUID id) {
    entityManager.find(Task.class, id);
    entityManager.remove(entityManager.getReference(Task.class, id));
  }
}
