package com.bannakon.zentasks.service;

import com.bannakon.zentasks.entity.Todo;
import com.bannakon.zentasks.repository.TodoRepository;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class TodoService {

//    @Autowired // ถ้าใช้ field DI, ไม่ใส่ @Autowired, จะ error 500 NullPointerException, because "this.todoRepository" is null
    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public List<Todo> getAllDataTodos() {
        log.info("TodoService todoRepository findAll: {}", todoRepository.findAll());
        return todoRepository.findAll();
    }

    public Todo createDataTodo(Todo todo) {
        log.info("createDataTodo: {}", todo);
        return todoRepository.save(todo);
    }

    public Optional<Todo> updateTodo(Long id, Todo updateTodo) {
        Optional<Todo> existingTodo =  todoRepository.findById(id);

        if (existingTodo.isPresent()) {
            Todo todo = existingTodo.get();
            todo.setTitle(updateTodo.getTitle());
            todo.setCompleted(updateTodo.isCompleted());

            Todo saved = todoRepository.save(todo);
            log.info("Successfully updated todo: {}", saved);
            return Optional.of(saved);
        } else {
            log.warn("Todo not found with id: {}", id);
            return Optional.empty();
        }
    }

    public void deleteTodo(Long id) {
        log.info("Requested delete todo with id: {}", id);
        todoRepository.deleteById(id);
    }

    public List<Todo> getTodosByCompletion(boolean completed) {
        log.info("TodoService findByCompleted: {}", completed);
        List<Todo> todos =  todoRepository.findByCompleted(completed);
        log.info("Found {} todos with completed status: {}", todos.size(), completed);
        return todos;
    }
}
