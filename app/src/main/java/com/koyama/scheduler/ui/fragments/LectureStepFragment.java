package com.koyama.scheduler.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.koyama.scheduler.R;
import com.koyama.scheduler.adapter.LectureChapterAdapter;
import com.koyama.scheduler.data.model.LectureChapter;

import java.util.ArrayList;
import java.util.List;

public class LectureStepFragment extends Fragment {
    private static final String ARG_STEP = "step";
    
    private RecyclerView recyclerView;
    private TextView emptyView;
    private LectureChapterAdapter adapter;
    private int step;
    
    public static LectureStepFragment newInstance(int step) {
        LectureStepFragment fragment = new LectureStepFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_STEP, step);
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            step = getArguments().getInt(ARG_STEP);
        }
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lecture_step, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        recyclerView = view.findViewById(R.id.recycler_view);
        emptyView = view.findViewById(R.id.empty_view);
        
        adapter = new LectureChapterAdapter();
        recyclerView.setAdapter(adapter);
        
        loadChapters();
    }
    
    private void loadChapters() {
        List<LectureChapter> chapters = new ArrayList<>();
        
        if (step == 1) {
            chapters.add(new LectureChapter(1, "1st step 1", "Before you get behind the wheel", "Rules of\nthe road", "11~22", false));
            chapters.add(new LectureChapter(2, "1st step 2", "Observation of traffic signals", "Rules of\nthe road", "23~32", false));
            chapters.add(new LectureChapter(3, "1st step 3", "Observation of signs and markings", "Rules of\nthe road", "33~52", false));
            chapters.add(new LectureChapter(4, "1st step 4", "Where vehicles can and cannot drive", "Rules of\nthe road", "53~62", false));
            chapters.add(new LectureChapter(5, "1st step 6", "Driving through an intersection/Railway crossing", "Rules of\nthe road", "67~80", false));
            chapters.add(new LectureChapter(6, "1st step 7,14", "Safe speed and distance between vehicles\nDriving vehicles with an automatic transmission", "Rules of\nthe road", "81~90\n145~148", false));
            chapters.add(new LectureChapter(7, "1st step 8", "Be on the alert for pedestrians", "Rules of\nthe road", "91~100", false));
            chapters.add(new LectureChapter(8, "1st step 9,10", "Safety checks and use of signals and horn\nChanging lanes", "Rules of\nthe road", "101~112", false));
            chapters.add(new LectureChapter(9, "1st step 5,11,12", "Priority for Emergency vehicle, etc\nOvertaking and passing\nPassing by", "Rules of\nthe road", "63~66\n113~124", false));
            chapters.add(new LectureChapter(10, "1st step 13", "Driver's license system / Traffic violation\nNotification system", "Rules of\nthe road", "125~144", false));
        } else {
            chapters.add(new LectureChapter(11, "2nd step 4", "Blind spots and driving", "Rules of\nthe road", "161~170", true));
            chapters.add(new LectureChapter(12, "2nd step 5", "Behavior analysis based on the results of Aptitude tests", "Rules of\nthe road", "171~180", true));
            chapters.add(new LectureChapter(13, "2nd step 6", "Human ability and driving", "Rules of\nthe road", "181~190", true));
            chapters.add(new LectureChapter(14, "2nd step 7", "Natural forces that act on a vehicle and driving", "Rules of\nthe road", "191~208", true));
            chapters.add(new LectureChapter(15, "2nd step 8", "Driving in adverse environments", "Rules of\nthe road", "209~228", true));
            chapters.add(new LectureChapter(16, "2nd step 9", "Characteristics of traffic accidents and tragic results", "Rules of\nthe road", "229~240", true));
            chapters.add(new LectureChapter(17, "2nd step 10", "Maintenance / Management of motor Vehicles", "Rules of\nthe road", "241~256", true));
            chapters.add(new LectureChapter(18, "2nd step 11", "Parking and stopping", "Rules of\nthe road", "257~270", true));
            chapters.add(new LectureChapter(19, "2nd step 12,13", "Vehicle seating capacity and loading\nTowing", "Rules of\nthe road", "271~278", true));
            chapters.add(new LectureChapter(20, "2nd step 14,15", "Traffic Accidents\nMotor vehicle owners requirements and insurance system", "Rules of\nthe road", "279~294", true));
            chapters.add(new LectureChapter(21, "2nd step 1", "Anticipating danger : A discussion", "Rules of\nthe road", "149~160", true));
            chapters.add(new LectureChapter(22, "2nd step 16", "Route determination", "Rules of\nthe road", "295~302", true));
            chapters.add(new LectureChapter(23, "2nd step 17", "Expressway driving", "Rules of\nthe road", "303~324", true));
            chapters.add(new LectureChapter(24, "2nd step 2,3", "CPR", "Emergency\nFirst Aid", "", true));
        }
        
        adapter.setChapters(chapters);
        
        if (chapters.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }
} 