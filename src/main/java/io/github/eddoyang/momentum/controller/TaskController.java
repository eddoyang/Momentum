package io.github.eddoyang.momentum.controller;

import io.github.eddoyang.momentum.model.Task;
import io.github.eddoyang.momentum.service.TaskManager;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import io.github.eddoyang.momentum.parser.ParsedTask;
import io.github.eddoyang.momentum.parser.TaskParser;
import org.springframework.beans.factory.annotation.Value;
import java.time.ZoneId;

import java.time.ZonedDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskManager taskManager;
    private final TaskParser taskParser;

    @Value("${momentum.parser.max-input-length}")
    private int maxInputLength;

    //---------------- Methods ----------------
    public TaskController(TaskManager taskManager, TaskParser taskParser) {
        this.taskManager = taskManager;
        this.taskParser = taskParser;
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
        return new JSONObject()
                .put("categories", new JSONArray(taskManager.getCategories())).toString();
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

        //---------------- PARSE ----------------
    public record ParseRequest(String text, String timezone) {}
    
    @PostMapping(value = "/parse", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public String parseTask(@RequestBody ParseRequest req) {
        String text = req.text() == null ? "" : req.text().strip();
        
        if (text.isEmpty() || text.length() > maxInputLength) {
            return new JSONObject().put("error", "Input too short or too long").toString();
        }

        try {
            ZonedDateTime now = ZonedDateTime.now(ZoneId.of(req.timezone()));
            ParsedTask draft = taskParser.parse(text, taskManager.getCategories(), now);
            
            return new JSONObject()
                    .put("title", draft.title())
                    .put("deadline", draft.deadline())
                    .put("category", draft.category())
                    .toString();
        } catch (Exception e) {
            return new JSONObject()
                    .put("title", text)
                    .put("deadline", JSONObject.NULL)
                    .put("category", JSONObject.NULL)
                    .toString();
        }
    }
}

