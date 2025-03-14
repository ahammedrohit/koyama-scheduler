package com.koyama.scheduler.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.koyama.scheduler.data.model.Lesson;

import java.util.List;

/**
 * Data Access Object for Lesson entity
 */
@Dao
public interface LessonDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLesson(Lesson lesson);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllLessons(List<Lesson> lessons);
    
    @Update
    void updateLesson(Lesson lesson);
    
    @Delete
    void deleteLesson(Lesson lesson);
    
    @Query("SELECT * FROM lessons WHERE id = :id")
    LiveData<Lesson> getLessonById(String id);
    
    @Query("SELECT * FROM lessons ORDER BY date ASC, startTime ASC")
    LiveData<List<Lesson>> getAllLessons();
    
    @Query("SELECT * FROM lessons WHERE date = :date ORDER BY startTime ASC")
    LiveData<List<Lesson>> getLessonsByDate(String date);
    
    @Query("SELECT * FROM lessons WHERE date >= :startDate AND date <= :endDate ORDER BY date ASC, startTime ASC")
    LiveData<List<Lesson>> getLessonsBetweenDates(String startDate, String endDate);
    
    @Query("SELECT * FROM lessons WHERE isCompleted = 0 ORDER BY date ASC, startTime ASC LIMIT 1")
    LiveData<Lesson> getNextUpcomingLesson();
    
    @Query("SELECT COUNT(*) FROM lessons")
    int getTotalLessonCount();
    
    @Query("SELECT COUNT(*) FROM lessons WHERE isCompleted = 1")
    int getCompletedLessonCount();
    
    @Query("SELECT * FROM lessons WHERE isCompleted = 0 AND isNotified = 0 ORDER BY date ASC, startTime ASC")
    List<Lesson> getUnnotifiedUpcomingLessons();
    
    @Query("UPDATE lessons SET isNotified = 1 WHERE id = :lessonId")
    void markLessonAsNotified(String lessonId);
}