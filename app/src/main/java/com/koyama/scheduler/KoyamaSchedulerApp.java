package com.koyama.scheduler;

import android.app.Application;
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
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        try {
            Log.d(TAG, "Initializing application");
            
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
