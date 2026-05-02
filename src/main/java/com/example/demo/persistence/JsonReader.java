package com.example.demo.persistence;
import org.json.*;

import com.example.demo.model.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.stream.Stream;

public class JsonReader {

    private String source;


    public JsonReader(String source) {
        this.source = source;
    }

    public TaskManager read() throws IOException {
        String jsonData = readFile(source);
        JSONObject jsonObject = new JSONObject(jsonData);
        return parseTaskManager(jsonObject);
    }


    private String readFile(String source) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(source), StandardCharsets.UTF_8)) {
            stream.forEach(contentBuilder::append);
        }

        return contentBuilder.toString();
    }

    private TaskManager parseTaskManager(JSONObject jsonObject) {
        TaskManager taskManager = new TaskManager();
        addTasks(taskManager, jsonObject);
        return taskManager;
    }
    private void addTasks(TaskManager taskManager, JSONObject jsonObject) {
        JSONArray jsonArray = jsonObject.getJSONArray("tasks");
        for (Object json : jsonArray) {
            JSONObject taskJson = (JSONObject) json;
            Task task = parseTask(taskJson);
            taskManager.addTask(task);
        }
    }

    private Task parseTask(JSONObject jsonObject) {
        UUID id =  UUID.fromString(jsonObject.getString("id"));
        String title =   jsonObject.getString("title");
        String category =   jsonObject.optString("category", null);
        boolean isComplete =  jsonObject.getBoolean("isComplete");
        ZonedDateTime deadline = ZonedDateTime.parse(jsonObject.getString("deadline"));

        return new Task(id, title, category, isComplete, deadline);
    }
}
