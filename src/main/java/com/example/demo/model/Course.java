package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.example.demo.persistence.Writable;

public class Course implements Task, Writable {
    
    private String name;
    private List<Assignment> assignments;


    public Course(String name) {
        this.name = name;
        assignments = new ArrayList<>();
    }

    //----------------Getter/Setters----------------

    public void setName(String name) {this.name = name;}

    public String getName() {return this.name;}

    public int getSize() {return this.assignments.size();}

    public Assignment getAssignment(int index) {return this.assignments.get(index);} 


    //----------------Methods----------------

    public void addAssignment(Assignment a) {
        if (assignments.contains(a)) {
            return;
        }

        this.assignments.add(a);
    }


    public void removeAssignment(Assignment a) {
        if (!assignments.contains(a)) {
            return;
        }

        this.assignments.remove(a);
    }


    //----------------JSON----------------
    
    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("course", AssignmentToJson());
        return json;
    }

    // returns Assignments in Course as a JSON array
    private JSONArray AssignmentToJson() {
        JSONArray jsonArray = new JSONArray();

        for (Assignment current : this.assignments) {
            jsonArray.put(current.toJson());
        }

        return jsonArray;
    }


}
