package com.bannakon.zentasks.controller;

import com.bannakon.zentasks.entity.Todo;
import com.bannakon.zentasks.service.TodoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


import java.util.List;

import static org.mockito.Mockito.when;
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


    @Test
    void shouldReturnAllTodos() throws Exception {
        Todo todo = new Todo(1L, "Task", false);
        when(todoService.getAllDataTodos()).thenReturn(List.of(todo));

        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Task"));
    }


}