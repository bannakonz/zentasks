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

import java.time.LocalDateTime;
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
        LocalDateTime now = LocalDateTime.now();

        Todo todo1 = new Todo(1L, "Task 1", false, now, now);
        Todo todo2 = new Todo(2L, "Task 2", false, now, now);
        when(todoRepository.findAll()).thenReturn(Arrays.asList(todo1, todo2));
//        when(...).thenReturn(...) = ถ้าเรียก ... ให้ตอบ ...

        // Act
        List<Todo> todos = todoService.getAllDataTodos();

        // Assert
        assertThat(todos).hasSize(2);
        assertThat(todos.get(0).getTitle()).isEqualTo("Task 1");
        assertThat(todos.get(1).getTitle()).isEqualTo("Task 2");
        verify(todoRepository, times(1)).findAll();
    }

    @Test
    void shouldCreateTodo() {
        TodoRequest request = new TodoRequest();

        request.setTitle("New Task");
        request.setCompleted(true);

        LocalDateTime now = LocalDateTime.now();
        Todo saved = new Todo(1L, "New Task", true, now, now);
        when(todoRepository.save(any(Todo.class))).thenReturn(saved);

        // Act
        Todo result = todoService.createDataTodo(request);

        // Asset
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("New Task");
        assertThat(result.isCompleted()).isTrue();
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getUpdatedAt()).isNotNull();
        assertThat(result.getCreatedAt()).isEqualTo(result.getUpdatedAt());
        verify(todoRepository, times(1)).save(any(Todo.class));
    }

    @Test
    void shouldUpdateTodo() {
        // Arrange
        LocalDateTime createdTime = LocalDateTime.now().minusDays(1);
        Todo existing = new Todo(1L, "Old", false, createdTime, createdTime);

        UpdateTodoRequest update = new UpdateTodoRequest();
        update.setTitle("Task Updated");
        update.setCompleted(true);

        when(todoRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(todoRepository.save(any(Todo.class))).thenReturn(existing);

        // Act
        Todo result = todoService.updateTodo(1L, update);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Task Updated");
        assertThat(result.isCompleted()).isTrue();

        // getUpdatedAt ควรใหม่กว่า getUpdatedAt
        assertThat(result.getCreatedAt()).isEqualTo(createdTime);
        assertThat(result.getUpdatedAt()).isAfter(createdTime);

        verify(todoRepository).findById(1L);
        verify(todoRepository).save(existing);

    }

    @Test
    void shouldUpdateOnlyTitle() {
        // Arrange
        LocalDateTime createdTime = LocalDateTime.now().minusDays(1);
        Todo existing = new Todo(1L, "Old Task", false, createdTime, createdTime);

        UpdateTodoRequest update = new UpdateTodoRequest();
        update.setTitle("Updated Title Only");
        // completed is null, should not change

        when(todoRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(todoRepository.save(any(Todo.class))).thenReturn(existing);

        // Act
        todoService.updateTodo(1L, update);

        // Assert
        assertThat(existing.getTitle()).isEqualTo("Updated Title Only");
        assertThat(existing.isCompleted()).isFalse(); // Should remain unchanged
        assertThat(existing.getUpdatedAt()).isAfter(createdTime);
    }

    @Test
    void shouldUpdateOnlyCompleted() {
        // Arrange
        LocalDateTime createdTime = LocalDateTime.now().minusDays(1);
        Todo existing = new Todo(1L, "Original Title", false, createdTime, createdTime);

        UpdateTodoRequest update = new UpdateTodoRequest();
        update.setCompleted(true);
        // title is null, should not change

        when(todoRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(todoRepository.save(any(Todo.class))).thenReturn(existing);

        // Act
        todoService.updateTodo(1L, update);

        // Assert
        assertThat(existing.getTitle()).isEqualTo("Original Title"); // Should remain unchanged
        assertThat(existing.isCompleted()).isTrue();
        assertThat(existing.getUpdatedAt()).isAfter(createdTime);
    }

    @Test
    void shouldThrowWhenUpdateNotFound() {
        // Arrange
        UpdateTodoRequest update = new UpdateTodoRequest();
        when(todoRepository.findById(99L)).thenReturn(Optional.empty());

        // Assert
        assertThatThrownBy(() -> todoService.updateTodo(99L, update))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Todo not found");

        verify(todoRepository).findById(99L);
        verify(todoRepository, never()).save(any(Todo.class));
    }

    @Test
    void shouldDeleteTodo() {
        // Arrange
        when(todoRepository.existsById(1L)).thenReturn(true); // ตั้งเงื่อนไขจำลอง: ถ้าเรียกแบบนี้ → ให้ตอบแบบนี้!

        // Act
        todoService.deleteTodo(1L);

        // Asset
        verify(todoRepository).existsById(1L);  // ควรตรวจด้วย
        verify(todoRepository).deleteById(1L);
    }

    @Test
    void shouldGetTodosByCompletion() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        Todo completedTodo = new Todo(1L, "Completed Task", true, now, now);
        when(todoRepository.findByCompleted(true)).thenReturn(List.of(completedTodo));

        // Act
        List<Todo> todos = todoService.getTodosByCompletion(true);

        // Asset
        assertThat(todos).hasSize(1);
        assertThat(todos.get(0).getTitle()).isEqualTo("Completed Task");
        assertThat(todos.get(0).isCompleted()).isTrue();

        verify(todoRepository).findByCompleted(true);
    }

    @Test
    void shouldGetTodosByCompletionReturnEmpty() {
        // Arrange
        when(todoRepository.findByCompleted(false)).thenReturn(List.of());

        // Act
        List<Todo> todos = todoService.getTodosByCompletion(false);

        // Assert
        assertThat(todos).isEmpty();
        verify(todoRepository).findByCompleted(false);
    }

}