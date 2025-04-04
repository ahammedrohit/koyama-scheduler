package com.koyama.scheduler.adapter;

import android.content.Context;
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
import com.koyama.scheduler.util.AbbreviationHelper;

import java.util.List;
import java.util.Map;

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
            
            // Add lesson number if available
            if (lesson.getEventNumber() != null && !lesson.getEventNumber().isEmpty()) {
                lessonType += " " + lesson.getEventNumber();
            }
        } else if (lesson.getEventNumber() != null && !lesson.getEventNumber().isEmpty()) {
            lessonType = "L" + lesson.getEventNumber();
        } else {
            lessonType = "Lesson";
        }
        
        holder.lessonTypeTextView.setText(lessonType);
        
        // Set color indicator using event type
        int colorResId = getColorForLessonType(lesson.getEventType());
        holder.lessonColorView.setBackgroundColor(
            ContextCompat.getColor(holder.itemView.getContext(), colorResId));
        
        // Format and set the lesson time
        String timeDisplay = String.format("%s - %s", lesson.getStartTime(), lesson.getEndTime());
        holder.lessonTimeTextView.setText(timeDisplay);
        
        // Set description if available (currently hidden per request)
        if (lesson.getDescription() != null && !lesson.getDescription().isEmpty()) {
            holder.lessonDescriptionTextView.setText(lesson.getDescription());
            holder.lessonDescriptionTextView.setVisibility(View.GONE); // Hidden as requested
        } else {
            holder.lessonDescriptionTextView.setVisibility(View.GONE);
        }
        
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
     * Gets the display name for a lesson type
     */
    private String getDisplayForLessonType(String eventType, Context context) {
        if (eventType == null || eventType.isEmpty()) {
            return "Lesson";
        }
        
        // Standardize the abbreviation
        String standardAbbr = AbbreviationHelper.standardizeAbbreviation(eventType);
        
        // Get display name from context using AbbreviationHelper
        Map<String, String> displayNames = AbbreviationHelper.getAbbreviationDisplayNames(context);
        String displayName = displayNames.get(standardAbbr);
        
        return displayName != null ? displayName : standardAbbr;
    }
    
    /**
     * Gets the color resource for a lesson type
     */
    private int getColorForLessonType(String eventType) {
        if (eventType == null || eventType.isEmpty()) {
            return R.color.lesson_general;
        }
        
        // Standardize the abbreviation
        String standardAbbr = AbbreviationHelper.standardizeAbbreviation(eventType);
        
        // Get the color for the standard abbreviation
        switch (standardAbbr) {
            case "AT":
                return R.color.lesson_at;
            case "A50":
                return R.color.lesson_a50;
            case "ATP":
                return R.color.lesson_atp;
            case "PT":
                return R.color.lesson_pt;
            case "CPR":
                return R.color.lesson_cpr;
            case "APTIT":
                return R.color.lesson_aptit;
            case "CDD":
                return R.color.lesson_cdd;
            case "EXT":
                return R.color.lesson_ext;
            case "EX&RD":
                return R.color.lesson_exrd;
            case "APS":
                return R.color.lesson_aps;
            default:
                return R.color.lesson_general;
        }
    }
}