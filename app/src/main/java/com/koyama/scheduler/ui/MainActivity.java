package com.koyama.scheduler.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.koyama.scheduler.R;
import com.koyama.scheduler.adapter.LessonAdapter;
import com.koyama.scheduler.data.model.Lesson;
import com.koyama.scheduler.viewmodel.LessonViewModel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LessonViewModel lessonViewModel;
    private TextView nextLessonEmpty;
    private LinearLayout nextLessonContent;
    private TextView nextLessonDate;
    private TextView nextLessonTime;
    private TextView nextLessonType;
    private TextView nextLessonDescription;
    private TextView todayEmpty;
    private RecyclerView todayLessonsRecycler;
    private ProgressBar progressBar;
    private TextView progressPercentage;
    private LessonAdapter adapter;
    private Lesson currentLesson;

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
            nextLessonEmpty = findViewById(R.id.text_next_lesson_empty);
            nextLessonContent = findViewById(R.id.layout_next_lesson_content);
            nextLessonDate = findViewById(R.id.text_next_lesson_date);
            nextLessonTime = findViewById(R.id.text_next_lesson_time);
            nextLessonType = findViewById(R.id.text_next_lesson_type);
            nextLessonDescription = findViewById(R.id.text_next_lesson_description);
            todayEmpty = findViewById(R.id.text_today_empty);
            todayLessonsRecycler = findViewById(R.id.recycler_today_lessons);
            progressBar = findViewById(R.id.progress_bar);
            progressPercentage = findViewById(R.id.text_progress_percentage);

            // Set up recycler view
            Log.d(TAG, "Setting up recycler view");
            todayLessonsRecycler.setLayoutManager(new LinearLayoutManager(this));
            adapter = new LessonAdapter(new ArrayList<>());
            todayLessonsRecycler.setAdapter(adapter);

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

            // Set up click listener for lesson adapter
            adapter.setOnItemClickListener(lesson -> {
                Intent intent = new Intent(MainActivity.this, LessonDetailActivity.class);
                intent.putExtra("LESSON_ID", lesson.getId());
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
        
        // Load next upcoming lesson based on the current time
        lessonViewModel.getNextUpcomingLesson(currentDate, currentTime).observe(this, lesson -> {
            if (lesson == null) {
                Log.d(TAG, "No next lesson found");
                nextLessonEmpty.setVisibility(View.VISIBLE);
                nextLessonContent.setVisibility(View.GONE);
            } else {
                Log.d(TAG, "Next lesson found: " + lesson.getDate() + " " + lesson.getStartTime() + " - " + lesson.getEndTime() + " " + lesson.getDescription());
                nextLessonEmpty.setVisibility(View.GONE);
                nextLessonContent.setVisibility(View.VISIBLE);
                
                // Format the date
                LocalDate date = LocalDate.parse(lesson.getDate());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, MMM d, yyyy");
                String formattedDate = date.format(formatter);
                
                nextLessonDate.setText(formattedDate);
                nextLessonTime.setText(String.format("%s - %s", lesson.getStartTime(), lesson.getEndTime()));
                nextLessonType.setText(getLessonTypeDisplay(lesson.getEventType()));
                nextLessonDescription.setText(lesson.getDescription());
                currentLesson = lesson;
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
                adapter.setLessons(lessons);
            }
        });
    }

    /**
     * Parse time string in format like "7:40 PM" to get 24-hour format integer value
     * for comparison
     */
    private int parseTimeToMinutes(String timeString) {
        try {
            String[] parts = timeString.split(" ");
            String timePart = parts[0];
            String amPm = parts[1];
            
            String[] timeParts = timePart.split(":");
            int hours = Integer.parseInt(timeParts[0]);
            int minutes = Integer.parseInt(timeParts[1]);
            
            // Convert to 24-hour format
            if (amPm.equalsIgnoreCase("PM") && hours < 12) {
                hours += 12;
            } else if (amPm.equalsIgnoreCase("AM") && hours == 12) {
                hours = 0;
            }
            
            return hours * 60 + minutes;
        } catch (Exception e) {
            Log.e(TAG, "Error parsing time: " + timeString, e);
            return 0;
        }
    }

    // Helper method to get user-friendly lesson type display from code
    private String getLessonTypeDisplay(String eventType) {
        switch (eventType) {
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
            case "Apti.t":
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
