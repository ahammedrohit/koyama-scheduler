package com.koyama.scheduler.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.card.MaterialCardView;
import com.koyama.scheduler.R;

public class AbbreviationsGuideActivity extends AppCompatActivity {
    
    private static final String PREF_NAME = "abbreviation_prefs";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abbreviations_guide);
        
        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        
        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        // Enable "Up" navigation
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        
        // Setup long click listeners for all abbreviation cards
        setupLongClickListeners();
    }
    
    private void setupLongClickListeners() {
        // Get all card views
        setupCardLongClick(R.id.card_at, "AT");
        setupCardLongClick(R.id.card_a50, "A50");
        setupCardLongClick(R.id.card_atp, "ATP");
        setupCardLongClick(R.id.card_pt, "PT");
        setupCardLongClick(R.id.card_cpr, "CPR");
        setupCardLongClick(R.id.card_aptit, "APTIT");
        setupCardLongClick(R.id.card_cdd, "CDD");
        setupCardLongClick(R.id.card_exrd, "EX&RD");
        setupCardLongClick(R.id.card_aps, "APS");
        setupCardLongClick(R.id.card_ext, "EXT");
    }
    
    private void setupCardLongClick(int cardId, final String abbr) {
        MaterialCardView cardView = findViewById(cardId);
        if (cardView == null) return; // Skip if card not found
        
        // Find the correct description TextView based on which card it is
        int descriptionViewId;
        switch (abbr) {
            case "AT":
                descriptionViewId = R.id.text_description;
                break;
            case "A50":
                descriptionViewId = R.id.text_description_a50;
                break;
            case "ATP":
                descriptionViewId = R.id.text_description_atp;
                break;
            case "PT":
                descriptionViewId = R.id.text_description_pt;
                break;
            case "CPR":
                descriptionViewId = R.id.text_description_cpr;
                break;
            case "APTIT":
                descriptionViewId = R.id.text_description_aptit;
                break;
            case "CDD":
                descriptionViewId = R.id.text_description_cdd;
                break;
            case "EX&RD":
                descriptionViewId = R.id.text_description_exrd;
                break;
            case "APS":
                descriptionViewId = R.id.text_description_aps;
                break;
            case "EXT":
                descriptionViewId = R.id.text_description_ext;
                break;
            default:
                return; // Skip if unknown abbreviation
        }
        
        TextView descriptionView = cardView.findViewById(descriptionViewId);
        if (descriptionView == null) return; // Skip if description view not found
        
        cardView.setOnLongClickListener(v -> {
            showEditDialog(abbr, descriptionView);
            return true;
        });
        
        // Load and display any saved custom descriptions
        String savedDesc = sharedPreferences.getString(abbr + "_desc", null);
        if (savedDesc != null && !savedDesc.isEmpty()) {
            descriptionView.setText(savedDesc);
        }
    }
    
    private void showEditDialog(String abbreviation, TextView descriptionView) {
        String currentDescription = descriptionView.getText().toString();
        
        // Create dialog with edit text
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_description, null);
        EditText editText = dialogView.findViewById(R.id.edit_description);
        editText.setText(currentDescription);
        
        builder.setTitle("Edit " + abbreviation + " Description")
               .setView(dialogView)
               .setPositiveButton("Save", (dialog, which) -> {
                   String newDescription = editText.getText().toString().trim();
                   if (!newDescription.isEmpty()) {
                       // Save to SharedPreferences
                       sharedPreferences.edit()
                           .putString(abbreviation + "_desc", newDescription)
                           .apply();
                       
                       // Update the UI
                       descriptionView.setText(newDescription);
                       Toast.makeText(AbbreviationsGuideActivity.this, 
                               abbreviation + " description updated", Toast.LENGTH_SHORT).show();
                   }
               })
               .setNegativeButton("Cancel", null)
               .create()
               .show();
    }

    // Handle "Up" navigation
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Close this activity and return to the caller
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 