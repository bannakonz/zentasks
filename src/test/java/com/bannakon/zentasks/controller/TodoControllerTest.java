package com.bannakon.zentasks.controller;

import com.bannakon.zentasks.dto.TodoRequest;
import com.bannakon.zentasks.dto.UpdateTodoRequest;
import com.bannakon.zentasks.entity.Todo;
import com.bannakon.zentasks.service.TodoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import java.util.List;

import static org.mockito.Mockito.when;
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
        Todo todo = new Todo(1L, "Task", false);
        when(todoService.getAllDataTodos()).thenReturn(List.of(todo));

        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Task"));
    }

    @Test
    void shouldCreateTodo() throws Exception {
        TodoRequest request = new TodoRequest();
        request.setTitle("New Task");
        Todo created = new Todo(1L, "New Task", false);

        when(todoService.createDataTodo(request)).thenReturn(created);

        mockMvc.perform(post("/api/todos")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.title").value("New Task"));
    }

    @Test
    void shouldUpdateTodo() throws Exception {
        UpdateTodoRequest request = new UpdateTodoRequest();
        request.setTitle("Update Task");
        request.setCompleted(true);

        Todo updated = new Todo(1L, "Update Task", true);

        when(todoService.updateTodo(1L, request)).thenReturn(updated);

        mockMvc.perform(put("/api/todos/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Update Task"))
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    void shouldDeleteTodo() throws Exception {

        mockMvc.perform(delete("/api/todos/1")
                        .contentType("application/json"))
                .andExpect(status().isNoContent()); // 204
    }

}