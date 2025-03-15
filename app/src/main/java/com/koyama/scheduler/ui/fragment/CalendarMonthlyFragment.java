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

        // Setup RecyclerView
        lessonsRecyclerView = view.findViewById(R.id.recycler_lessons);
        lessonsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
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
        List<Lesson> lessonsForDate = new ArrayList<>();
        for (Lesson lesson : lessons) {
            if (lesson.getDate().equals(selectedDate.toString())) {
                lessonsForDate.add(lesson);
            }
        }

        if (lessonsForDate.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            lessonsRecyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            lessonsRecyclerView.setVisibility(View.VISIBLE);
            adapter.setLessons(lessonsForDate);
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

        // Make the cell fill its space
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = 0;
        params.setGravity(Gravity.FILL);
        params.columnSpec = GridLayout.spec(col, 1f);
        params.rowSpec = GridLayout.spec(row, 1f);
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
            selectedDate = date;
            updateSelectedDateView();
            updateCalendar(); // To refresh the selected day highlight
        });
        calendarGrid.addView(dayView, params);
    }

    private void addEmptyCell(int row, int col) {
        TextView emptyView = new TextView(requireContext());
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = 0;
        params.setGravity(Gravity.FILL);
        params.columnSpec = GridLayout.spec(col, 1f);
        params.rowSpec = GridLayout.spec(row, 1f);
        params.setMargins(1, 1, 1, 1);
        calendarGrid.addView(emptyView, params);
    }

    private void showLessonsForSelectedDate(LocalDate date) {
        List<Lesson> lessonsForDate = new ArrayList<>();
        
        for (Lesson lesson : lessons) {
            if (lesson.getDate().equals(date.toString())) {
                lessonsForDate.add(lesson);
            }
        }

        if (!lessonsForDate.isEmpty()) {
            // Open the lesson detail activity for the first lesson of the day
            openLessonDetail(lessonsForDate.get(0));
        } else {
            Toast.makeText(getContext(),
                    String.format("No lessons on %s", 
                        date.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))),
                    Toast.LENGTH_SHORT).show();
        }
    }
}