package com.koyama.scheduler.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.koyama.scheduler.R;
import com.koyama.scheduler.data.model.LectureChapter;
import com.koyama.scheduler.ui.LessonDetailActivity;
import com.koyama.scheduler.viewmodel.LessonViewModel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LectureChapterAdapter extends RecyclerView.Adapter<LectureChapterAdapter.ViewHolder> {
    private static final String TAG = "LectureChapterAdapter";
    private List<LectureChapter> chapters = new ArrayList<>();
    private OnItemClickListener listener;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private static final SimpleDateFormat DISPLAY_DATE_FORMAT = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
    private LessonViewModel lessonViewModel;
    
    // Comprehensive mapping table from lecture number to lesson data
    private static final Map<Integer, LectureLessonMapping> LECTURE_MAPPINGS = new HashMap<>();
    
    static {
        // Initialize the mapping for all lectures
        // Format: lectureNumber -> (date, eventType)
        LECTURE_MAPPINGS.put(1, new LectureLessonMapping("2025-03-15", "1"));
        LECTURE_MAPPINGS.put(2, new LectureLessonMapping("2025-04-08", "2"));
        LECTURE_MAPPINGS.put(3, new LectureLessonMapping("2025-03-27", "3"));
        LECTURE_MAPPINGS.put(4, new LectureLessonMapping("2025-04-17", "4"));
        LECTURE_MAPPINGS.put(5, new LectureLessonMapping("2025-03-26", "5"));
        LECTURE_MAPPINGS.put(6, new LectureLessonMapping("2025-03-19", "6"));
        LECTURE_MAPPINGS.put(7, new LectureLessonMapping("2025-03-30", "7"));
        LECTURE_MAPPINGS.put(8, new LectureLessonMapping("2025-03-19", "8"));
        LECTURE_MAPPINGS.put(9, new LectureLessonMapping("2025-03-30", "9"));
        LECTURE_MAPPINGS.put(10, new LectureLessonMapping("2025-04-13", "10"));
        
        // 2nd step lectures (11-24)
        LECTURE_MAPPINGS.put(11, new LectureLessonMapping("2025-05-24", "11"));
        LECTURE_MAPPINGS.put(12, new LectureLessonMapping("2025-05-07", "12"));
        LECTURE_MAPPINGS.put(13, new LectureLessonMapping("2025-05-07", "13"));
        LECTURE_MAPPINGS.put(14, new LectureLessonMapping("2025-05-06", "14"));
        LECTURE_MAPPINGS.put(15, new LectureLessonMapping("2025-05-17", "15"));
        LECTURE_MAPPINGS.put(16, new LectureLessonMapping("2025-05-17", "16"));
        LECTURE_MAPPINGS.put(17, new LectureLessonMapping("2025-05-14", "17"));
        LECTURE_MAPPINGS.put(18, new LectureLessonMapping("2025-05-14", "18"));
        LECTURE_MAPPINGS.put(19, new LectureLessonMapping("2025-05-06", "19"));
        LECTURE_MAPPINGS.put(20, new LectureLessonMapping("2025-05-04", "20"));
        LECTURE_MAPPINGS.put(21, new LectureLessonMapping("2025-05-03", "21", "CPR")); // Special case - CPR
        LECTURE_MAPPINGS.put(22, new LectureLessonMapping("2025-05-03", "22"));
        LECTURE_MAPPINGS.put(23, new LectureLessonMapping("2025-05-03", "23"));
        LECTURE_MAPPINGS.put(24, new LectureLessonMapping("2025-05-03", "24", "CPR")); // Special case - CPR
    }

    public LectureChapterAdapter(LessonViewModel lessonViewModel) {
        this.lessonViewModel = lessonViewModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lecture_chapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LectureChapter chapter = chapters.get(position);
        Context context = holder.itemView.getContext();
        
        // Set lecture number
        holder.lectureNumberView.setText(String.valueOf(chapter.getLectureNumber()));
        
        // Set chapter number
        holder.chapterNumberView.setText(chapter.getChapterNumber());
        
        // Set subject with appropriate formatting
        holder.subjectView.setText(chapter.getSubject());
        
        // Set textbook
        holder.textBookView.setText(chapter.getTextBook());
        
        // Set pages
        holder.pagesView.setText(chapter.getPages());
        
        // Set date if available
        if (!TextUtils.isEmpty(chapter.getDate())) {
            try {
                Date date = DATE_FORMAT.parse(chapter.getDate());
                if (date != null) {
                    holder.dateView.setText(DISPLAY_DATE_FORMAT.format(date));
                    holder.dateView.setVisibility(View.VISIBLE);
                } else {
                    holder.dateView.setVisibility(View.GONE);
                }
            } catch (ParseException e) {
                holder.dateView.setVisibility(View.GONE);
            }
        } else {
            holder.dateView.setVisibility(View.GONE);
        }
        
        // Set color based on step
        View accentStrip = holder.itemView.findViewById(R.id.accent_strip);
        if (chapter.isStep2()) {
            accentStrip.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(context, R.color.secondary)));
        } else {
            accentStrip.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(context, R.color.primary)));
        }
        
        // Apply completed status visual indication - only alpha, no strikethrough
        if (chapter.isCompleted()) {
            // Only change alpha for completed items, no strikethrough
            holder.itemView.setAlpha(0.7f);
        } else {
            // Make sure to remove strikethrough if recycled view
            holder.subjectView.setPaintFlags(holder.subjectView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.itemView.setAlpha(1.0f);
        }
        
        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(chapter);
            }
            
            // Use the lecture number directly to look up in our mapping table
            int lectureNumber = chapter.getLectureNumber();
            
            // Get the mapping for this lecture
            LectureLessonMapping mapping = LECTURE_MAPPINGS.get(lectureNumber);
            
            if (mapping != null) {
                // We have a specific mapping for this lecture
                Intent intent = new Intent(context, LessonDetailActivity.class);
                
                // Use the mapped data
                intent.putExtra("LESSON_DATE", mapping.date);
                intent.putExtra("LECTURE_NUMBER", String.valueOf(lectureNumber));
                intent.putExtra("DIRECT_EVENT_TYPE", mapping.eventType);
                
                // Set optional alternate event type if needed
                if (mapping.alternateEventType != null) {
                    intent.putExtra("ALTERNATE_EVENT_TYPE", mapping.alternateEventType);
                }
                
                // Set flag to find the lesson
                intent.putExtra("FIND_LECTURE", true);
                
                Log.d(TAG, "Opening lecture " + lectureNumber + " using mapping - date: " + 
                      mapping.date + ", eventType: " + mapping.eventType);
                
                context.startActivity(intent);
            } else {
                // No mapping available for this lecture - use the original date if available
                if (!TextUtils.isEmpty(chapter.getDate())) {
                    // Get the step number from the chapter number (like 6 from "1st step 6")
                    String stepNumber = null;
                    String chapterNumberText = chapter.getChapterNumber();
                    
                    // Parse the chapter number to extract the actual lecture step number
                    if (chapterNumberText.contains(" ")) {
                        String[] parts = chapterNumberText.split(" ");
                        if (parts.length >= 3) {
                            stepNumber = parts[2]; // Get the step number
                            // If it contains a comma (like "1st step 7,14"), just use the first number
                            if (stepNumber.contains(",")) {
                                stepNumber = stepNumber.split(",")[0];
                            }
                        }
                    }
                    
                    // Get the step type (1st step or 2nd step)
                    String stepType = chapter.isStep2() ? "2nd step" : "1st step";
                    
                    // Log what we're trying to match
                    Log.d(TAG, "Trying to match lecture: lectureNum=" + lectureNumber + 
                          ", stepNum=" + stepNumber + ", stepType=" + stepType +
                          ", date=" + chapter.getDate());
                    
                    Intent intent = new Intent(context, LessonDetailActivity.class);
                    intent.putExtra("LESSON_DATE", chapter.getDate());
                    intent.putExtra("LECTURE_NUMBER", String.valueOf(lectureNumber));
                    intent.putExtra("STEP_NUMBER", stepNumber);
                    intent.putExtra("STEP_TYPE", stepType);
                    intent.putExtra("FIND_LECTURE", true);
                    
                    context.startActivity(intent);
                } else {
                    // No date associated with this chapter yet
                    Toast.makeText(context, "No scheduled date found for this lecture", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return chapters.size();
    }

    public void setChapters(List<LectureChapter> chapters) {
        this.chapters = chapters;
        notifyDataSetChanged();
    }
    
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    
    public interface OnItemClickListener {
        void onItemClick(LectureChapter chapter);
    }
    
    /**
     * Static class to store mapping information for lectures
     */
    private static class LectureLessonMapping {
        final String date;
        final String eventType;
        final String alternateEventType;
        
        LectureLessonMapping(String date, String eventType) {
            this.date = date;
            this.eventType = eventType;
            this.alternateEventType = null;
        }
        
        LectureLessonMapping(String date, String eventType, String alternateEventType) {
            this.date = date;
            this.eventType = eventType;
            this.alternateEventType = alternateEventType;
        }
    }
    
    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView lectureNumberView;
        final TextView chapterNumberView;
        final TextView subjectView;
        final TextView textBookView;
        final TextView pagesView;
        final TextView dateView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            lectureNumberView = itemView.findViewById(R.id.text_lecture_number);
            chapterNumberView = itemView.findViewById(R.id.text_chapter_number);
            subjectView = itemView.findViewById(R.id.text_subject);
            textBookView = itemView.findViewById(R.id.text_textbook);
            pagesView = itemView.findViewById(R.id.text_pages);
            dateView = itemView.findViewById(R.id.text_date);
        }
    }
} 