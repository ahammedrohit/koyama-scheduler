package com.koyama.scheduler.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.koyama.scheduler.ui.fragments.LectureStepFragment;

public class LectureStepPagerAdapter extends FragmentStateAdapter {
    private static final int TAB_COUNT = 2;
    
    public LectureStepPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }
    
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Position 0 is first step, position 1 is second step
        return LectureStepFragment.newInstance(position + 1);
    }
    
    @Override
    public int getItemCount() {
        return TAB_COUNT;
    }
} 