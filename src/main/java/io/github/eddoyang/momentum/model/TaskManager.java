package io.github.eddoyang.momentum.model;

import java.io.*;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import com.sun.source.tree.UsesTree;
import io.github.eddoyang.momentum.persistence.JsonReader;
import io.github.eddoyang.momentum.persistence.JsonWriter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import jakarta.annotation.*;

@Service
public class TaskManager {

    private Map<UUID, Task> taskMap = new HashMap<>();
    private Map<String, Set<UUID>> categoryMap = new LinkedHashMap<>();
    private TreeMap<ZonedDateTime, List<UUID>> hourlySchedule = new TreeMap<>();

    private static final String DATA_FILE = "data/momentum.json";

    //---------------- Methods ----------------

    // Adds task to data structures
    public void addTask(Task task) {
        taskMap.put(task.getId(), task);

        ZonedDateTime hourBucket = task.getDeadline().truncatedTo(ChronoUnit.HOURS);
        hourlySchedule.computeIfAbsent(hourBucket, k -> new ArrayList<>()).add(task.getId());

        if (task.getCategory() != null) {
            categoryMap.computeIfAbsent(task.getCategory(), k -> new HashSet<>()).add(task.getId());
        }
        saveToDisk();
    }

    // Adds task to data structures without writing to JSON (used for iterating tasks)
    public void addTaskWithoutSaving(Task task) {
        taskMap.put(task.getId(), task);

        ZonedDateTime hourBucket = task.getDeadline().truncatedTo(ChronoUnit.HOURS);
        hourlySchedule.computeIfAbsent(hourBucket, k -> new ArrayList<>()).add(task.getId());

        if (task.getCategory() != null) {
            categoryMap.computeIfAbsent(task.getCategory(), k -> new HashSet<>()).add(task.getId());
        }
    }

    // Mark task as complete, remove from hourlySchedule and categoryMap
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
            }
        }

        saveToDisk();
    }

    // Removes a task
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
            }
        }

        saveToDisk();
    }

    // Returns task with the closest deadline
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

    public void editTask(UUID id, String title, String category, ZonedDateTime deadline) {
        Task task = taskMap.get(id);

        if (task == null) {
            return;
        }

        ZonedDateTime oldBucket = task.getDeadline().truncatedTo(ChronoUnit.HOURS);
        List<UUID> bucket = hourlySchedule.get(oldBucket);

        if (bucket != null) {
            bucket.remove(id);
            if (bucket.isEmpty()) {
                hourlySchedule.remove(oldBucket);
            }
        }

        String oldCategory = task.getCategory();
        if (oldCategory != null) {
            Set<UUID> oldSet = categoryMap.get(oldCategory);
            if (oldSet != null) {
                oldSet.remove(id);
            }
        }

        task.setTitle(title);
        task.setCategory(category);
        task.setDeadline(deadline);

        if (category != null) {
            categoryMap.computeIfAbsent(category, k -> new HashSet<>()).add(id);
        }

        ZonedDateTime newBucket = task.getDeadline().truncatedTo(ChronoUnit.HOURS);
        hourlySchedule.computeIfAbsent(newBucket, k -> new ArrayList<>()).add(id);

        saveToDisk();
    }

    // Add category
    public void addCategory(String name) {
        categoryMap.computeIfAbsent(name, k -> new HashSet<>());
        saveToDisk();
    }

    // Remove category
    public void removeCategory(String name) {
        Set<UUID> members = categoryMap.remove(name);
        if(members != null) {
            for (UUID id : members) {
                Task task = taskMap.get(id);
                if (task != null)
                    task.setCategory(null);
            }
        }
        saveToDisk();
    }

    // Reordering category tabs
    public void reorderCategories(List<String> order) {
        LinkedHashMap<String, Set<UUID>> reordered = new LinkedHashMap<>();

        for (String name : order) {
            if (categoryMap.containsKey(name)) reordered.put(name, categoryMap.get(name));
        }

        for (String name : categoryMap.keySet()) {
            reordered.putIfAbsent(name, categoryMap.get(name));
        }

        categoryMap = reordered;
        saveToDisk();
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
        json.put("categories", new JSONArray(categoryMap.keySet()));
        return json;
    }

    public void addCategoryWithoutSaving(String name) {
        categoryMap.computeIfAbsent(name, k -> new HashSet<>());
    }

    @PostConstruct
    private void loadFromDisk() {
        try {
            JsonReader reader = new JsonReader(DATA_FILE);
            TaskManager loaded = reader.read();

            for (String category : loaded.getCategoryMap().keySet()) {
                this.addCategoryWithoutSaving(category);
            }

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

