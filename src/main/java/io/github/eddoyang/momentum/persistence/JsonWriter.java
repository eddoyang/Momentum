package io.github.eddoyang.momentum.persistence;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import io.github.eddoyang.momentum.model.TaskManager;
import org.json.JSONObject;


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
        if (parent != null) {
            parent.mkdirs();
        }

        writer = new PrintWriter(file);
    }

    public void write(TaskManager taskManager) {
        JSONObject json = taskManager.toJson();
        saveToFile(json.toString(TAB));
    }

    public void close() {
        writer.close();
    }

    private void saveToFile(String json) {
        writer.print(json);
    }
}
