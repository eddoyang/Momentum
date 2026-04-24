package com.example.demo.persistence;

import com.example.demo.model.*;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class JsonReaderTest extends JsonTest {
    TaskManager taskManager;
    private UUID id1;
    private UUID id2;
    private ZonedDateTime time1;
    private ZonedDateTime time2;
    private Task task1;
    private Task task2;

    @BeforeEach
    void setUp() {
        id1 = UUID.fromString("00000000-0000-0000-0000-000000000001");
        id2 = UUID.fromString("00000000-0000-0000-0000-000000000002");
        time1 = ZonedDateTime.of(2026, 1, 1, 14, 30, 0, 0, ZoneId.of("UTC"));
        time2 = ZonedDateTime.of(2026, 1, 1, 4, 30, 0, 0, ZoneId.of("UTC"));
        taskManager = new TaskManager();
        task1 = new Task(id1, "abc", "aaa", false, time1);
        task2 = new Task(id2,"zxy", "bbb", true, time2);
    }
    //No such JSON
    @Test
    void testReaderNonExistentFile() {
        JsonReader reader = new JsonReader("data/noSuch.json");
        try {
            taskManager = reader.read();
            fail("IOException expected");
        } catch (IOException e) {
            // pass
        }
    }

    //Test empty JSON
    @Test
    void testReaderEmptyManager() {
        JsonReader reader = new JsonReader("data/persistence/testReaderEmpty.json");
        try {
            taskManager = reader.read();
            assertEquals(0, taskManager.getTaskMap().size());
        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }

    //Test general JSON
    @Test
    void testReaderGeneral() {
        JsonReader reader = new JsonReader("data/persistence/testReaderGeneral.json");
        try {
            taskManager = reader.read();
            Map<UUID, Task> map = taskManager.getTaskMap();
            task1 = map.get(id1);
            task2 = map.get(id2);

            checkTask(id1, "abc", "aaa", false, time1, task1);
            checkTask(id2, "zxy", "bbb", true, time2, task2);

        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }
}

