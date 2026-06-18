package io.github.eddoyang.momentum.controller;

import io.github.eddoyang.momentum.model.Task;
import io.github.eddoyang.momentum.model.TaskManager;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.*;
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

    @PatchMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public void editTask(@RequestBody String body) {
        JSONObject json = new JSONObject(body);
        UUID id = UUID.fromString(json.getString("id"));
        String title = json.getString("title");
        String category = json.optString("category", null);
        ZonedDateTime deadline = ZonedDateTime.parse(json.getString("deadline"));
        taskManager.editTask(id, title, category, deadline);
    }

    /* endpoint for existing tasks */
    @GetMapping("/next")
    public String getNextTask() {
        return taskManager.getNextTask().toString();
    }

    /* endpoint for marking existing tasks */
    @PatchMapping("/{id}/complete")
    public void markComplete(@PathVariable UUID id) {
        taskManager.markAsComplete(id);
    }

    /* endpoint for deleting existing tasks */
    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable UUID id) {
        taskManager.removeTask(id);
    }

    /* endpoint for existing categories */
    @GetMapping(value = "/categories", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getCategories() {
        JSONArray categories = new JSONArray();
        taskManager.getCategoryMap().keySet().stream().forEach(categories::put);
        return new JSONObject().put("categories", categories).toString();
    }

    /* endpoint for creating categories */
    @PostMapping(value = "/categories", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void addCateogry(@RequestBody String body) {
        taskManager.addCategory(new JSONObject(body).getString("name"));
    }

    /* endpoint for deleting categories */
    @DeleteMapping("/categories/{name}")
    public void deleteCategory(@PathVariable String name) {
        taskManager.removeCategory(name);
    }

    /* endpoint for sorting category tabs */
    @PutMapping(value = "/categories/order", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void reorderCategories(@RequestBody String body) {
        JSONArray order = new JSONObject(body).getJSONArray("order");
        List<String> names = new ArrayList<>();

        for (Object name : order)
            names.add((String) name);

        taskManager.reorderCategories(names);
    }
}
