package com.example.demo.model;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class TaskManager {
    private Map<UUID, Task> taskMap = new HashMap<>();
    private Map<String, Set<UUID>> categoryMap = new HashMap<>();
    private TreeMap<ZonedDateTime, List<UUID>> hourlySchedule = new TreeMap<>();

    //----------------Methods----------------

    public void addTask(Task task) {
        taskMap.put(task.getId(), task);

        ZonedDateTime hourBucket = task.getDeadline().truncatedTo(ChronoUnit.HOURS);
        hourlySchedule.computeIfAbsent(hourBucket, k -> new ArrayList<>()).add(task.getId());

        if (task.getCategory() != null) {
            categoryMap.computeIfAbsent(task.getCategory(), k -> new HashSet<>()).add(task.getId());
        }


    }

    public void markAsComplete(UUID taskId) {
        Task task = taskMap.get(taskId);
        if (task != null) {
            task.setComplete(true);
        }
    }

    //----------------Getter/Setters----------------
    public Map<UUID, Task> getTaskMap() {
        return taskMap;
    }
    public TreeMap<ZonedDateTime, List<UUID>> getHourlySchedule() {
        return hourlySchedule;
    }
    public Map<String, Set<UUID>> getCategoryMap() { return categoryMap; }
    //----------------JSON----------------
    public JSONArray taskToJson() {
        JSONArray jsonArray = new JSONArray();
        for (Task task : taskMap.values()) {
            jsonArray.put(task.toJson());
        }
        return jsonArray;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("tasks", taskToJson());
        return json;
    }
}

