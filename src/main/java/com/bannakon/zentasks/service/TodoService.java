package com.bannakon.zentasks.service;

import com.bannakon.zentasks.entity.Todo;
import com.bannakon.zentasks.repository.TodoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TodoService {
    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public List<Todo> getAllDataTodos() {
        return todoRepository.findAll();
    }
}
