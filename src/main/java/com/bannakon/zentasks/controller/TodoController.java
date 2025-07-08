package com.bannakon.zentasks.controller;

import com.bannakon.zentasks.dto.TodoRequest;
import com.bannakon.zentasks.dto.TodoResponse;
import com.bannakon.zentasks.dto.UpdateTodoRequest;
import com.bannakon.zentasks.entity.Todo;
import com.bannakon.zentasks.service.TodoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping("/api/todos")
@RestController
@RequiredArgsConstructor // สร้าง constructor ที่รับ ค่าพารามิเตอร์สำหรับทุก final field และทุก field ที่ถูก @NonNull (ถ้ามี)
public class TodoController {

    private final TodoService todoService;

    @GetMapping
    public ResponseEntity<List<TodoResponse>> getAllTodos() {
        List<TodoResponse> todos = todoService.getAllDataTodos()
                .stream()
                .map(todo-> new TodoResponse(
                        todo.getId(),
                        todo.getTitle(),
                        todo.isCompleted(),
                        todo.getCreatedAt(),
                        todo.getUpdatedAt()
                ))
                .toList();

        return ResponseEntity.ok(todos);
    }

    @PostMapping
    public ResponseEntity<TodoResponse> createTodo(@Valid @RequestBody TodoRequest request) {
        Todo created =  todoService.createDataTodo(request);
        TodoResponse todoResponse = new TodoResponse(
                created.getId(),
                created.getTitle(),
                created.isCompleted(),
                created.getCreatedAt(),
                created.getUpdatedAt()
        );

        return ResponseEntity.status(201).body(todoResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TodoResponse> updateTodo(@PathVariable Long id, @Valid @RequestBody UpdateTodoRequest request) {
        Todo updated = todoService.updateTodo(id, request);
        TodoResponse response = new TodoResponse(updated.getId(), updated.getTitle(), updated.isCompleted(), updated.getCreatedAt(), updated.getUpdatedAt());
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTodo(@PathVariable Long id) {
        todoService.deleteTodo(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping(value = "/filter", params = "completed")
    public ResponseEntity<List<TodoResponse>> getTodoByCompleted(@RequestParam boolean completed) {
        List<TodoResponse> todos = todoService.getTodosByCompletion(completed)
                .stream()
                .map(todo -> new TodoResponse(todo.getId(), todo.getTitle(), todo.isCompleted(), todo.getCreatedAt(), todo.getUpdatedAt()))
                .toList();

        return ResponseEntity.ok().body(todos);
    }
}
