package io.github.eddoyang.momentum.persistence;

import io.github.eddoyang.momentum.model.Task;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Repository
public class TaskDao {

    private final JdbcClient db;
    private static final String COLUMNS = "id, title, category, is_complete, deadline";


    //---------------- Methods ----------------

    public TaskDao(JdbcClient db) {
        this.db = db;
    }

    private static final RowMapper<Task> MAPPER = (rs, rowNum) -> new Task(
        UUID.fromString(rs.getString("id")),
        rs.getString("title"),
        rs.getString("category"),
        rs.getBoolean("is_complete"),
        fromDb(rs.getObject("deadline", LocalDateTime.class))
    );

    public void insert(Task t) {
        db.sql("""
                INSERT INTO tasks (id, title, category, is_complete, deadline)
                VALUES (:id, :title, :category, :isComplete, :deadline)
                """)
            .param("id", t.getId().toString())
            .param("title", t.getTitle())
            .param("category", t.getCategory())
            .param("isComplete", t.isComplete())
            .param("deadline", toDb(t.getDeadline()))
            .update();
    }

    public Optional<Task> findById(UUID id) {
        return db.sql("SELECT " + COLUMNS + " FROM tasks WHERE id = :id")
                .param("id", id.toString())
                .query(MAPPER)
                .optional();
    }

    public Optional<Task> findNext() {
        return db.sql("""
                SELECT %s FROM tasks
                WHERE is_complete = FALSE
                ORDER BY deadline ASC
                LIMIT 1
                """.formatted(COLUMNS))
                .query(MAPPER)
                .optional();
    }

    public List<Task> findAllByDeadline() {
        return db.sql("SELECT " + COLUMNS + " FROM tasks ORDER BY deadline ASC")
                .query(MAPPER)
                .list();
    }


    public List<Task> findByCategory(String category) {
        return db.sql("SELECT " + COLUMNS + " FROM tasks WHERE category = :c ORDER BY deadline ASC")
                .param("c", category)
                .query(MAPPER)
                .list();
    }

    public void update(Task t) {
        db.sql("""
                UPDATE tasks
                SET title = :title, category = :category, deadline = :deadline
                WHERE id = :id
                """)
            .param("title", t.getTitle())
            .param("category", t.getCategory())
            .param("deadline", toDb(t.getDeadline()))
            .param("id", t.getId().toString())
            .update();
    }

    public void markComplete(UUID id) {
        db.sql("UPDATE tasks SET is_complete = TRUE WHERE id = :id")
            .param("id", id.toString())
            .update();
    }

    public void delete(UUID id) {
        db.sql("DELETE FROM tasks WHERE id = :id")
            .param("id", id.toString())
            .update();
    }

    public long count() {
        return db.sql("SELECT COUNT(*) FROM tasks").query(Long.class).single();
    }

    //---------------- Helpers ----------------

    private static LocalDateTime toDb(ZonedDateTime t) {
        return t.withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime().truncatedTo(ChronoUnit.SECONDS);
    }

    private static ZonedDateTime fromDb(LocalDateTime t) {
        return t.atZone(ZoneOffset.UTC);
    }
}
