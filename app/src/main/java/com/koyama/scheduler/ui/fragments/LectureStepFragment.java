package com.koyama.scheduler.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.koyama.scheduler.R;
import com.koyama.scheduler.adapter.LectureChapterAdapter;
import com.koyama.scheduler.data.model.LectureChapter;
import com.koyama.scheduler.data.model.Lesson;
import com.koyama.scheduler.viewmodel.LessonViewModel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LectureStepFragment extends Fragment {
    private static final String ARG_STEP = "step";
    private static final String TAG = "LectureStepFragment";
    
    private RecyclerView recyclerView;
    private TextView emptyView;
    private LectureChapterAdapter adapter;
    private LessonViewModel lessonViewModel;
    private int step;
    
    public static LectureStepFragment newInstance(int step) {
        LectureStepFragment fragment = new LectureStepFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_STEP, step);
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            step = getArguments().getInt(ARG_STEP);
        }
        lessonViewModel = new ViewModelProvider(requireActivity()).get(LessonViewModel.class);
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lecture_step, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        recyclerView = view.findViewById(R.id.recycler_view);
        emptyView = view.findViewById(R.id.empty_view);
        
        adapter = new LectureChapterAdapter(lessonViewModel);
        recyclerView.setAdapter(adapter);
        
        // Observe all lessons to check completion status
        lessonViewModel.getAllLessons().observe(getViewLifecycleOwner(), lessons -> {
            loadChapters(lessons);
        });
    }
    
    private void loadChapters(List<Lesson> lessons) {
        List<LectureChapter> chapters = new ArrayList<>();
        
        if (step == 1) {
            chapters.add(new LectureChapter(1, "1st step 1", "Before you get behind the wheel", "Rules of\nthe road", "11~22", false));
            chapters.add(new LectureChapter(2, "1st step 2", "Observation of traffic signals", "Rules of\nthe road", "23~32", false));
            chapters.add(new LectureChapter(3, "1st step 3", "Observation of signs and markings", "Rules of\nthe road", "33~52", false));
            chapters.add(new LectureChapter(4, "1st step 4", "Where vehicles can and cannot drive", "Rules of\nthe road", "53~62", false));
            chapters.add(new LectureChapter(5, "1st step 6", "Driving through an intersection/Railway crossing", "Rules of\nthe road", "67~80", false));
            chapters.add(new LectureChapter(6, "1st step 7,14", "Safe speed and distance between vehicles\nDriving vehicles with an automatic transmission", "Rules of\nthe road", "81~90\n145~148", false));
            chapters.add(new LectureChapter(7, "1st step 8", "Be on the alert for pedestrians", "Rules of\nthe road", "91~100", false));
            chapters.add(new LectureChapter(8, "1st step 9,10", "Safety checks and use of signals and horn\nChanging lanes", "Rules of\nthe road", "101~112", false));
            chapters.add(new LectureChapter(9, "1st step 5,11,12", "Priority for Emergency vehicle, etc\nOvertaking and passing\nPassing by", "Rules of\nthe road", "63~66\n113~124", false));
            chapters.add(new LectureChapter(10, "1st step 13", "Driver's license system / Traffic violation\nNotification system", "Rules of\nthe road", "125~144", false));
        } else {
            chapters.add(new LectureChapter(11, "2nd step 4", "Blind spots and driving", "Rules of\nthe road", "161~170", true));
            chapters.add(new LectureChapter(12, "2nd step 5", "Behavior analysis based on the results of Aptitude tests", "Rules of\nthe road", "171~180", true));
            chapters.add(new LectureChapter(13, "2nd step 6", "Human ability and driving", "Rules of\nthe road", "181~190", true));
            chapters.add(new LectureChapter(14, "2nd step 7", "Natural forces that act on a vehicle and driving", "Rules of\nthe road", "191~208", true));
            chapters.add(new LectureChapter(15, "2nd step 8", "Driving in adverse environments", "Rules of\nthe road", "209~228", true));
            chapters.add(new LectureChapter(16, "2nd step 9", "Characteristics of traffic accidents and tragic results", "Rules of\nthe road", "229~240", true));
            chapters.add(new LectureChapter(17, "2nd step 10", "Maintenance / Management of motor Vehicles", "Rules of\nthe road", "241~256", true));
            chapters.add(new LectureChapter(18, "2nd step 11", "Parking and stopping", "Rules of\nthe road", "257~270", true));
            chapters.add(new LectureChapter(19, "2nd step 12,13", "Vehicle seating capacity and loading\nTowing", "Rules of\nthe road", "271~278", true));
            chapters.add(new LectureChapter(20, "2nd step 14,15", "Traffic Accidents\nMotor vehicle owners requirements and insurance system", "Rules of\nthe road", "279~294", true));
            chapters.add(new LectureChapter(21, "2nd step 1", "Anticipating danger : A discussion", "Rules of\nthe road", "149~160", true));
            chapters.add(new LectureChapter(22, "2nd step 16", "Route determination", "Rules of\nthe road", "295~302", true));
            chapters.add(new LectureChapter(23, "2nd step 17", "Expressway driving", "Rules of\nthe road", "303~324", true));
            chapters.add(new LectureChapter(24, "2nd step 2,3", "CPR", "Emergency\nFirst Aid", "", true));
        }
        
        // Get current date for checking automatic completion
        LocalDate today = LocalDate.now();
        
        // Create map of chapter keys to lessons and completed status
        Map<String, LessonChapterInfo> chapterInfoMap = createChapterInfoMap(lessons);
        
        // Check completion status for each chapter and set dates
        for (LectureChapter chapter : chapters) {
            String chapterKey = chapter.getChapterNumber();
            
            if (chapterInfoMap.containsKey(chapterKey)) {
                LessonChapterInfo info = chapterInfoMap.get(chapterKey);
                
                // Set the lesson date for the chapter
                if (info.date != null) {
                    chapter.setDate(info.date);
                }
                
                // Check if chapter is completed based on lessons
                if (info.completed) {
                    chapter.setCompleted(true);
                } 
                // Also check if auto-completion based on date should be applied
                else if (info.date != null) {
                    try {
                        LocalDate chapterDate = LocalDate.parse(info.date);
                        if (chapterDate.isBefore(today)) {
                            // This is a past lesson that should be counted as completed
                            chapter.setCompleted(true);
                            Log.d(TAG, "Auto marking chapter " + chapterKey + " as completed due to date: " + info.date);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing chapter date: " + info.date, e);
                    }
                }
            }
        }
        
        adapter.setChapters(chapters);
        
        if (chapters.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }
    
    /**
     * Helper class to store information about a chapter's associated lesson
     */
    private static class LessonChapterInfo {
        String date;
        boolean completed;
        
        LessonChapterInfo(String date, boolean completed) {
            this.date = date;
            this.completed = completed;
        }
    }
    
    /**
     * Creates a mapping between chapter numbers and their lesson information
     * (date and completion status) based on the lessons.
     */
    private Map<String, LessonChapterInfo> createChapterInfoMap(List<Lesson> lessons) {
        Map<String, LessonChapterInfo> chapterInfoMap = new HashMap<>();
        
        if (lessons == null) {
            return chapterInfoMap;
        }
        
        // Current date for checking if lessons are in the past
        LocalDate today = LocalDate.now();
        
        for (Lesson lesson : lessons) {
            String eventType = lesson.getEventType();
            String eventNumber = lesson.getEventNumber();
            boolean isLectureLesson = false;
            String chapterKey = null;
            
            try {
                // First, check if eventType contains "lecture" keyword
                if (eventType != null && eventType.toLowerCase().contains("lecture")) {
                    isLectureLesson = true;
                    
                    if (eventType.toLowerCase().contains("1st step") || 
                        eventType.toLowerCase().contains("first step")) {
                        // Format: "1st step X"
                        if (eventNumber != null && !eventNumber.isEmpty()) {
                            chapterKey = "1st step " + eventNumber;
                        } else {
                            // Try to extract step number from eventType
                            String[] parts = eventType.split(" ");
                            if (parts.length >= 3) {
                                chapterKey = "1st step " + parts[2];
                            }
                        }
                    } else if (eventType.toLowerCase().contains("2nd step") || 
                               eventType.toLowerCase().contains("second step")) {
                        // Format: "2nd step X"
                        if (eventNumber != null && !eventNumber.isEmpty()) {
                            chapterKey = "2nd step " + eventNumber;
                        } else {
                            // Try to extract step number from eventType
                            String[] parts = eventType.split(" ");
                            if (parts.length >= 3) {
                                chapterKey = "2nd step " + parts[2];
                            }
                        }
                    } else {
                        // Try to determine if it's a lecture by number
                        if (eventNumber != null && !eventNumber.isEmpty()) {
                            int lectureNum = Integer.parseInt(eventNumber);
                            if (lectureNum <= 10) {
                                chapterKey = "1st step " + eventNumber;
                            } else {
                                chapterKey = "2nd step " + (lectureNum - 10);
                            }
                        }
                    }
                } 
                // Second, check if eventType is a number (representing a lecture directly)
                else if (eventType != null && eventType.matches("\\d+")) {
                    isLectureLesson = true;
                    
                    // eventType is the lecture number
                    int lectureNum = Integer.parseInt(eventType);
                    
                    if (lectureNum <= 10) {
                        chapterKey = "1st step " + lectureNum;
                    } else {
                        chapterKey = "2nd step " + (lectureNum - 10);
                    }
                    
                    Log.d(TAG, "Found numeric lecture: " + eventType + ", mapped to: " + chapterKey);
                }
                
                // If we found a lecture lesson with a valid chapter key, add it to the map
                if (chapterKey != null) {
                    // Determine completed status - either marked as completed or in the past
                    boolean isCompleted = lesson.isCompleted();
                    
                    // Also check if lesson is in the past
                    if (!isCompleted && lesson.getDate() != null) {
                        try {
                            LocalDate lessonDate = LocalDate.parse(lesson.getDate());
                            if (lessonDate.isBefore(today)) {
                                isCompleted = true;
                                Log.d(TAG, "Lesson " + lesson.getId() + " is in the past, marking as completed");
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing lesson date: " + lesson.getDate(), e);
                        }
                    }
                    
                    String date = lesson.getDate();
                    
                    if (chapterInfoMap.containsKey(chapterKey)) {
                        // Get existing info
                        LessonChapterInfo existingInfo = chapterInfoMap.get(chapterKey);
                        
                        // Only update if this lesson is completed or we don't have a date yet
                        if (isCompleted || existingInfo.date == null) {
                            chapterInfoMap.put(chapterKey, new LessonChapterInfo(
                                date, isCompleted || existingInfo.completed));
                        }
                    } else {
                        // This is the first time we've seen this chapter
                        chapterInfoMap.put(chapterKey, new LessonChapterInfo(date, isCompleted));
                    }
                    
                    Log.d(TAG, "Found lesson for chapter " + chapterKey + 
                          ", date: " + date + ", completed: " + isCompleted);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error processing lesson for chapter key", e);
            }
        }
        
        return chapterInfoMap;
    }
} 