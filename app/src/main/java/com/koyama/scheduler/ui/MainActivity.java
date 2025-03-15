package com.koyama.scheduler.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.koyama.scheduler.R;
import com.koyama.scheduler.adapter.LessonAdapter;
import com.koyama.scheduler.adapter.NumberedLessonAdapter;
import com.koyama.scheduler.data.model.Lesson;
import com.koyama.scheduler.viewmodel.LessonViewModel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private LessonViewModel lessonViewModel;
    private TextView todayEmpty;
    private RecyclerView todayLessonsRecycler;
    private TextView nextDayEmpty;
    private RecyclerView nextDayLessonsRecycler;
    private ProgressBar progressBar;
    private TextView progressPercentage;
    private LessonAdapter todayAdapter;
    private NumberedLessonAdapter nextDayAdapter;
    private ImageView carIcon;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            Log.d(TAG, "Starting MainActivity.onCreate()");
            super.onCreate(savedInstanceState);
            Log.d(TAG, "Setting content view");
            setContentView(R.layout.activity_main);

            // Set up toolbar
            Log.d(TAG, "Setting up toolbar");
            Toolbar toolbar = findViewById(R.id.toolbar);

            // Initialize view model
            Log.d(TAG, "Initializing view model");
            lessonViewModel = new ViewModelProvider(this).get(LessonViewModel.class);

            // Find views
            Log.d(TAG, "Finding views");
            todayEmpty = findViewById(R.id.text_today_empty);
            todayLessonsRecycler = findViewById(R.id.recycler_today_lessons);
            nextDayEmpty = findViewById(R.id.text_next_day_empty);
            nextDayLessonsRecycler = findViewById(R.id.recycler_next_day_lessons);
            progressBar = findViewById(R.id.progress_bar);
            progressPercentage = findViewById(R.id.text_progress_percentage);
            carIcon = findViewById(R.id.car_icon);

            // Set up recycler views
            Log.d(TAG, "Setting up recycler views");
            todayLessonsRecycler.setLayoutManager(new LinearLayoutManager(this));
            todayAdapter = new LessonAdapter(new ArrayList<>());
            todayAdapter.setOnItemClickListener(this::openLessonDetail);
            todayLessonsRecycler.setAdapter(todayAdapter);
            
            nextDayLessonsRecycler.setLayoutManager(new LinearLayoutManager(this));
            nextDayAdapter = new NumberedLessonAdapter(this::openLessonDetail);
            nextDayLessonsRecycler.setAdapter(nextDayAdapter);

            // Set up button click listeners
            Log.d(TAG, "Setting up button click listeners");
            MaterialButton viewCalendarButton = findViewById(R.id.button_view_calendar);
            FloatingActionButton settingsFab = findViewById(R.id.fab_settings);

            viewCalendarButton.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, CalendarViewActivity.class);
                startActivity(intent);
            });

            settingsFab.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            });

            // Observe progress updates
            lessonViewModel.getProgressPercentage().observe(this, progress -> {
                int progressInt = Math.round(progress * 100);
                progressBar.setProgress(progressInt);
                progressPercentage.setText(String.format("%d%% Complete", progressInt));
                Log.d(TAG, "Progress updated in UI: " + progressInt + "%");
            });

            // Load data
            Log.d(TAG, "Loading data");
            loadData();

        } catch (Exception e) {
            Log.e(TAG, "Error in MainActivity.onCreate()", e);
            Toast.makeText(this, "Error starting app: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    private void openLessonDetail(Lesson lesson) {
        Intent intent = new Intent(MainActivity.this, LessonDetailActivity.class);
        intent.putExtra("LESSON_ID", lesson.getId());
        startActivity(intent);
    }

    private void loadData() {
        // Get the current date and time
        String currentDate = LocalDate.now().toString();
        
        // Format current time in the same format as lesson times (e.g. "11:30 PM")
        java.time.LocalTime now = java.time.LocalTime.now();
        int hour = now.getHour();
        String amPm = hour >= 12 ? "PM" : "AM";
        int hour12 = hour > 12 ? hour - 12 : (hour == 0 ? 12 : hour);
        String currentTime = String.format("%d:%02d %s", hour12, now.getMinute(), amPm);
        
        Log.d(TAG, "Current date: " + currentDate + ", Current time: " + currentTime);
        
        // Load all lessons for the next day
        lessonViewModel.getNextDayLessons(currentDate, currentTime).observe(this, lessons -> {
            if (lessons == null || lessons.isEmpty()) {
                Log.d(TAG, "No lessons found for next day");
                nextDayEmpty.setVisibility(View.VISIBLE);
                nextDayLessonsRecycler.setVisibility(View.GONE);
            } else {
                Log.d(TAG, "Next day's lessons found: " + lessons.size() + " lessons");
                for (Lesson lesson : lessons) {
                    Log.d(TAG, "Next day lesson: " + lesson.getDate() + " " + lesson.getStartTime() + " - " + lesson.getEndTime() + " " + lesson.getDescription());
                }
                nextDayEmpty.setVisibility(View.GONE);
                nextDayLessonsRecycler.setVisibility(View.VISIBLE);
                nextDayAdapter.setLessons(lessons);
            }
        });

        // Load today's lessons
        String todayDate = LocalDate.now().toString();
        Log.d(TAG, "Fetching lessons for today: " + todayDate);
        lessonViewModel.getLessonsByDate(todayDate).observe(this, lessons -> {
            if (lessons == null || lessons.isEmpty()) {
                Log.d(TAG, "No lessons found for today");
                todayEmpty.setVisibility(View.VISIBLE);
                todayLessonsRecycler.setVisibility(View.GONE);
            } else {
                Log.d(TAG, "Today's lessons found: " + lessons.size() + " lessons");
                for (Lesson lesson : lessons) {
                    Log.d(TAG, "Lesson: " + lesson.getDate() + " " + lesson.getStartTime() + " - " + lesson.getEndTime() + " " + lesson.getDescription());
                }
                todayEmpty.setVisibility(View.GONE);
                todayLessonsRecycler.setVisibility(View.VISIBLE);
                todayAdapter.setLessons(lessons);
            }
        });
    }

    // Helper method to get user-friendly lesson type display from code
    private String getLessonTypeDisplay(String eventType) {
        if (eventType == null) return getString(R.string.lesson_generic);
        
        switch (eventType.toUpperCase()) {
            case "AT":
                return getString(R.string.lesson_at);
            case "A50":
                return getString(R.string.lesson_a50);
            case "ATP":
                return getString(R.string.lesson_atp);
            case "PT":
                return getString(R.string.lesson_pt);
            case "CPR":
                return getString(R.string.lesson_cpr);
            case "APTI.T":
            case "APTIT":
                return getString(R.string.lesson_aptit);
            case "CDD":
                return getString(R.string.lesson_cdd);
            case "EXT":
                return getString(R.string.lesson_ext);
            case "EX&RD":
                return getString(R.string.lesson_exrd);
            case "APS":
                return getString(R.string.lesson_aps);
            default:
                return eventType;
        }
    }
}
