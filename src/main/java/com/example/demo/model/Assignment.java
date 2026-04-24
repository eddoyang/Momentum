package com.example.demo.model;

import java.time.LocalDate;

import org.json.JSONObject;

import com.example.demo.persistence.Writable;

public class Assignment implements Task, Writable {

    private String name;
    private LocalDate date;
    private String priority;
    private boolean completed;
    
    public Assignment(String name, LocalDate date) {
        this.name = name;
        this.date = date;
        this.priority = "low";
        this.completed = false;
    }

    public Assignment(String name, LocalDate date, String priority, Boolean completed) {
        this.name = name;
        this.date = date;
        this.priority = priority;
        this.completed = completed;
    }


    //----------------Getter/Setters----------------

    public void setName(String name) {this.name = name;}

    public String getName() {return this.name;}

    public void setDate(LocalDate date) {this.date = date;}

    public LocalDate getDate() {return this.date;}

    public void setPriority(String priority) {
        if (!this.priority.equalsIgnoreCase("low") &
            !this.priority.equalsIgnoreCase("high")) {
                return;
        }

        this.priority = priority;
    }

    public String getPriority() {return this.priority;}

    public void setCompleted(boolean b) {this.completed = b;}

    public boolean getCompleted() {return this.completed;}
    
    //----------------JSON----------------

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("date", date);
        json.put("priority", priority);
        json.put("completed", completed);
        return json;
    }
}
