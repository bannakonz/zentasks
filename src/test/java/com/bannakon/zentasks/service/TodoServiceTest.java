package com.bannakon.zentasks.service;

import com.bannakon.zentasks.dto.TodoRequest;
import com.bannakon.zentasks.dto.UpdateTodoRequest;
import com.bannakon.zentasks.entity.Todo;
import com.bannakon.zentasks.repository.TodoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private TodoService todoService;


    @Test
    void shouldGetAllTodos() {
        // Arrange
        Todo todo1 = new Todo(1L, "Task 1", false);
        Todo todo2 = new Todo(1L, "Task 2", false);
        when(todoRepository.findAll()).thenReturn(Arrays.asList(todo1, todo2));
//        when(...).thenReturn(...) = ถ้าเรียก ... ให้ตอบ ...

        // Act
        List<Todo> todos = todoService.getAllDataTodos();

        // Assert
        assertThat(todos).hasSize(2);
        verify(todoRepository, times(1)).findAll();
    }

    @Test
    void shouldCreateTodo() {
        TodoRequest request = new TodoRequest();
        request.setTitle("New Task");
        request.setCompleted(false);

        Todo saved = new Todo(1L, "New Task", false);
        when(todoRepository.save(any(Todo.class))).thenReturn(saved);

        // Act
        Todo result = todoService.createDataTodo(request);

        // Asset
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("New Task");
        verify(todoRepository, times(1)).save(any(Todo.class));
    }

    @Test
    void shouldUpdateTodo() {
        // Arrange
        Todo existing = new Todo(1L, "Old", false);
        UpdateTodoRequest update = new  UpdateTodoRequest();
        update.setTitle("Task Updated");
        update.setCompleted(true);

        when(todoRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(todoRepository.save(any(Todo.class))).thenReturn(existing);

        // Act
        Todo result = todoService.updateTodo(1L, update);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Task Updated");
        assertThat(result.isCompleted()).isTrue();
        verify(todoRepository).save(existing);

    }

    @Test
    void shouldThrowWhenUpdateNotFound() {
        // Arrange
        UpdateTodoRequest update = new UpdateTodoRequest();
        when(todoRepository.findById(99L)).thenReturn(Optional.empty());

        // Assert
        assertThatThrownBy(()-> todoService.updateTodo(99L, update))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Todo not found");
    }

    @Test
    void shouldDeleteTodo() {
        // Act
        todoService.deleteTodo(1L);

        // Asset
        verify(todoRepository).deleteById(1L);
    }

    @Test
    void shouldGetTodosByCompletion() {
        // Arrange
        Todo todo = new Todo(1L, "Task", true);
        when(todoRepository.findByCompleted(true)).thenReturn(List.of(todo));

        // Act
        List<Todo> todos = todoService.getTodosByCompletion(true);

        // Asset
        assertThat(todos).hasSize(1);
        assertThat(todos.get(0).isCompleted()).isTrue();
    }

}