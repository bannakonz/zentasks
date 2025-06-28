package com.bannakon.zentasks.controller;

import com.bannakon.zentasks.entity.Todo;
import com.bannakon.zentasks.service.TodoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping("/api/todos")
@RestController // REST -> JSON , SOAP-> XML ,
public class TodoController { // oauth2 , cache redis,

//        @Autowired // ถ้าใช้ field DI, ไม่ใส่ @Autowired, จะ error 500 NullPointerException, because "this.todoService" is null
    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    public ResponseEntity<List<Todo>> getAllTodos() { // จัดการ response http , response code
        log.info("TodoController getAllDataTodos: {}", todoService.getAllDataTodos());
        return ResponseEntity.ok(todoService.getAllDataTodos());
    }

    @PostMapping
    public ResponseEntity<Todo> createTodo(@RequestBody Todo newTodo) {
        Todo created = todoService.createDataTodo(newTodo);
        return ResponseEntity.status(201).body(created);
//        return todoService.createDataTodo(newTodo); // ส่งแบบนี้กลับเลยได้ไหม
    }

    @PutMapping("/{id}")
    public ResponseEntity<Todo> updateTodo(@PathVariable Long id, @RequestBody Todo updateTodo) {
        log.info("TodoController updateTodo with id: {} and data: {}", id, updateTodo);
        return todoService.updateTodo(id, updateTodo).map((updatedTodo)-> {
            log.info("Successfully updated todo in controller: {}", updatedTodo);
            return ResponseEntity.ok(updatedTodo);
        }).orElseGet(()->{
            log.warn("Todo not found in controller with id: {}", id);
            return ResponseEntity.notFound().build();
        });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTodo(@PathVariable Long id) {
        log.info("Delete todo in controller with id: {}", id);
        todoService.deleteTodo(id);
        return ResponseEntity.noContent().build();
    }
}
// LAMBDA , stream,
// SERVER, จัดการ,  THREAD, SECURITY

// AUTHEn JWT
