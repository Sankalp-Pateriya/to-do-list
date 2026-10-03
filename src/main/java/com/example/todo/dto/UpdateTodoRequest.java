package com.example.todo.dto;

import com.example.todo.model.Priority;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class UpdateTodoRequest {

    @Size(min = 1, max = 150, message = "Title must be between 1 and 150 characters if provided")
    private String title;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    private Boolean completed;

    private Priority priority;

    private LocalDate dueDate;

    public UpdateTodoRequest() {
    }

    public UpdateTodoRequest(String title, String description, Boolean completed, Priority priority, LocalDate dueDate) {
        this.title = title;
        this.description = description;
        this.completed = completed;
        this.priority = priority;
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

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
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
