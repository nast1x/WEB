package ru.ssau.todo.repository;

import org.springframework.context.annotation.Profile;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.ssau.todo.entity.Task;
import ru.ssau.todo.entity.TaskStatus;

import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Profile("jdbc")
public class TaskJdbcRepository implements TaskRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Task> taskRowMapper = (rs, rowNum) -> {
        Task task = new Task();
        task.setId(rs.getLong("id"));
        task.setTitle(rs.getString("title"));
        task.setStatus(TaskStatus.valueOf(rs.getString("status")));
        task.setCreatedBy(rs.getLong("created_by"));
        task.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return task;
    };

    public TaskJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Task create(Task task) {

        String sql = "INSERT INTO task (title, status, created_by, created_at) VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    sql,
                    new String[]{"id"}
            );
            ps.setString(1, task.getTitle());
            ps.setString(2, task.getStatus().name());
            ps.setLong(3, task.getCreatedBy());
            ps.setTimestamp(4, java.sql.Timestamp.valueOf(LocalDateTime.now()));
            return ps;
        }, keyHolder);

        task.setId(keyHolder.getKey().longValue());
        task.setCreatedAt(LocalDateTime.now());

        return task;
    }

    @Override
    public Optional<Task> findById(Long id) {
        String sql = "SELECT * FROM task WHERE id = ?";
        try {
            Task task = jdbcTemplate.queryForObject(sql, taskRowMapper, id);
            return Optional.ofNullable(task);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Task> findAll(LocalDateTime from, LocalDateTime to, Long userId) {
        String sql = "SELECT * FROM task WHERE created_by = ? AND created_at >= ? AND created_at <= ? ORDER BY created_at DESC";

        return jdbcTemplate.query(sql, taskRowMapper, userId,
                java.sql.Timestamp.valueOf(from),
                java.sql.Timestamp.valueOf(to));
    }

    @Override
    public void update(Task task) throws Exception {
        String sql = "UPDATE task SET title = ?, status = ? WHERE id = ?";

        int rowsAffected = jdbcTemplate.update(sql,
                task.getTitle(),
                task.getStatus().name(),
                task.getId());

        if (rowsAffected == 0) {
            throw new Exception("Task not found with id: " + task.getId());
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM task WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public Long countActiveTasksByUserId(Long userId) {
        String sql = "SELECT COUNT(*) FROM task WHERE created_by = ? AND status IN (?, ?)";

        return jdbcTemplate.queryForObject(sql, Long.class,
                userId,
                TaskStatus.OPEN.name(),
                TaskStatus.IN_PROGRESS.name());
    }
}
