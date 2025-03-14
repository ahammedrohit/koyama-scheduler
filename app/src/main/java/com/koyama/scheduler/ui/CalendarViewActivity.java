package com.koyama.scheduler.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.koyama.scheduler.R;
import com.koyama.scheduler.adapter.CalendarPagerAdapter;
import com.koyama.scheduler.data.model.Lesson;
import com.koyama.scheduler.viewmodel.LessonViewModel;

import java.util.List;

public class CalendarViewActivity extends AppCompatActivity {

    private LessonViewModel viewModel;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private CalendarPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_view);

        // Set up toolbar without setting it as support action bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        toolbar.setTitle(R.string.view_calendar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Initialize view model
        viewModel = new ViewModelProvider(this).get(LessonViewModel.class);

        // Find views
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);

        // Setup ViewPager with adapter
        adapter = new CalendarPagerAdapter(this);
        viewPager.setAdapter(adapter);
        
        // Connect TabLayout with ViewPager
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText(R.string.monthly_view);
                    break;
                case 1:
                    tab.setText(R.string.weekly_view);
                    break;
                case 2:
                    tab.setText(R.string.daily_view);
                    break;
            }
        }).attach();

        // Load lessons data into adapter
        loadLessons();
    }

    private void loadLessons() {
        // Observe all lessons
        viewModel.getAllLessons().observe(this, lessons -> {
            if (lessons != null) {
                adapter.setLessons(lessons);
            }
        });
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