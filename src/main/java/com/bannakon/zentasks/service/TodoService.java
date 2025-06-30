package com.bannakon.zentasks.service;

import com.bannakon.zentasks.dto.TodoRequest;
import com.bannakon.zentasks.dto.UpdateTodoRequest;
import com.bannakon.zentasks.entity.Todo;
import com.bannakon.zentasks.repository.TodoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@Service
public class TodoService {

//    @Autowired // ถ้าใช้ field DI, ไม่ใส่ @Autowired, จะ error 500 NullPointerException, because "this.todoRepository" is null
    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public List<Todo> getAllDataTodos() {
        return todoRepository.findAll();
    }

    public Todo createDataTodo(TodoRequest todoRequest) {
        Todo todo = new Todo();
        todo.setTitle(todoRequest.getTitle());
        todo.setCompleted(todoRequest.isCompleted());
        return todoRepository.save(todo);
    }

    public Todo updateTodo(Long id, UpdateTodoRequest request) {
        Todo todo = todoRepository.findById(id).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Todo not found with id: " + id
        ));
        if (request.getTitle() != null) {
            todo.setTitle(request.getTitle());
        }
        if (request.getCompleted() != null) {
            todo.setCompleted(request.getCompleted());
        }
        return todoRepository.save(todo);
    }

    public void deleteTodo(Long id) {
        todoRepository.deleteById(id);
    }

    public List<Todo> getTodosByCompletion(boolean completed) {
        List<Todo> todos =  todoRepository.findByCompleted(completed);
        return todos;
    }
}
