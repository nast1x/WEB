package ru.ssau.todo.entity;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;


public class Task {

    private Long id;
    @NotBlank(message = "Title cannot be empty or null")
    private String title;
    @NotNull(message = "Status cannot be null")
    private TaskStatus status;
    @Min(value = 1, message = "Invalid user ID")
    private Long createdBy;
    private LocalDateTime createdAt;

    public Task() {
    }

    public Task(String title, TaskStatus status, Long createdBy) {
        this.title = title;
        this.status = status;
        this.createdBy = createdBy;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
