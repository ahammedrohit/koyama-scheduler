package com.koyama.scheduler.ui.fragment;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.gridlayout.widget.GridLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.koyama.scheduler.R;
import com.koyama.scheduler.adapter.LessonAdapter;
import com.koyama.scheduler.data.model.Lesson;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class CalendarMonthlyFragment extends BaseCalendarFragment {

    private TextView emptyView;
    private TextView selectedDateText;
    private YearMonth currentMonth = YearMonth.now();
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
    private DateTimeFormatter selectedDateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
    private GridLayout calendarGrid;
    private RecyclerView lessonsRecyclerView;
    private LessonAdapter adapter;
    private LocalDate selectedDate = LocalDate.now();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar_monthly, container, false);
    }

    @Override
    protected void initializeViews(View view) {
        emptyView = view.findViewById(R.id.text_empty);
        selectedDateText = view.findViewById(R.id.text_selected_date);
        TextView monthTitle = view.findViewById(R.id.text_month_title);
        monthTitle.setText(currentMonth.format(formatter));

        // Setup RecyclerView with optimized scrolling
        lessonsRecyclerView = view.findViewById(R.id.recycler_lessons);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        lessonsRecyclerView.setLayoutManager(layoutManager);
        lessonsRecyclerView.setNestedScrollingEnabled(true);
        lessonsRecyclerView.setHasFixedSize(false);
        lessonsRecyclerView.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        
        adapter = new LessonAdapter(new ArrayList<>());
        lessonsRecyclerView.setAdapter(adapter);
        
        // Set up click listener to show lesson details
        adapter.setOnItemClickListener(this::openLessonDetail);

        View previousButton = view.findViewById(R.id.button_previous);
        View nextButton = view.findViewById(R.id.button_next);
        View todayButton = view.findViewById(R.id.button_today);
        calendarGrid = view.findViewById(R.id.grid_calendar);

        // Set fixed column and row count
        calendarGrid.setColumnCount(7);
        calendarGrid.setRowCount(6);

        previousButton.setOnClickListener(v -> {
            currentMonth = currentMonth.minusMonths(1);
            monthTitle.setText(currentMonth.format(formatter));
            updateCalendar();
        });

        nextButton.setOnClickListener(v -> {
            currentMonth = currentMonth.plusMonths(1);
            monthTitle.setText(currentMonth.format(formatter));
            updateCalendar();
        });

        todayButton.setOnClickListener(v -> {
            currentMonth = YearMonth.now();
            selectedDate = LocalDate.now();
            monthTitle.setText(currentMonth.format(formatter));
            updateSelectedDateView();
            updateCalendar();
        });

        // Initialize views
        updateSelectedDateView();
        updateCalendar();
    }

    private void updateSelectedDateView() {
        selectedDateText.setText(selectedDate.format(selectedDateFormatter));
        updateLessonsList();
    }

    private void updateLessonsList() {
        // Create a fresh list for the selected date
        List<Lesson> lessonsForDate = new ArrayList<>();
        
        // Make sure we have lessons data first
        if (lessons == null || lessons.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            lessonsRecyclerView.setVisibility(View.GONE);
            return;
        }
        
        // Log for debugging
        String selectedDateStr = selectedDate.toString();
        boolean foundLessons = false;
        
        // Find all lessons for the selected date
        for (Lesson lesson : lessons) {
            try {
                if (lesson.getDate() != null && lesson.getDate().equals(selectedDateStr)) {
                    lessonsForDate.add(lesson);
                    foundLessons = true;
                }
            } catch (Exception e) {
                // Skip invalid lessons
            }
        }

        // Sort lessons by start time
        lessonsForDate.sort((l1, l2) -> {
            if (l1.getStartTime() == null) return 1;
            if (l2.getStartTime() == null) return -1;
            return l1.getStartTime().compareTo(l2.getStartTime());
        });

        // Update UI based on whether we found lessons
        if (lessonsForDate.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            lessonsRecyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            lessonsRecyclerView.setVisibility(View.VISIBLE);
            
            // Force refresh the adapter
            adapter.setLessons(new ArrayList<>()); // Clear first
            adapter.setLessons(lessonsForDate);   // Then set new data
            adapter.notifyDataSetChanged();      // Explicitly notify of changes
            
            // Scroll to the top when showing new lessons
            if (lessonsRecyclerView.getLayoutManager() != null) {
                lessonsRecyclerView.getLayoutManager().scrollToPosition(0);
            }
        }
    }

    @Override
    protected void updateCalendar() {
        if (getView() == null) return;

        if (lessons == null || lessons.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            return;
        }

        emptyView.setVisibility(View.GONE);
        calendarGrid.removeAllViews();

        YearMonth yearMonth = currentMonth;
        LocalDate firstOfMonth = yearMonth.atDay(1);
        
        // Adjust to start week with Sunday (DayOfWeek.SUNDAY.getValue() is 7)
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue() % 7;

        LocalDate today = LocalDate.now();
        Map<LocalDate, Integer> lessonsPerDay = new HashMap<>();

        // Count lessons per day
        for (Lesson lesson : lessons) {
            LocalDate lessonDate = LocalDate.parse(lesson.getDate());
            if (lessonDate.getMonth() == currentMonth.getMonth() &&
                lessonDate.getYear() == currentMonth.getYear()) {
                lessonsPerDay.merge(lessonDate, 1, Integer::sum);
            }
        }

        // Add empty cells for days before the first of the month
        for (int i = 0; i < dayOfWeek; i++) {
            addEmptyCell(i / 7, i % 7);
        }

        // Add days of the month
        for (int i = 1; i <= yearMonth.lengthOfMonth(); i++) {
            LocalDate date = yearMonth.atDay(i);
            int position = dayOfWeek + i - 1;
            int row = position / 7;
            int col = position % 7;

            addDayCell(date, row, col, today, lessonsPerDay.containsKey(date));
        }

        // Fill remaining cells
        int totalCells = 42; // 6 rows * 7 columns
        int filledCells = dayOfWeek + yearMonth.lengthOfMonth();
        for (int i = filledCells; i < totalCells; i++) {
            addEmptyCell(i / 7, i % 7);
        }
    }

    private void addDayCell(LocalDate date, int row, int col, LocalDate today, boolean hasLessons) {
        TextView dayView = new TextView(requireContext());
        dayView.setText(String.valueOf(date.getDayOfMonth()));
        dayView.setGravity(Gravity.CENTER);
        dayView.setPadding(8, 8, 8, 8);

        // Calculate cell dimensions based on grid height
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = GridLayout.LayoutParams.WRAP_CONTENT;
        params.setGravity(Gravity.FILL_HORIZONTAL | Gravity.CENTER_VERTICAL);
        params.columnSpec = GridLayout.spec(col, 1f);
        params.rowSpec = GridLayout.spec(row);
        params.setMargins(1, 1, 1, 1);

        // Style current day
        if (date.equals(today)) {
            dayView.setBackgroundResource(R.drawable.bg_current_day);
            dayView.setTextColor(getResources().getColor(android.R.color.white, null));
        }

        // Style selected day
        if (date.equals(selectedDate)) {
            dayView.setBackgroundResource(R.drawable.bg_selected_day);
        }

        // Show lesson indicator
        if (hasLessons) {
            dayView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.ic_lesson_indicator);
            dayView.setCompoundDrawablePadding(4);
        }

        dayView.setOnClickListener(v -> {
            // Update selected date
            selectedDate = date;
            
            // Update the UI to show the selected date text
            updateSelectedDateView();
            
            // Update the calendar grid to refresh the selected day highlight
            updateCalendar();
            
            // Force a refresh of the lessons list for this date
            // This is important to ensure data is shown when first clicking a date
            showLessonsForSelectedDate(date);
        });
        calendarGrid.addView(dayView, params);
    }

    private void addEmptyCell(int row, int col) {
        TextView emptyView = new TextView(requireContext());
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = GridLayout.LayoutParams.WRAP_CONTENT;
        params.setGravity(Gravity.FILL_HORIZONTAL | Gravity.CENTER_VERTICAL);
        params.columnSpec = GridLayout.spec(col, 1f);
        params.rowSpec = GridLayout.spec(row);
        params.setMargins(1, 1, 1, 1);
        calendarGrid.addView(emptyView, params);
    }

    private void showLessonsForSelectedDate(LocalDate date) {
        List<Lesson> lessonsForDate = new ArrayList<>();
        
        for (Lesson lesson : lessons) {
            try {
                if (lesson.getDate() != null && lesson.getDate().equals(date.toString())) {
                    lessonsForDate.add(lesson);
                }
            } catch (Exception e) {
                // Skip invalid lessons
            }
        }
        
        // Sort lessons by start time
        lessonsForDate.sort((l1, l2) -> {
            if (l1.getStartTime() == null) return 1;
            if (l2.getStartTime() == null) return -1;
            return l1.getStartTime().compareTo(l2.getStartTime());
        });
        
        // Force refresh the adapter with the selected date's lessons
        if (lessonsForDate.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            lessonsRecyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            lessonsRecyclerView.setVisibility(View.VISIBLE);
            
            // Force adapter refresh
            adapter.setLessons(new ArrayList<>()); // Clear first
            adapter.setLessons(lessonsForDate);    // Then set new data
            adapter.notifyDataSetChanged();        // Explicitly notify of changes
            
            // Scroll to the top
            lessonsRecyclerView.scrollToPosition(0);
        }
    }

    @Override
    public void setLessons(List<Lesson> lessons) {
        if (lessons != null) {
            this.lessons = lessons;
            if (isAdded() && getView() != null) {
                // First update the calendar grid to show lesson indicators
                updateCalendar();
                
                // Then make sure to update the currently selected date's lesson list
                updateSelectedDateView();
                
                // Explicitly refresh lessons for the selected date to ensure they're shown
                showLessonsForSelectedDate(selectedDate);
            }
        }
    }
}