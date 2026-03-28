package tech.itk.task.task.infrastructure.secondary;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import tech.itk.task.task.domain.User;
import tech.itk.task.task.domain.repository.UserRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

/**
 * JDBC реализация порта UserRepository.
 * Прямой доступ к БД без JPA/Hibernate согласно ТЗ.
 */
@Repository
public class JdbcUserRepository implements UserRepository {

  private final JdbcTemplate jdbcTemplate;

  public JdbcUserRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  private static final RowMapper<User> USER_ROW_MAPPER = (rs, rowNum) -> {
    User user = new User();
    user.setId(rs.getObject("id", UUID.class));
    user.setName(rs.getString("name"));
    user.setEmail(rs.getString("email"));
    return user;
  };

  @Override
  public User save(User user) {
    if (user.getId() == null) {
      // INSERT
      UUID id = UUID.randomUUID();
      jdbcTemplate.update(
        """
        INSERT INTO users (id, name, email)
        VALUES (?, ?, ?)
        """,
        id,
        user.getName(),
        user.getEmail()
      );
      user.setId(id);
      return user;
    } else {
      // UPDATE
      jdbcTemplate.update(
        """
        UPDATE users
        SET name = ?, email = ?
        WHERE id = ?
        """,
        user.getName(),
        user.getEmail(),
        user.getId()
      );
      return user;
    }
  }

  @Override
  public Optional<User> findById(UUID id) {
    try {
      User user = jdbcTemplate.queryForObject(
        "SELECT * FROM users WHERE id = ?",
        USER_ROW_MAPPER,
        id
      );
      return Optional.ofNullable(user);
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  @Override
  public Optional<User> findByEmail(String email) {
    try {
      User user = jdbcTemplate.queryForObject(
        "SELECT * FROM users WHERE email = ?",
        USER_ROW_MAPPER,
        email
      );
      return Optional.ofNullable(user);
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  @Override
  public void deleteById(UUID id) {
    jdbcTemplate.update("DELETE FROM users WHERE id = ?", id);
  }
}
