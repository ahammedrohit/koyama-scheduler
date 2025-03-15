package com.koyama.scheduler.ui.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.koyama.scheduler.R;
import com.koyama.scheduler.data.model.Lesson;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Fragment for weekly calendar view
 * This is a simplified version for demonstration purposes
 */
public class CalendarWeeklyFragment extends BaseCalendarFragment {

    private LocalDate currentWeekStart;
    private final DateTimeFormatter headerFormatter = DateTimeFormatter.ofPattern("MMM d");
    private final DateTimeFormatter weekFormatter = DateTimeFormatter.ofPattern("MMMM d - ");
    private List<TextView> dayTextViews = new ArrayList<>();
    private List<View> dayContainers = new ArrayList<>();
    private TextView weekTitle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Initialize current week to start of this week
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        currentWeekStart = LocalDate.now().with(weekFields.dayOfWeek(), 1);
        return inflater.inflate(R.layout.fragment_calendar_weekly, container, false);
    }

    @Override
    protected void initializeViews(View view) {
        emptyView = view.findViewById(R.id.text_empty);
        weekTitle = view.findViewById(R.id.text_week_title);
        
        // Initialize day header and date textviews from our redesigned layout
        dayTextViews.clear();
        dayContainers.clear();
        
        // Using the new text views for days of week
        dayTextViews.add(view.findViewById(R.id.text_date_sunday));
        dayTextViews.add(view.findViewById(R.id.text_date_monday));
        dayTextViews.add(view.findViewById(R.id.text_date_tuesday));
        dayTextViews.add(view.findViewById(R.id.text_date_wednesday));
        dayTextViews.add(view.findViewById(R.id.text_date_thursday));
        dayTextViews.add(view.findViewById(R.id.text_date_friday));
        dayTextViews.add(view.findViewById(R.id.text_date_saturday));
        
        // Set up day selection listeners
        for (int i = 0; i < dayTextViews.size(); i++) {
            final int dayIndex = i;
            TextView textView = dayTextViews.get(i);
            textView.setOnClickListener(v -> {
                LocalDate selectedDate = currentWeekStart.plusDays(dayIndex);
                showLessonsForSelectedDate(selectedDate);
            });
        }
        
        // Set up grid for week view
        androidx.gridlayout.widget.GridLayout gridCalendar = view.findViewById(R.id.grid_weekly_calendar);
        // The grid will be populated in updateCalendar()
        
        // Setup navigation buttons
        View previousButton = view.findViewById(R.id.button_previous);
        View nextButton = view.findViewById(R.id.button_next);
        
        previousButton.setOnClickListener(v -> {
            currentWeekStart = currentWeekStart.minusWeeks(1);
            updateCalendar();
        });
        
        nextButton.setOnClickListener(v -> {
            currentWeekStart = currentWeekStart.plusWeeks(1);
            updateCalendar();
        });
        
        // Initialize calendar with current lessons
        updateCalendar();
    }

    @Override
    protected void updateCalendar() {
        // Update week title
        LocalDate weekEnd = currentWeekStart.plusDays(6);
        String title;
        
        if (currentWeekStart.getMonth() == weekEnd.getMonth()) {
            // Same month
            title = currentWeekStart.format(DateTimeFormatter.ofPattern("MMMM d")) + 
                    " - " + weekEnd.getDayOfMonth() + ", " + weekEnd.getYear();
        } else {
            // Different months
            title = currentWeekStart.format(DateTimeFormatter.ofPattern("MMM d")) + 
                    " - " + weekEnd.format(DateTimeFormatter.ofPattern("MMM d, yyyy"));
        }
        
        weekTitle.setText(title);
        
        // Update date numbers
        for (int i = 0; i < 7; i++) {
            LocalDate date = currentWeekStart.plusDays(i);
            TextView dateTextView = dayTextViews.get(i);
            
            // Simply set the date number
            dateTextView.setText(String.valueOf(date.getDayOfMonth()));
            
            // Highlight today
            if (date.equals(LocalDate.now())) {
                dateTextView.setTextColor(getResources().getColor(R.color.primary, null));
                dateTextView.setTypeface(null, Typeface.BOLD);
            } else {
                dateTextView.setTextColor(getResources().getColor(getResources().getBoolean(R.bool.is_night_mode) ? 
                    R.color.text_secondary_dark : R.color.text_secondary, null));
                dateTextView.setTypeface(null, Typeface.NORMAL);
            }
        }
        
        // Get lessons for the entire week
        List<Lesson> weekLessons = new ArrayList<>();
        for (int day = 0; day < 7; day++) {
            LocalDate date = currentWeekStart.plusDays(day);
            for (Lesson lesson : lessons) {
                if (LocalDate.parse(lesson.getDate()).equals(date)) {
                    weekLessons.add(lesson);
                }
            }
        }
        
        // Update empty view visibility
        if (weekLessons.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
            
            // Clear and repopulate the grid
            androidx.gridlayout.widget.GridLayout gridCalendar = requireView().findViewById(R.id.grid_weekly_calendar);
            gridCalendar.removeAllViews();
            
            // Add time column headers and lesson cells (implement based on your lesson data model)
            // This would typically involve creating cells for each hour of the day and placing lessons
            // in the appropriate cells based on their start and end times
            
            // For simplicity, we'll just create a basic representation here
            populateWeekGrid(gridCalendar, weekLessons);
        }
    }
    
    private void populateWeekGrid(androidx.gridlayout.widget.GridLayout grid, List<Lesson> weekLessons) {
        Context context = requireContext();
        
        // Add time column (first column)
        for (int hour = 8; hour <= 18; hour++) {
            TextView timeText = new TextView(context);
            timeText.setText(String.format(Locale.getDefault(), "%02d:00", hour));
            timeText.setTextColor(getResources().getColor(getResources().getBoolean(R.bool.is_night_mode) ? 
                R.color.text_secondary_dark : R.color.text_secondary, null));
            timeText.setPadding(8, 16, 8, 16);
            
            androidx.gridlayout.widget.GridLayout.LayoutParams params = new androidx.gridlayout.widget.GridLayout.LayoutParams();
            params.rowSpec = androidx.gridlayout.widget.GridLayout.spec(hour - 7, 1, 1f);
            params.columnSpec = androidx.gridlayout.widget.GridLayout.spec(0, 1);
            timeText.setLayoutParams(params);
            
            grid.addView(timeText);
        }
        
        // Add cells for lessons based on their day and time
        for (Lesson lesson : weekLessons) {
            // In a real implementation, you would parse the lesson's date and time
            // and place it in the appropriate grid cell
            // This is a simplified example
            LocalDate lessonDate = LocalDate.parse(lesson.getDate());
            int dayOfWeek = lessonDate.getDayOfWeek().getValue() % 7; // Map to 0-6 (Sun-Sat)
            
            // Create a card for the lesson
            CardView lessonCard = new CardView(context);
            lessonCard.setCardBackgroundColor(getLessonColor(lesson.getEventType()));
            lessonCard.setCardElevation(4);
            lessonCard.setRadius(8);
            
            TextView lessonText = new TextView(context);
            lessonText.setText(lesson.getEventType());
            lessonText.setTextColor(Color.WHITE);
            lessonText.setPadding(8, 4, 8, 4);
            
            lessonCard.addView(lessonText);
            
            // Position based on time (simplified)
            // In a real app, you would calculate this based on start/end times
            int startHour = 9; // Example
            int duration = 2; // Example - 2 hour duration
            
            androidx.gridlayout.widget.GridLayout.LayoutParams params = new androidx.gridlayout.widget.GridLayout.LayoutParams();
            params.rowSpec = androidx.gridlayout.widget.GridLayout.spec(startHour - 7, duration, 1f);
            params.columnSpec = androidx.gridlayout.widget.GridLayout.spec(dayOfWeek + 1, 1);
            params.setMargins(4, 4, 4, 4);
            lessonCard.setLayoutParams(params);
            
            grid.addView(lessonCard);
        }
    }
    
    private int getLessonColor(String eventType) {
        // Return appropriate color based on lesson type
        int colorResId;
        switch (eventType) {
            case "AT":
                colorResId = getResources().getBoolean(R.bool.is_night_mode) ? 
                    R.color.lesson_at_dark : R.color.lesson_at;
                break;
            case "A50":
                colorResId = getResources().getBoolean(R.bool.is_night_mode) ? 
                    R.color.lesson_a50_dark : R.color.lesson_a50;
                break;
            case "ATP":
                colorResId = getResources().getBoolean(R.bool.is_night_mode) ? 
                    R.color.lesson_atp_dark : R.color.lesson_atp;
                break;
            case "PT":
                colorResId = getResources().getBoolean(R.bool.is_night_mode) ? 
                    R.color.lesson_pt_dark : R.color.lesson_pt;
                break;
            case "CPR":
                colorResId = getResources().getBoolean(R.bool.is_night_mode) ? 
                    R.color.lesson_cpr_dark : R.color.lesson_cpr;
                break;
            case "Apti.t":
                colorResId = getResources().getBoolean(R.bool.is_night_mode) ? 
                    R.color.lesson_aptit_dark : R.color.lesson_aptit;
                break;
            case "CDD":
                colorResId = getResources().getBoolean(R.bool.is_night_mode) ? 
                    R.color.lesson_cdd_dark : R.color.lesson_cdd;
                break;
            case "EXT":
                colorResId = getResources().getBoolean(R.bool.is_night_mode) ? 
                    R.color.lesson_ext_dark : R.color.lesson_ext;
                break;
            case "EX&RD":
                colorResId = getResources().getBoolean(R.bool.is_night_mode) ? 
                    R.color.lesson_exrd_dark : R.color.lesson_exrd;
                break;
            case "APS":
                colorResId = getResources().getBoolean(R.bool.is_night_mode) ? 
                    R.color.lesson_aps_dark : R.color.lesson_aps;
                break;
            default:
                colorResId = getResources().getBoolean(R.bool.is_night_mode) ? 
                    R.color.primary_dark : R.color.primary;
                break;
        }
        return getResources().getColor(colorResId, null);
    }
    
    /**
     * Show lessons for the selected date
     */
    private void showLessonsForSelectedDate(LocalDate date) {
        // Find lessons for the selected date
        List<Lesson> lessonsForDate = new ArrayList<>();
        for (Lesson lesson : lessons) {
            LocalDate lessonDate = LocalDate.parse(lesson.getDate());
            if (lessonDate.equals(date)) {
                lessonsForDate.add(lesson);
            }
        }
        
        if (!lessonsForDate.isEmpty()) {
            // In a real implementation, we would show these lessons in a list dialog or bottom sheet
            // For now, just show a toast with the count
            Toast.makeText(getContext(), 
                    lessonsForDate.size() + " lessons on " + 
                            date.format(DateTimeFormatter.ofPattern("EEE, MMM d")),
                    Toast.LENGTH_SHORT).show();
            
            // Open detail for the first lesson (for demonstration)
            if (!lessonsForDate.isEmpty()) {
                openLessonDetail(lessonsForDate.get(0));
            }
        } else {
            Toast.makeText(getContext(),
                    "No lessons on " + date.format(DateTimeFormatter.ofPattern("EEE, MMM d")),
                    Toast.LENGTH_SHORT).show();
        }
    }
}
