package com.koyama.scheduler.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.koyama.scheduler.R;
import com.koyama.scheduler.adapter.LessonAdapter;
import com.koyama.scheduler.data.model.Lesson;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Fragment for daily calendar view
 */
public class CalendarDailyFragment extends BaseCalendarFragment {

    private LocalDate currentDate;
    private TextView dateText;
    private RecyclerView lessonsRecyclerView;
    private LessonAdapter adapter;
    private TextView emptyView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        currentDate = LocalDate.now();
        View rootView = inflater.inflate(R.layout.fragment_calendar_daily, container, false);
        
        // Set accessibility description for the calendar view
        rootView.setContentDescription(getString(R.string.daily_calendar_description));
        return rootView;
    }

    @Override
    protected void initializeViews(View view) {
        emptyView = view.findViewById(R.id.text_empty);
        dateText = view.findViewById(R.id.text_day_title);
        lessonsRecyclerView = view.findViewById(R.id.recycler_lessons);
        
        // Set up RecyclerView
        lessonsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new LessonAdapter(new ArrayList<>());
        lessonsRecyclerView.setAdapter(adapter);
        
        // Set up click listener to show lesson details
        adapter.setOnItemClickListener(lesson -> {
            // Show lesson details and options (calendar, directions, reminder)
            showLessonOptionsDialog(lesson);
        });
        
        // Setup navigation buttons with accessibility
        View previousButton = view.findViewById(R.id.button_previous);
        View nextButton = view.findViewById(R.id.button_next);
        View todayButton = view.findViewById(R.id.button_today);

        previousButton.setContentDescription(getString(R.string.previous_day_description));
        nextButton.setContentDescription(getString(R.string.next_day_description));
        todayButton.setContentDescription(getString(R.string.today_description));
        
        previousButton.setOnClickListener(v -> {
            currentDate = currentDate.minusDays(1);
            updateCalendar();
            announceContentChange();
        });
        
        nextButton.setOnClickListener(v -> {
            currentDate = currentDate.plusDays(1);
            updateCalendar();
            announceContentChange();
        });
        
        todayButton.setOnClickListener(v -> {
            currentDate = LocalDate.now();
            updateCalendar();
            announceContentChange();
        });
        
        // Initialize calendar
        updateCalendar();
    }

    @Override
    protected void updateCalendar() {
        // Update title with accessibility announcement
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
        String formattedDate = currentDate.format(formatter);
        dateText.setText(formattedDate);
        dateText.setContentDescription(getString(R.string.current_date_description, formattedDate));
        
        // Find lessons for the selected date
        List<Lesson> lessonsForDate = getLessonsForDate(currentDate);
        
        // Update UI with accessibility announcements
        if (lessonsForDate.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            lessonsRecyclerView.setVisibility(View.GONE);
            emptyView.announceForAccessibility(getString(R.string.no_lessons_today));
        } else {
            emptyView.setVisibility(View.GONE);
            lessonsRecyclerView.setVisibility(View.VISIBLE);
            adapter.setLessons(lessonsForDate);
            lessonsRecyclerView.announceForAccessibility(
                getString(R.string.lessons_count_description, lessonsForDate.size()));
        }
    }
    
    /**
     * Get lessons for a specific date, sorted by start time
     */
    private List<Lesson> getLessonsForDate(LocalDate date) {
        return lessons.stream()
            .filter(lesson -> LocalDate.parse(lesson.getDate()).equals(date))
            .sorted((l1, l2) -> l1.getStartTime().compareTo(l2.getStartTime()))
            .collect(Collectors.toList());
    }

    /**
     * Show dialog with lesson options (calendar, directions, reminder)
     */
    private void showLessonOptionsDialog(Lesson lesson) {
        // Create a bottom sheet dialog with options
        // This will be implemented in LessonDetailActivity
        openLessonDetail(lesson);
    }

    private void announceContentChange() {
        if (getView() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d");
            String announcement = getString(R.string.current_date_description, 
                    currentDate.format(formatter));
            getView().announceForAccessibility(announcement);
        }
    }
}