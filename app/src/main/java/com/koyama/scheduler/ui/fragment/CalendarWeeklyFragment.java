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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Fragment for weekly calendar view
 * This is a simplified version for demonstration purposes
 */
public class CalendarWeeklyFragment extends BaseCalendarFragment {

    private LocalDate currentWeekStart;
    private final DateTimeFormatter headerFormatter = DateTimeFormatter.ofPattern("MMM d");
    private final DateTimeFormatter weekFormatter = DateTimeFormatter.ofPattern("MMMM d - ");
    // Support both 24-hour format and 12-hour format with AM/PM
    private final DateTimeFormatter timeFormatter24h = DateTimeFormatter.ofPattern("HH:mm");
    private final DateTimeFormatter timeFormatter12h = DateTimeFormatter.ofPattern("h:mm a", Locale.US);
    private List<TextView> dayTextViews = new ArrayList<>();
    private List<View> dayContainers = new ArrayList<>();
    private TextView weekTitle;
    private LocalDate selectedDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Initialize current week to start of this week
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        currentWeekStart = LocalDate.now().with(weekFields.dayOfWeek(), 1);
        selectedDate = LocalDate.now(); // Start with today selected
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
                // First, deselect all days
                for (TextView tv : dayTextViews) {
                    tv.setSelected(false);
                }
                
                // Select this day
                textView.setSelected(true);
                
