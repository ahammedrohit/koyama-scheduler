package com.koyama.scheduler.data.model;

public class LectureChapter {
    private int lectureNumber;
    private String chapterNumber;
    private String subject;
    private String textBook;
    private String pages;
    private boolean isStep2;

    public LectureChapter(int lectureNumber, String chapterNumber, String subject, String textBook, String pages, boolean isStep2) {
        this.lectureNumber = lectureNumber;
        this.chapterNumber = chapterNumber;
        this.subject = subject;
        this.textBook = textBook;
        this.pages = pages;
        this.isStep2 = isStep2;
    }

    public int getLectureNumber() { return lectureNumber; }
    public String getChapterNumber() { return chapterNumber; }
    public String getSubject() { return subject; }
    public String getTextBook() { return textBook; }
    public String getPages() { return pages; }
    public boolean isStep2() { return isStep2; }
} 