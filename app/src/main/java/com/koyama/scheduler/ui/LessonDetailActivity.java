package com.koyama.scheduler.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.koyama.scheduler.R;
import com.koyama.scheduler.data.model.Lesson;
import com.koyama.scheduler.viewmodel.LessonViewModel;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class LessonDetailActivity extends AppCompatActivity {

    private LessonViewModel viewModel;
    private String lessonId;
    private Lesson currentLesson;

    private TextView lessonTypeText;
    private TextView lessonNumberText;
    private TextView startTimeText;
    private TextView endTimeText;
    private TextView dateText;
    private TextView descriptionText;
    private MaterialButton addToCalendarButton;
    private MaterialButton getDirectionsButton;
    private MaterialButton setReminderButton;
    private MaterialButton markCompletedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_detail);

        // Get lesson ID from intent
        lessonId = getIntent().getStringExtra("LESSON_ID");
        if (lessonId == null) {
            Toast.makeText(this, "Error: Lesson ID is missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Find views
        lessonTypeText = findViewById(R.id.text_lesson_type);
        lessonNumberText = findViewById(R.id.text_lesson_number);
        startTimeText = findViewById(R.id.text_start_time);
        endTimeText = findViewById(R.id.text_end_time);
        dateText = findViewById(R.id.text_date);
        descriptionText = findViewById(R.id.text_description);
        addToCalendarButton = findViewById(R.id.button_add_to_calendar);
        getDirectionsButton = findViewById(R.id.button_get_directions);
        setReminderButton = findViewById(R.id.button_set_reminder);
        markCompletedButton = findViewById(R.id.button_mark_completed);

        // Set up view model
        viewModel = new ViewModelProvider(this).get(LessonViewModel.class);

        // Load lesson data
        viewModel.getLessonById(lessonId).observe(this, lesson -> {
            if (lesson != null) {
                currentLesson = lesson;
                updateUI(lesson);
            }
        });

        // Set up button click listeners
        addToCalendarButton.setOnClickListener(v -> addToCalendar());
        getDirectionsButton.setOnClickListener(v -> getDirections());
        setReminderButton.setOnClickListener(v -> setReminder());
        markCompletedButton.setOnClickListener(v -> markAsCompleted());
    }

    private void updateUI(Lesson lesson) {
        lessonTypeText.setText(getLessonTypeDisplay(lesson.getEventType()));
        lessonNumberText.setText(String.format("Lesson #%s", lesson.getEventNumber()));
        startTimeText.setText(lesson.getStartTime());
        endTimeText.setText(lesson.getEndTime());
        
        // Format date
        LocalDate date = LocalDate.parse(lesson.getDate());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
        dateText.setText(date.format(formatter));
        
        descriptionText.setText(lesson.getDescription());
        
        // Update mark completed button state
        if (lesson.isCompleted()) {
            markCompletedButton.setText(R.string.lesson_completed);
            markCompletedButton.setEnabled(false);
        } else {
            markCompletedButton.setText(R.string.mark_completed);
            markCompletedButton.setEnabled(true);
        }
    }

    private void addToCalendar() {
        if (currentLesson == null) return;

        LocalDate date = LocalDate.parse(currentLesson.getDate());
        LocalTime startTime = LocalTime.parse(currentLesson.getStartTime());
        LocalTime endTime = LocalTime.parse(currentLesson.getEndTime());
        
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth(),
                startTime.getHour(), startTime.getMinute());
        
        Calendar finishTime = Calendar.getInstance();
        finishTime.set(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth(),
                endTime.getHour(), endTime.getMinute());

        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, finishTime.getTimeInMillis())
                .putExtra(CalendarContract.Events.TITLE, getLessonTypeDisplay(currentLesson.getEventType()))
                .putExtra(CalendarContract.Events.DESCRIPTION, currentLesson.getDescription())
                .putExtra(CalendarContract.Events.EVENT_LOCATION, "Koyama Driving School")
                .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
        
        startActivity(intent);
    }

    private void getDirections() {
        // For demonstration, using a fixed location for Koyama Driving School
        // In a real application, you would use the actual school coordinates
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=Koyama+Driving+School");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Toast.makeText(this, "Maps application not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void setReminder() {
        // For simplicity, we'll just show a toast message
        // In a real app, you would implement a dialog to choose reminder time
        Toast.makeText(this, "Reminder set for this lesson", Toast.LENGTH_SHORT).show();
    }

    private void markAsCompleted() {
        if (currentLesson != null) {
            viewModel.markAsCompleted(currentLesson);
            Toast.makeText(this, "Lesson marked as completed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
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