package com.bannakon.zentasks.controller;

import com.bannakon.zentasks.entity.Todo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/api/todos")
@RestController
public class TodoController {

    @PostMapping
    public void createTodo() {

    }

    @GetMapping // example
    public Object getAllTodosDemo() {
        List<Map<String, Object>> todos = new ArrayList<>();

        Map<String , Object> todo1 = new HashMap<>();
        todo1.put("id", 1);
        todo1.put("title", "Buy groceries");
        todo1.put("completed", false);

        Map<String, Object> todo2 = new HashMap<>();
        todo2.put("id", 2);
        todo2.put("title", "Study Spring Boot");
        todo2.put("completed", false);

        todos.add(todo1);
        todos.add(todo2);

        return todos;
    }
}
