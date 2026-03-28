package tech.itk.task.task.infrastructure.secondary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.itk.task.task.domain.Task;

import java.util.UUID;

/**
 * Spring Data JPA репозиторий для Task.
 * Автоматическая генерация CRUD операций.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
}
