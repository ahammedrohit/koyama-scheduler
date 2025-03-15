package com.koyama.scheduler.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.koyama.scheduler.R;
import com.koyama.scheduler.data.model.Lesson;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for numbered lesson items, typically used in the MainActivity
 */
public class NumberedLessonAdapter extends RecyclerView.Adapter<NumberedLessonAdapter.NumberedLessonViewHolder> {

    private static final String TAG = "NumberedLessonAdapter";
    private List<Lesson> lessons = new ArrayList<>();
    private final OnItemClickListener listener;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");

    public interface OnItemClickListener {
        void onItemClick(Lesson lesson);
    }

    public NumberedLessonAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
        Log.d(TAG, "Number of lessons set: " + lessons.size());
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NumberedLessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_numbered_lesson, parent, false);
        return new NumberedLessonViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NumberedLessonViewHolder holder, int position) {
        Lesson lesson = lessons.get(position);
        
        // Set the lesson number - ensuring it's visible and correctly formatted
        if (lesson.getEventNumber() != null && !lesson.getEventNumber().isEmpty()) {
            holder.lessonNumberTextView.setText(lesson.getEventNumber());
            holder.lessonNumberTextView.setVisibility(View.VISIBLE);
        } else if (lesson.getEventType() != null && !lesson.getEventType().isEmpty()) {
            // If no number but has a type, display the type code
            holder.lessonNumberTextView.setText(lesson.getEventType());
            holder.lessonNumberTextView.setVisibility(View.VISIBLE);
        } else {
            // Default to position + 1 if no number or type available
            holder.lessonNumberTextView.setText(String.valueOf(position + 1));
            holder.lessonNumberTextView.setVisibility(View.VISIBLE);
        }
        
        // Format and set the lesson date
        try {
            LocalDate date = LocalDate.parse(lesson.getDate());
            String dayOfWeek = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
            String formattedDate = dayOfWeek + ", " + date.format(dateFormatter);
            holder.lessonDateTextView.setText(formattedDate);
            holder.lessonDateTextView.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            // If date parsing fails, just show the raw date
            holder.lessonDateTextView.setText(lesson.getDate());
            holder.lessonDateTextView.setVisibility(View.VISIBLE);
        }
        
        // Set the lesson time
        holder.lessonTimeTextView.setText(String.format("%s - %s", 
                lesson.getStartTime(), 
                lesson.getEndTime()));
        
        // Always hide the description as requested
        holder.lessonDescriptionTextView.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return lessons.size();
    }

    class NumberedLessonViewHolder extends RecyclerView.ViewHolder {
        private final TextView lessonNumberTextView;
        private final TextView lessonDateTextView;
        private final TextView lessonTimeTextView;
        private final TextView lessonDescriptionTextView;

        NumberedLessonViewHolder(View view) {
            super(view);
            lessonNumberTextView = view.findViewById(R.id.text_lesson_number);
            lessonDateTextView = view.findViewById(R.id.text_lesson_date);
            lessonTimeTextView = view.findViewById(R.id.text_lesson_time);
            lessonDescriptionTextView = view.findViewById(R.id.text_lesson_description);

            view.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(lessons.get(position));
                }
            });
        }
    }
} 