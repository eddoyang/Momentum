package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.example.demo.persistence.Writable;

public class Todo implements Writable {

    private List<Task> tasks;

    public Todo() {
        this.tasks = new ArrayList<>();
    }

    //----------------Getter/Setters----------------

    public List<Task> getTodo() {return this.tasks;}

    public int getSize() {return this.tasks.size();}

    public Task getTask(int index) {return this.tasks.get(index);}

    public List<Task> getAllTasks() {return this.tasks;}

    //----------------Methods----------------

    public void addTask(Task task) {
        if (this.tasks.contains(task)) {
            return;
        }

        tasks.add(task);
    }

    public void removeTask(Task task) {
        if (!this.tasks.contains(task)) {
            return;
        }

        tasks.remove(task);
    }

    //----------------JSON----------------

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("tasks", TodoToJson());
        return json;
    }

    // returns Task's in the todo list as a JSON array
    public JSONArray TodoToJson() {
        JSONArray jsonArray = new JSONArray();
 
        for (Task current : this.tasks) {
            jsonArray.put(((Task) current).toJson());
        }
        return jsonArray;
    }
    
}
