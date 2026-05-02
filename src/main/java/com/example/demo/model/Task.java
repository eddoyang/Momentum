package com.example.demo.model;

import com.example.demo.persistence.Writable;
import org.json.JSONObject;

import java.time.ZonedDateTime;
import java.util.UUID;

public class Task implements Writable {

    private UUID id;
    private String title;
    private String category;
    private boolean isComplete;
    private ZonedDateTime deadline;

    //---------------- Methods ----------------
    public Task(UUID id, String title, String category,
                boolean isComplete, ZonedDateTime deadline) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.isComplete = isComplete;
        this.deadline = deadline;
    }

    //---------------- Getter/Setters ----------------
    public UUID getId() { return id; }

    public void setId(UUID id) { this.id = id; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }

    public void setCategory(String category) { this.category = category; }

    public boolean isComplete() { return isComplete; }

    public void setComplete(boolean complete) { isComplete = complete; }

    public ZonedDateTime getDeadline() { return deadline; }

    public void setDeadline(ZonedDateTime deadline) { this.deadline = deadline; }
    //---------------- JSON ----------------
    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("id", id.toString());
        json.put("title", title);
        json.put("category", category);
        json.put("isComplete", isComplete);
        json.put("deadline", deadline.toString());
        return json;
    }
}
