package com.koyama.scheduler.adapter;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.koyama.scheduler.R;
import com.koyama.scheduler.model.TrafficTerm;
import java.util.List;

public class TrafficTermAdapter extends RecyclerView.Adapter<TrafficTermAdapter.ViewHolder> {
    private List<TrafficTerm> terms;

    public TrafficTermAdapter(List<TrafficTerm> terms) {
        this.terms = terms;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_traffic_term, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TrafficTerm term = terms.get(position);
        holder.japaneseText.setText(term.getJapanese());
        holder.englishText.setText(term.getEnglish());
        
        // Alternate background colors for better readability
        int backgroundColor = position % 2 == 0 ? 
                R.color.row_even : R.color.row_odd;
        
        holder.itemView.setBackgroundColor(
            ContextCompat.getColor(holder.itemView.getContext(), backgroundColor)
        );
        
        // Add a slightly different alpha to texts for visual hierarchy
        if (position % 2 == 0) {
            holder.japaneseText.setAlpha(0.87f);
            holder.englishText.setAlpha(0.87f);
        } else {
            holder.japaneseText.setAlpha(0.95f);
            holder.englishText.setAlpha(0.95f);
        }
    }

    @Override
    public int getItemCount() {
        return terms.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView japaneseText;
        TextView englishText;

        ViewHolder(View view) {
            super(view);
            japaneseText = view.findViewById(R.id.japaneseText);
            englishText = view.findViewById(R.id.englishText);
        }
    }
} 