package com.koyama.scheduler.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.koyama.scheduler.data.model.Lesson;
import com.koyama.scheduler.data.repository.LessonRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewModel for lesson-related UI operations
 */
public class LessonViewModel extends AndroidViewModel {
    private static final String TAG = "LessonViewModel";
    private LessonRepository repository;
    private LiveData<List<Lesson>> allLessons;
    private final MediatorLiveData<Float> progressPercentage = new MediatorLiveData<>();

    public LessonViewModel(@NonNull Application application) {
        super(application);
        
        // Initialize with empty values
        allLessons = new MutableLiveData<>(new ArrayList<>());
        repository = null;
        progressPercentage.setValue(0f);
        
        try {
            Log.d(TAG, "Initializing LessonViewModel");
            Log.d(TAG, "Creating LessonRepository");
            repository = new LessonRepository(application);
            Log.d(TAG, "Getting all lessons");
            allLessons = repository.getAllLessons();
            
            // Add a source to progressPercentage that observes allLessons
            progressPercentage.addSource(allLessons, lessons -> {
                updateProgress();
            });
            
            Log.d(TAG, "LessonViewModel initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing LessonViewModel", e);
            // We already initialized with empty values, so just set progress to 0
            progressPercentage.setValue(0f);
        }
    }

    public LiveData<List<Lesson>> getAllLessons() {
        return allLessons;
    }

    public LiveData<Lesson> getNextUpcomingLesson(String currentDate, String currentTime) {
        return repository.getNextUpcomingLesson(currentDate, currentTime);
    }

    public LiveData<List<Lesson>> getLessonsByDate(String date) {
        Log.d(TAG, "Fetching lessons for date: " + date);
        LiveData<List<Lesson>> lessonsByDate = repository.getLessonsByDate(date);
        lessonsByDate.observeForever(lessons -> {
            if (lessons.isEmpty()) {
                Log.d(TAG, "No lessons found for date: " + date);
            } else {
                for (Lesson lesson : lessons) {
                    Log.d(TAG, "Lesson: " + lesson.getDate() + " " + lesson.getStartTime() + " - " + lesson.getEndTime() + " " + lesson.getDescription());
                }
            }
        });
        return lessonsByDate;
    }

    public LiveData<List<Lesson>> getLessonsBetweenDates(String startDate, String endDate) {
        if (repository == null) {
            return new MutableLiveData<>(new ArrayList<>());
        }
        return repository.getLessonsBetweenDates(startDate, endDate);
    }

    public LiveData<Lesson> getLessonById(String id) {
        if (repository == null) {
            return new MutableLiveData<>(null);
        }
        return repository.getLessonById(id);
    }

    public void insert(Lesson lesson) {
        if (repository != null) {
            repository.insert(lesson);
            updateProgress();
        }
    }

    public void update(Lesson lesson) {
        if (repository != null) {
            repository.update(lesson);
            updateProgress();
        }
    }

    public void delete(Lesson lesson) {
        if (repository != null) {
            repository.delete(lesson);
            updateProgress();
        }
    }

    public void markAsCompleted(Lesson lesson) {
        if (repository != null) {
            lesson.setCompleted(true);
            repository.update(lesson);
            // Progress will be updated automatically through MediatorLiveData
        }
    }

    public void undoMarkAsCompleted(Lesson lesson) {
        if (repository != null) {
            lesson.setCompleted(false);
            repository.update(lesson);
            // Progress will be updated automatically through MediatorLiveData
        }
    }

    public LiveData<Float> getProgressPercentage() {
        return progressPercentage;
    }

    private void updateProgress() {
        if (repository == null) {
            progressPercentage.postValue(0f);
            return;
        }
        
        try {
            List<Lesson> lessons = allLessons.getValue();
            if (lessons == null || lessons.isEmpty()) {
                progressPercentage.postValue(0f);
                return;
            }
            
            int total = lessons.size();
            int completed = 0;
            for (Lesson lesson : lessons) {
                if (lesson.isCompleted()) {
                    completed++;
                }
            }
            
            float progress = total > 0 ? (float) completed / total : 0f;
            progressPercentage.postValue(progress);
            Log.d(TAG, "Progress updated: " + progress);
        } catch (Exception e) {
            Log.e(TAG, "Error updating progress", e);
            progressPercentage.postValue(0f);
        }
    }

    public LiveData<List<Lesson>> getNextDayLessons(String currentDate, String currentTime) {
        if (repository == null) {
            return new MutableLiveData<>(new ArrayList<>());
        }
        return repository.getNextDayLessons(currentDate, currentTime);
    }
}
