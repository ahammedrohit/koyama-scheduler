package com.koyama.scheduler.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.koyama.scheduler.R;
import com.koyama.scheduler.data.model.Lesson;
import com.koyama.scheduler.util.ScheduleGroup;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class ScheduleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_LESSON = 1;

    private final List<Object> items = new ArrayList<>();
    private final OnLessonClickListener clickListener;
    
    // Formatters for different date formats
    private final DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault());
    private final DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.getDefault());
    private final DateTimeFormatter weekFormatter = DateTimeFormatter.ofPattern("'Week' w, yyyy", Locale.getDefault());

    public interface OnLessonClickListener {
        void onLessonClick(Lesson lesson);
    }

    public ScheduleAdapter(List<Lesson> lessons, OnLessonClickListener clickListener) {
        this.clickListener = clickListener;
        updateData(lessons, ScheduleGroup.MONTH);
    }

    public void updateData(List<Lesson> lessons, ScheduleGroup groupType) {
        items.clear();
        
        if (lessons == null || lessons.isEmpty()) {
            notifyDataSetChanged();
            return;
        }

        // Group lessons based on selected group type
        Map<String, List<Lesson>> groupedLessons;
        
        // Use a specific comparator for month sorting to ensure chronological order
        if (groupType == ScheduleGroup.MONTH) {
            // This TreeMap will sort by actual YearMonth value, not by the formatted string
            Map<YearMonth, List<Lesson>> tempMap = new TreeMap<>();
            groupByMonthChronologically(lessons, tempMap);
            
            // Convert to string keys in the correct order
            groupedLessons = new LinkedHashMap<>();
            for (Map.Entry<YearMonth, List<Lesson>> entry : tempMap.entrySet()) {
                String key = entry.getKey().format(monthFormatter);
                groupedLessons.put(key, entry.getValue());
            }
        } else {
            groupedLessons = new TreeMap<>();
            
            switch (groupType) {
                case WEEK:
                    groupByWeek(lessons, groupedLessons);
                    break;
                case DAY:
                    groupByDay(lessons, groupedLessons);
                    break;
                case TYPE:
                    groupByType(lessons, groupedLessons);
                    break;
                case STATUS:
                    groupByStatus(lessons, groupedLessons);
                    break;
            }
        }

        // Add headers and lessons to items list
        for (Map.Entry<String, List<Lesson>> entry : groupedLessons.entrySet()) {
            // Add header
            items.add(new SectionHeader(entry.getKey(), entry.getValue().size()));
            
            // Add lessons in this group
            items.addAll(entry.getValue());
        }

        notifyDataSetChanged();
    }

    private void groupByMonthChronologically(List<Lesson> lessons, Map<YearMonth, List<Lesson>> groupedLessons) {
        for (Lesson lesson : lessons) {
            try {
                LocalDate date = LocalDate.parse(lesson.getDate());
                YearMonth yearMonth = YearMonth.of(date.getYear(), date.getMonth());
                
                if (!groupedLessons.containsKey(yearMonth)) {
                    groupedLessons.put(yearMonth, new ArrayList<>());
                }
                groupedLessons.get(yearMonth).add(lesson);
            } catch (Exception e) {
                // If date parsing fails, we'll handle "Other" at the end
            }
        }
        
        // Handle any lessons with invalid dates by adding them to an "Other" group at the end
        List<Lesson> invalidDateLessons = new ArrayList<>();
        for (Lesson lesson : lessons) {
            try {
                LocalDate.parse(lesson.getDate());
            } catch (Exception e) {
                invalidDateLessons.add(lesson);
            }
        }
        
        if (!invalidDateLessons.isEmpty()) {
            // Create a "far future" YearMonth to ensure "Other" appears at the end
            YearMonth farFuture = YearMonth.of(9999, 12);
            groupedLessons.put(farFuture, invalidDateLessons);
        }
    }

    private void groupByMonth(List<Lesson> lessons, Map<String, List<Lesson>> groupedLessons) {
        for (Lesson lesson : lessons) {
            try {
                LocalDate date = LocalDate.parse(lesson.getDate());
                YearMonth yearMonth = YearMonth.of(date.getYear(), date.getMonth());
                String key = yearMonth.format(monthFormatter);
                
                addToGroup(groupedLessons, key, lesson);
            } catch (Exception e) {
                // If date parsing fails, add to "Other" group
                addToGroup(groupedLessons, "Other", lesson);
            }
        }
    }

    private void groupByWeek(List<Lesson> lessons, Map<String, List<Lesson>> groupedLessons) {
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        
        for (Lesson lesson : lessons) {
            try {
                LocalDate date = LocalDate.parse(lesson.getDate());
                int weekNumber = date.get(weekFields.weekOfWeekBasedYear());
                int year = date.getYear();
                
                // Find start of week
                LocalDate weekStart = date.with(DayOfWeek.MONDAY);
                LocalDate weekEnd = date.with(DayOfWeek.SUNDAY);
                
                String key = String.format("Week %d (%s - %s)", 
                        weekNumber,
                        weekStart.format(DateTimeFormatter.ofPattern("MMM d")),
                        weekEnd.format(DateTimeFormatter.ofPattern("MMM d, yyyy")));
                
                addToGroup(groupedLessons, key, lesson);
            } catch (Exception e) {
                // If date parsing fails, add to "Other" group
                addToGroup(groupedLessons, "Other", lesson);
            }
        }
    }

    private void groupByDay(List<Lesson> lessons, Map<String, List<Lesson>> groupedLessons) {
        for (Lesson lesson : lessons) {
            try {
                LocalDate date = LocalDate.parse(lesson.getDate());
                String key = date.format(dayFormatter);
                
                addToGroup(groupedLessons, key, lesson);
            } catch (Exception e) {
                // If date parsing fails, add to "Other" group
                addToGroup(groupedLessons, "Other", lesson);
            }
        }
    }

    private void groupByType(List<Lesson> lessons, Map<String, List<Lesson>> groupedLessons) {
        for (Lesson lesson : lessons) {
            String type = lesson.getEventType();
            if (type == null || type.isEmpty()) {
                type = "Other";
            }
            
            addToGroup(groupedLessons, type, lesson);
        }
    }

    private void groupByStatus(List<Lesson> lessons, Map<String, List<Lesson>> groupedLessons) {
        LocalDate today = LocalDate.now();
        
        for (Lesson lesson : lessons) {
            String status;
            if (lesson.isCompleted()) {
                status = "Completed";
            } else {
                try {
                    LocalDate lessonDate = LocalDate.parse(lesson.getDate());
                    if (lessonDate.isBefore(today)) {
                        status = "Past (Auto-completed)";
                    } else if (lessonDate.equals(today)) {
                        status = "Today";
                    } else {
                        status = "Upcoming";
                    }
                } catch (Exception e) {
                    status = "Unknown Date";
                }
            }
            
            addToGroup(groupedLessons, status, lesson);
        }
    }

    private void addToGroup(Map<String, List<Lesson>> groupedLessons, String key, Lesson lesson) {
        if (!groupedLessons.containsKey(key)) {
            groupedLessons.put(key, new ArrayList<>());
        }
        groupedLessons.get(key).add(lesson);
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position) instanceof SectionHeader ? TYPE_HEADER : TYPE_LESSON;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        
        if (viewType == TYPE_HEADER) {
            View view = inflater.inflate(R.layout.item_schedule_section_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_lesson, parent, false);
            return new LessonViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            SectionHeader header = (SectionHeader) items.get(position);
            ((HeaderViewHolder) holder).bind(header);
        } else if (holder instanceof LessonViewHolder) {
            Lesson lesson = (Lesson) items.get(position);
            ((LessonViewHolder) holder).bind(lesson);
            
            holder.itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onLessonClick(lesson);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView;
        private final TextView countTextView;

        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.text_section_title);
            countTextView = itemView.findViewById(R.id.text_section_count);
        }

        void bind(SectionHeader header) {
            titleTextView.setText(header.getTitle());
            
            String countText = header.getCount() == 1 
                    ? "1 lesson" 
                    : header.getCount() + " lessons";
            countTextView.setText(countText);
        }
    }

    /**
     * Class representing a section header in the list
     */
    private static class SectionHeader {
        private final String title;
        private final int count;

        SectionHeader(String title, int count) {
            this.title = title;
            this.count = count;
        }

        String getTitle() {
            return title;
        }

        int getCount() {
            return count;
        }
    }

    /**
     * ViewHolder for lesson items
     */
    class LessonViewHolder extends RecyclerView.ViewHolder {
        private final View lessonColorView;
        private final TextView lessonTypeTextView;
        private final TextView lessonTimeTextView;
        private final TextView lessonDateTextView;
        private final TextView lessonDescriptionTextView;
        private final Chip lessonLocationChip;

        LessonViewHolder(@NonNull View view) {
            super(view);
            lessonColorView = view.findViewById(R.id.view_lesson_color);
            lessonTypeTextView = view.findViewById(R.id.text_lesson_type);
            lessonTimeTextView = view.findViewById(R.id.text_lesson_time);
            lessonDateTextView = view.findViewById(R.id.text_lesson_date);
            lessonDescriptionTextView = view.findViewById(R.id.text_lesson_description);
            lessonLocationChip = view.findViewById(R.id.chip_lesson_location);
        }

        void bind(Lesson lesson) {
            // Set the lesson type - show original abbreviation code (AT, PT, etc.)
            String lessonType = "";
            
            if (lesson.getEventType() != null && !lesson.getEventType().isEmpty()) {
                // Use the original code directly (AT, PT, etc.)
                lessonType = lesson.getEventType().toUpperCase();
            } else if (lesson.getEventNumber() != null && !lesson.getEventNumber().isEmpty()) {
                lessonType = "L" + lesson.getEventNumber();
            } else {
                lessonType = "L";
            }
            
            lessonTypeTextView.setText(lessonType);
            
            // Use primary color for all lesson indicators
            lessonColorView.setBackgroundResource(R.color.primary);
            
            // Format and set the lesson time
            String timeDisplay = String.format("%s - %s", lesson.getStartTime(), lesson.getEndTime());
            lessonTimeTextView.setText(timeDisplay);
            
            // Set the date separately
            try {
                LocalDate date = LocalDate.parse(lesson.getDate());
                // Updated format to include day of week
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEE, MMM d, yyyy", Locale.getDefault());
                String formattedDate = date.format(dateFormatter);
                lessonDateTextView.setText(formattedDate);
            } catch (Exception e) {
                // If date parsing fails, show "Unknown Date"
                lessonDateTextView.setText("Unknown Date");
            }
            
            // Always hide the description as requested
            lessonDescriptionTextView.setVisibility(View.GONE);
            
            // Set lesson completion status 
            itemView.setAlpha(lesson.isCompleted() ? 0.6f : 1.0f);
            
            // Set location chip if available (future feature)
            lessonLocationChip.setVisibility(View.GONE);
        }
    }
} 