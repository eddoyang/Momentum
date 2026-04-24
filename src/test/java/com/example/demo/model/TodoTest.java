package com.example.demo.model;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class TodoTest {
    Todo todo;
    Course a;
    Course b;
    Assignment c;
    Assignment d;
    Assignment ae;

    @BeforeEach
    void runBefore() {
        todo = new Todo();
        a = new Course("a");
        b = new Course("b");
        c = new Assignment("c", LocalDate.of(2025, 4, 28));
        d = new Assignment("d", LocalDate.of(2026, 1, 1));
        ae = new Assignment("ae", LocalDate.of(2025, 12, 25));
        a.addAssignment(ae);
    }

    @Test
    void testConstructor() {
        todo = new Todo();
        assertEquals(0, todo.getSize());
    }

    @Test
    void testAddTask() {
        todo.addTask(a);
        todo.addTask(c);
        assertEquals(a, todo.getTask(0));
        assertEquals(c, todo.getTask(1));
    }

    @Test
    void testAddTaskDuplicate() {
        todo.addTask(a);
        todo.addTask(a);
        assertEquals(1, todo.getSize());
    }

    @Test
    void testRemoveTask() {
        todo.addTask(a);
        assertEquals(a, todo.getTask(0));
        assertEquals(1, todo.getSize());
        todo.removeTask(a);
        assertEquals(0, todo.getSize());
    }

    @Test
    void testRemoveTaskNA() {
        assertEquals(0, todo.getSize());
        todo.removeTask(a);
    }
}
