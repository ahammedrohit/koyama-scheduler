package com.koyama.scheduler.data.db;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.koyama.scheduler.data.dao.LessonDao;
import com.koyama.scheduler.data.model.Lesson;

/**
 * Main database class for the application
 */
@Database(entities = {Lesson.class}, version = 1, exportSchema = false)
@TypeConverters({TimeConverters.class})
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
                lessonDao.insertLesson(new Lesson("1", "2025-03-15", "2:30 PM", "3:20 PM", "Apti.t", "1", "Aptitude test"));
                lessonDao.insertLesson(new Lesson("2", "2025-03-15", "3:30 PM", "4:20 PM", "1", "", ""));
                lessonDao.insertLesson(new Lesson("3", "2025-03-19", "6:40 PM", "7:30 PM", "6", "", ""));
                lessonDao.insertLesson(new Lesson("4", "2025-03-19", "7:40 PM", "8:30 PM", "8", "", ""));
                lessonDao.insertLesson(new Lesson("5", "2025-03-26", "6:40 PM", "7:30 PM", "AT", "", ""));
                lessonDao.insertLesson(new Lesson("6", "2025-03-26", "7:40 PM", "8:30 PM", "5", "", ""));
                lessonDao.insertLesson(new Lesson("7", "2025-03-27", "7:40 PM", "8:30 PM", "3", "", ""));
                lessonDao.insertLesson(new Lesson("8", "2025-03-29", "4:30 PM", "5:20 PM", "AT", "", ""));
                lessonDao.insertLesson(new Lesson("9", "2025-03-29", "5:40 PM", "6:30 PM", "AT", "", ""));
                lessonDao.insertLesson(new Lesson("10", "2025-03-30", "11:40 AM", "12:30 PM", "7", "", ""));
                lessonDao.insertLesson(new Lesson("11", "2025-03-30", "12:40 PM", "1:30 PM", "9", "", ""));

                // April 2025
                lessonDao.insertLesson(new Lesson("12", "2025-04-01", "6:40 PM", "7:30 PM", "AT", "", ""));
                lessonDao.insertLesson(new Lesson("13", "2025-04-01", "7:40 PM", "8:30 PM", "AT", "", ""));
                lessonDao.insertLesson(new Lesson("14", "2025-04-08", "6:40 PM", "7:30 PM", "AT", "", ""));
                lessonDao.insertLesson(new Lesson("15", "2025-04-08", "7:40 PM", "8:30 PM", "2", "", ""));
                lessonDao.insertLesson(new Lesson("16", "2025-04-13", "2:30 PM", "3:20 PM", "PT", "", ""));
                lessonDao.insertLesson(new Lesson("17", "2025-04-13", "3:30 PM", "4:20 PM", "PT", "", ""));
                lessonDao.insertLesson(new Lesson("18", "2025-04-13", "4:30 PM", "5:20 PM", "AT", "", ""));
                lessonDao.insertLesson(new Lesson("19", "2025-04-13", "5:40 PM", "6:30 PM", "10", "", ""));
                lessonDao.insertLesson(new Lesson("20", "2025-04-17", "6:40 PM", "7:30 PM", "AT", "", ""));
                lessonDao.insertLesson(new Lesson("21", "2025-04-17", "7:40 PM", "8:30 PM", "4", "", ""));
                lessonDao.insertLesson(new Lesson("22", "2025-04-20", "2:30 PM", "3:20 PM", "PT", "", ""));
                lessonDao.insertLesson(new Lesson("23", "2025-04-20", "3:30 PM", "4:20 PM", "PT", "", ""));
                lessonDao.insertLesson(new Lesson("24", "2025-04-20", "4:30 PM", "5:20 PM", "AT", "", ""));
                lessonDao.insertLesson(new Lesson("25", "2025-04-20", "5:40 PM", "6:30 PM", "AT", "", ""));
                lessonDao.insertLesson(new Lesson("26", "2025-04-24", "6:40 PM", "7:30 PM", "AT", "", ""));
                lessonDao.insertLesson(new Lesson("27", "2025-04-24", "7:40 PM", "8:30 PM", "AT", "", ""));

                // May 2025
                lessonDao.insertLesson(new Lesson("28", "2025-05-03", "11:40 AM", "12:30 PM", "22", "", ""));
                lessonDao.insertLesson(new Lesson("29", "2025-05-03", "12:40 PM", "1:30 PM", "23", "", ""));
                lessonDao.insertLesson(new Lesson("30", "2025-05-03", "2:30 PM", "3:20 PM", "CPR", "", ""));
                lessonDao.insertLesson(new Lesson("31", "2025-05-03", "3:30 PM", "4:20 PM", "CPR", "", ""));
                lessonDao.insertLesson(new Lesson("32", "2025-05-03", "4:30 PM", "5:20 PM", "CPR", "", ""));
                lessonDao.insertLesson(new Lesson("33", "2025-05-04", "3:30 PM", "4:20 PM", "20", "", ""));
                lessonDao.insertLesson(new Lesson("34", "2025-05-04", "4:30 PM", "5:20 PM", "A50", "", ""));
                lessonDao.insertLesson(new Lesson("35", "2025-05-04", "5:40 PM", "6:30 PM", "A50", "", ""));
                lessonDao.insertLesson(new Lesson("36", "2025-05-06", "4:30 PM", "5:20 PM", "14", "", ""));
                lessonDao.insertLesson(new Lesson("37", "2025-05-06", "5:40 PM", "6:30 PM", "19", "", ""));
                lessonDao.insertLesson(new Lesson("38", "2025-05-07", "6:40 PM", "7:30 PM", "12", "", ""));
                lessonDao.insertLesson(new Lesson("39", "2025-05-07", "7:40 PM", "8:30 PM", "13", "", ""));
                lessonDao.insertLesson(new Lesson("40", "2025-05-13", "6:40 PM", "7:30 PM", "A50", "", ""));
                lessonDao.insertLesson(new Lesson("41", "2025-05-13", "7:40 PM", "8:30 PM", "A50", "", ""));
                lessonDao.insertLesson(new Lesson("42", "2025-05-14", "6:40 PM", "7:30 PM", "17", "", ""));
                lessonDao.insertLesson(new Lesson("43", "2025-05-14", "7:40 PM", "8:30 PM", "18", "", ""));
                lessonDao.insertLesson(new Lesson("44", "2025-05-17", "2:30 PM", "3:20 PM", "A50", "", ""));
                lessonDao.insertLesson(new Lesson("45", "2025-05-17", "3:30 PM", "4:20 PM", "A50", "", ""));
                lessonDao.insertLesson(new Lesson("46", "2025-05-17", "4:30 PM", "5:20 PM", "15", "", ""));
                lessonDao.insertLesson(new Lesson("47", "2025-05-17", "5:40 PM", "6:30 PM", "16", "", ""));
                lessonDao.insertLesson(new Lesson("48", "2025-05-21", "6:40 PM", "7:30 PM", "A50", "", ""));
                lessonDao.insertLesson(new Lesson("49", "2025-05-21", "7:40 PM", "8:30 PM", "A50", "", ""));
                lessonDao.insertLesson(new Lesson("50", "2025-05-24", "4:30 PM", "5:20 PM", "ATP", "", ""));
                lessonDao.insertLesson(new Lesson("51", "2025-05-24", "5:40 PM", "6:30 PM", "11", "", ""));
                lessonDao.insertLesson(new Lesson("52", "2025-05-27", "6:40 PM", "7:30 PM", "ATP", "", ""));
                lessonDao.insertLesson(new Lesson("53", "2025-05-27", "7:40 PM", "8:30 PM", "ATP", "", ""));
                lessonDao.insertLesson(new Lesson("54", "2025-05-28", "6:40 PM", "7:30 PM", "APS", "", ""));
                lessonDao.insertLesson(new Lesson("55", "2025-05-28", "7:40 PM", "8:30 PM", "APS", "", ""));

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