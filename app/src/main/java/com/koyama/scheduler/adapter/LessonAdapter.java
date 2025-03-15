package com.koyama.scheduler.adapter;

import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.koyama.scheduler.R;
import com.koyama.scheduler.data.model.Lesson;

import java.util.List;

public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.LessonViewHolder> {

    private static final String TAG = "LessonAdapter";
    private List<Lesson> lessons;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Lesson lesson);
    }

    public LessonAdapter(List<Lesson> lessons) {
        this.lessons = lessons;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
        Log.d(TAG, "Setting lessons: " + lessons.size() + " lessons");
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lesson, parent, false);
        return new LessonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LessonViewHolder holder, int position) {
        Lesson lesson = lessons.get(position);
        
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
        
        holder.lessonTypeTextView.setText(lessonType);
        
        // Use primary color for all lesson indicators regardless of type
        holder.lessonColorView.setBackgroundResource(R.color.primary);
        
        // Format and set the lesson time
        String timeDisplay = String.format("%s - %s", lesson.getStartTime(), lesson.getEndTime());
        holder.lessonTimeTextView.setText(timeDisplay);
        
        // Always hide the description as requested
        holder.lessonDescriptionTextView.setVisibility(View.GONE);
        
        // Set lesson completion status 
        holder.itemView.setAlpha(lesson.isCompleted() ? 0.6f : 1.0f);
        
        // Set location chip if available (future feature)
        holder.lessonLocationChip.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return lessons == null ? 0 : lessons.size();
    }

    class LessonViewHolder extends RecyclerView.ViewHolder {
        private final View lessonColorView;
        private final TextView lessonTypeTextView;
        private final TextView lessonTimeTextView;
        private final TextView lessonDescriptionTextView;
        private final Chip lessonLocationChip;

        LessonViewHolder(View view) {
            super(view);
            lessonColorView = view.findViewById(R.id.view_lesson_color);
            lessonTypeTextView = view.findViewById(R.id.text_lesson_type);
            lessonTimeTextView = view.findViewById(R.id.text_lesson_time);
            lessonDescriptionTextView = view.findViewById(R.id.text_lesson_description);
            lessonLocationChip = view.findViewById(R.id.chip_lesson_location);

            view.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(lessons.get(position));
                }
            });
        }
    }
    
    /**
     * Get the full name of a lesson type from its code
     */
    private String getLessonTypeFullName(String eventType) {
        if (eventType == null) return "Lesson";
        
        switch (eventType.toUpperCase()) {
            case "AT":
                return "Autonomous Theory";
            case "A50":
                return "Advanced 50";
            case "ATP":
                return "Autonomous Theory Practice";
            case "PT":
                return "Practice Test";
            case "CPR":
                return "Comprehensive Review";
            case "APTIT":
            case "APTI.T":
                return "Aptitude Test";
            case "CDD":
                return "Comprehensive Driving";
            case "EXT":
                return "Extended Training";
            case "EX&RD":
                return "Exam & Road Driving";
            case "APS":
                return "Advanced Practical Skills";
            default:
                return eventType;
        }
    }

    // No longer need to use different colors, but keeping method for reference
    private int getLessonColor(String eventType) {
        return R.color.primary; // Always return primary color for consistency
    }
}