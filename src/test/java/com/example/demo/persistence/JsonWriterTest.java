package com.example.demo.persistence;

import model.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonWriterTest extends JsonTest {

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

    @Test
    void testWriterEmptyFolder() {
        try {
            Todo todo = new Todo();
            JsonWriter writer = new JsonWriter("data/persistence/testWriterEmpty.json");
            writer.open();
            writer.write(todo);
            writer.close();

            JsonReader reader = new JsonReader("data/persistence/testWriterEmpty.json");
            todo = reader.read();
            assertEquals(0, todo.getSize());
        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }

    @Test
    void testWriterGeneralFolder() {
        try {
            Todo todo = new Todo();
            Course c1 = new Course("abc");
            c1.addAssignment(new Assignment("aaa", LocalDate.of(2026,1,1), "low", false));
            Course c2 = new Course("zxy");
            c2.addAssignment(new Assignment("bbb", LocalDate.of(2026,1,1), "high", false));

            todo.addTask(c1);
            todo.addTask(c2);
            todo.addTask(new Assignment("ccc", LocalDate.of(2026,1,1), "low", true));

            
            JsonWriter writer = new JsonWriter("data/persistence/testReaderGeneralFolder.json");
            writer.open();
            writer.write(todo);
            writer.close();

            JsonReader reader = new JsonReader("data/persistence/testReaderGeneralFolder.json");
            todo = reader.read();

            List<Task> tempTasks = todo.getAllTasks();
            Course temp1 = (Course) todo.getTask(0);
            Course temp2 = (Course) todo.getTask(1);
            Assignment temp3 = (Assignment) temp1.getAssignment(0);
            Assignment temp4 = (Assignment) temp2.getAssignment(0);
            Assignment temp5 = (Assignment) todo.getTask(2);

            assertEquals(3, tempTasks.size());
            checkCourse("abc", temp1, "aaa", LocalDate.of(2026, 1, 1), 
                        "low", false, temp3);
            checkCourse("zxy", temp2, "bbb", LocalDate.of(2026, 1, 1),
                        "high", false, temp4);
            checkAssignment("ccc", LocalDate.of(2026,1,1), "low", true, temp5);
        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }
}