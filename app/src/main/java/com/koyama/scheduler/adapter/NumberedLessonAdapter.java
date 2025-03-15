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

import java.util.List;

/**
 * Adapter for numbered lesson items, typically used in the MainActivity
 */
public class NumberedLessonAdapter extends RecyclerView.Adapter<NumberedLessonAdapter.NumberedLessonViewHolder> {

    private List<Lesson> lessons;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Lesson lesson);
    }

    public NumberedLessonAdapter(List<Lesson> lessons) {
        this.lessons = lessons;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
        Log.d("NumberedLessonAdapter", "Setting lessons: " + lessons.size() + " lessons");
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NumberedLessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_numbered_lesson, parent, false);
        return new NumberedLessonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NumberedLessonViewHolder holder, int position) {
        Lesson lesson = lessons.get(position);
        holder.bind(lesson, position + 1); // Position + 1 as the lesson number
    }

    @Override
    public int getItemCount() {
        return lessons == null ? 0 : lessons.size();
    }

    class NumberedLessonViewHolder extends RecyclerView.ViewHolder {
        private TextView lessonNumber;
        private TextView lessonTime;

        public NumberedLessonViewHolder(@NonNull View itemView) {
            super(itemView);
            lessonNumber = itemView.findViewById(R.id.text_lesson_number);
            lessonTime = itemView.findViewById(R.id.text_lesson_time);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(lessons.get(position));
                }
            });
        }

        public void bind(Lesson lesson, int number) {
            // Set the lesson number
            lessonNumber.setText(String.valueOf(number));
            
            // Format and set the time range
            String timeRange = lesson.getStartTime() + " - " + lesson.getEndTime();
            lessonTime.setText(timeRange);
        }
    }
} 