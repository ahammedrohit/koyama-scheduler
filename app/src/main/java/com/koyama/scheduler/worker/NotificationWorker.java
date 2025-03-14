package com.koyama.scheduler.worker;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.koyama.scheduler.KoyamaSchedulerApp;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.koyama.scheduler.data.repository.LessonRepository;
import com.koyama.scheduler.util.NotificationHelper;

/**
 * Worker class for scheduling notifications in the background
 */
public class NotificationWorker extends Worker {
    
    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    private static final String TAG = "NotificationWorker";

    @NonNull
    @Override
    public Result doWork() {
        try {
            Log.d(TAG, "Starting NotificationWorker.doWork()");
            Context context = getApplicationContext();
            Log.d(TAG, "Got application context: " + context);
            
            // Try a different approach - get the application directly
            Application app = null;
            try {
                app = (Application) context;
                Log.d(TAG, "Cast context to Application successful");
            } catch (ClassCastException e) {
                Log.e(TAG, "Failed to cast context to Application", e);
                // Try to get the application from the context
                try {
                    app = (Application) context.getApplicationContext();
                    Log.d(TAG, "Cast context.getApplicationContext() to Application successful");
                } catch (ClassCastException e2) {
                    Log.e(TAG, "Failed to cast context.getApplicationContext() to Application", e2);
                    return Result.failure();
                }
            }
            
            Log.d(TAG, "Creating LessonRepository");
            LessonRepository repository = new LessonRepository(app);
            Log.d(TAG, "Creating NotificationHelper");
            NotificationHelper notificationHelper = new NotificationHelper(context, repository);
            
            // Check for and schedule notifications for upcoming lessons
            Log.d(TAG, "Scheduling notifications");
            notificationHelper.scheduleNotificationsForUpcomingLessons();
            Log.d(TAG, "NotificationWorker completed successfully");
            
            return Result.success();
        } catch (Exception e) {
            Log.e(TAG, "Error in NotificationWorker", e);
            return Result.failure();
        }
    }
}
