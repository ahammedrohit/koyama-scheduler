package com.koyama.scheduler.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
        
        // Find day containers and textviews
        for (int i = 0; i < 7; i++) {
            int dayId = getResources().getIdentifier("text_day_" + i, "id", requireContext().getPackageName());
            int containerId = getResources().getIdentifier("container_day_" + i, "id", requireContext().getPackageName());
            
            TextView dayTextView = view.findViewById(dayId);
            View dayContainer = view.findViewById(containerId);
            
            dayTextViews.add(dayTextView);
            dayContainers.add(dayContainer);
            
            final int dayIndex = i;
            dayContainer.setOnClickListener(v -> {
                LocalDate selectedDate = currentWeekStart.plusDays(dayIndex);
                showLessonsForSelectedDate(selectedDate);
            });
        }
        
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
        
        // Update day headers
        for (int i = 0; i < 7; i++) {
            LocalDate date = currentWeekStart.plusDays(i);
            String dayText = date.format(DateTimeFormatter.ofPattern("EEE")) + 
                    "\n" + date.format(DateTimeFormatter.ofPattern("d"));
            dayTextViews.get(i).setText(dayText);
            
            // Highlight today
            if (date.equals(LocalDate.now())) {
                dayTextViews.get(i).setTextColor(getResources().getColor(android.R.color.white, null));
                dayTextViews.get(i).setBackgroundResource(R.drawable.bg_current_day);
                dayTextViews.get(i).setTextSize(16);
            } else {
                dayTextViews.get(i).setTextColor(getResources().getColor(android.R.color.black, null));
                dayTextViews.get(i).setBackground(null);
                dayTextViews.get(i).setTextSize(14);
            }
            
            // Count lessons for this day
            int lessonCount = 0;
            for (Lesson lesson : lessons) {
                if (LocalDate.parse(lesson.getDate()).equals(date)) {
                    lessonCount++;
                }
            }
            
            // Update day container appearance based on lesson count
            int indicatorId = getResources().getIdentifier("lesson_indicator_" + i, "id", requireContext().getPackageName());
            int countId = getResources().getIdentifier("lesson_count_" + i, "id", requireContext().getPackageName());
            View indicator = dayContainers.get(i).findViewById(indicatorId);
            TextView countText = dayContainers.get(i).findViewById(countId);
            
            if (lessonCount > 0) {
                indicator.setVisibility(View.VISIBLE);
                countText.setVisibility(View.VISIBLE);
                countText.setText(String.valueOf(lessonCount));
            } else {
                indicator.setVisibility(View.INVISIBLE);
                countText.setVisibility(View.INVISIBLE);
            }
        }
        
        // Check if there are any lessons this week
        boolean hasLessonsThisWeek = false;
        for (Lesson lesson : lessons) {
            LocalDate lessonDate = LocalDate.parse(lesson.getDate());
            if (!lessonDate.isBefore(currentWeekStart) && !lessonDate.isAfter(weekEnd)) {
                hasLessonsThisWeek = true;
                break;
            }
        }
        
        emptyView.setVisibility(hasLessonsThisWeek ? View.GONE : View.VISIBLE);
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
