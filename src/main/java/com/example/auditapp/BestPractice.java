package com.example.auditapp;

public class BestPractice {
    private String chapter;
    private String category;
    private String bestPractice;

    public BestPractice(String chapter, String category, String bestPractice) {
        this.chapter = chapter;
        this.category = category;
        this.bestPractice = bestPractice;
    }

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBestPractice() {
        return bestPractice;
    }

    public void setBestPractice(String bestPractice) {
        this.bestPractice = bestPractice;
    }

    @Override
    public String toString() {
        return "BestPractice{" +
                "Capitolul ='" + chapter + '\'' +
                ", Categoria ='" + category + '\'' +
                ", bestPractice ='" + bestPractice + '\'' +
                '}';
    }
}
