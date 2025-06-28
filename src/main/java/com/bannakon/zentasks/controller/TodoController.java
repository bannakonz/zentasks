package com.bannakon.zentasks.controller;

import com.bannakon.zentasks.entity.Todo;
import com.bannakon.zentasks.service.TodoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping("/api/todos")
@RestController
public class TodoController {

    //    @Autowired // ถ้าใช้ field DI, ไม่ใส่ @Autowired, จะ error 500 NullPointerException, because "this.todoService" is null
    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    public ResponseEntity<List<Todo>> getAllTodos() {
        log.info("TodoController getAllDataTodos: {}", todoService.getAllDataTodos());
        return ResponseEntity.ok(todoService.getAllDataTodos());
    }

    @PostMapping
    public ResponseEntity<Todo> createTodo(@RequestBody Todo newTodo) {
        Todo created = todoService.createDataTodo(newTodo);
        return ResponseEntity.status(201).body(created);
//        return todoService.createDataTodo(newTodo); // ส่งแบบนี้กลับเลยได้ไหม
    }
}
