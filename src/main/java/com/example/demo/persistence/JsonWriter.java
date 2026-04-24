package com.example.demo.persistence;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.json.JSONObject;

import com.example.demo.model.*;

public class JsonWriter {
    private static final int TAB = 4;
    private PrintWriter writer;
    private String destination;


    public JsonWriter(String destination) {
        this.destination = destination;
    }

    public void open() throws FileNotFoundException {
        File file = new File(destination);
        File parent = file.getParentFile();
        if (parent != null) parent.mkdirs();
        writer = new PrintWriter(file);
    }

    public void write(Todo todo) {
        JSONObject json = todo.toJson();
        saveToFile(json.toString(TAB));
    }

    public void close() {
        writer.close();
    }

    private void saveToFile(String json) {
        writer.print(json);
    }
}
