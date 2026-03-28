package tech.itk.task.task.infrastructure.primary;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tech.itk.task.IntegrationTest;
import tech.itk.task.task.domain.Task;
import tech.itk.task.task.domain.TaskStatus;
import tech.itk.task.task.domain.User;
import tech.itk.task.task.domain.repository.TaskRepository;
import tech.itk.task.task.domain.repository.UserRepository;

/**
 * Интеграционные тесты для Task REST API.
 * Тестируют все endpoints с валидными JSON запросами.
 */
@IntegrationTest
@AutoConfigureMockMvc
class TaskResourceIT {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private TaskRepository taskRepository;

  @Autowired
  private UserRepository userRepository;

  @Test
  @DisplayName("POST /api/tasks - Создание задачи с валидным JSON")
  void shouldCreateTaskWithValidJson() throws Exception {
    // Given
    String requestBody = """
      {
        "title": "Реализовать авторизацию",
        "description": "Добавить JWT аутентификацию для API endpoints"
      }
      """;

    // When & Then
    String responseContent = mockMvc
      .perform(post("/api/tasks")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
      .andExpect(status().isOk())
      .andReturn()
      .getResponse()
      .getContentAsString();

    // Проверка ответа
    Map<String, Object> response = objectMapper.readValue(responseContent, Map.class);
    assertThat(response).containsKeys("id", "title", "status");
    assertThat(response.get("title")).isEqualTo("Реализовать авторизацию");
    assertThat(response.get("status")).isEqualTo("NEW");
    assertThat(response.get("id")).isNotNull();
  }

  @Test
  @DisplayName("POST /api/tasks - Создание задачи без описания")
  void shouldCreateTaskWithoutDescription() throws Exception {
    // Given
    String requestBody = """
      {
        "title": "Задача без описания"
      }
      """;

    // When & Then
    String responseContent = mockMvc
      .perform(post("/api/tasks")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
      .andExpect(status().isOk())
      .andReturn()
      .getResponse()
      .getContentAsString();

    Map<String, Object> response = objectMapper.readValue(responseContent, Map.class);
    assertThat(response.get("title")).isEqualTo("Задача без описания");
    assertThat(response.get("status")).isEqualTo("NEW");
  }

  @Test
  @DisplayName("POST /api/tasks - Ошибка при пустом title")
  void shouldReturnBadRequestForEmptyTitle() throws Exception {
    // Given
    String requestBody = """
      {
        "title": ""
      }
      """;

    // When & Then
    mockMvc
      .perform(post("/api/tasks")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
      .andExpect(status().is4xxClientError());
  }

  @Test
  @DisplayName("GET /api/tasks/{id} - Получение задачи по ID")
  void shouldGetTaskById() throws Exception {
    // Given
    Task savedTask = createAndSaveTask("Тестовая задача", "Описание");

    // When & Then
    String responseContent = mockMvc
      .perform(get("/api/tasks/" + savedTask.getId()))
      .andExpect(status().isOk())
      .andReturn()
      .getResponse()
      .getContentAsString();

    Map<String, Object> response = objectMapper.readValue(responseContent, Map.class);
    assertThat(response.get("id")).isEqualTo(savedTask.getId().toString());
    assertThat(response.get("title")).isEqualTo("Тестовая задача");
  }

  @Test
  @DisplayName("GET /api/tasks/{id} - Задача не найдена")
  void shouldReturnNotFoundForNonExistentTask() throws Exception {
    // Given
    UUID nonExistentId = UUID.randomUUID();

    // When & Then
    String responseContent = mockMvc
      .perform(get("/api/tasks/" + nonExistentId))
      .andExpect(status().isNotFound())
      .andReturn()
      .getResponse()
      .getContentAsString();

    Map<String, Object> response = objectMapper.readValue(responseContent, Map.class);
    assertThat(response.get("status")).isEqualTo(404);
    assertThat(response.get("errorCode")).isEqualTo("task_not_found");
  }

  @Test
  @DisplayName("GET /api/tasks - Список задач с пагинацией")
  void shouldGetTasksWithPagination() throws Exception {
    // Given
    createAndSaveTask("Задача 1", "Описание 1");
    createAndSaveTask("Задача 2", "Описание 2");

    // When & Then
    String responseContent = mockMvc
      .perform(get("/api/tasks?page=0&size=10"))
      .andExpect(status().isOk())
      .andReturn()
      .getResponse()
      .getContentAsString();

    Map<String, Object> response = objectMapper.readValue(responseContent, Map.class);
    assertThat(response).containsKeys("content", "currentPage", "pageSize", "totalElementsCount");
    
    @SuppressWarnings("unchecked")
    var content = (java.util.List<?>) response.get("content");
    assertThat(content).hasSize(2);
  }

  @Test
  @DisplayName("PATCH /api/tasks/{id}/status - Смена статуса")
  void shouldUpdateTaskStatus() throws Exception {
    // Given
    Task savedTask = createAndSaveTask("Задача для смены статуса", null);
    String requestBody = """
      {
        "status": "IN_PROGRESS"
      }
      """;

    // When & Then
    String responseContent = mockMvc
      .perform(patch("/api/tasks/" + savedTask.getId() + "/status")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
      .andExpect(status().isOk())
      .andReturn()
      .getResponse()
      .getContentAsString();

    Map<String, Object> response = objectMapper.readValue(responseContent, Map.class);
    assertThat(response.get("id")).isEqualTo(savedTask.getId().toString());
    assertThat(response.get("status")).isEqualTo("IN_PROGRESS");
  }

  @Test
  @DisplayName("PATCH /api/tasks/{id}/status - Ошибка при невалидном статусе")
  void shouldReturnBadRequestForInvalidStatus() throws Exception {
    // Given
    Task savedTask = createAndSaveTask("Задача", null);
    String requestBody = """
      {
        "status": "INVALID_STATUS"
      }
      """;

    // When & Then
    mockMvc
      .perform(patch("/api/tasks/" + savedTask.getId() + "/status")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
      .andExpect(status().is4xxClientError());
  }

  @Test
  @DisplayName("PATCH /api/tasks/{id}/assignee - Назначение исполнителя")
  void shouldAssignTaskToUser() throws Exception {
    // Given
    Task savedTask = createAndSaveTask("Задача для назначения", null);
    User savedUser = createAndSaveUser("Иван Петров", "ivan@example.com");
    
    String requestBody = """
      {
        "assigneeId": "%s"
      }
      """.formatted(savedUser.getId());

    // When & Then
    String responseContent = mockMvc
      .perform(patch("/api/tasks/" + savedTask.getId() + "/assignee")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
      .andExpect(status().isOk())
      .andReturn()
      .getResponse()
      .getContentAsString();

    Map<String, Object> response = objectMapper.readValue(responseContent, Map.class);
    assertThat(response.get("id")).isEqualTo(savedTask.getId().toString());
    
    @SuppressWarnings("unchecked")
    Map<String, Object> assignee = (Map<String, Object>) response.get("assignee");
    assertThat(assignee).isNotNull();
    assertThat(assignee.get("name")).isEqualTo("Иван Петров");
  }

  @Test
  @DisplayName("PATCH /api/tasks/{id}/assignee - Пользователь не найден")
  void shouldReturnNotFoundForNonExistentUser() throws Exception {
    // Given
    Task savedTask = createAndSaveTask("Задача", null);
    UUID nonExistentUserId = UUID.randomUUID();
    
    String requestBody = """
      {
        "assigneeId": "%s"
      }
      """.formatted(nonExistentUserId);

    // When & Then
    String responseContent = mockMvc
      .perform(patch("/api/tasks/" + savedTask.getId() + "/assignee")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
      .andExpect(status().isNotFound())
      .andReturn()
      .getResponse()
      .getContentAsString();

    Map<String, Object> response = objectMapper.readValue(responseContent, Map.class);
    assertThat(response.get("errorCode")).isEqualTo("user_not_found");
  }

  // Вспомогательные методы
  private Task createAndSaveTask(String title, String description) {
    Task task = new Task();
    task.setTitle(title);
    task.setDescription(description);
    task.setStatus(TaskStatus.NEW);
    return taskRepository.save(task);
  }

  private User createAndSaveUser(String name, String email) {
    User user = new User();
    user.setName(name);
    user.setEmail(email);
    return userRepository.save(user);
  }
}
