package com.bannakon.zentasks.controller;

import com.bannakon.zentasks.dto.TodoRequest;
import com.bannakon.zentasks.dto.TodoResponse;
import com.bannakon.zentasks.dto.UpdateTodoRequest;
import com.bannakon.zentasks.entity.Todo;
import com.bannakon.zentasks.entity.User;
import com.bannakon.zentasks.service.TodoService;
import com.bannakon.zentasks.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@RequestMapping("/api/todos")
@RestController
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;
    private final UserService userService;

    private User validateAndGetUser(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        return userService.getCurrentUser(token);
    }

    @GetMapping
    public ResponseEntity<List<TodoResponse>> getAllTodos(
            @RequestHeader(value = "Authorization", required = false)
            String authHeader) {
        User user = validateAndGetUser(authHeader);

        List<TodoResponse> todos = todoService.getAllDataTodos(user)
                .stream()
                .map(todo -> new TodoResponse(
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
    public ResponseEntity<TodoResponse> createTodo(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody TodoRequest request) {

        User user = validateAndGetUser(authHeader);

        Todo created = todoService.createDataTodo(request, user);
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
    public ResponseEntity<TodoResponse> updateTodo(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody UpdateTodoRequest request) {

        User user = validateAndGetUser(authHeader);

        Todo updated = todoService.updateTodo(id, request, user);

        TodoResponse response = new TodoResponse(
                updated.getId(),
                updated.getTitle(),
                updated.isCompleted(),
                updated.getCreatedAt(),
                updated.getUpdatedAt()
        );

        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTodo(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        User user = validateAndGetUser(authHeader);

        todoService.deleteTodo(id, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/filter", params = "completed")
    public ResponseEntity<List<TodoResponse>> getTodoByCompleted(
            @RequestParam boolean completed,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        User user = validateAndGetUser(authHeader);

        List<TodoResponse> todos = todoService.getTodosByCompletion(user, completed)
                .stream()
                .map(todo -> new TodoResponse(todo.getId(), todo.getTitle(), todo.isCompleted(), todo.getCreatedAt(), todo.getUpdatedAt()))
                .toList();

        return ResponseEntity.ok().body(todos);
    }
}
