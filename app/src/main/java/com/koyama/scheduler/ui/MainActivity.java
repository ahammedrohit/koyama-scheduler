package com.koyama.scheduler.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.button.MaterialButton;
import com.koyama.scheduler.R;
import com.koyama.scheduler.adapter.LessonAdapter;
import com.koyama.scheduler.adapter.NumberedLessonAdapter;
import com.koyama.scheduler.data.model.Lesson;
import com.koyama.scheduler.util.AbbreviationHelper;
import com.koyama.scheduler.viewmodel.LessonViewModel;
import com.koyama.scheduler.ui.TrafficTermsActivity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

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
    private DrawerLayout drawerLayout;

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
            setSupportActionBar(toolbar);

            // Set up drawer layout
            drawerLayout = findViewById(R.id.drawer_layout);
            NavigationView navigationView = findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, 
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();
            
            // Set username in nav header
            View headerView = navigationView.getHeaderView(0);
            TextView usernameTextView = headerView.findViewById(R.id.text_user_name);
            if (usernameTextView != null) {
                usernameTextView.setText("Rohid Ali Ahammed");
            }

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

            viewCalendarButton.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, CalendarViewActivity.class);
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
    
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the home action
            // Already on home screen, so just close drawer
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        } else if (id == R.id.nav_calendar) {
            // Open the calendar view
            Intent intent = new Intent(MainActivity.this, CalendarViewActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_all_schedules) {
            // Open the all schedules view
            Intent intent = new Intent(MainActivity.this, AllSchedulesActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_abbreviations) {
            Intent intent = new Intent(MainActivity.this, AbbreviationsGuideActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_license_guide) {
            Intent intent = new Intent(MainActivity.this, LicenseGuideActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_lecture_chapters) {
            Intent intent = new Intent(MainActivity.this, LectureChaptersActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_traffic_terms) {
            Intent intent = new Intent(MainActivity.this, TrafficTermsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_profile) {
            // TODO: Implement profile screen
        } else if (id == R.id.nav_settings) {
            // Open the settings
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    
    private void openLessonDetail(Lesson lesson) {
        Intent intent = new Intent(MainActivity.this, LessonDetailActivity.class);
        intent.putExtra("LESSON_ID", lesson.getId());
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        // Reload data from database when the activity resumes
        // This ensures the UI shows the latest data
        Log.d(TAG, "Activity resumed, reloading data from database");
        loadData();
    }

    private void loadData() {
        // Clear adapters first to remove any cached data
        if (todayAdapter != null) {
            todayAdapter.setLessons(new ArrayList<>());
        }
        
        if (nextDayAdapter != null) {
            nextDayAdapter.setLessons(new ArrayList<>());
        }
        
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
        lessonViewModel.getNextDayLessons(currentDate).observe(this, lessons -> {
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
        
        // Standardize the abbreviation
        String standardAbbr = AbbreviationHelper.standardizeAbbreviation(eventType);
        
        // Get display name from string resources
        switch (standardAbbr) {
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
