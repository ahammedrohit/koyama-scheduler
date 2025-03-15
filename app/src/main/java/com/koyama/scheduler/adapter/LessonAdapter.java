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

import com.koyama.scheduler.R;
import com.koyama.scheduler.data.model.Lesson;

import java.util.List;

public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.LessonViewHolder> {

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
        Log.d("LessonAdapter", "Setting lessons: " + lessons.size() + " lessons");
        for (Lesson lesson : lessons) {
            Log.d("LessonAdapter", "Lesson: " + lesson.getDate() + " " + lesson.getStartTime() + " - " + lesson.getEndTime() + " " + lesson.getDescription());
        }
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
        holder.bind(lesson);
    }

    @Override
    public int getItemCount() {
        return lessons == null ? 0 : lessons.size();
    }

    class LessonViewHolder extends RecyclerView.ViewHolder {
        private View lessonColor;
        private TextView lessonType;
        private TextView lessonTime;
        private TextView lessonDescription;

        public LessonViewHolder(@NonNull View itemView) {
            super(itemView);
            lessonColor = itemView.findViewById(R.id.view_lesson_color);
            lessonType = itemView.findViewById(R.id.text_lesson_type);
            lessonTime = itemView.findViewById(R.id.text_lesson_time);
            lessonDescription = itemView.findViewById(R.id.text_lesson_description);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(lessons.get(position));
                }
            });
        }

        public void bind(Lesson lesson) {
            Log.d("LessonAdapter", "Binding lesson: " + lesson.getDate() + " " + lesson.getStartTime() + " - " + lesson.getEndTime() + " " + lesson.getDescription());
            // Set lesson type color
            int colorResId = getLessonTypeColorResId(lesson.getEventType());
            lessonColor.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), colorResId));

            // Set text values
            lessonType.setText(getLessonTypeDisplay(lesson.getEventType()));
            lessonTime.setText(String.format("%s - %s", lesson.getStartTime(), lesson.getEndTime()));
            lessonDescription.setText(lesson.getDescription());
        }

        private int getLessonTypeColorResId(String eventType) {
            boolean isNightMode = itemView.getContext().getResources().getBoolean(R.bool.is_night_mode);
            switch (eventType) {
                case "AT":
                    return isNightMode ? R.color.lesson_at_dark : R.color.lesson_at;
                case "A50":
                    return isNightMode ? R.color.lesson_a50_dark : R.color.lesson_a50;
                case "ATP":
                    return isNightMode ? R.color.lesson_atp_dark : R.color.lesson_atp;
                case "PT":
                    return isNightMode ? R.color.lesson_pt_dark : R.color.lesson_pt;
                case "CPR":
                    return isNightMode ? R.color.lesson_cpr_dark : R.color.lesson_cpr;
                case "Apti.t":
                    return isNightMode ? R.color.lesson_aptit_dark : R.color.lesson_aptit;
                case "CDD":
                    return isNightMode ? R.color.lesson_cdd_dark : R.color.lesson_cdd;
                case "EXT":
                    return isNightMode ? R.color.lesson_ext_dark : R.color.lesson_ext;
                case "EX&RD":
                    return isNightMode ? R.color.lesson_exrd_dark : R.color.lesson_exrd;
                case "APS":
                    return isNightMode ? R.color.lesson_aps_dark : R.color.lesson_aps;
                default:
                    return isNightMode ? R.color.primary_dark : R.color.primary;
            }
        }

        private String getLessonTypeDisplay(String eventType) {
            switch (eventType) {
                case "AT":
                    return itemView.getContext().getString(R.string.lesson_at);
                case "A50":
                    return itemView.getContext().getString(R.string.lesson_a50);
                case "ATP":
                    return itemView.getContext().getString(R.string.lesson_atp);
                case "PT":
                    return itemView.getContext().getString(R.string.lesson_pt);
                case "CPR":
                    return itemView.getContext().getString(R.string.lesson_cpr);
                case "Apti.t":
                    return itemView.getContext().getString(R.string.lesson_aptit);
                case "CDD":
                    return itemView.getContext().getString(R.string.lesson_cdd);
                case "EXT":
                    return itemView.getContext().getString(R.string.lesson_ext);
                case "EX&RD":
                    return itemView.getContext().getString(R.string.lesson_exrd);
                case "APS":
                    return itemView.getContext().getString(R.string.lesson_aps);
                default:
                    return eventType;
            }
        }
    }
}