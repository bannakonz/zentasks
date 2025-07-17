package com.bannakon.zentasks.service;

import com.bannakon.zentasks.dto.TodoRequest;
import com.bannakon.zentasks.dto.UpdateTodoRequest;
import com.bannakon.zentasks.entity.Todo;
import com.bannakon.zentasks.entity.User;
import com.bannakon.zentasks.repository.TodoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class TodoService {

    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public List<Todo> getAllDataTodos(User user) {
        return todoRepository.findByUser(user);
    }

    public Todo createDataTodo(TodoRequest request, User user) {
        Todo todo = new Todo();
        todo.setTitle(request.getTitle());
        todo.setCompleted(request.isCompleted());

        LocalDateTime now = LocalDateTime.now();
        todo.setCreatedAt(now);
        todo.setUpdatedAt(now);
        todo.setUser(user);

        return todoRepository.save(todo);
    }

    public Todo updateTodo(Long id, UpdateTodoRequest request, User user) {
        Todo todo = todoRepository.findById(id)
                .filter(t -> t.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Todo not found for current user"));

        if (request.getTitle() != null) {
            todo.setTitle(request.getTitle());
        }

        if (request.getCompleted() != null) {
            todo.setCompleted(request.getCompleted());
        }

        todo.setUpdatedAt(LocalDateTime.now());
        return todoRepository.save(todo);
    }

    public void deleteTodo(Long id, User user) {
        Todo todo = todoRepository.findById(id)
                .filter(t -> t.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Todo not found for current user"));

        todoRepository.delete(todo);
    }

    public List<Todo> getTodosByCompletion(User user, boolean completed) {
        return todoRepository.findByUserAndCompleted(user, completed);
    }
}
