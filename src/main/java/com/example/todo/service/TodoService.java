package com.example.todo.service;

import com.example.todo.dto.CreateTodoRequest;
import com.example.todo.dto.TodoStats;
import com.example.todo.dto.UpdateTodoRequest;
import com.example.todo.exception.ResourceNotFoundException;
import com.example.todo.model.Priority;
import com.example.todo.model.Todo;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TodoService {

    // Thread-safe in-memory storage using List
    private final List<Todo> todoList = new CopyOnWriteArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    @PostConstruct
    public void init() {
        populateInitialData();
    }

    /**
     * Pre-populates dummy Todo items as required.
     */
    public synchronized void populateInitialData() {
        todoList.clear();
        idGenerator.set(0);

        LocalDateTime now = LocalDateTime.now();

        todoList.add(new Todo(
                idGenerator.incrementAndGet(),
                "Set up Spring Boot project structure",
                "Create Maven project with required dependencies and package layout",
                true,
                Priority.HIGH,
                LocalDate.now().minusDays(1),
                now.minusDays(2),
                now.minusDays(1)
        ));

        todoList.add(new Todo(
                idGenerator.incrementAndGet(),
                "Implement Todo REST API endpoints",
                "Create GET, POST, PUT, PATCH, and DELETE operations with validation",
                true,
                Priority.HIGH,
                LocalDate.now(),
                now.minusDays(1),
                now
        ));

        todoList.add(new Todo(
                idGenerator.incrementAndGet(),
                "Write comprehensive test cases",
                "Cover service logic, controller endpoints, and validation scenarios",
                false,
                Priority.MEDIUM,
                LocalDate.now().plusDays(2),
                now.minusHours(5),
                now.minusHours(5)
        ));

        todoList.add(new Todo(
                idGenerator.incrementAndGet(),
                "Build modern interactive Web UI dashboard",
                "Develop responsive dashboard with filtering, search, and instant updates",
                false,
                Priority.MEDIUM,
                LocalDate.now().plusDays(3),
                now.minusHours(3),
                now.minusHours(3)
        ));

        todoList.add(new Todo(
                idGenerator.incrementAndGet(),
                "Prepare Docker containerization",
                "Create Dockerfile and verify container build for deployment",
                false,
                Priority.LOW,
                LocalDate.now().plusDays(7),
                now.minusHours(1),
                now.minusHours(1)
        ));
    }

    /**
     * Retrieve all todos with optional filtering, search, and sorting.
     */
    public List<Todo> getAllTodos(Boolean completed, Priority priority, String search, String sortBy, String order) {
        Stream<Todo> stream = todoList.stream();

        // Filter by completion status
        if (completed != null) {
            stream = stream.filter(todo -> todo.isCompleted() == completed);
        }

        // Filter by priority
        if (priority != null) {
            stream = stream.filter(todo -> todo.getPriority() == priority);
        }

        // Filter by keyword search (case-insensitive) in title or description
        if (search != null && !search.trim().isEmpty()) {
            String query = search.trim().toLowerCase();
            stream = stream.filter(todo ->
                    (todo.getTitle() != null && todo.getTitle().toLowerCase().contains(query)) ||
                    (todo.getDescription() != null && todo.getDescription().toLowerCase().contains(query))
            );
        }

        // Sorting
        Comparator<Todo> comparator;
        String field = sortBy != null ? sortBy.trim().toLowerCase() : "id";
        switch (field) {
            case "title":
                comparator = Comparator.comparing(todo -> todo.getTitle() != null ? todo.getTitle().toLowerCase() : "");
                break;
            case "duedate":
            case "due_date":
                comparator = Comparator.comparing(Todo::getDueDate, Comparator.nullsLast(Comparator.naturalOrder()));
                break;
            case "priority":
                comparator = Comparator.comparing(todo -> todo.getPriority() != null ? todo.getPriority() : Priority.LOW);
                break;
            case "createdat":
            case "created_at":
                comparator = Comparator.comparing(Todo::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder()));
                break;
            case "id":
            default:
                comparator = Comparator.comparing(Todo::getId);
                break;
        }

        if ("desc".equalsIgnoreCase(order)) {
            comparator = comparator.reversed();
        }

        return stream.sorted(comparator).collect(Collectors.toList());
    }

    /**
     * Retrieve a single Todo by its ID.
     */
    public Todo getTodoById(Long id) {
        return todoList.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Todo not found with id: " + id));
    }

    /**
     * Create a new Todo.
     */
    public Todo createTodo(CreateTodoRequest request) {
        LocalDateTime now = LocalDateTime.now();
        Priority priority = request.getPriority() != null ? request.getPriority() : Priority.MEDIUM;

        Todo newTodo = new Todo(
                idGenerator.incrementAndGet(),
                request.getTitle().trim(),
                request.getDescription() != null ? request.getDescription().trim() : null,
                false,
                priority,
                request.getDueDate(),
                now,
                now
        );

        todoList.add(newTodo);
        return newTodo;
    }

    /**
     * Complete update of an existing Todo (PUT).
     */
    public Todo updateTodo(Long id, UpdateTodoRequest request) {
        Todo existing = getTodoById(id);

        if (request.getTitle() != null) {
            existing.setTitle(request.getTitle().trim());
        }
        existing.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);
        if (request.getCompleted() != null) {
            existing.setCompleted(request.getCompleted());
        }
        if (request.getPriority() != null) {
            existing.setPriority(request.getPriority());
        }
        existing.setDueDate(request.getDueDate());
        existing.setUpdatedAt(LocalDateTime.now());

        return existing;
    }

    /**
     * Partial update of an existing Todo (PATCH).
     */
    public Todo patchTodo(Long id, UpdateTodoRequest request) {
        Todo existing = getTodoById(id);

        if (request.getTitle() != null && !request.getTitle().trim().isEmpty()) {
            existing.setTitle(request.getTitle().trim());
        }
        if (request.getDescription() != null) {
            existing.setDescription(request.getDescription().trim());
        }
        if (request.getCompleted() != null) {
            existing.setCompleted(request.getCompleted());
        }
        if (request.getPriority() != null) {
            existing.setPriority(request.getPriority());
        }
        if (request.getDueDate() != null) {
            existing.setDueDate(request.getDueDate());
        }
        existing.setUpdatedAt(LocalDateTime.now());

        return existing;
    }

    /**
     * Toggle completion status of a Todo.
     */
    public Todo toggleTodoCompletion(Long id) {
        Todo existing = getTodoById(id);
        existing.setCompleted(!existing.isCompleted());
        existing.setUpdatedAt(LocalDateTime.now());
        return existing;
    }

    /**
     * Delete a Todo by its ID.
     */
    public void deleteTodo(Long id) {
        Todo existing = getTodoById(id);
        todoList.remove(existing);
    }

    /**
     * Delete all completed todos.
     */
    public int deleteCompletedTodos() {
        List<Todo> completedList = todoList.stream()
                .filter(Todo::isCompleted)
                .toList();
        todoList.removeAll(completedList);
        return completedList.size();
    }

    /**
     * Clear all todos.
     */
    public int clearAllTodos() {
        int count = todoList.size();
        todoList.clear();
        return count;
    }

    /**
     * Get aggregate statistics.
     */
    public TodoStats getStats() {
        int total = todoList.size();
        long completed = todoList.stream().filter(Todo::isCompleted).count();
        long pending = total - completed;
        double percentage = total > 0 ? Math.round(((double) completed / total) * 100.0 * 10.0) / 10.0 : 0.0;

        return new TodoStats(total, completed, pending, percentage);
    }
}
