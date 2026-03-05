package ru.ssau.todo.service;

import org.springframework.stereotype.Service;
import ru.ssau.todo.dto.TaskDto;
import ru.ssau.todo.entity.Task;
import ru.ssau.todo.entity.TaskStatus;
import ru.ssau.todo.entity.User;
import ru.ssau.todo.repository.TaskRepository;
import ru.ssau.todo.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskService {
    private static final int MAX_ACTIVE_TASKS = 10;
    private static final int MIN_TASK_AGE_MINUTES = 5;
    private static final String ERROR_MAX_ACTIVE_TASKS = "User cannot have more than %d active tasks";

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    private TaskDto toDto(Task task) {
        return new TaskDto(
                task.getId(),
                task.getTitle(),
                task.getStatus(),
                task.getCreatedBy().getId(),
                task.getCreatedAt()
        );
    }

    private Task toEntity(TaskDto dto, User user) {
        Task task = new Task();
        task.setId(dto.getId());
        task.setTitle(dto.getTitle());
        task.setStatus(dto.getStatus());
        task.setCreatedBy(user);
        task.setCreatedAt(dto.getCreatedAt());
        return task;
    }

    private void checkActiveTasksLimit(Long userId) {
        long activeCount = taskRepository.countActiveTasksByUserId(userId);
        if (activeCount >= MAX_ACTIVE_TASKS) {
            throw new IllegalStateException(
                    String.format(ERROR_MAX_ACTIVE_TASKS, MAX_ACTIVE_TASKS)
            );
        }
    }


    public TaskDto createTask(TaskDto taskDto) {
        User user = userRepository.findById(taskDto.getCreatedBy())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + taskDto.getCreatedBy()));

        checkActiveTasksLimit(user.getId());

        Task task = toEntity(taskDto, user);
        task.setCreatedAt(LocalDateTime.now());
        Task saved = taskRepository.save(task);
        return toDto(saved);
    }

    public TaskDto updateTask(TaskDto taskDto) {
        Optional<Task> existingTaskOpt = taskRepository.findById(taskDto.getId());

        Task existingTask = existingTaskOpt.get();

        if ((taskDto.getStatus() == TaskStatus.OPEN || taskDto.getStatus() == TaskStatus.IN_PROGRESS) &&
                (existingTask.getStatus() == TaskStatus.DONE || existingTask.getStatus() == TaskStatus.CLOSED)) {
            checkActiveTasksLimit(existingTask.getCreatedBy().getId());
        }

        existingTask.setTitle(taskDto.getTitle());
        existingTask.setStatus(taskDto.getStatus());
        Task saved = taskRepository.save(existingTask);
        return toDto(saved);
    }

    public void deleteTask(long id) {
        Optional<Task> taskOpt = taskRepository.findById(id);
        if (taskOpt.isEmpty()) {
            throw new IllegalArgumentException("Task not found with id: " + id);
        }

        Task task = taskOpt.get();
        LocalDateTime now = LocalDateTime.now();
        long minutesElapsed = ChronoUnit.MINUTES.between(task.getCreatedAt(), now);

        if (minutesElapsed < MIN_TASK_AGE_MINUTES) {
            throw new IllegalStateException(
                    "Cannot delete task created less than 5 minutes ago. " +
                            "Elapsed time: " + minutesElapsed + " minutes ");
        }

        taskRepository.deleteById(id);
    }

    public Optional<TaskDto> getTaskById(long id) {
        return taskRepository.findById(id).map(this::toDto);
    }

    public List<TaskDto> getTasks(LocalDateTime from, LocalDateTime to, long userId) {
        return taskRepository.findAll(from, to, userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public long countActiveTasks(long userId) {
        return taskRepository.countActiveTasksByUserId(userId);
    }
}