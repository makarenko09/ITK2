package tech.itk.task.task.domain.repository;

import tech.itk.task.task.domain.User;

import java.util.Optional;
import java.util.UUID;

/**
 * Порт для доступа к данным пользователей.
 * Интерфейс находится в domain, реализация - в infrastructure/secondary.
 */
public interface UserRepository {

  User save(User user);

  Optional<User> findById(UUID id);

  Optional<User> findByEmail(String email);

  void deleteById(UUID id);
}
