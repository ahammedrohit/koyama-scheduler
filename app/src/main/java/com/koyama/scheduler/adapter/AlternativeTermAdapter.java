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
import com.koyama.scheduler.model.AlternativeTerm;
import java.util.List;

public class AlternativeTermAdapter extends RecyclerView.Adapter<AlternativeTermAdapter.ViewHolder> {
    private List<AlternativeTerm> terms;

    public AlternativeTermAdapter(List<AlternativeTerm> terms) {
        this.terms = terms;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_alternative_term, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AlternativeTerm term = terms.get(position);
        holder.textbookText.setText(term.getTextBookWord());
        holder.alternativeText.setText(term.getAlternativeWord());
        
        // Alternate background colors for better readability
        int backgroundColor = position % 2 == 0 ? 
                R.color.row_even : R.color.row_odd;
        
        holder.itemView.setBackgroundColor(
            ContextCompat.getColor(holder.itemView.getContext(), backgroundColor)
        );
        
        // Add a slightly different alpha to texts for visual hierarchy
        if (position % 2 == 0) {
            holder.textbookText.setAlpha(0.87f);
            holder.alternativeText.setAlpha(0.87f);
        } else {
            holder.textbookText.setAlpha(0.95f);
            holder.alternativeText.setAlpha(0.95f);
        }
    }

    @Override
    public int getItemCount() {
        return terms.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textbookText;
        TextView alternativeText;

        ViewHolder(View view) {
            super(view);
            textbookText = view.findViewById(R.id.textbookText);
            alternativeText = view.findViewById(R.id.alternativeText);
        }
    }
} 