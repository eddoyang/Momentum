package com.example.demo.persistence;
import org.json.*;

import com.example.demo.model.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.stream.Stream;

public class JsonReader {
    
    private String source;


    public JsonReader(String source) {
        this.source = source;
    }

    public Todo read() throws IOException {
        String jsonData = readFile(source);
        JSONObject jsonObject = new JSONObject(jsonData);
        return parseFolder(jsonObject);
    }


    private String readFile(String source) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(source), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s));
        }

        return contentBuilder.toString();
    }

    private Todo parseFolder(JSONObject jsonObject) {
        Todo todo = new Todo();
        addTasks(todo, jsonObject);
        return todo;
    }

    private void addTasks(Todo todo, JSONObject jsonObject) {
        JSONArray jsonArray = jsonObject.getJSONArray("tasks");
        for (Object json : jsonArray) {
            JSONObject taskJson = (JSONObject) json;
            Task task = parseTask(taskJson);
            todo.addTask(task);
        }
    }

    private Task parseTask(JSONObject jsonObject) {
        String name = jsonObject.getString("name");

        if (jsonObject.has("course")) {
            Course course = new Course(name);
            JSONArray children = jsonObject.getJSONArray("course");

            for (Object json : children) {
                JSONObject childJson = (JSONObject) json;
                Assignment child = (Assignment) parseTask(childJson);
                course.addAssignment(child);
            }
            return course;
        } else {
            String dateStr = jsonObject.getString("date");
            String priority = jsonObject.getString("priority");
            Boolean completed = jsonObject.getBoolean("completed");
            LocalDate date = LocalDate.parse(dateStr);
            
            return new Assignment(name, date, priority, completed);
        }
    }


}
