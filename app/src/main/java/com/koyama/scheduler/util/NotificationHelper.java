package com.koyama.scheduler.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.koyama.scheduler.R;
import com.koyama.scheduler.data.model.Lesson;
import com.koyama.scheduler.data.repository.LessonRepository;
import com.koyama.scheduler.ui.LessonDetailActivity;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Helper class for creating and scheduling notifications
 */
public class NotificationHelper {
    public static final String CHANNEL_ID_LESSONS = "lesson_notifications";
    private static final int NOTIFICATION_ID_BASE = 1000;
    private final Context context;
    private final NotificationManager notificationManager;
    private final LessonRepository repository;

    public NotificationHelper(Context context, LessonRepository repository) {
        this.context = context;
        this.repository = repository;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID_LESSONS,
                    "Lesson Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Notifications for upcoming driving lessons");
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void showLessonNotification(Lesson lesson) {
        Intent intent = new Intent(context, LessonDetailActivity.class);
        intent.putExtra("LESSON_ID", lesson.getId());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 
                0, 
                intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        String title = "Upcoming Lesson: " + lesson.getEventType();
        String message = lesson.getDescription() + " at " + lesson.getStartTime();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID_LESSONS)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        int notificationId = NOTIFICATION_ID_BASE + lesson.getId().hashCode();
        notificationManager.notify(notificationId, builder.build());
        
        // Mark lesson as notified in the database
        repository.markAsNotified(lesson.getId());
    }

    public void scheduleNotificationsForUpcomingLessons() {
        List<Lesson> unnotifiedLessons = repository.getUnnotifiedUpcomingLessons();
        
        if (unnotifiedLessons == null || unnotifiedLessons.isEmpty()) {
            return;
        }

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        
        for (Lesson lesson : unnotifiedLessons) {
            LocalDate lessonDate = LocalDate.parse(lesson.getDate());
            LocalTime lessonTime = LocalTime.parse(lesson.getStartTime());
            
            // Only notify for lessons that are within the next 24 hours
            if (lessonDate.equals(today) || (lessonDate.equals(today.plusDays(1)) && lessonTime.compareTo(now) >= 0)) {
                showLessonNotification(lesson);
            }
        }
    }

    /**
     * Calculate when to schedule a notification for a specific lesson
     * based on user preferences (can be expanded)
     */
    public long getNotificationDelayForLesson(Lesson lesson, String notificationType) {
        try {
            LocalDate lessonDate = LocalDate.parse(lesson.getDate());
            LocalTime lessonTime = LocalTime.parse(lesson.getStartTime());
            LocalDate today = LocalDate.now();
            LocalTime now = LocalTime.now();
            
            long delayMillis = 0;
            
            // Calculate delay based on notification type
            switch (notificationType) {
                case "1_day_before":
                    if (lessonDate.isAfter(today)) {
                        Duration duration = Duration.between(
                                now.atDate(today),
                                lessonTime.atDate(lessonDate).minusDays(1)
                        );
                        delayMillis = duration.toMillis();
                    }
                    break;
                case "3_hours_before":
                    Duration duration = Duration.between(
                            now.atDate(today),
                            lessonTime.atDate(lessonDate).minusHours(3)
                    );
                    delayMillis = duration.toMillis();
                    break;
                case "30_minutes_before":
                    Duration duration30Min = Duration.between(
                            now.atDate(today),
                            lessonTime.atDate(lessonDate).minusMinutes(30)
                    );
                    delayMillis = duration30Min.toMillis();
                    break;
                default:
                    // Default to 1 hour before
                    Duration durationDefault = Duration.between(
                            now.atDate(today),
                            lessonTime.atDate(lessonDate).minusHours(1)
                    );
                    delayMillis = durationDefault.toMillis();
            }
            
            return Math.max(0, delayMillis);
        } catch (Exception e) {
            e.printStackTrace();
            return TimeUnit.HOURS.toMillis(1); // Default to 1 hour if there's an error
        }
    }
}