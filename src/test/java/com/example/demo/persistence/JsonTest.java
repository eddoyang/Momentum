package com.example.demo.persistence;

import com.example.demo.model.Task;
import java.time.ZonedDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


public class JsonTest {
    protected void checkTask(UUID id, String title, String category,
                             boolean isCompelte, ZonedDateTime deadline, Task task) {
        assertEquals(id, task.getId());
        assertEquals(title, task.getTitle());
        assertEquals(category, task.getCategory());
        assertEquals(isCompelte, task.isComplete());
        assertEquals(deadline, task.getDeadline());
    }
}

