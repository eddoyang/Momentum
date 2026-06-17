package io.github.eddoyang.momentum.persistence;

import org.json.JSONObject;

public interface Writable {
    //returns this as JSON object
    JSONObject toJson();
}
