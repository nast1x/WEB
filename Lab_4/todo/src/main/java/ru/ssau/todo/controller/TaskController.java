package ru.ssau.todo.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.ssau.todo.dto.TaskDto;
import ru.ssau.todo.repository.UserRepository;
import ru.ssau.todo.service.TaskService;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService taskService;
    private final UserRepository userRepository; // Инжектим репозиторий

    public TaskController(TaskService taskService, UserRepository userRepository) {
        this.taskService = taskService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<TaskDto>> getTasks(
            @RequestParam(required = false) LocalDateTime from,
            @RequestParam(required = false) LocalDateTime to,
            @RequestParam long userId) {

        LocalDateTime startDate = (from != null) ? from : LocalDateTime.MIN;
        LocalDateTime endDate = (to != null) ? to : LocalDateTime.MAX;

        List<TaskDto> tasks = taskService.getTasks(startDate, endDate, userId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable Long id) {
        Optional<TaskDto> task = taskService.getTaskById(id);
        return task.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TaskDto> createTask(@RequestBody @Valid TaskDto task) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Long userId = userRepository.findByUsername(username)
                .map(u -> u.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        task.setCreatedBy(userId);
        TaskDto created = taskService.createTask(task);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateTask(@PathVariable long id, @RequestBody @Valid TaskDto task) {
        task.setId(id);

        taskService.updateTask(task);

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