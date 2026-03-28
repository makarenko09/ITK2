package tech.itk.task.task.infrastructure.secondary;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import tech.itk.task.task.domain.Task;
import tech.itk.task.task.domain.TaskStatus;
import tech.itk.task.task.domain.User;
import tech.itk.task.task.domain.repository.TaskRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JDBC реализация порта TaskRepository.
 * Прямой доступ к БД без JPA/Hibernate согласно ТЗ.
 */
@Repository
public class JdbcTaskRepository implements TaskRepository {

  private final JdbcTemplate jdbcTemplate;

  public JdbcTaskRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  private static final RowMapper<Task> TASK_ROW_MAPPER = (rs, rowNum) -> {
    Task task = new Task();
    task.setId(rs.getObject("id", UUID.class));
    task.setTitle(rs.getString("title"));
    task.setDescription(rs.getString("description"));
    task.setStatus(TaskStatus.valueOf(rs.getString("status")));
    return task;
  };

  @Override
  public Task save(Task task) {
    if (task.getId() == null) {
      // INSERT
      UUID id = UUID.randomUUID();
      jdbcTemplate.update(
        """
        INSERT INTO tasks (id, title, description, status, assignee_id)
        VALUES (?, ?, ?, ?, ?)
        """,
        id,
        task.getTitle(),
        task.getDescription(),
        task.getStatus().name(),
        task.getAssignee() != null ? task.getAssignee().getId() : null
      );
      task.setId(id);
      return task;
    } else {
      // UPDATE
      jdbcTemplate.update(
        """
        UPDATE tasks
        SET title = ?, description = ?, status = ?, assignee_id = ?
        WHERE id = ?
        """,
        task.getTitle(),
        task.getDescription(),
        task.getStatus().name(),
        task.getAssignee() != null ? task.getAssignee().getId() : null,
        task.getId()
      );
      return task;
    }
  }

  @Override
  public Optional<Task> findById(UUID id) {
    try {
      Task task = jdbcTemplate.queryForObject(
        "SELECT * FROM tasks WHERE id = ?",
        TASK_ROW_MAPPER,
        id
      );
      return Optional.ofNullable(task);
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  @Override
  public List<Task> findAll(int offset, int limit) {
    return jdbcTemplate.query(
      """
      SELECT * FROM tasks
      ORDER BY id
      OFFSET ? LIMIT ?
      """,
      TASK_ROW_MAPPER,
      offset,
      limit
    );
  }

  @Override
  public long count() {
    return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM tasks", Long.class);
  }

  @Override
  public void deleteById(UUID id) {
    jdbcTemplate.update("DELETE FROM tasks WHERE id = ?", id);
  }

  /**
   * Загрузка задачи с исполнителем (JOIN).
   */
  public Optional<Task> findByIdWithAssignee(UUID id) {
    String sql = """
      SELECT t.id, t.title, t.description, t.status, t.assignee_id,
             u.id as user_id, u.name as user_name, u.email as user_email
      FROM tasks t
      LEFT JOIN users u ON t.assignee_id = u.id
      WHERE t.id = ?
      """;

    try {
      TaskWithUserMapper mapper = new TaskWithUserMapper();
      Task task = jdbcTemplate.queryForObject(sql, mapper, id);
      
      if (task != null && mapper.getUser() != null) {
        task.setAssignee(mapper.getUser());
      }
      
      return Optional.ofNullable(task);
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  private static class TaskWithUserMapper implements RowMapper<Task> {
    private User user;

    @Override
    public Task mapRow(ResultSet rs, int rowNum) throws SQLException {
      Task task = new Task();
      task.setId(rs.getObject("id", UUID.class));
      task.setTitle(rs.getString("title"));
      task.setDescription(rs.getString("description"));
      task.setStatus(TaskStatus.valueOf(rs.getString("status")));

      UUID userId = rs.getObject("user_id", UUID.class);
      if (userId != null) {
        user = new User();
        user.setId(userId);
        user.setName(rs.getString("user_name"));
        user.setEmail(rs.getString("user_email"));
      }

      return task;
    }

    public User getUser() {
      return user;
    }
  }
}
