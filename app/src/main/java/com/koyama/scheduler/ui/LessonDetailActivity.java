package com.koyama.scheduler.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.koyama.scheduler.R;
import com.koyama.scheduler.data.model.Lesson;
import com.koyama.scheduler.util.AbbreviationHelper;
import com.koyama.scheduler.viewmodel.LessonViewModel;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Calendar;

public class LessonDetailActivity extends AppCompatActivity {

    private LessonViewModel viewModel;
    private String lessonId;
    private Lesson currentLesson;

    private TextView lessonTypeFullText;
    private TextView lessonTypeFullMeaningText;
    private TextView lessonNumberText;
    private TextView startTimeText;
    private TextView endTimeText;
    private TextView dateText;
    private TextView descriptionText;
    private MaterialButton addToCalendarButton;
    private MaterialButton getDirectionsButton;
    private MaterialButton setReminderButton;
    private MaterialButton markCompletedButton;
    
    private Map<String, String> lessonTypeFullDescriptions = new HashMap<>();
    private Map<String, String> lessonTypeFullMeanings = new HashMap<>();
    
    /**
     * Starts the LessonDetailActivity with the given lesson ID
     *
     * @param context The context to start the activity from
     * @param lessonId The ID of the lesson to display
     */
    public static void start(Context context, String lessonId) {
        Intent intent = new Intent(context, LessonDetailActivity.class);
        intent.putExtra("LESSON_ID", lessonId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_detail);

        // Initialize lesson type descriptions map
        initLessonTypeDescriptions();
        initLessonTypeFullMeanings();
        
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
        lessonTypeFullText = findViewById(R.id.text_lesson_type_full);
        lessonTypeFullMeaningText = findViewById(R.id.text_lesson_type_full_meaning);
        lessonNumberText = findViewById(R.id.text_lesson_number);
        startTimeText = findViewById(R.id.text_start_time);
        endTimeText = findViewById(R.id.text_end_time);
        dateText = findViewById(R.id.text_date);
        descriptionText = findViewById(R.id.text_description);
        addToCalendarButton = findViewById(R.id.button_add_to_calendar);
        getDirectionsButton = findViewById(R.id.button_get_directions);
        setReminderButton = findViewById(R.id.button_set_reminder);
        markCompletedButton = findViewById(R.id.button_mark_completed);
        MaterialButton undoMarkCompletedButton = findViewById(R.id.button_undo_mark_completed);

        // Set up view model
        viewModel = new ViewModelProvider(this).get(LessonViewModel.class);

        // Load lesson data
        viewModel.getLessonById(lessonId).observe(this, lesson -> {
            if (lesson != null) {
                currentLesson = lesson;
                populateView(lesson);

                // Show or hide the undo button based on lesson completion status
                if (lesson.isCompleted()) {
                    undoMarkCompletedButton.setVisibility(View.VISIBLE);
                } else {
                    undoMarkCompletedButton.setVisibility(View.GONE);
                }
            }
        });

        // Set up button click listeners
        addToCalendarButton.setOnClickListener(v -> addToCalendar());
        getDirectionsButton.setOnClickListener(v -> getDirections());
        setReminderButton.setOnClickListener(v -> setReminder());
        markCompletedButton.setOnClickListener(v -> markAsCompleted());
        undoMarkCompletedButton.setOnClickListener(v -> undoMarkAsCompleted());
    }

    private void initLessonTypeDescriptions() {
        // This map is for the short descriptions shown in the header
        lessonTypeFullDescriptions.put("AT", getString(R.string.lesson_at));
        lessonTypeFullDescriptions.put("A50", getString(R.string.lesson_a50));
        lessonTypeFullDescriptions.put("ATP", getString(R.string.lesson_atp));
        lessonTypeFullDescriptions.put("PT", getString(R.string.lesson_pt));
        lessonTypeFullDescriptions.put("CPR", getString(R.string.lesson_cpr));
        lessonTypeFullDescriptions.put("APTIT", getString(R.string.lesson_aptit));
        lessonTypeFullDescriptions.put("APTI.T", getString(R.string.lesson_aptit));
        lessonTypeFullDescriptions.put("Apti.t", getString(R.string.lesson_aptit));
        lessonTypeFullDescriptions.put("CDD", getString(R.string.lesson_cdd));
        lessonTypeFullDescriptions.put("EXT", getString(R.string.lesson_ext));
        lessonTypeFullDescriptions.put("EX&RD", getString(R.string.lesson_exrd));
        lessonTypeFullDescriptions.put("APS", getString(R.string.lesson_aps));
    }
    
