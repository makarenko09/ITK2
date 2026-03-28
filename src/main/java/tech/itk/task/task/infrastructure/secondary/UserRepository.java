package tech.itk.task.task.infrastructure.secondary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.itk.task.task.domain.User;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA репозиторий для User.
 * Автоматическая генерация CRUD операций.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

  Optional<User> findByEmail(String email);
}
