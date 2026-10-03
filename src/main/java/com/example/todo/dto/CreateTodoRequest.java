package com.example.todo.dto;

import com.example.todo.model.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class CreateTodoRequest {

    @NotBlank(message = "Title must not be empty or blank")
    @Size(min = 1, max = 150, message = "Title must be between 1 and 150 characters")
    private String title;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    private Priority priority = Priority.MEDIUM;

    private LocalDate dueDate;

    public CreateTodoRequest() {
    }

    public CreateTodoRequest(String title, String description, Priority priority, LocalDate dueDate) {
        this.title = title;
        this.description = description;
        this.priority = priority != null ? priority : Priority.MEDIUM;
        this.dueDate = dueDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
}
