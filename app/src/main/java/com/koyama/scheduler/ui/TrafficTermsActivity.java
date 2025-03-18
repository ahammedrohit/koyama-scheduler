package com.koyama.scheduler.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.koyama.scheduler.R;
import com.koyama.scheduler.adapter.TermsPagerAdapter;

public class TrafficTermsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic_terms);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.traffic_terms);
            getSupportActionBar().setElevation(0);
        }

        // Set up ViewPager and TabLayout
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        // Create and set adapter
        TermsPagerAdapter pagerAdapter = new TermsPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        
        // Reduce swipe sensitivity to prevent accidental swipes
        viewPager.setUserInputEnabled(true);

        // Connect the TabLayout with the ViewPager
        new TabLayoutMediator(tabLayout, viewPager,
            (tab, position) -> {
                switch (position) {
                    case 0:
                        tab.setText(R.string.traffic_terms_tab);
                        break;
                    case 1:
                        tab.setText(R.string.alternative_terms);
                        break;
                }
            }
        ).attach();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 