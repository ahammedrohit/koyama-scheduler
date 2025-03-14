package com.koyama.scheduler.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.koyama.scheduler.data.model.Lesson;
import com.koyama.scheduler.ui.LessonDetailActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Base fragment for calendar views
 */
public abstract class BaseCalendarFragment extends Fragment {

    protected List<Lesson> lessons = new ArrayList<>();
    protected TextView emptyView;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
    }

    /**
     * Initialize the views
     */
    protected abstract void initializeViews(View view);

    /**
     * Update the calendar with the lessons data
     */
    protected abstract void updateCalendar();

    /**
     * Set the lessons data and update the calendar
     */
    public void setLessons(List<Lesson> lessons) {
        if (lessons != null) {
            this.lessons = lessons;
            if (isAdded() && getView() != null) {
                updateCalendar();
            }
        }
    }

    /**
     * Open lesson detail activity for the selected lesson
     */
    protected void openLessonDetail(Lesson lesson) {
        if (lesson != null && getActivity() != null) {
            Intent intent = new Intent(getActivity(), LessonDetailActivity.class);
            intent.putExtra("LESSON_ID", lesson.getId());
            startActivity(intent);
        }
    }
}