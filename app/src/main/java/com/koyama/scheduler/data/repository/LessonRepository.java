package com.koyama.scheduler.data.repository;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.koyama.scheduler.data.dao.LessonDao;
import com.koyama.scheduler.data.db.AppDatabase;
import com.koyama.scheduler.data.model.Lesson;

import java.util.List;

/**
 * Repository class to abstract data operations for Lessons
 */
public class LessonRepository {
    private static final String TAG = "LessonRepository";
    private final LessonDao lessonDao;
    private final LiveData<List<Lesson>> allLessons;

    public LessonRepository(Application application) {
        try {
            Log.d(TAG, "Initializing LessonRepository");
            Log.d(TAG, "Getting database instance");
            AppDatabase database = AppDatabase.getDatabase(application);
            Log.d(TAG, "Getting lessonDao");
            lessonDao = database.lessonDao();
            Log.d(TAG, "Getting all lessons");
            allLessons = lessonDao.getAllLessons();
            Log.d(TAG, "LessonRepository initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing LessonRepository", e);
            throw e; // Re-throw to let the caller handle it
        }
    }

    public LiveData<List<Lesson>> getAllLessons() {
        Log.d(TAG, "Fetching all lessons");
        allLessons.observeForever(lessons -> {
            for (Lesson lesson : lessons) {
                Log.d(TAG, "Lesson: " + lesson.getDate() + " " + lesson.getStartTime() + " - " + lesson.getEndTime() + " " + lesson.getDescription());
            }
        });
        return allLessons;
    }

    public LiveData<Lesson> getNextUpcomingLesson(String currentDate, String currentTime) {
        return lessonDao.getNextUpcomingLesson(currentDate, currentTime);
    }

    public LiveData<List<Lesson>> getLessonsByDate(String date) {
        return lessonDao.getLessonsByDate(date);
    }

    public LiveData<List<Lesson>> getLessonsBetweenDates(String startDate, String endDate) {
        return lessonDao.getLessonsBetweenDates(startDate, endDate);
    }

    public LiveData<Lesson> getLessonById(String id) {
        return lessonDao.getLessonById(id);
    }

    public LiveData<List<Lesson>> getNextDayLessons(String currentDate) {
        return lessonDao.getNextDayLessons(currentDate);
    }

    public void insert(Lesson lesson) {
        AppDatabase.databaseWriteExecutor(() -> lessonDao.insertLesson(lesson));
    }

    public void insertAll(List<Lesson> lessons) {
        AppDatabase.databaseWriteExecutor(() -> lessonDao.insertAllLessons(lessons));
    }

    public void update(Lesson lesson) {
        AppDatabase.databaseWriteExecutor(() -> lessonDao.updateLesson(lesson));
    }

    public void delete(Lesson lesson) {
        AppDatabase.databaseWriteExecutor(() -> lessonDao.deleteLesson(lesson));
    }

    public void markAsCompleted(Lesson lesson) {
        lesson.setCompleted(true);
        update(lesson);
    }

    public void markAsNotified(String lessonId) {
        AppDatabase.databaseWriteExecutor(() -> lessonDao.markLessonAsNotified(lessonId));
    }

    public int getTotalLessonCount() {
        // For simplicity, using AsyncTask to get the value synchronously
        // In a real app, you might want to use a different approach
        try {
            return new GetTotalLessonCountTask(lessonDao).execute().get();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int getCompletedLessonCount() {
        try {
            return new GetCompletedLessonCountTask(lessonDao).execute().get();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public List<Lesson> getUnnotifiedUpcomingLessons() {
        try {
            return new GetUnnotifiedLessonsTask(lessonDao).execute().get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // AsyncTask classes for synchronous database operations
    private static class GetTotalLessonCountTask extends AsyncTask<Void, Void, Integer> {
        private final LessonDao lessonDao;

        GetTotalLessonCountTask(LessonDao lessonDao) {
            this.lessonDao = lessonDao;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            return lessonDao.getTotalLessonCount();
        }
    }

    private static class GetCompletedLessonCountTask extends AsyncTask<Void, Void, Integer> {
        private final LessonDao lessonDao;

        GetCompletedLessonCountTask(LessonDao lessonDao) {
            this.lessonDao = lessonDao;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            return lessonDao.getCompletedLessonCount();
        }
    }

    private static class GetUnnotifiedLessonsTask extends AsyncTask<Void, Void, List<Lesson>> {
        private final LessonDao lessonDao;

        GetUnnotifiedLessonsTask(LessonDao lessonDao) {
            this.lessonDao = lessonDao;
        }

        @Override
        protected List<Lesson> doInBackground(Void... voids) {
            return lessonDao.getUnnotifiedUpcomingLessons();
        }
    }
}
