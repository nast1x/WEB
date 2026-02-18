package ru.ssau.todo.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.ssau.todo.entity.Task;
import ru.ssau.todo.service.TaskService;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<Task>> getTasks(@RequestParam(required = false) LocalDateTime from, @RequestParam(required = false) LocalDateTime to, @RequestParam long userId) {

        LocalDateTime startDate = (from != null) ? from : LocalDateTime.MIN;
        LocalDateTime endDate = (to != null) ? to : LocalDateTime.MAX;

        List<Task> tasks = taskService.getTasks(startDate, endDate, userId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        Optional<Task> task = taskService.getTaskById(id);
        return task.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody @Valid Task task) {
        Task created = taskService.createTask(task);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateTask(@PathVariable long id, @RequestBody @Valid Task task) {
        task.setId(id);
        try {
            taskService.updateTask(task);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable long id) {
        try {
            taskService.deleteTask(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @GetMapping("/active/count")
    public ResponseEntity<Long> countActiveTasks(@RequestParam long userId) {
        long count = taskService.countActiveTasks(userId);
        return ResponseEntity.ok(count);
    }
}
