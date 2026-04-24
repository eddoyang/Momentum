package com.example.demo.model;

import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.BeforeEach;

public class CourseTest {
    Course a;
    Course b;
    Assignment c;
    Assignment d;
    Assignment ae;

    @BeforeEach
    void runBefore() {
        a = new Course("a");
        b = new Course("b");
        c = new Assignment("c", LocalDate.of(2025, 4, 28));
        d = new Assignment("d", LocalDate.of(2026, 1, 1));
        ae = new Assignment("ae", LocalDate.of(2025, 12, 25));
    }

    @Test
    void testConstructor() {
        assertEquals(0, a.getSize());
    }

    @Test
    void testAddAssignment() {
        a.addAssignment(ae);
        a.addAssignment(c);
        assertEquals(ae, a.getAssignment(0));
        assertEquals(c, a.getAssignment(1));
        assertEquals(2, a.getSize());
    }

    @Test
    void testAddAssignmentDupe() {
        a.addAssignment(ae);
        a.addAssignment(ae);
        assertEquals(ae, a.getAssignment(0));
        assertEquals(1, a.getSize());
    }

    @Test
    void testRemoveAssignment() {
        a.addAssignment(ae);
        assertEquals(ae, a.getAssignment(0));
        assertEquals(1, a.getSize());
        a.removeAssignment(ae);
        assertEquals(0, a.getSize());
    }

    @Test
    void testRemoveAssignmentNA() {
        assertEquals(0, a.getSize());
        a.removeAssignment(ae);
        assertEquals(0, a.getSize());
    }

    


}


