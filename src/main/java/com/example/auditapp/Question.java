package com.example.auditapp;

import java.util.List;

public class Question {
    //    private int id;
    private String chapter;
    private String questionText;
    private List<String> options;
    private List<Integer> scores;

    public Question(String chapter, String questionText, List<String> options, List<Integer> scores) {
//        this.id = id;
        this.chapter = chapter;
        this.questionText = questionText;
        this.options = options;
        this.scores = scores;
    }

//    public int getId() {
//        return id;
//    }

    public String getChapter() {
        return chapter;
    }

    public String getQuestionText() {
        return questionText;
    }

    public List<String> getOptions() {
        return options;
    }

    public List<Integer> getScores() {
        return scores;
    }
}
