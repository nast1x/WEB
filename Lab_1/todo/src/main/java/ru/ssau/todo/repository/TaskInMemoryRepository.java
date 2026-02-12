package ru.ssau.todo.repository;

import org.springframework.stereotype.Repository;
import ru.ssau.todo.entity.Task;
import ru.ssau.todo.entity.TaskStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class TaskInMemoryRepository implements TaskRepository {

    private final Map<Long, Task> tasks = new ConcurrentHashMap<>();
    private long idGenerator = 1;

    @Override
    public Task create(Task task) {
        if (task == null || task.getStatus() == null || task.getTitle() == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }

        task.setId(idGenerator);
        task.setCreatedAt(LocalDateTime.now());
        tasks.put(idGenerator, task);
        idGenerator++;
        return task;
    }

    @Override
    public Optional<Task> findById(long id) {
        return Optional.ofNullable(tasks.get(id));
    }

    @Override
    public List<Task> findAll(LocalDateTime from, LocalDateTime to, long userId) {
        return tasks.values().stream()
                .filter(task -> task.getCreatedBy() == userId)
                .filter(task -> !task.getCreatedAt().isBefore(from))
                .filter(task -> !task.getCreatedAt().isAfter(to)).collect(Collectors.toList());
    }

    @Override
    public void update(Task task) throws Exception {
        Task existingTask = tasks.get(task.getId());
        if (existingTask == null) {
            throw new Exception();
        }
        task.setCreatedAt(existingTask.getCreatedAt());
        task.setCreatedBy(existingTask.getCreatedBy());
        tasks.put(task.getId(), task);
    }

    @Override
    public void deleteById(long id) {
        tasks.remove(id);
    }

    @Override
    public long countActiveTasksByUserId(long userId) {
        return tasks.values().stream()
                .filter(task -> task.getCreatedBy() == userId)
                .filter(task -> task.getStatus() == TaskStatus.OPEN || task.getStatus() == TaskStatus.IN_PROGRESS).count();
    }
}
