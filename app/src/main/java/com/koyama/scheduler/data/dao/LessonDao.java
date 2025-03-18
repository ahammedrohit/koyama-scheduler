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
    
    @Query("SELECT * FROM lessons WHERE isCompleted = 0 AND " +
           "(date > :currentDate OR " +
           "(date = :currentDate AND " +
           "CASE " +
           "  WHEN substr(startTime, -2) = 'PM' AND substr(:currentTime, -2) = 'AM' THEN 1 " +
           "  WHEN substr(startTime, -2) = 'AM' AND substr(:currentTime, -2) = 'PM' THEN 0 " +
           "  WHEN substr(startTime, -2) = substr(:currentTime, -2) THEN " +
           "    CASE " +
           "      WHEN CAST(substr(startTime, 0, instr(startTime, ':')) AS INTEGER) > CAST(substr(:currentTime, 0, instr(:currentTime, ':')) AS INTEGER) THEN 1 " +
           "      WHEN CAST(substr(startTime, 0, instr(startTime, ':')) AS INTEGER) < CAST(substr(:currentTime, 0, instr(:currentTime, ':')) AS INTEGER) THEN 0 " +
           "      ELSE CAST(substr(startTime, instr(startTime, ':')+1, 2) AS INTEGER) > CAST(substr(:currentTime, instr(:currentTime, ':')+1, 2) AS INTEGER) " +
           "    END " +
           "  ELSE 0 " +
           "END = 1)) " +
           "ORDER BY date ASC, " +
           "CASE WHEN substr(startTime, -2) = 'AM' THEN 0 ELSE 1 END, " +
           "CAST(substr(startTime, 0, instr(startTime, ':')) AS INTEGER), " +
           "CAST(substr(startTime, instr(startTime, ':')+1, 2) AS INTEGER) " +
           "LIMIT 1")
    LiveData<Lesson> getNextUpcomingLesson(String currentDate, String currentTime);
    
    @Query("SELECT COUNT(*) FROM lessons")
    int getTotalLessonCount();
    
    @Query("SELECT COUNT(*) FROM lessons WHERE isCompleted = 1")
    int getCompletedLessonCount();
    
    @Query("SELECT * FROM lessons WHERE isCompleted = 0 AND isNotified = 0 ORDER BY date ASC, startTime ASC")
    List<Lesson> getUnnotifiedUpcomingLessons();
    
    @Query("UPDATE lessons SET isNotified = 1 WHERE id = :lessonId")
    void markLessonAsNotified(String lessonId);

    @Query("SELECT * FROM lessons WHERE isCompleted = 0 AND " +
           "(date = (SELECT MIN(date) FROM lessons WHERE isCompleted = 0 AND " +
           "date > :currentDate)) " +  
           "ORDER BY startTime ASC")
    LiveData<List<Lesson>> getNextDayLessons(String currentDate);
}