package com.koyama.scheduler;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.koyama.scheduler.data.db.AppDatabase;
import com.koyama.scheduler.worker.NotificationWorker;

import java.util.concurrent.TimeUnit;

/**
 * Main Application class to initialize app-wide components
 */
public class KoyamaSchedulerApp extends Application {
    private static final String TAG = "KoyamaSchedulerApp";
    private static final String NOTIFICATION_WORK_NAME = "notification_work";
    private static final String PREFS_NAME = "KoyamaSchedulerPrefs";
    private static final String PREF_LAST_SCHEMA_VERSION = "last_schema_version";
    private static final int CURRENT_SCHEMA_VERSION = 3; // Match this with database version
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        try {
            Log.d(TAG, "Initializing application");
            
            // Check if we need to clear database due to schema changes
            checkForDatabaseReset();
            
            // Initialize database
            Log.d(TAG, "Initializing database");
            AppDatabase.getDatabase(this);
            
            // Initialize notification scheduling
            Log.d(TAG, "Scheduling notification work");
            scheduleNotificationWork();
            
            Log.d(TAG, "Application initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing application", e);
        }
    }
    
    private void checkForDatabaseReset() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int lastSchemaVersion = sharedPreferences.getInt(PREF_LAST_SCHEMA_VERSION, 0);
        
        if (lastSchemaVersion < CURRENT_SCHEMA_VERSION) {
            Log.d(TAG, "Schema version changed from " + lastSchemaVersion + " to " + CURRENT_SCHEMA_VERSION + ". Cleaning database...");
            
            // Clear the database by deleting its file
            boolean deleted = deleteDatabase("koyama_scheduler_db");
            Log.d(TAG, "Database deletion " + (deleted ? "successful" : "failed"));
            
            // Update the schema version in preferences
            sharedPreferences.edit()
                    .putInt(PREF_LAST_SCHEMA_VERSION, CURRENT_SCHEMA_VERSION)
                    .apply();
            
            Log.d(TAG, "Schema version updated in preferences");
        } else {
            Log.d(TAG, "Schema version unchanged: " + lastSchemaVersion);
        }
    }
    
    private void scheduleNotificationWork() {
        // Set constraints - can run without network
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build();
        
        // Create a periodic work request to run every 15 minutes
        PeriodicWorkRequest notificationWorkRequest =
                new PeriodicWorkRequest.Builder(NotificationWorker.class, 15, TimeUnit.MINUTES)
                        .setConstraints(constraints)
                        .build();
        
        // Enqueue the work request
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                NOTIFICATION_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP, // Keep existing if one exists
                notificationWorkRequest
        );
    }
}
