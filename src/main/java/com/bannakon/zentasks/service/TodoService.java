package com.bannakon.zentasks.service;

import com.bannakon.zentasks.repository.TodoRepository;
import org.springframework.stereotype.Service;

@Service
public class TodoService {
    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public Object getAllDataTodos() {
        return todoRepository.findAll();
    }
}
