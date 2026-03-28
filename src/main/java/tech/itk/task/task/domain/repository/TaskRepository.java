package tech.itk.task.task.domain.repository;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import tech.itk.task.task.domain.Task;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TaskRepository {

  private final SessionFactory sessionFactory;

  public TaskRepository(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  public Task save(Task task) {
    sessionFactory.getCurrentSession().merge(task);
    return task;
  }

  public Optional<Task> findById(UUID id) {
    Task task = sessionFactory.getCurrentSession().getReference(Task.class, id);
    return Optional.ofNullable(task);
  }

  public List<Task> findAll() {
    return sessionFactory.getCurrentSession()
      .createQuery("FROM Task", Task.class)
      .getResultList();
  }

  public List<Task> findAll(int offset, int limit) {
    return sessionFactory.getCurrentSession()
      .createQuery("FROM Task", Task.class)
      .setFirstResult(offset)
      .setMaxResults(limit)
      .getResultList();
  }

  public long count() {
    return sessionFactory.getCurrentSession()
      .createQuery("SELECT COUNT(t) FROM Task t", Long.class)
      .getSingleResult();
  }

  public void deleteById(UUID id) {
    Task task = sessionFactory.getCurrentSession().getReference(Task.class, id);
    if (task != null) {
      sessionFactory.getCurrentSession().remove(task);
    }
  }
}