    private void initLessonTypeFullMeanings() {
        // This map is for the full meanings shown in the description area
        lessonTypeFullMeanings.put("AT", getString(R.string.lesson_at_full));
        lessonTypeFullMeanings.put("A50", getString(R.string.lesson_a50_full));
        lessonTypeFullMeanings.put("ATP", getString(R.string.lesson_atp_full));
        lessonTypeFullMeanings.put("PT", getString(R.string.lesson_pt_full));
        lessonTypeFullMeanings.put("CPR", getString(R.string.lesson_cpr_full));
        lessonTypeFullMeanings.put("APTIT", getString(R.string.lesson_aptit_full));
        lessonTypeFullMeanings.put("APTI.T", getString(R.string.lesson_aptit_full));
        lessonTypeFullMeanings.put("Apti.t", getString(R.string.lesson_aptit_full));
        lessonTypeFullMeanings.put("CDD", getString(R.string.lesson_cdd_full));
        lessonTypeFullMeanings.put("EXT", getString(R.string.lesson_ext_full));
        lessonTypeFullMeanings.put("EX&RD", getString(R.string.lesson_exrd_full));
        lessonTypeFullMeanings.put("APS", getString(R.string.lesson_aps_full));
    }

    private void undoMarkAsCompleted() {
        if (currentLesson != null) {
            currentLesson.setCompleted(false);
            viewModel.update(currentLesson);
            Toast.makeText(this, "Lesson marked as not completed", Toast.LENGTH_SHORT).show();
            
            // The UI will be updated automatically through the lesson observer
            populateView(currentLesson);
            findViewById(R.id.button_undo_mark_completed).setVisibility(View.GONE);
        }
    }

    private void populateView(Lesson lesson) {
        if (lesson == null) {
            return;
        }

        // Standardize the event type
        String eventType = lesson.getEventType();
        if (eventType != null) {
            eventType = AbbreviationHelper.standardizeAbbreviation(eventType);
        }

        // Get descriptions from AbbreviationHelper
        Map<String, String> lessonTypeFullDescriptions = AbbreviationHelper.getAbbreviationDisplayNames(this);
        Map<String, String> lessonTypeFullMeanings = AbbreviationHelper.getAbbreviationFullDescriptions(this);
        
        // Set date
        dateText.setText(formatDate(lesson.getDate()));
        
        // Set title with event type and number
        String titleText = "";
        if (eventType != null && !eventType.isEmpty()) {
            String displayName = lessonTypeFullDescriptions.get(eventType);
            if (displayName != null) {
                titleText = displayName;
            } else {
                titleText = eventType;
            }
        }
        
        if (lesson.getEventNumber() != null && !lesson.getEventNumber().isEmpty()) {
            titleText += " #" + lesson.getEventNumber();
        }
        lessonTypeFullText.setText(titleText);
        
        // Set times
        startTimeText.setText(lesson.getStartTime());
        endTimeText.setText(lesson.getEndTime());
        
        // Set description
        if (eventType != null && !eventType.isEmpty()) {
            String fullDescription = lessonTypeFullMeanings.get(eventType);
            if (fullDescription != null) {
                descriptionText.setText(fullDescription);
                descriptionText.setVisibility(View.VISIBLE);
            } else {
                descriptionText.setVisibility(View.GONE);
            }
        } else {
            descriptionText.setVisibility(View.GONE);
        }
        
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
        
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault());
        
        LocalTime startTime = LocalTime.parse(currentLesson.getStartTime(), timeFormatter);
        LocalTime endTime = LocalTime.parse(currentLesson.getEndTime(), timeFormatter);
        
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
                .putExtra(CalendarContract.Events.EVENT_LOCATION, "Koyama Driving School Futako-tamagawa")
                .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
        
        startActivity(intent);
    }

    private void getDirections() {
        // For demonstration, using a fixed location for Koyama Driving School
        // In a real application, you would use the actual school coordinates
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=Koyama+Driving+School+Futako-tamagawa");
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
            currentLesson.setCompleted(true);
            viewModel.update(currentLesson);
            Toast.makeText(this, "Lesson marked as completed", Toast.LENGTH_SHORT).show();
            
            // The UI will be updated automatically through the lesson observer
            populateView(currentLesson);
            findViewById(R.id.button_undo_mark_completed).setVisibility(View.VISIBLE);
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
        if (eventType == null) return "Lesson";
        
        return lessonTypeFullDescriptions.getOrDefault(eventType.toUpperCase(), eventType);
    }
    
    // Helper method to get full meaning of lesson type code
    private String getLessonTypeFullMeaning(String eventType) {
        if (eventType == null) return "";
        
        return lessonTypeFullMeanings.getOrDefault(eventType.toUpperCase(), eventType);
    }

    /**
     * Formats a date string into a user-friendly format
     * @param dateString Date in ISO format (yyyy-MM-dd)
     * @return Formatted date string
     */
    private String formatDate(String dateString) {
        try {
            LocalDate date = LocalDate.parse(dateString);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
            return date.format(formatter);
        } catch (Exception e) {
            return dateString; // Return original string if parsing fails
        }
    }
}