package com.bannakon.zentasks.service;

import com.bannakon.zentasks.dto.TodoRequest;
import com.bannakon.zentasks.dto.UpdateTodoRequest;
import com.bannakon.zentasks.entity.Todo;
import com.bannakon.zentasks.entity.User;
import com.bannakon.zentasks.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

        @Mock
        private TodoRepository todoRepository;

        @InjectMocks
        private TodoService todoService;

        private User testUser;
        private Todo testTodo;
        private TodoRequest todoRequest;
        private UpdateTodoRequest updateTodoRequest;

        @BeforeEach
        void setUp() {
            testUser = new User();
            testUser.setId(1L);
            testUser.setFirstName("John");
            testUser.setLastName("Doe");
            testUser.setEmail("john.doe@example.com");
            testUser.setPassword("password123");

            testTodo = new Todo();
            testTodo.setId(1L);
            testTodo.setTitle("Test Todo");
            testTodo.setCompleted(false);
            testTodo.setCreatedAt(LocalDateTime.now());
            testTodo.setUpdatedAt(LocalDateTime.now());
            testTodo.setUser(testUser);

            todoRequest = new TodoRequest();
            todoRequest.setTitle("New Todo");
            todoRequest.setCompleted(false);

            updateTodoRequest = new UpdateTodoRequest();
            updateTodoRequest.setTitle("Updated Todo");
            updateTodoRequest.setCompleted(true);
        }

        @Test
        void getAllDataTodos_ShouldReturnTodosForUser() {
            // Given
            Todo todo1 = new Todo();
            todo1.setId(1L);
            todo1.setTitle("Todo 1");
            todo1.setUser(testUser);

            Todo todo2 = new Todo();
            todo2.setId(2L);
            todo2.setTitle("Todo 2");
            todo2.setUser(testUser);

            List<Todo> expectedTodos = Arrays.asList(todo1, todo2);
            when(todoRepository.findByUser(testUser)).thenReturn(expectedTodos);

            // When
            List<Todo> result = todoService.getAllDataTodos(testUser);

            // Then
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals(expectedTodos, result);
            verify(todoRepository, times(1)).findByUser(testUser);
        }

    @Test
    void getAllDataTodos_ShouldReturnEmptyListWhenNoTodos() {
        // Given
        when(todoRepository.findByUser(testUser)).thenReturn(List.of());

        // When
        List<Todo> result = todoService.getAllDataTodos(testUser);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(todoRepository, times(1)).findByUser(testUser);
    }

    @Test
    void createDataTodo_ShouldCreateAndReturnTodo() {
        // Given
        Todo savedTodo = new Todo();
        savedTodo.setId(1L);
        savedTodo.setTitle(todoRequest.getTitle());
        savedTodo.setCompleted(todoRequest.isCompleted());
        savedTodo.setUser(testUser);
        savedTodo.setCreatedAt(LocalDateTime.now());
        savedTodo.setUpdatedAt(LocalDateTime.now());

        when(todoRepository.save(any(Todo.class))).thenReturn(savedTodo);

        // When
        Todo result = todoService.createDataTodo(todoRequest, testUser);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("New Todo", result.getTitle());
        assertEquals(false, result.isCompleted());
        assertEquals(testUser, result.getUser());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());

        verify(todoRepository, times(1)).save(any(Todo.class));
    }

    @Test
    void createDataTodo_ShouldSetCorrectTimestamps() {
        // Given
        LocalDateTime beforeCreation = LocalDateTime.now().minusSeconds(1);
        when(todoRepository.save(any(Todo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Todo result = todoService.createDataTodo(todoRequest, testUser);

        // Then
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
        assertTrue(result.getCreatedAt().isAfter(beforeCreation));
        assertTrue(result.getUpdatedAt().isAfter(beforeCreation));
        assertEquals(result.getCreatedAt(), result.getUpdatedAt());
    }

    @Test
    void updateTodo_ShouldUpdateTitleAndCompleted() {
        // Given
        when(todoRepository.findById(1L)).thenReturn(Optional.of(testTodo));
        when(todoRepository.save(any(Todo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Todo result = todoService.updateTodo(1L, updateTodoRequest, testUser);

        // Then
        assertNotNull(result);
        assertEquals("Updated Todo", result.getTitle());
        assertEquals(true, result.isCompleted());
        assertNotNull(result.getUpdatedAt());
        verify(todoRepository, times(1)).findById(1L);
        verify(todoRepository, times(1)).save(testTodo);
    }

    @Test
    void updateTodo_ShouldUpdateOnlyTitle() {
        // Given
        UpdateTodoRequest titleOnlyRequest = new UpdateTodoRequest();
        titleOnlyRequest.setTitle("Only Title Updated");
        titleOnlyRequest.setCompleted(null);

        when(todoRepository.findById(1L)).thenReturn(Optional.of(testTodo));
        when(todoRepository.save(any(Todo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Todo result = todoService.updateTodo(1L, titleOnlyRequest, testUser);

        // Then
        assertNotNull(result);
        assertEquals("Only Title Updated", result.getTitle());
        assertEquals(false, result.isCompleted()); // Should remain unchanged
        verify(todoRepository, times(1)).save(testTodo);
    }

    @Test
    void updateTodo_ShouldUpdateOnlyCompleted() {
        // Given
        UpdateTodoRequest completedOnlyRequest = new UpdateTodoRequest();
        completedOnlyRequest.setTitle(null);
        completedOnlyRequest.setCompleted(true);

        when(todoRepository.findById(1L)).thenReturn(Optional.of(testTodo));
        when(todoRepository.save(any(Todo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Todo result = todoService.updateTodo(1L, completedOnlyRequest, testUser);

        // Then
        assertNotNull(result);
        assertEquals("Test Todo", result.getTitle()); // Should remain unchanged
        assertEquals(true, result.isCompleted());
        verify(todoRepository, times(1)).save(testTodo);
    }

    @Test
    void updateTodo_ShouldThrowExceptionWhenTodoNotFound() {
        // Given
        when(todoRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> todoService.updateTodo(1L, updateTodoRequest, testUser));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Todo not found for current user", exception.getReason());
        verify(todoRepository, times(1)).findById(1L);
        verify(todoRepository, never()).save(any(Todo.class));
    }

    @Test
    void updateTodo_ShouldThrowExceptionWhenTodoBelongsToDifferentUser() {
        // Given
        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setEmail("another@example.com");

        testTodo.setUser(anotherUser);
        when(todoRepository.findById(1L)).thenReturn(Optional.of(testTodo));

        // When & Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> todoService.updateTodo(1L, updateTodoRequest, testUser));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Todo not found for current user", exception.getReason());
        verify(todoRepository, times(1)).findById(1L);
        verify(todoRepository, never()).save(any(Todo.class));
    }

    @Test
    void updateTodo_ShouldUpdateTimestamp() {
        // Given
        LocalDateTime originalUpdatedAt = testTodo.getUpdatedAt();
        when(todoRepository.findById(1L)).thenReturn(Optional.of(testTodo));
        when(todoRepository.save(any(Todo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Todo result = todoService.updateTodo(1L, updateTodoRequest, testUser);

        // Then
        assertNotNull(result.getUpdatedAt());
        assertTrue(result.getUpdatedAt().isAfter(originalUpdatedAt));
    }

    @Test
    void deleteTodo_ShouldDeleteTodoSuccessfully() {
        // Given
        when(todoRepository.findById(1L)).thenReturn(Optional.of(testTodo));

        // When
        todoService.deleteTodo(1L, testUser);

        // Then
        verify(todoRepository, times(1)).findById(1L);
        verify(todoRepository, times(1)).delete(testTodo);
    }

    @Test
    void deleteTodo_ShouldThrowExceptionWhenTodoNotFound() {
        // Given
        when(todoRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> todoService.deleteTodo(1L, testUser));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Todo not found for current user", exception.getReason());
        verify(todoRepository, times(1)).findById(1L);
        verify(todoRepository, never()).delete(any(Todo.class));
    }

    @Test
    void deleteTodo_ShouldThrowExceptionWhenTodoBelongsToDifferentUser() {
        // Given
        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setEmail("another@example.com");

        testTodo.setUser(anotherUser);
        when(todoRepository.findById(1L)).thenReturn(Optional.of(testTodo));

        // When & Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> todoService.deleteTodo(1L, testUser));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Todo not found for current user", exception.getReason());
        verify(todoRepository, times(1)).findById(1L);
        verify(todoRepository, never()).delete(any(Todo.class));
    }

    @Test
    void getTodosByCompletion_ShouldReturnCompletedTodos() {
        // Given
        Todo completedTodo1 = new Todo();
        completedTodo1.setId(1L);
        completedTodo1.setTitle("Completed Todo 1");
        completedTodo1.setCompleted(true);
        completedTodo1.setUser(testUser);

        Todo completedTodo2 = new Todo();
        completedTodo2.setId(2L);
        completedTodo2.setTitle("Completed Todo 2");
        completedTodo2.setCompleted(true);
        completedTodo2.setUser(testUser);

        List<Todo> expectedCompletedTodos = Arrays.asList(completedTodo1, completedTodo2);
        when(todoRepository.findByUserAndCompleted(testUser, true)).thenReturn(expectedCompletedTodos);

        // When
        List<Todo> result = todoService.getTodosByCompletion(testUser, true);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedCompletedTodos, result);
        assertTrue(result.stream().allMatch(Todo::isCompleted));
        verify(todoRepository, times(1)).findByUserAndCompleted(testUser, true);
    }


    @Test
    void getTodosByCompletion_ShouldReturnIncompleteTodos() {
        // Given
        Todo incompleteTodo1 = new Todo();
        incompleteTodo1.setId(1L);
        incompleteTodo1.setTitle("Incomplete Todo 1");
        incompleteTodo1.setCompleted(false);
        incompleteTodo1.setUser(testUser);

        Todo incompleteTodo2 = new Todo();
        incompleteTodo2.setId(2L);
        incompleteTodo2.setTitle("Incomplete Todo 2");
        incompleteTodo2.setCompleted(false);
        incompleteTodo2.setUser(testUser);

        List<Todo> expectedIncompleteTodos = Arrays.asList(incompleteTodo1, incompleteTodo2);
        when(todoRepository.findByUserAndCompleted(testUser, false)).thenReturn(expectedIncompleteTodos);

        // When
        List<Todo> result = todoService.getTodosByCompletion(testUser, false);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedIncompleteTodos, result);
        assertTrue(result.stream().noneMatch(Todo::isCompleted));
        verify(todoRepository, times(1)).findByUserAndCompleted(testUser, false);
    }

    @Test
    void getTodosByCompletion_ShouldReturnEmptyListWhenNoTodosFound() {
        // Given
        when(todoRepository.findByUserAndCompleted(testUser, true)).thenReturn(Arrays.asList());

        // When
        List<Todo> result = todoService.getTodosByCompletion(testUser, true);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(todoRepository, times(1)).findByUserAndCompleted(testUser, true);
    }

    @Test
    void createDataTodo_ShouldHandleCompletedTodoRequest() {
        // Given
        TodoRequest completedTodoRequest = new TodoRequest();
        completedTodoRequest.setTitle("Completed Todo");
        completedTodoRequest.setCompleted(true);

        Todo savedTodo = new Todo();
        savedTodo.setId(1L);
        savedTodo.setTitle(completedTodoRequest.getTitle());
        savedTodo.setCompleted(completedTodoRequest.isCompleted());
        savedTodo.setUser(testUser);

        when(todoRepository.save(any(Todo.class))).thenReturn(savedTodo);

        // When
        Todo result = todoService.createDataTodo(completedTodoRequest, testUser);

        // Then
        assertNotNull(result);
        assertEquals("Completed Todo", result.getTitle());
        assertEquals(true, result.isCompleted());
        assertEquals(testUser, result.getUser());
        verify(todoRepository, times(1)).save(any(Todo.class));
    }

    @Test
    void updateTodo_ShouldNotUpdateWhenBothFieldsAreNull() {
        // Given
        UpdateTodoRequest nullRequest = new UpdateTodoRequest();
        nullRequest.setTitle(null);
        nullRequest.setCompleted(null);

        String originalTitle = testTodo.getTitle();
        boolean originalCompleted = testTodo.isCompleted();

        when(todoRepository.findById(1L)).thenReturn(Optional.of(testTodo));
        when(todoRepository.save(any(Todo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Todo result = todoService.updateTodo(1L, nullRequest, testUser);

        // Then
        assertNotNull(result);
        assertEquals(originalTitle, result.getTitle());
        assertEquals(originalCompleted, result.isCompleted());
        verify(todoRepository, times(1)).save(testTodo);
    }

}