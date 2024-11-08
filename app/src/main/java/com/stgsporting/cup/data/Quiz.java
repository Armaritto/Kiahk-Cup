package com.stgsporting.cup.data;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Quiz {
    private final int id;
    private final String name;
    private final int coins;

    private final LocalDateTime startedAt;

    private final Boolean isSolved;

    private List<Question> questions;

    private final String url;

    public Quiz(int id, String name, int coins, LocalDateTime startedAt, Boolean isSolved, String url) {
        this.id = id;
        this.name = name;
        this.coins = coins;
        questions = new ArrayList<>();
        this.startedAt = startedAt;
        this.isSolved = isSolved;
        this.url = url;
    }

    public static Quiz fromJson(JSONObject json) throws JSONException {
        String startedAtString = json.getString("started_at");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startedAt = LocalDateTime.parse(startedAtString, formatter);

        boolean isSolved = false;
        if (json.has("is_solved")) {
            isSolved = json.getInt("is_solved") == 1;
        }

        String url = "";
        if (json.has("url")) {
            url = json.getString("url");
        }

        Quiz quiz = new Quiz(
                json.getInt("id"),
                json.getString("name"),
                json.getInt("coins"),
                startedAt,
                isSolved,
                url
        );

        if(json.has("questions")) {
            JSONArray questions = json.getJSONArray("questions");
            for (int i = 0; i < questions.length(); i++) {
                quiz.addQuestion(Question.fromJson(questions.getJSONObject(i)));
            }
        }

        return quiz;
    }

    public void addEmptyQuestion() {
        Question question = questions
                .stream()
                .max(Comparator.comparingInt(Question::getId)).orElse(null);
        int id = question == null ? -1 : question.getId();

        addQuestion(new Question(id + 1, "", -1, new ArrayList<>(), -1));
    }

    public JSONObject toJson() {
        JSONObject data = new JSONObject();

        try {
            JSONArray questions = new JSONArray();
            for (Question question : this.questions) {
                questions.put(question.toJson());
            }

            data
                    .put("id", id)
                    .put("name", name)
                    .put("coins", coins)
                    .put("started_at", getStartedAtFormat("yyyy-MM-dd HH:mm:ss"))
                    .put("is_solved", isSolved() ? 1 : 0)
                    .put("questions", questions)
                    .put("url", url)
            ;
        }catch (JSONException ignored) {}

        return data;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCoins() {
        return coins;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public void addQuestion(Question question) {
        if (questions == null) {
            questions = new ArrayList<>();
        }

        questions.add(question);
    }

    public void removeQuestion(Question question) {
        if (questions == null) {
            return;
        }
        questions.remove(question);
    }

    @NonNull
    @Override
    public String toString() {
        return "Quiz{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coins=" + coins +
                ", questions=" + questions +
                ", startedAt=" + startedAt +
                '}';
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public String getStartedAtFormatted() {
        return getStartedAtFormat("dd/MM/yyyy hh:mm a");
    }
    public String getStartedAtFormat(String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);

        return startedAt.format(formatter);
    }

    public Boolean isSolved() {
        return isSolved;
    }

    public String getUrl() {
        return url;
    }
}
