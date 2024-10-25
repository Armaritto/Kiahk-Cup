package com.stgsporting.quiz_fut.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Question {
    private final int id;
    private String title;
    private int correctOption;
    private int points;
    private List<String> options;

    private int selectedOption = -1;

    public Question(int id, String title, int correctOption, List<String> options, int points) {
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
        return options.get(correctOption);
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

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public List<String> getOptions() {
        return options;
    }

    public void addOption(String option) {
        options.add(option);
    }

    public static Question fromJson(JSONObject json) throws JSONException {
        int id = json.getInt("id");
        String title = json.getString("title");
        int correctOption = json.getInt("correct_option");
        int points = json.getInt("points");
        JSONArray options = json.getJSONArray("options");
        List<String> optionsArray = new ArrayList<>();
        for (int i = 0; i < options.length(); i++) {
            optionsArray.add(options.getString(i));
        }

        return new Question(id, title, correctOption, optionsArray, points);
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
                    .put("correct_option", correctOption)
                    .put("points", points)
                    .put("options", options);
        }catch (JSONException ignored) {}

        return data;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Question q = (Question) obj;

        return q.id == id;
    }

    @NonNull
    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", correctOption=" + correctOption +
                ", points=" + points +
                ", options=" + options +
                '}';
    }

    public int getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(int selectedOption) {
        this.selectedOption = selectedOption;
    }
}
