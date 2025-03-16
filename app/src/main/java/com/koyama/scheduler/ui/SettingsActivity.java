package com.koyama.scheduler.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.koyama.scheduler.R;

public class SettingsActivity extends AppCompatActivity {

    // Preference keys
    private static final String KEY_NOTIFICATIONS_ENABLED = "notifications_enabled";
    private static final String KEY_NOTIFICATION_TIMING = "notification_timing";
    private static final String KEY_TIME_FORMAT = "time_format";
    private static final String KEY_THEME = "theme";
    
    // Default values
    private static final boolean DEFAULT_NOTIFICATIONS_ENABLED = true;
    private static final String DEFAULT_NOTIFICATION_TIMING = "three_hours_before";
    private static final String DEFAULT_TIME_FORMAT = "24h";
    private static final String DEFAULT_THEME = "light"; // Changed to light since we're removing dark mode

    // UI elements
    private SwitchMaterial notificationsEnabledSwitch;
    private Spinner notificationTimingSpinner;
    private RadioGroup calendarRadioGroup;
    private SwitchMaterial darkModeSwitch;

    // Shared preferences
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        toolbar.setTitle(R.string.settings);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Get shared preferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Find views
        notificationsEnabledSwitch = findViewById(R.id.switch_notifications);
        notificationTimingSpinner = findViewById(R.id.spinner_notification_time);
        calendarRadioGroup = findViewById(R.id.radio_group_calendar);
        darkModeSwitch = findViewById(R.id.switch_dark_mode);

        // Load saved preferences
        loadPreferences();

        // Set up listeners
        setupListeners();
    }

    private void loadPreferences() {
        // Load notification preferences
        boolean notificationsEnabled = sharedPreferences.getBoolean(
                KEY_NOTIFICATIONS_ENABLED, DEFAULT_NOTIFICATIONS_ENABLED);
        notificationsEnabledSwitch.setChecked(notificationsEnabled);
        
        // Load notification timing preference
        String notificationTiming = sharedPreferences.getString(
                KEY_NOTIFICATION_TIMING, DEFAULT_NOTIFICATION_TIMING);
        
        // Set spinner selection based on saved preference
        int spinnerPosition = 1; // Default to three_hours_before (position 1)
        switch (notificationTiming) {
            case "one_day_before":
                spinnerPosition = 0;
                break;
            case "three_hours_before":
                spinnerPosition = 1;
                break;
            case "thirty_minutes_before":
                spinnerPosition = 2;
                break;
        }
        
        // Create adapter for spinner
        String[] notificationOptions = {
            getString(R.string.one_day_before),
            getString(R.string.three_hours_before),
            getString(R.string.thirty_minutes_before)
        };
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this, android.R.layout.simple_spinner_item, notificationOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        notificationTimingSpinner.setAdapter(adapter);
        notificationTimingSpinner.setSelection(spinnerPosition);

        // Load theme from shared preferences
        String themeValue = sharedPreferences.getString(KEY_THEME, DEFAULT_THEME);
        if ("dark".equals(themeValue)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        boolean isDarkEnabled = "dark".equals(themeValue);
        darkModeSwitch.setChecked(isDarkEnabled);
    }

    private void setupListeners() {
        // Notification enabled switch
        notificationsEnabledSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, isChecked).apply();
            
            // Enable/disable notification timing spinner based on switch state
            notificationTimingSpinner.setEnabled(isChecked);
        });
        
        // Notification timing spinner
        notificationTimingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String timing;
                switch (position) {
                    case 0:
                        timing = "one_day_before";
                        break;
                    case 1:
                        timing = "three_hours_before";
                        break;
                    case 2:
                        timing = "thirty_minutes_before";
                        break;
                    default:
                        timing = DEFAULT_NOTIFICATION_TIMING;
                }
                
                sharedPreferences.edit().putString(KEY_NOTIFICATION_TIMING, timing).apply();
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
        
        // Dark mode switch
        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String themeValue = isChecked ? "dark" : "light";
            sharedPreferences.edit().putString(KEY_THEME, themeValue).apply();
            if ("dark".equals(themeValue)) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        // Initialize enabled state for notification timing spinner
        boolean notificationsEnabled = notificationsEnabledSwitch.isChecked();
        notificationTimingSpinner.setEnabled(notificationsEnabled);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
