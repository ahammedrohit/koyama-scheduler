package com.koyama.scheduler.model;

public class AlternativeTerm {
    private String textBookWord;
    private String alternativeWord;

    public AlternativeTerm(String textBookWord, String alternativeWord) {
        this.textBookWord = textBookWord;
        this.alternativeWord = alternativeWord;
    }

    public String getTextBookWord() {
        return textBookWord;
    }

    public String getAlternativeWord() {
        return alternativeWord;
    }
} 