package com.example.demo.persistence;

import model.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;


public class JsonTest {
    protected void checkCourse(String CourseName, Course course, String AssignmentName, 
                            LocalDate date, String priority, Boolean completed, Assignment 
                            assignment) {
        assertEquals(CourseName, course.getName());
        assertEquals(AssignmentName, assignment.getName());
        assertEquals(date, assignment.getDate());
        assertEquals(priority, assignment.getPriority());
        assertEquals(completed, assignment.getCompleted());
    }
    
    protected void checkAssignment(String name, LocalDate date, String priority, 
                                Boolean completed, Assignment assignment) { 
        assertEquals(name, assignment.getName());        
        assertEquals(date, assignment.getDate());
        assertEquals(priority, assignment.getPriority());
        assertEquals(completed, assignment.getCompleted());
    }
}

