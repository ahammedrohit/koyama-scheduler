package com.koyama.scheduler.data.db;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.koyama.scheduler.data.dao.LessonDao;
import com.koyama.scheduler.data.model.Lesson;

/**
 * Main database class for the application
 */
@Database(entities = {Lesson.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static final String TAG = "AppDatabase";
    private static final String DATABASE_NAME = "koyama_scheduler_db";
    private static volatile AppDatabase INSTANCE;

    public abstract LessonDao lessonDao();

    // Create a single thread executor to run database operations
    private static final ExecutorService databaseWriteExecutor = Executors.newSingleThreadExecutor();

    // Callback for database creation
    private static final RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            Log.d(TAG, "Database created");

            // Seed the database with updated lessons
            databaseWriteExecutor(() -> {
                LessonDao lessonDao = INSTANCE.lessonDao();

                // March 2025
                lessonDao.insertLesson(new Lesson("1", "2025-03-15", "14:30", "15:20", "Apti.t", "1", "Aptitude test"));
                lessonDao.insertLesson(new Lesson("2", "2025-03-15", "15:30", "16:20", "1", "", ""));
                lessonDao.insertLesson(new Lesson("3", "2025-03-19", "18:40", "19:30", "6", "", ""));
                lessonDao.insertLesson(new Lesson("4", "2025-03-19", "19:40", "20:30", "8", "", ""));
                lessonDao.insertLesson(new Lesson("5", "2025-03-26", "18:40", "19:30", "AT", "", ""));
                lessonDao.insertLesson(new Lesson("6", "2025-03-26", "19:40", "20:30", "5", "", ""));
                lessonDao.insertLesson(new Lesson("7", "2025-03-27", "19:40", "20:30", "3", "", ""));
                lessonDao.insertLesson(new Lesson("8", "2025-03-29", "16:30", "17:20", "AT", "", ""));
                lessonDao.insertLesson(new Lesson("9", "2025-03-29", "17:40", "18:30", "AT", "", ""));
                lessonDao.insertLesson(new Lesson("10", "2025-03-30", "11:40", "12:30", "7", "", ""));
                lessonDao.insertLesson(new Lesson("11", "2025-03-30", "12:40", "13:30", "9", "", ""));

                // April 2025
                lessonDao.insertLesson(new Lesson("12", "2025-04-01", "18:40", "19:30", "AT", "", ""));
                lessonDao.insertLesson(new Lesson("13", "2025-04-01", "19:40", "20:30", "AT", "", ""));
                lessonDao.insertLesson(new Lesson("14", "2025-04-08", "18:40", "19:30", "AT", "", ""));
                lessonDao.insertLesson(new Lesson("15", "2025-04-08", "19:40", "20:30", "2", "", ""));
                lessonDao.insertLesson(new Lesson("16", "2025-04-13", "14:30", "15:20", "PT", "", ""));
                lessonDao.insertLesson(new Lesson("17", "2025-04-13", "15:30", "16:20", "PT", "", ""));
                lessonDao.insertLesson(new Lesson("18", "2025-04-13", "16:30", "17:20", "AT", "", ""));
                lessonDao.insertLesson(new Lesson("19", "2025-04-13", "17:40", "18:30", "10", "", ""));
                lessonDao.insertLesson(new Lesson("20", "2025-04-17", "18:40", "19:30", "AT", "", ""));
                lessonDao.insertLesson(new Lesson("21", "2025-04-17", "19:40", "20:30", "4", "", ""));
                lessonDao.insertLesson(new Lesson("22", "2025-04-20", "14:30", "15:20", "PT", "", ""));
                lessonDao.insertLesson(new Lesson("23", "2025-04-20", "15:30", "16:20", "PT", "", ""));
                lessonDao.insertLesson(new Lesson("24", "2025-04-20", "16:30", "17:20", "AT", "", ""));
                lessonDao.insertLesson(new Lesson("25", "2025-04-20", "17:40", "18:30", "AT", "", ""));
                lessonDao.insertLesson(new Lesson("26", "2025-04-24", "18:40", "19:30", "AT", "", ""));
                lessonDao.insertLesson(new Lesson("27", "2025-04-24", "19:40", "20:30", "AT", "", ""));

                // May 2025
                lessonDao.insertLesson(new Lesson("28", "2025-05-03", "11:40", "12:30", "22", "", ""));
                lessonDao.insertLesson(new Lesson("29", "2025-05-03", "12:40", "13:30", "23", "", ""));
                lessonDao.insertLesson(new Lesson("30", "2025-05-03", "14:30", "15:20", "CPR", "", ""));
                lessonDao.insertLesson(new Lesson("31", "2025-05-03", "15:30", "16:20", "CPR", "", ""));
                lessonDao.insertLesson(new Lesson("32", "2025-05-03", "16:30", "17:20", "CPR", "", ""));
                lessonDao.insertLesson(new Lesson("33", "2025-05-04", "15:30", "16:20", "20", "", ""));
                lessonDao.insertLesson(new Lesson("34", "2025-05-04", "16:30", "17:20", "A50", "", ""));
                lessonDao.insertLesson(new Lesson("35", "2025-05-04", "17:40", "18:30", "A50", "", ""));
                lessonDao.insertLesson(new Lesson("36", "2025-05-06", "16:30", "17:20", "14", "", ""));
                lessonDao.insertLesson(new Lesson("37", "2025-05-06", "17:40", "18:30", "19", "", ""));
                lessonDao.insertLesson(new Lesson("38", "2025-05-07", "18:40", "19:30", "12", "", ""));
                lessonDao.insertLesson(new Lesson("39", "2025-05-07", "19:40", "20:30", "13", "", ""));
                lessonDao.insertLesson(new Lesson("40", "2025-05-13", "18:40", "19:30", "A50", "", ""));
                lessonDao.insertLesson(new Lesson("41", "2025-05-13", "19:40", "20:30", "A50", "", ""));
                lessonDao.insertLesson(new Lesson("42", "2025-05-14", "18:40", "19:30", "17", "", ""));
                lessonDao.insertLesson(new Lesson("43", "2025-05-14", "19:40", "20:30", "18", "", ""));
                lessonDao.insertLesson(new Lesson("44", "2025-05-17", "14:30", "15:20", "A50", "", ""));
                lessonDao.insertLesson(new Lesson("45", "2025-05-17", "15:30", "16:20", "A50", "", ""));
                lessonDao.insertLesson(new Lesson("46", "2025-05-17", "16:30", "17:20", "15", "", ""));
                lessonDao.insertLesson(new Lesson("47", "2025-05-17", "17:40", "18:30", "16", "", ""));
                lessonDao.insertLesson(new Lesson("48", "2025-05-21", "18:40", "19:30", "A50", "", ""));
                lessonDao.insertLesson(new Lesson("49", "2025-05-21", "19:40", "20:30", "A50", "", ""));
                lessonDao.insertLesson(new Lesson("50", "2025-05-24", "16:30", "17:20", "ATP", "", ""));
                lessonDao.insertLesson(new Lesson("51", "2025-05-24", "17:40", "18:30", "11", "", ""));
                lessonDao.insertLesson(new Lesson("52", "2025-05-27", "18:40", "19:30", "ATP", "", ""));
                lessonDao.insertLesson(new Lesson("53", "2025-05-27", "19:40", "20:30", "ATP", "", ""));
                lessonDao.insertLesson(new Lesson("54", "2025-05-28", "18:40", "19:30", "APS", "", ""));
                lessonDao.insertLesson(new Lesson("55", "2025-05-28", "19:40", "20:30", "APS", "", ""));

                Log.d(TAG, "Updated lessons inserted into the database");
            });
        }

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            Log.d(TAG, "Database opened");
        }
    };

    public static AppDatabase getDatabase(final Context context) {
        try {
            Log.d(TAG, "Getting database instance");
            if (INSTANCE == null) {
                synchronized (AppDatabase.class) {
                    if (INSTANCE == null) {
                        Log.d(TAG, "Creating new database instance");
                        Context appContext = context.getApplicationContext();
                        Log.d(TAG, "Using application context: " + appContext);
                        
                        // Build the database with additional configuration
                        INSTANCE = Room.databaseBuilder(
                                appContext,
                                AppDatabase.class,
                                DATABASE_NAME)
                                .fallbackToDestructiveMigration() // For simplicity, we'll use destructive migration
                                .addCallback(sRoomDatabaseCallback)
                                .build();
                        
                        Log.d(TAG, "Database instance created successfully");
                    }
                }
            }
            return INSTANCE;
        } catch (Exception e) {
            Log.e(TAG, "Error creating database", e);
            throw e; // Re-throw to let the caller handle it
        }
    }
    
    // Helper method to run operations on a background thread
    public static void databaseWriteExecutor(Runnable runnable) {
        databaseWriteExecutor.execute(runnable);
    }
}
