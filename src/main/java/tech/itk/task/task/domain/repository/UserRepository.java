package tech.itk.task.task.domain.repository;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import tech.itk.task.task.domain.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepository {

  private final SessionFactory sessionFactory;

  public UserRepository(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  public User save(User user) {
    sessionFactory.getCurrentSession().merge(user);
    return user;
  }

  public Optional<User> findById(UUID id) {
    User user = sessionFactory.getCurrentSession().getReference(User.class, id);
    return Optional.ofNullable(user);
  }

  public List<User> findAll() {
    return sessionFactory.getCurrentSession()
      .createQuery("FROM User", User.class)
      .getResultList();
  }

  public Optional<User> findByEmail(String email) {
    List<User> users = sessionFactory.getCurrentSession()
      .createQuery("FROM User WHERE email = :email", User.class)
      .setParameter("email", email)
      .setMaxResults(1)
      .getResultList();
    return users.stream().findFirst();
  }

  public void deleteById(UUID id) {
    User user = sessionFactory.getCurrentSession().getReference(User.class, id);
    if (user != null) {
      sessionFactory.getCurrentSession().remove(user);
    }
  }
}
