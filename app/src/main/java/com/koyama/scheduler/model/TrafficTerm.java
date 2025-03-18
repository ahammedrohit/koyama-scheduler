package com.koyama.scheduler.model;

public class TrafficTerm {
    private String japanese;
    private String english;

    public TrafficTerm(String japanese, String english) {
        this.japanese = japanese;
        this.english = english;
    }

    public String getJapanese() {
        return japanese;
    }

    public String getEnglish() {
        return english;
    }
} 