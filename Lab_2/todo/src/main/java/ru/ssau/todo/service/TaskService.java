package ru.ssau.todo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.todo.entity.Task;
import ru.ssau.todo.entity.TaskStatus;
import ru.ssau.todo.repository.TaskRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /**
     * Создание новой задачи с проверкой бизнес-правил
     */
    @Transactional
    public Task createTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }
        // Проверка 1: не более 10 активных задач у пользователя
        long activeCount = taskRepository.countActiveTasksByUserId(task.getCreatedBy());
        if (activeCount >= 10) {
            throw new IllegalStateException(
                    "User cannot have more than 10 active tasks. Current count: " + activeCount);
        }

        return taskRepository.create(task);
    }

    /**
     * Обновление задачи
     */
    @Transactional
    public void updateTask(Task task) throws Exception {
        validateTask(task);

        // Проверка существования задачи
        Optional<Task> existingTaskOpt = taskRepository.findById(task.getId());
        if (existingTaskOpt.isEmpty()) {
            throw new Exception("Task not found with id: " + task.getId());
        }

        Task existingTask = existingTaskOpt.get();

        // Если статус меняется на активный, проверяем лимит
        if ((task.getStatus() == TaskStatus.OPEN || task.getStatus() == TaskStatus.IN_PROGRESS) &&
                (existingTask.getStatus() == TaskStatus.DONE || existingTask.getStatus() == TaskStatus.CLOSED)) {

            long activeCount = taskRepository.countActiveTasksByUserId(task.getCreatedBy());
            if (activeCount >= 10) {
                throw new IllegalStateException(
                        "User cannot have more than 10 active tasks. Current count: " + activeCount);
            }
        }

        taskRepository.update(task);
    }

    /**
     * Удаление задачи с проверкой времени создания
     */
    @Transactional
    public void deleteTask(long id) {
        Optional<Task> taskOpt = taskRepository.findById(id);
        if (taskOpt.isEmpty()) {
            throw new IllegalArgumentException("Task not found with id: " + id);
        }

        Task task = taskOpt.get();
        LocalDateTime now = LocalDateTime.now();
        long minutesElapsed = ChronoUnit.MINUTES.between(task.getCreatedAt(), now);

        // Проверка 2: нельзя удалять задачи младше 5 минут
        if (minutesElapsed < 5) {
            throw new IllegalStateException(
                    "Cannot delete task created less than 5 minutes ago. " +
                            "Elapsed time: " + minutesElapsed + " minutes");
        }

        taskRepository.deleteById(id);
    }

    /**
     * Получение задачи по ID
     */
    public Optional<Task> getTaskById(long id) {
        return taskRepository.findById(id);
    }

    /**
     * Получение всех задач пользователя в диапазоне дат
     */
    public List<Task> getTasks(LocalDateTime from, LocalDateTime to, long userId) {
        return taskRepository.findAll(from, to, userId);
    }

    /**
     * Подсчет активных задач пользователя
     */
    public long countActiveTasks(long userId) {
        return taskRepository.countActiveTasksByUserId(userId);
    }

    /**
     * Валидация задачи
     */
    private void validateTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }
        if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Task title cannot be empty");
        }
        if (task.getStatus() == null) {
            throw new IllegalArgumentException("Task status cannot be null");
        }
        if (task.getCreatedBy() == null || task.getCreatedBy() <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
    }
}
