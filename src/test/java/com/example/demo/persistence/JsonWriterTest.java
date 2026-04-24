package com.example.demo.persistence;

import com.example.demo.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


class JsonWriterTest extends JsonTest {
    private TaskManager taskManager;
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


    //Test invalid file
    @Test
    void testWriterInvalidFile() {
        try {
            JsonWriter writer = new JsonWriter("data/my\0illegal:fileName.json");
            writer.open();
            fail("IOException was expected");
        } catch (IOException e) {
            // pass
        }
    }


    //Test writing empty manager
    @Test
    void testWriterEmptyManager() {
        try {
            JsonWriter writer = new JsonWriter("data/persistence/testWriterEmpty.json");
            writer.open();
            writer.write(taskManager);
            writer.close();

            JsonReader reader = new JsonReader("data/persistence/testWriterEmpty.json");
            taskManager = reader.read();
            assertEquals(0, taskManager.getTaskMap().size());
        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }

    //Test writing General Manager
    @Test
    void testWriterGeneralManager() {
        try {
            taskManager.addTask(task1);
            taskManager.addTask(task2);

            JsonWriter writer = new JsonWriter("data/persistence/testReaderGeneralFolder.json");
            writer.open();
            writer.write(taskManager);
            writer.close();

            JsonReader reader = new JsonReader("data/persistence/testReaderGeneralFolder.json");
            TaskManager tempManager = reader.read();

            assertEquals(2, tempManager.getTaskMap().size());
            Task temp1 = tempManager.getTaskMap().get(id1);
            Task temp2 = tempManager.getTaskMap().get(id2);
            checkTask(id1, "abc", "aaa", false, time1, temp1);
            checkTask(id2, "zxy", "bbb", true, time2, temp2);
        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }

}