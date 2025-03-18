package com.koyama.scheduler.adapter;

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
        
        // Set alternating background colors
        holder.itemView.setBackgroundColor(position % 2 == 0 ?
                ContextCompat.getColor(holder.itemView.getContext(), android.R.color.white) :
                ContextCompat.getColor(holder.itemView.getContext(), R.color.colorPrimaryLight));

        holder.lectureNumberView.setText(String.valueOf(chapter.getLectureNumber()));
        holder.chapterNumberView.setText(chapter.getChapterNumber());
        holder.subjectView.setText(chapter.getSubject());
        holder.textBookView.setText(chapter.getTextBook());
        holder.pagesView.setText(chapter.getPages());
    }

    @Override
    public int getItemCount() {
        return chapters.size();
    }

    public void setChapters(List<LectureChapter> chapters) {
        this.chapters = chapters;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView lectureNumberView;
        final TextView chapterNumberView;
        final TextView subjectView;
        final TextView textBookView;
        final TextView pagesView;

        ViewHolder(View view) {
            super(view);
            lectureNumberView = view.findViewById(R.id.text_lecture_number);
            chapterNumberView = view.findViewById(R.id.text_chapter_number);
            subjectView = view.findViewById(R.id.text_subject);
            textBookView = view.findViewById(R.id.text_textbook);
            pagesView = view.findViewById(R.id.text_pages);
        }
    }
} 