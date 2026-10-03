package com.example.todo.dto;

public class TodoStats {

    private long total;
    private long completed;
    private long pending;
    private double completionPercentage;

    public TodoStats() {
    }

    public TodoStats(long total, long completed, long pending, double completionPercentage) {
        this.total = total;
        this.completed = completed;
        this.pending = pending;
        this.completionPercentage = completionPercentage;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getCompleted() {
        return completed;
    }

    public void setCompleted(long completed) {
        this.completed = completed;
    }

    public long getPending() {
        return pending;
    }

    public void setPending(long pending) {
        this.pending = pending;
    }

    public double getCompletionPercentage() {
        return completionPercentage;
    }

    public void setCompletionPercentage(double completionPercentage) {
        this.completionPercentage = completionPercentage;
    }
}
