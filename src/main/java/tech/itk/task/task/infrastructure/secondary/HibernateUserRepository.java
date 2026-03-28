package tech.itk.task.task.infrastructure.secondary;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import tech.itk.task.task.domain.User;
import tech.itk.task.task.domain.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Реализация порта UserRepository через Hibernate.
 * Находится в infrastructure/secondary согласно hexagonal architecture.
 */
@Repository
public class HibernateUserRepository implements UserRepository {

  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public User save(User user) {
    if (user.getId() == null) {
      entityManager.persist(user);
    } else {
      entityManager.merge(user);
    }
    return user;
  }

  @Override
  public Optional<User> findById(UUID id) {
    User user = entityManager.find(User.class, id);
    return Optional.ofNullable(user);
  }

  @Override
  public Optional<User> findByEmail(String email) {
    List<User> users = entityManager
      .createQuery("FROM User WHERE email = :email", User.class)
      .setParameter("email", email)
      .getResultList();
    return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
  }

  @Override
  public void deleteById(UUID id) {
    entityManager.find(User.class, id);
    entityManager.remove(entityManager.getReference(User.class, id));
  }
}
