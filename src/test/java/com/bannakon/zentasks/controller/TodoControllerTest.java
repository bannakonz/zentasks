package com.bannakon.zentasks.controller;

import com.bannakon.zentasks.dto.TodoRequest;
import com.bannakon.zentasks.dto.UpdateTodoRequest;
import com.bannakon.zentasks.entity.Todo;
import com.bannakon.zentasks.service.TodoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;


import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// ใช้ @WebMvcTest เพื่อโหลดเฉพาะชั้น Controller ไม่โหลด Bean ทั้งระบบ Spring Boot
@WebMvcTest(TodoController.class)
class TodoControllerTest {

    // MockMvc คือเครื่องมือสำหรับยิง HTTP Request จำลองไปยัง Controller จริง (ไม่ต้องรัน Server จริง)
    @Autowired
    private MockMvc mockMvc;

    // @MockitoBean จะสร้าง Mock สำหรับ TodoService
    // เพื่อฉีดเข้า TodoController ให้แทนของจริง (ไม่ต้องต่อ Database หรือ Service จริง)
    @MockitoBean
    private TodoService todoService;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void shouldReturnAllTodos() throws Exception {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        Todo todo1 = new Todo(1L, "Task 1", false, now, now);
        Todo todo2 = new Todo(2L, "Task 2", true, now, now);
        when(todoService.getAllDataTodos()).thenReturn(List.of(todo1, todo2));

        // Act & Assert
        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Task 1"))
                .andExpect(jsonPath("$[0].completed").value(false))
                .andExpect(jsonPath("$[0].createdAt").exists())
                .andExpect(jsonPath("$[0].updatedAt").exists())
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("Task 2"))
                .andExpect(jsonPath("$[1].completed").value(true));

        verify(todoService).getAllDataTodos();
    }

    @Test
    void shouldReturnEmptyListWhenNoTodos() throws Exception {
        // Arrange
        when(todoService.getAllDataTodos()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldCreateTodo() throws Exception {
        // Arrange
        TodoRequest request = new TodoRequest();
        request.setTitle("New Task");
        request.setCompleted(false);

        LocalDateTime now = LocalDateTime.now();
        Todo created = new Todo(1L, "New Task", false, now, now);
        when(todoService.createDataTodo(any(TodoRequest.class))).thenReturn(created);

        // Act & Assert
        mockMvc.perform(post("/api/todos")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("New Task"))
                .andExpect(jsonPath("$.completed").value(false))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());

        verify(todoService).createDataTodo(any(TodoRequest.class));
    }

    @Test
    void shouldUpdateTodo() throws Exception {
        // Arrange
        UpdateTodoRequest request = new UpdateTodoRequest();
        request.setTitle("Updated Task");
        request.setCompleted(true);

        LocalDateTime createdTime = LocalDateTime.now().minusDays(1);
        LocalDateTime updatedTime = LocalDateTime.now();
        Todo updated = new Todo(1L, "Updated Task", true, createdTime, updatedTime);

        when(todoService.updateTodo(eq(1L), any(UpdateTodoRequest.class))).thenReturn(updated);

        // Act & Assert
        mockMvc.perform(put("/api/todos/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Updated Task"))
                .andExpect(jsonPath("$.completed").value(true))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());

        verify(todoService).updateTodo(eq(1L), any(UpdateTodoRequest.class));
    }

    @Test
    void shouldReturn404WhenUpdateNonExistentTodo() throws Exception {
        // Arrange
        UpdateTodoRequest request = new UpdateTodoRequest();
        request.setTitle("Updated Task");

        when(todoService.updateTodo(eq(99L), any(UpdateTodoRequest.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo not found"));

        // Act & Assert
        mockMvc.perform(put("/api/todos/99")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteTodo() throws Exception {
        // Arrange
        doNothing().when(todoService).deleteTodo(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/todos/1"))
                .andExpect(status().isNoContent());

        verify(todoService).deleteTodo(1L);
    }

    @Test
    void shouldGetTodosByCompletion() throws Exception {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        Todo completedTodo = new Todo(1L, "Completed Task", true, now, now);
        when(todoService.getTodosByCompletion(true)).thenReturn(List.of(completedTodo));

        // Act & Assert
        mockMvc.perform(get("/api/todos?completed=true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].completed").value(true));

        verify(todoService).getTodosByCompletion(true);
    }

}