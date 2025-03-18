package com.koyama.scheduler.model;

public class AlternativeTerm {
    private String textBookWord;
    private String alternativeWord;
    private int signImageResId; // Resource ID for the sign image

    // Constructor with sign image
    public AlternativeTerm(String textBookWord, String alternativeWord, int signImageResId) {
        this.textBookWord = textBookWord;
        this.alternativeWord = alternativeWord;
        this.signImageResId = signImageResId;
    }

    // Constructor without sign image for backwards compatibility
    public AlternativeTerm(String textBookWord, String alternativeWord) {
        this(textBookWord, alternativeWord, 0); // 0 means no image
    }

    public String getTextBookWord() {
        return textBookWord;
    }

    public String getAlternativeWord() {
        return alternativeWord;
    }

    public int getSignImageResId() {
        return signImageResId;
    }
    
    public boolean hasSignImage() {
        return signImageResId != 0;
    }
} 