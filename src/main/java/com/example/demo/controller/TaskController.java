package com.example.demo.controller;

import com.example.demo.model.Task;
import com.example.demo.model.TaskManager;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskManager taskManager;


    //---------------- Methods ----------------
    public TaskController(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public String getAllTasks() {
        return taskManager.toJson().toString();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public void addTask(@RequestBody String body) {
        JSONObject json = new JSONObject(body);
        UUID id = UUID.fromString(json.getString("id"));
        String title = json.getString("title");
        String category = json.optString("category", null);
        boolean isComplete = false;
        ZonedDateTime deadline = ZonedDateTime.parse(json.getString("deadline"));
        taskManager.addTask(new Task(id, title, category, isComplete, deadline));
    }

    @PatchMapping("/{id}/complete")
    public void markComplete(@PathVariable UUID id) {
        taskManager.markAsComplete(id);
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable UUID id) {
        taskManager.removeTask(id);
    }

    @GetMapping("/next")
    public String getNextTask() {
        return taskManager.getNextTask().toString();
    }

}
