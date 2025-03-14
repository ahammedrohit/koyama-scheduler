package com.koyama.scheduler.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.koyama.scheduler.data.model.Lesson;
import com.koyama.scheduler.ui.fragment.CalendarDailyFragment;
import com.koyama.scheduler.ui.fragment.CalendarMonthlyFragment;
import com.koyama.scheduler.ui.fragment.CalendarWeeklyFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for the calendar view pager
 */
public class CalendarPagerAdapter extends FragmentStateAdapter {

    private List<Lesson> lessons = new ArrayList<>();
    private final CalendarMonthlyFragment monthlyFragment = new CalendarMonthlyFragment();
    private final CalendarWeeklyFragment weeklyFragment = new CalendarWeeklyFragment();
    private final CalendarDailyFragment dailyFragment = new CalendarDailyFragment();

    public CalendarPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return monthlyFragment;
            case 1:
                return weeklyFragment;
            case 2:
                return dailyFragment;
            default:
                return monthlyFragment;
        }
    }

    @Override
    public int getItemCount() {
        // We have 3 tabs: monthly, weekly, daily
        return 3;
    }

    /**
     * Set the lessons data and update all fragments
     * @param lessons List of lessons to display
     */
    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;

        monthlyFragment.setLessons(lessons);
        weeklyFragment.setLessons(lessons);
        dailyFragment.setLessons(lessons);

    }
}