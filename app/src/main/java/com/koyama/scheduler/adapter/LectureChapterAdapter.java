package com.koyama.scheduler.adapter;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.koyama.scheduler.R;
import com.koyama.scheduler.data.model.LectureChapter;
import java.util.ArrayList;
import java.util.List;

public class LectureChapterAdapter extends RecyclerView.Adapter<LectureChapterAdapter.ViewHolder> {
    private List<LectureChapter> chapters = new ArrayList<>();
    private OnItemClickListener listener;

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
        
        // Set color based on step
        View accentStrip = holder.itemView.findViewById(R.id.accent_strip);
        if (chapter.isStep2()) {
            accentStrip.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.secondary)));
        } else {
            accentStrip.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.primary)));
        }
        
        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(chapter);
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
    
    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView lectureNumberView;
        final TextView chapterNumberView;
        final TextView subjectView;
        final TextView textBookView;
        final TextView pagesView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            lectureNumberView = itemView.findViewById(R.id.text_lecture_number);
            chapterNumberView = itemView.findViewById(R.id.text_chapter_number);
            subjectView = itemView.findViewById(R.id.text_subject);
            textBookView = itemView.findViewById(R.id.text_textbook);
            pagesView = itemView.findViewById(R.id.text_pages);
        }
    }
} 