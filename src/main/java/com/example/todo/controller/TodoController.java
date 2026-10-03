package com.example.todo.controller;

import com.example.todo.dto.CreateTodoRequest;
import com.example.todo.dto.TodoStats;
import com.example.todo.dto.UpdateTodoRequest;
import com.example.todo.model.Priority;
import com.example.todo.model.Todo;
import com.example.todo.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/todos")
@CrossOrigin(origins = "*")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    /**
     * GET /api/todos
     * Retrieve all todos with optional query parameters:
     * - completed: true/false
     * - priority: LOW/MEDIUM/HIGH
     * - search: keyword in title or description
     * - sortBy: id, title, dueDate, priority, createdAt
     * - order: asc/desc
     */
    @GetMapping
    public ResponseEntity<List<Todo>> getAllTodos(
            @RequestParam(required = false) Boolean completed,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String order) {

        List<Todo> todos = todoService.getAllTodos(completed, priority, search, sortBy, order);
        return ResponseEntity.ok(todos);
    }

    /**
     * GET /api/todos/{id}
     * Retrieve a specific Todo by its ID.
     */
    @GetMapping("/{id:[0-9]+}")
    public ResponseEntity<Todo> getTodoById(@PathVariable Long id) {
        Todo todo = todoService.getTodoById(id);
        return ResponseEntity.ok(todo);
    }

    /**
     * POST /api/todos
     * Create a new Todo.
     */
    @PostMapping
    public ResponseEntity<Todo> createTodo(@Valid @RequestBody CreateTodoRequest request) {
        Todo created = todoService.createTodo(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    /**
     * PUT /api/todos/{id}
     * Fully update an existing Todo.
     */
    @PutMapping("/{id:[0-9]+}")
    public ResponseEntity<Todo> updateTodo(@PathVariable Long id, @Valid @RequestBody UpdateTodoRequest request) {
        Todo updated = todoService.updateTodo(id, request);
        return ResponseEntity.ok(updated);
    }

    /**
     * PATCH /api/todos/{id}
     * Partially update an existing Todo.
     */
    @PatchMapping("/{id:[0-9]+}")
    public ResponseEntity<Todo> patchTodo(@PathVariable Long id, @RequestBody UpdateTodoRequest request) {
        Todo patched = todoService.patchTodo(id, request);
        return ResponseEntity.ok(patched);
    }

    /**
     * PATCH /api/todos/{id}/toggle
     * Toggle completion status between done and pending.
     */
    @PatchMapping("/{id:[0-9]+}/toggle")
    public ResponseEntity<Todo> toggleTodo(@PathVariable Long id) {
        Todo toggled = todoService.toggleTodoCompletion(id);
        return ResponseEntity.ok(toggled);
    }

    /**
     * DELETE /api/todos/{id}
     * Delete a single Todo by ID.
     */
    @DeleteMapping("/{id:[0-9]+}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
        todoService.deleteTodo(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * DELETE /api/todos/completed
     * Bulk delete all completed todos.
     */
    @DeleteMapping("/completed")
    public ResponseEntity<Map<String, Object>> deleteCompletedTodos() {
        int count = todoService.deleteCompletedTodos();
        return ResponseEntity.ok(Map.of(
                "message", "Completed todos removed successfully",
                "deletedCount", count
        ));
    }

    /**
     * DELETE /api/todos
     * Delete all todos.
     */
    @DeleteMapping
    public ResponseEntity<Void> clearAllTodos() {
        todoService.clearAllTodos();
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/todos/stats
     * Get aggregate statistics on todos.
     */
    @GetMapping("/stats")
    public ResponseEntity<TodoStats> getStats() {
        TodoStats stats = todoService.getStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * POST /api/todos/reset
     * Reset the todo list back to initial sample populated data.
     */
    @PostMapping("/reset")
    public ResponseEntity<Map<String, Object>> resetData() {
        todoService.populateInitialData();
        return ResponseEntity.ok(Map.of(
                "message", "Todo list reset to default populated data",
                "stats", todoService.getStats()
        ));
    }
}