                // Update selected date and show its lessons
                selectedDate = currentWeekStart.plusDays(dayIndex);
                showLessonsForSelectedDate(selectedDate);
            });
        }
        
        // Set up grid for week view
        androidx.gridlayout.widget.GridLayout gridCalendar = view.findViewById(R.id.grid_weekly_calendar);
        // The grid will be populated in updateCalendar()
        
        // Setup navigation buttons
        View previousButton = view.findViewById(R.id.button_previous);
        View nextButton = view.findViewById(R.id.button_next);
        View todayButton = view.findViewById(R.id.button_today);
        
        previousButton.setOnClickListener(v -> {
            currentWeekStart = currentWeekStart.minusWeeks(1);
            updateCalendar();
        });
        
        nextButton.setOnClickListener(v -> {
            currentWeekStart = currentWeekStart.plusWeeks(1);
            updateCalendar();
        });
        
        // Add click listener for the "Today" button to go to current week
        todayButton.setOnClickListener(v -> {
            // Reset to current week
            WeekFields weekFields = WeekFields.of(Locale.getDefault());
            currentWeekStart = LocalDate.now().with(weekFields.dayOfWeek(), 1);
            selectedDate = LocalDate.now(); // Select today
            updateCalendar();
            
            // Show toast to confirm
            Toast.makeText(getContext(), R.string.showing_current_week, Toast.LENGTH_SHORT).show();
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
        LocalDate today = LocalDate.now();
        boolean todayInRange = !today.isBefore(currentWeekStart) && !today.isAfter(weekEnd);
        
        for (int i = 0; i < 7; i++) {
            LocalDate date = currentWeekStart.plusDays(i);
            TextView dateTextView = dayTextViews.get(i);
            
            // Set the date number
            dateTextView.setText(String.valueOf(date.getDayOfMonth()));
            
            // Reset state
            dateTextView.setActivated(false);
            dateTextView.setSelected(false);
            dateTextView.setTypeface(null, Typeface.NORMAL);
            
            // Highlight today with a circle
            if (date.equals(today)) {
                dateTextView.setActivated(true);
                dateTextView.setTypeface(null, Typeface.BOLD);
            }
            
            // Highlight selected date
            if (date.equals(selectedDate)) {
                dateTextView.setSelected(true);
                dateTextView.setTextColor(getResources().getColor(R.color.white, null));
            } else {
                dateTextView.setTextColor(getResources().getColor(getResources().getBoolean(R.bool.is_night_mode) ? 
                    R.color.text_secondary_dark : R.color.text_secondary, null));
            }
        }
        
        // If the selected date is not in this week, select the first day
        boolean selectedDateInRange = !selectedDate.isBefore(currentWeekStart) && !selectedDate.isAfter(weekEnd);
        if (!selectedDateInRange) {
            // Default to first day or today if it's in range
            selectedDate = todayInRange ? today : currentWeekStart;
            
            // Update selection visually
            for (int i = 0; i < 7; i++) {
                LocalDate date = currentWeekStart.plusDays(i);
                if (date.equals(selectedDate)) {
                    TextView tv = dayTextViews.get(i);
                    tv.setSelected(true);
                    tv.setTextColor(getResources().getColor(R.color.white, null));
                }
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
            
            // Add time column headers and lesson cells
            populateWeekGrid(gridCalendar, weekLessons);
        }
        
        // Show lessons for the selected date
        showLessonsForSelectedDate(selectedDate);
    }
    
    /**
     * Parses a time string that could be in either 12-hour (h:mm a) or 24-hour (HH:mm) format
     * @param timeStr the time string to parse
     * @return the parsed LocalTime, or null if parsing fails
     */
    private LocalTime parseTimeString(String timeStr) {
        try {
            // First try 24-hour format (HH:mm)
            return LocalTime.parse(timeStr, timeFormatter24h);
        } catch (DateTimeParseException e1) {
            try {
                // Then try 12-hour format (h:mm a)
                return LocalTime.parse(timeStr, timeFormatter12h);
            } catch (DateTimeParseException e2) {
                // If both fail, log error and use a default time
                System.err.println("Failed to parse time: " + timeStr);
                return LocalTime.of(8, 0); // Default to 8:00 AM
            }
        }
    }
    
    private void populateWeekGrid(androidx.gridlayout.widget.GridLayout grid, List<Lesson> weekLessons) {
        Context context = requireContext();
        
        // Map to track occupied cells
        Map<String, Boolean> occupiedCells = new HashMap<>();
        
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
        
        // Add lesson cards
        for (Lesson lesson : weekLessons) {
            LocalDate lessonDate = LocalDate.parse(lesson.getDate());
            int dayOfWeek = lessonDate.getDayOfWeek().getValue() % 7; // Map to 0-6 (Sun-Sat)
            
            // Parse time format using our helper method that supports both formats
            LocalTime startTime = parseTimeString(lesson.getStartTime());
            LocalTime endTime = parseTimeString(lesson.getEndTime());
            
            if (startTime == null || endTime == null) {
                continue; // Skip this lesson if times cannot be parsed
            }
            
            // Calculate hour position for the grid (subtract 7 to start grid at 08:00)
            int startHour = startTime.getHour() - 7;
            int endHour = endTime.getHour() - 7;
            
            // Ensure the hours are within our displayed range
            if (startHour < 1) startHour = 1;
            if (endHour < startHour) endHour = startHour + 1;
            
            // Calculate duration in hours, rounded up
            int durationHours = endHour - startHour;
            if (endTime.getMinute() > 0) durationHours++;
            durationHours = Math.max(1, durationHours); // Minimum 1 hour
            
            // Check if this cell position conflicts with existing lessons
            String cellKey = dayOfWeek + ":" + startHour;
            if (occupiedCells.containsKey(cellKey)) {
                // Skip or handle conflict (could shift horizontally)
                continue;
            }
            
            // Mark cell as occupied
            occupiedCells.put(cellKey, true);
            
            // Create a card for the lesson
            CardView lessonCard = new CardView(context);
            lessonCard.setCardBackgroundColor(getLessonColor(lesson.getEventType()));
            lessonCard.setCardElevation(4);
            lessonCard.setRadius(8);
            lessonCard.setPadding(4, 4, 4, 4);
            
            // Create content layout
            View.inflate(context, R.layout.item_lesson_mini, lessonCard);
            TextView lessonTitle = lessonCard.findViewById(R.id.text_lesson_title);
            TextView lessonTime = lessonCard.findViewById(R.id.text_lesson_time);
            
            // Set lesson details
            lessonTitle.setText(lesson.getEventType() + 
                               (TextUtils.isEmpty(lesson.getEventNumber()) ? "" : " " + lesson.getEventNumber()));
            lessonTime.setText(lesson.getStartTime() + " - " + lesson.getEndTime());
            
            // Add click listener to show details
            lessonCard.setOnClickListener(v -> {
                // Show lesson details or navigate to detail screen
                Toast.makeText(context, "Lesson: " + lesson.getEventType() + " " + 
                            lesson.getStartTime() + " - " + lesson.getEndTime(), Toast.LENGTH_SHORT).show();
            });
            
            // Position based on calculated time values
            androidx.gridlayout.widget.GridLayout.LayoutParams params = new androidx.gridlayout.widget.GridLayout.LayoutParams();
            params.rowSpec = androidx.gridlayout.widget.GridLayout.spec(startHour, durationHours, 1f);
            params.columnSpec = androidx.gridlayout.widget.GridLayout.spec(dayOfWeek + 1, 1); // +1 because first column is time
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
            default:
                colorResId = getResources().getBoolean(R.bool.is_night_mode) ? 
                    R.color.lesson_general_dark : R.color.lesson_general;
                break;
        }
        return getResources().getColor(colorResId, null);
    }
    
    private void showLessonsForSelectedDate(LocalDate date) {
        // This would typically update another part of the UI with lessons for the selected date
        // For now, just add a visual indicator for the selected date
        
        // Show a toast with the selected date
        if (getContext() != null) {
            Toast.makeText(getContext(), 
                "Selected: " + date.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")), 
                Toast.LENGTH_SHORT).show();
        }
    }
}
