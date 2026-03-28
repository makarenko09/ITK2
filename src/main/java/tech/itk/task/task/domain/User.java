package tech.itk.task.task.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * Агрегат User в доменной модели.
 * Использует Factory Method для создания сущностей.
 * POJO без JPA аннотаций (JDBC напрямую).
 */
public class User {

  private UUID id;
  private String name;
  private String email;

  /**
   * Конструктор по умолчанию.
   */
  public User() {
  }

  /**
   * Factory Method для создания нового пользователя.
   * @param name имя
   * @param email email (уникальный)
   * @return новый пользователь
   */
  public static User create(String name, String email) {
    User user = new User();
    user.name = name;
    user.email = email;
    return user;
  }

  // Getters
  public UUID getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getEmail() {
    return email;
  }

  // Setters (требуются для JDBC RowMapper)
  public void setId(UUID id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    User user = (User) o;
    return Objects.equals(id, user.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }

  @Override
  public String toString() {
    return "User{" +
      "id=" + id +
      ", name='" + name + '\'' +
      ", email='" + email + '\'' +
      '}';
  }
}
