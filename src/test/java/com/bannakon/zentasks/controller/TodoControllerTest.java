package com.bannakon.zentasks.controller;

import com.bannakon.zentasks.dto.TodoRequest;
import com.bannakon.zentasks.dto.UpdateTodoRequest;
import com.bannakon.zentasks.entity.Todo;
import com.bannakon.zentasks.entity.User;
import com.bannakon.zentasks.service.TodoService;
import com.bannakon.zentasks.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class TodoControllerTest {

    @Mock
    private TodoService todoService;

    @Mock
    private UserService userService;

    @InjectMocks
    private TodoController todoController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private User testUser;
    private Todo testTodo;


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(todoController).build();
        objectMapper = new ObjectMapper();

        // Setup test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");

        // Setup test todo
        testTodo = new Todo();
        testTodo.setId(1L);
        testTodo.setTitle("Test Todo");
        testTodo.setCompleted(false);
        testTodo.setCreatedAt(LocalDateTime.now());
        testTodo.setUpdatedAt(LocalDateTime.now());
        testTodo.setUser(testUser);
    }

    @Test
    void getAllTodos_ValidToken_ReturnsListOfTodos() throws Exception {
        // Given
        String validToken = "Bearer valid-token";
        List<Todo> todos = Arrays.asList(testTodo);

        when(userService.getCurrentUser("valid-token")).thenReturn(testUser);
        when(todoService.getAllDataTodos(testUser)).thenReturn(todos);

        // When & Then
        mockMvc.perform(get("/api/todos")
                        .header("Authorization", validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Test Todo"))
                .andExpect(jsonPath("$[0].completed").value(false));

        verify(userService).getCurrentUser("valid-token");
        verify(todoService).getAllDataTodos(testUser);
    }

    @Test
    void getAllTodos_MissingAuthHeader_ReturnsUnauthorized() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(userService, todoService);
    }

    @Test
    void getAllTodos_InvalidAuthHeader_ReturnsUnauthorized() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/todos")
                        .header("Authorization", "Invalid token"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(userService, todoService);
    }

    @Test
    void createTodo_ValidRequest_ReturnsCreatedTodo() throws Exception {
        // Given
        String validToken = "Bearer valid-token";
        TodoRequest request = new TodoRequest();
        request.setTitle("New Todo");
        request.setCompleted(false);

        when(userService.getCurrentUser("valid-token")).thenReturn(testUser);
        when(todoService.createDataTodo(any(TodoRequest.class), eq(testUser))).thenReturn(testTodo);

        // When & Then
        mockMvc.perform(post("/api/todos")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Todo"))
                .andExpect(jsonPath("$.completed").value(false));

        verify(userService).getCurrentUser("valid-token");
        verify(todoService).createDataTodo(any(TodoRequest.class), eq(testUser));
    }

    @Test
    void createTodo_MissingAuthHeader_ReturnsUnauthorized() throws Exception {
        // Given
        TodoRequest request = new TodoRequest();
        request.setTitle("New Todo");
        request.setCompleted(false);

        // When & Then
        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(userService, todoService);
    }

    @Test
    void createTodo_InvalidAuthHeader_ReturnsUnauthorized() throws Exception {
        // Given
        TodoRequest request = new TodoRequest();
        request.setTitle("New Todo");
        request.setCompleted(false);

        // When & Then
        mockMvc.perform(post("/api/todos")
                        .header("Authorization", "Invalid token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(userService, todoService);
    }

    @Test
    void updateTodo_ValidRequest_ReturnsUpdatedTodo() throws Exception {
        // Given
        String validToken = "Bearer valid-token";
        Long todoId = 1L;
        UpdateTodoRequest request = new UpdateTodoRequest();
        request.setTitle("Updated Todo");
        request.setCompleted(true);

        Todo updatedTodo = new Todo();
        updatedTodo.setId(todoId);
        updatedTodo.setTitle("Updated Todo");
        updatedTodo.setCompleted(true);
        updatedTodo.setCreatedAt(LocalDateTime.now());
        updatedTodo.setUpdatedAt(LocalDateTime.now());
        updatedTodo.setUser(testUser);

        when(userService.getCurrentUser("valid-token")).thenReturn(testUser);
        when(todoService.updateTodo(eq(todoId), any(UpdateTodoRequest.class), eq(testUser))).thenReturn(updatedTodo);

        // When & Then
        mockMvc.perform(put("/api/todos/{id}", todoId)
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Updated Todo"))
                .andExpect(jsonPath("$.completed").value(true));

        verify(userService).getCurrentUser("valid-token");
        verify(todoService).updateTodo(eq(todoId), any(UpdateTodoRequest.class), eq(testUser));
    }

    @Test
    void updateTodo_MissingAuthHeader_ReturnsUnauthorized() throws Exception {
        // Given
        Long todoId = 1L;
        UpdateTodoRequest request = new UpdateTodoRequest();
        request.setTitle("Updated Todo");

        // When & Then
        mockMvc.perform(put("/api/todos/{id}", todoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(userService, todoService);
    }

    @Test
    void updateTodo_InvalidAuthHeader_ReturnsUnauthorized() throws Exception {
        // Given
        Long todoId = 1L;
        UpdateTodoRequest request = new UpdateTodoRequest();
        request.setTitle("Updated Todo");

        // When & Then
        mockMvc.perform(put("/api/todos/{id}", todoId)
                        .header("Authorization", "Invalid token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(userService, todoService);
    }

    @Test
    void deleteTodo_ValidRequest_ReturnsNoContent() throws Exception {
        // Given
        String validToken = "Bearer valid-token";
        Long todoId = 1L;

        when(userService.getCurrentUser("valid-token")).thenReturn(testUser);
        doNothing().when(todoService).deleteTodo(todoId, testUser);

        // When & Then
        mockMvc.perform(delete("/api/todos/{id}", todoId)
                        .header("Authorization", validToken))
                .andExpect(status().isNoContent());

        verify(userService).getCurrentUser("valid-token");
        verify(todoService).deleteTodo(todoId, testUser);
    }

    @Test
    void deleteTodo_MissingAuthHeader_ReturnsUnauthorized() throws Exception {
        // Given
        Long todoId = 1L;

        // When & Then
        mockMvc.perform(delete("/api/todos/{id}", todoId))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(userService, todoService);
    }

    @Test
    void deleteTodo_InvalidAuthHeader_ReturnsUnauthorized() throws Exception {
        // Given
        Long todoId = 1L;

        // When & Then
        mockMvc.perform(delete("/api/todos/{id}", todoId)
                        .header("Authorization", "Invalid token"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(userService, todoService);
    }


    @Test
    void getTodoByCompleted_ValidRequest_ReturnsFilteredTodos() throws Exception {
        // Given
        String validToken = "Bearer valid-token";
        boolean completed = true;

        Todo completedTodo = new Todo();
        completedTodo.setId(2L);
        completedTodo.setTitle("Completed Todo");
        completedTodo.setCompleted(true);
        completedTodo.setCreatedAt(LocalDateTime.now());
        completedTodo.setUpdatedAt(LocalDateTime.now());
        completedTodo.setUser(testUser);

        List<Todo> completedTodos = Arrays.asList(completedTodo);

        when(userService.getCurrentUser("valid-token")).thenReturn(testUser);
        when(todoService.getTodosByCompletion(testUser, completed)).thenReturn(completedTodos);

        // When & Then
        mockMvc.perform(get("/api/todos/filter")
                        .param("completed", String.valueOf(completed))
                        .header("Authorization", validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].title").value("Completed Todo"))
                .andExpect(jsonPath("$[0].completed").value(true));

        verify(userService).getCurrentUser("valid-token");
        verify(todoService).getTodosByCompletion(testUser, completed);
    }


    @Test
    void getTodoByCompleted_MissingAuthHeader_ReturnsUnauthorized() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/todos/filter")
                        .param("completed", "true"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(userService, todoService);
    }

    @Test
    void getTodoByCompleted_InvalidAuthHeader_ReturnsUnauthorized() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/todos/filter")
                        .param("completed", "true")
                        .header("Authorization", "Invalid token"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(userService, todoService);
    }


    @Test
    void getAllTodos_UserServiceThrowsException_PropagatesException() throws Exception {
        // Given
        String validToken = "Bearer valid-token";

        when(userService.getCurrentUser("valid-token"))
                .thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Invalid token"));

        // When & Then
        mockMvc.perform(get("/api/todos")
                        .header("Authorization", validToken))
                .andExpect(status().isUnauthorized());

        verify(userService).getCurrentUser("valid-token");
        verifyNoInteractions(todoService);
    }


    @Test
    void createTodo_TodoServiceThrowsException_PropagatesException() throws Exception {
        // Given
        String validToken = "Bearer valid-token";
        TodoRequest request = new TodoRequest();
        request.setTitle("New Todo");
        request.setCompleted(false);

        when(userService.getCurrentUser("valid-token")).thenReturn(testUser);
        when(todoService.createDataTodo(any(TodoRequest.class), eq(testUser)))
                .thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Invalid request"));

        // When & Then
        mockMvc.perform(post("/api/todos")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(userService).getCurrentUser("valid-token");
        verify(todoService).createDataTodo(any(TodoRequest.class), eq(testUser));
    }

    @Test
    void updateTodo_TodoServiceThrowsNotFoundException_PropagatesException() throws Exception {
        // Given
        String validToken = "Bearer valid-token";
        Long todoId = 999L;
        UpdateTodoRequest request = new UpdateTodoRequest();
        request.setTitle("Updated Todo");

        when(userService.getCurrentUser("valid-token")).thenReturn(testUser);
        when(todoService.updateTodo(eq(todoId), any(UpdateTodoRequest.class), eq(testUser)))
                .thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Todo not found"));

        // When & Then
        mockMvc.perform(put("/api/todos/{id}", todoId)
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(userService).getCurrentUser("valid-token");
        verify(todoService).updateTodo(eq(todoId), any(UpdateTodoRequest.class), eq(testUser));
    }


    @Test
    void deleteTodo_TodoServiceThrowsNotFoundException_PropagatesException() throws Exception {
        // Given
        String validToken = "Bearer valid-token";
        Long todoId = 999L;

        when(userService.getCurrentUser("valid-token")).thenReturn(testUser);
        doThrow(new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Todo not found"))
                .when(todoService).deleteTodo(todoId, testUser);

        // When & Then
        mockMvc.perform(delete("/api/todos/{id}", todoId)
                        .header("Authorization", validToken))
                .andExpect(status().isNotFound());

        verify(userService).getCurrentUser("valid-token");
        verify(todoService).deleteTodo(todoId, testUser);
    }

}
