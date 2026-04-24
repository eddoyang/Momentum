package com.example.demo.persistence;

import model.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class JsonReaderTest extends JsonTest {
    Todo todo;

    @Test
    void testReaderNonExistentFile() {
        JsonReader reader = new JsonReader("data/noSuch.json");
        try {
            todo = reader.read();
            fail("IOException expected");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    void testReaderEmptyCourse() {
        JsonReader reader = new JsonReader("data/persistence/testReaderEmpty.json");
        try {
            todo = reader.read();
            assertEquals(0, todo.getSize());
        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }

    @Test
    void testReaderGeneralCourse() {
        JsonReader reader = new JsonReader("data/persistence/testReaderGeneral.json");
        try {
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
            fail("Couldn't read from file");
        }
    }
}
