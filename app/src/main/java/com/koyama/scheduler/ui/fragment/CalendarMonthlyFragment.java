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

import com.koyama.scheduler.R;
import com.koyama.scheduler.data.model.Lesson;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Fragment for monthly calendar view
 * For a complete implementation, we would use a proper calendar library like CalendarView from kizitonwose
 * This is a simplified version for demonstration purposes
 */
public class CalendarMonthlyFragment extends BaseCalendarFragment {

    private TextView emptyView;
    private YearMonth currentMonth = YearMonth.now();
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Simplified calendar view for demonstration
        // In a real app, we'd use a proper calendar library
        return inflater.inflate(R.layout.fragment_calendar_monthly, container, false);
    }

    @Override
    protected void initializeViews(View view) {
        emptyView = view.findViewById(R.id.text_empty);
        TextView monthTitle = view.findViewById(R.id.text_month_title);
        monthTitle.setText(currentMonth.format(formatter));

        View previousButton = view.findViewById(R.id.button_previous);
        View nextButton = view.findViewById(R.id.button_next);
        View todayButton = view.findViewById(R.id.button_today);
        GridLayout calendarGrid = view.findViewById(R.id.grid_calendar);

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
            monthTitle.setText(currentMonth.format(formatter));
            updateCalendar();
        });

        updateCalendar();
    }

    @Override
    protected void updateCalendar() {
        if (lessons.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            return;
        }

        emptyView.setVisibility(View.GONE);

        GridLayout calendarGrid = getView().findViewById(R.id.grid_calendar);
        calendarGrid.removeAllViews();

        YearMonth yearMonth = currentMonth;
        LocalDate firstOfMonth = yearMonth.atDay(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();

        LocalDate today = LocalDate.now();
        Map<LocalDate, Integer> lessonsPerDay = new HashMap<>();

        for (Lesson lesson : lessons) {
            LocalDate lessonDate = LocalDate.parse(lesson.getDate());
            if (lessonDate.getMonth() == currentMonth.getMonth() &&
                lessonDate.getYear() == currentMonth.getYear()) {
                lessonsPerDay.put(lessonDate, lessonsPerDay.getOrDefault(lessonDate, 0) + 1);
            }
        }

        for (int i = 1; i <= yearMonth.lengthOfMonth(); i++) {
            LocalDate date = yearMonth.atDay(i);
            TextView dayView = new TextView(getContext());
            dayView.setText(String.valueOf(i));
            dayView.setGravity(Gravity.CENTER);
            dayView.setPadding(8, 8, 8, 8);

            if (date.equals(today)) {
                dayView.setBackgroundColor(getResources().getColor(R.color.primary, null));
            }

            if (lessonsPerDay.containsKey(date)) {
                dayView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.ic_notification);
            }

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.rowSpec = GridLayout.spec((i + dayOfWeek - 2) / 7);
            params.columnSpec = GridLayout.spec((i + dayOfWeek - 2) % 7);
            calendarGrid.addView(dayView, params);

            dayView.setOnClickListener(v -> showLessonsForSelectedDate(date));
        }
    }

    /**
     * Show lessons for the selected date
     */
    private void showLessonsForSelectedDate(LocalDate date) {
        // Find lessons for the selected date
        int count = 0;
        for (Lesson lesson : lessons) {
            LocalDate lessonDate = LocalDate.parse(lesson.getDate());
            if (lessonDate.equals(date)) {
                count++;
                // In a real implementation, we might display these lessons in a popup or bottom sheet
            }
        }

        if (count > 0) {
            Toast.makeText(getContext(), 
                    count + " lessons on " + date.format(DateTimeFormatter.ofPattern("MMM d, yyyy")),
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(),
                    "No lessons on " + date.format(DateTimeFormatter.ofPattern("MMM d, yyyy")),
                    Toast.LENGTH_SHORT).show();
        }
    }
}