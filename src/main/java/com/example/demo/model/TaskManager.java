package com.example.demo.model;

import java.io.*;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import jakarta.annotation.*;
import com.example.demo.persistence.*;

@Service
public class TaskManager {

    private Map<UUID, Task> taskMap = new HashMap<>();
    private Map<String, Set<UUID>> categoryMap = new HashMap<>();
    private TreeMap<ZonedDateTime, List<UUID>> hourlySchedule = new TreeMap<>();

    private static final String DATA_FILE = "data/momentum.json";

    //---------------- Methods ----------------

    //Adds task to data structures
    public void addTask(Task task) {
        taskMap.put(task.getId(), task);

        ZonedDateTime hourBucket = task.getDeadline().truncatedTo(ChronoUnit.HOURS);
        hourlySchedule.computeIfAbsent(hourBucket, k -> new ArrayList<>()).add(task.getId());

        if (task.getCategory() != null) {
            categoryMap.computeIfAbsent(task.getCategory(), k -> new HashSet<>()).add(task.getId());
        }
        saveToDisk();
    }

    //Adds task to data structures without writing to JSON (used for iterating tasks)
    public void addTaskWithoutSaving(Task task) {
        taskMap.put(task.getId(), task);

        ZonedDateTime hourBucket = task.getDeadline().truncatedTo(ChronoUnit.HOURS);
        hourlySchedule.computeIfAbsent(hourBucket, k -> new ArrayList<>()).add(task.getId());

        if (task.getCategory() != null) {
            categoryMap.computeIfAbsent(task.getCategory(), k -> new HashSet<>()).add(task.getId());
        }
    }

    //Mark task as complete, remove from hourlySchedule and categoryMap
    public void markAsComplete(UUID taskId) {
        Task task = taskMap.get(taskId);
        if (task == null) {
            return;
        }
        if (task.isComplete()) {
            return;
        }
        task.setComplete(true);

        //remove
        ZonedDateTime hourBucket = task.getDeadline().truncatedTo(ChronoUnit.HOURS);
        List<UUID> bucket =  hourlySchedule.get(hourBucket);
        if (bucket != null) {
            bucket.remove(taskId);
            if (bucket.isEmpty()) {
                hourlySchedule.remove(hourBucket);
            }
        }

        if (task.getCategory() != null) {
            Set<UUID> categorySet = categoryMap.get(task.getCategory());
            if (categorySet != null) {
                categorySet.remove(taskId);
                if (categorySet.isEmpty()) {
                    categoryMap.remove(task.getCategory());
                }
            }
        }

        saveToDisk();
    }

    //Removes a task
    public void removeTask(UUID taskId) {
        Task task = taskMap.remove(taskId);
        if (task == null) return;

        ZonedDateTime hourBucket = task.getDeadline().truncatedTo(ChronoUnit.HOURS);
        List<UUID> bucket =  hourlySchedule.get(hourBucket);
        if (bucket != null) {
            bucket.remove(taskId);
            if (bucket.isEmpty()) {
                hourlySchedule.remove(hourBucket);
            }
        }

        if (task.getCategory() != null) {
            Set<UUID> categorySet = categoryMap.get(task.getCategory());
            if (categorySet != null) {
                categorySet.remove(taskId);
                if (categorySet.isEmpty()) {
                    categoryMap.remove(task.getCategory());
                }
            }
        }

        saveToDisk();
    }

    //Returns task with the closest deadline
    public JSONObject getNextTask() {
        if (hourlySchedule.isEmpty()) return new JSONObject();

        Map.Entry<ZonedDateTime, List<UUID>> firstHour = hourlySchedule.firstEntry();

        for (UUID id : firstHour.getValue()) {
            Task task = taskMap.get(id);
            if (!task.isComplete()) {
                return task.toJson();
            }
        }

        return new JSONObject();
    }

    //---------------- Getter/Setters ----------------
    public Map<UUID, Task> getTaskMap() {
        return taskMap;
    }
    public TreeMap<ZonedDateTime, List<UUID>> getHourlySchedule() {
        return hourlySchedule;
    }
    public Map<String, Set<UUID>> getCategoryMap() {
        return categoryMap;
    }

    //---------------- JSON ----------------
    public JSONArray taskToJson() {
        JSONArray jsonArray = new JSONArray();
        for (List<UUID> bucket : hourlySchedule.values()) {
            for (UUID id : bucket) {
                Task task = taskMap.get(id);
                if (task != null) {
                    jsonArray.put(task.toJson());
                }
            }
        }
        return jsonArray;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("tasks", taskToJson());
        return json;
    }

    @PostConstruct
    private void loadFromDisk() {
        try {
            JsonReader reader = new JsonReader(DATA_FILE);
            TaskManager loaded = reader.read();

            for (Task task : loaded.getTaskMap().values()) {
                this.addTaskWithoutSaving(task);
            }
        } catch (IOException e) {
            System.out.println("No existing load file, creating new...");
        }
    }

    private void saveToDisk() {
        try {
            JsonWriter writer = new JsonWriter(DATA_FILE);
            writer.open();
            writer.write(this);
            writer.close();
        } catch (FileNotFoundException e) {
            System.err.println("Failed to save: " + e.getMessage());
        }
    }
}

