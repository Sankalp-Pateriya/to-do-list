package com.example.todo.service;

import com.example.todo.dto.CreateTodoRequest;
import com.example.todo.dto.TodoStats;
import com.example.todo.dto.UpdateTodoRequest;
import com.example.todo.exception.ResourceNotFoundException;
import com.example.todo.model.Priority;
import com.example.todo.model.Todo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TodoServiceTest {

    private TodoService todoService;

    @BeforeEach
    void setUp() {
        todoService = new TodoService();
        todoService.populateInitialData();
    }

    @Test
    void testPopulatedDataIsNotEmpty() {
        List<Todo> all = todoService.getAllTodos(null, null, null, "id", "asc");
        assertFalse(all.isEmpty(), "Initial populated list should not be empty");
        assertEquals(5, all.size(), "Should have 5 initial dummy todo items");
    }

    @Test
    void testGetTodoById_Success() {
        Todo todo = todoService.getTodoById(1L);
        assertNotNull(todo);
        assertEquals(1L, todo.getId());
    }

    @Test
    void testGetTodoById_NotFound() {
        assertThrows(ResourceNotFoundException.class, () -> todoService.getTodoById(9999L));
    }

    @Test
    void testCreateTodo() {
        CreateTodoRequest request = new CreateTodoRequest(
                "New Test Task",
                "Testing create operation",
                Priority.HIGH,
                LocalDate.now().plusDays(5)
        );

        Todo created = todoService.createTodo(request);
        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals("New Test Task", created.getTitle());
        assertEquals(Priority.HIGH, created.getPriority());
        assertFalse(created.isCompleted());

        Todo retrieved = todoService.getTodoById(created.getId());
        assertEquals(created.getTitle(), retrieved.getTitle());
    }

    @Test
    void testUpdateTodo() {
        UpdateTodoRequest updateRequest = new UpdateTodoRequest(
                "Updated Title",
                "Updated Description",
                true,
                Priority.LOW,
                LocalDate.now().plusDays(10)
        );

        Todo updated = todoService.updateTodo(1L, updateRequest);
        assertEquals("Updated Title", updated.getTitle());
        assertEquals("Updated Description", updated.getDescription());
        assertTrue(updated.isCompleted());
        assertEquals(Priority.LOW, updated.getPriority());
    }

    @Test
    void testPatchTodo() {
        UpdateTodoRequest patchRequest = new UpdateTodoRequest();
        patchRequest.setTitle("Only Title Changed");

        Todo patched = todoService.patchTodo(1L, patchRequest);
        assertEquals("Only Title Changed", patched.getTitle());
        // Priority should remain what it was
        assertNotNull(patched.getPriority());
    }

    @Test
    void testToggleTodoCompletion() {
        Todo before = todoService.getTodoById(1L);
        boolean initialStatus = before.isCompleted();

        Todo toggled = todoService.toggleTodoCompletion(1L);
        assertEquals(!initialStatus, toggled.isCompleted());
    }

    @Test
    void testDeleteTodo() {
        todoService.deleteTodo(1L);
        assertThrows(ResourceNotFoundException.class, () -> todoService.getTodoById(1L));
    }

    @Test
    void testDeleteCompletedTodos() {
        int deletedCount = todoService.deleteCompletedTodos();
        assertTrue(deletedCount > 0);

        List<Todo> remaining = todoService.getAllTodos(true, null, null, "id", "asc");
        assertTrue(remaining.isEmpty(), "No completed todos should remain");
    }

    @Test
    void testFilterByCompleted() {
        List<Todo> completed = todoService.getAllTodos(true, null, null, "id", "asc");
        for (Todo t : completed) {
            assertTrue(t.isCompleted());
        }

        List<Todo> pending = todoService.getAllTodos(false, null, null, "id", "asc");
        for (Todo t : pending) {
            assertFalse(t.isCompleted());
        }
    }

    @Test
    void testFilterByPriority() {
        List<Todo> high = todoService.getAllTodos(null, Priority.HIGH, null, "id", "asc");
        for (Todo t : high) {
            assertEquals(Priority.HIGH, t.getPriority());
        }
    }

    @Test
    void testSearch() {
        List<Todo> searchResults = todoService.getAllTodos(null, null, "Docker", "id", "asc");
        assertFalse(searchResults.isEmpty());
        assertTrue(searchResults.getFirst().getTitle().contains("Docker") ||
                searchResults.getFirst().getDescription().contains("Docker"));
    }

    @Test
    void testGetStats() {
        TodoStats stats = todoService.getStats();
        assertEquals(5, stats.getTotal());
        assertEquals(2, stats.getCompleted());
        assertEquals(3, stats.getPending());
        assertEquals(40.0, stats.getCompletionPercentage());
    }
}
