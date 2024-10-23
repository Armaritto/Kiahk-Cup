package com.stgsporting.quiz_fut.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class Question {
    private final int id;
    private String title;
    private int correctOption;
    private int points;
    private String[] options;

    public Question(int id, String title, int correctOption, String[] options, int points) {
        this.id = id;
        this.title = title;
        this.correctOption = correctOption;
        this.options = options;
        this.points = points;
    }

    public int getId() {
        return id;
    }

    public int getCorrectOption() {
        return correctOption;
    }

    public String getOption() {
        return options[correctOption];
    }

    public void setCorrectOption(int correctOption) {
        this.correctOption = correctOption;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setOptions(String[] options) {
        this.options = options;
    }

    public JSONObject toJson() {
        JSONObject data = new JSONObject();

        try {
            JSONArray options = new JSONArray();
            for (String option : this.options) {
                options.put(option);
            }
            data
                    .put("id", id)
                    .put("title", title)
                    .put("correctOption", correctOption)
                    .put("points", points)
                    .put("options", options);
        }catch (JSONException ignored) {}

        return data;
    }
}
