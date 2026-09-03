package io.github.eddoyang.momentum.service;

import io.github.eddoyang.momentum.persistence.CategoryDao;
import io.github.eddoyang.momentum.persistence.TaskDao;
import io.github.eddoyang.momentum.model.Task;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.*;


@Service
public class TaskManager {

    private final TaskDao tasks;
    private final CategoryDao categories;


    //---------------- Methods ----------------

    public TaskManager(TaskDao tasks, CategoryDao categories) {
        this.tasks = tasks;
        this.categories = categories;
    }   

    public void addTask(Task task) {
        tasks.insert(task);
    }

    public void markAsComplete(UUID id) {
        tasks.markComplete(id);
    }

    public void removeTask(UUID id) {
        tasks.delete(id);
    }

    public void editTask(UUID id, String title, String category, ZonedDateTime deadline) {
        tasks.findById(id).ifPresent(task -> {
            task.setTitle(title);
            task.setCategory(category);
            task.setDeadline(deadline);
            task.update(task);
        });
    }

    public JSONObject getNextTask() {
        return tasks.findNext().map(Task::toJson).orElseGet(JSONObject::new);
    }

    public void addCategory(String name) {
        categories.insert(name);
    }

    public void removeCategory(String name) {
        categories.delete(name);
    }

    public void reorderCategories(List<String> order) {
        categories.reorder(order);
    }

    public List<String> getCategories() {
        return categories.findAllOrdered();
    }

    public JSONObject toJson() {
        JSONArray taskArray = new JSONArray();
        tasks.findAllByDeadline().forEach(t -> taskArray.put(t.toJson()));

        return new JSONObject()
            .put("tasks", taskArray)
            .put("categories", new JSONArray(categories.findAllOrdered()));
    }
}


