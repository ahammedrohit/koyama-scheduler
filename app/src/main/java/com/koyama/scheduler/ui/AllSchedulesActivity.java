package com.koyama.scheduler.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.koyama.scheduler.R;
import com.koyama.scheduler.adapter.ScheduleAdapter;
import com.koyama.scheduler.data.model.Lesson;
import com.koyama.scheduler.util.AbbreviationHelper;
import com.koyama.scheduler.util.ScheduleGroup;
import com.koyama.scheduler.viewmodel.LessonViewModel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AllSchedulesActivity extends AppCompatActivity {

    private static final String EXTRA_FILTER_LESSON_TYPE = "FILTER_LESSON_TYPE";
    
    private LessonViewModel viewModel;
    private RecyclerView scheduleRecyclerView;
    private ScheduleAdapter adapter;
    private ProgressBar progressIndicator;
    private LinearLayout emptyStateView;
    private TextView emptyStateTitle;
    private TextView emptyStateDescription;
    private Spinner sortSpinner;
    private Spinner groupSpinner;
    private ChipGroup filterChipGroup;
    private Chip chipFilterAll;
    private Chip chipFilterUpcoming;
    private Chip chipFilterCompleted;
    private Chip chipFilterDate;

    // Filter states
    private boolean showCompleted = true;
    private boolean showUpcoming = true;
    private LocalDate startDate = null;
    private LocalDate endDate = null;
    private String lessonTypeFilter = null; // New field for lesson type filter

    // Sorting and grouping options
    private String[] sortOptions;
    private String[] groupOptions;
    private int currentSortOption = 0; // Default: Date (ascending)
    private int currentGroupOption = 0; // Default: Month

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_schedules);

        // Check for lesson type filter from intent
        if (getIntent() != null && getIntent().hasExtra(EXTRA_FILTER_LESSON_TYPE)) {
            lessonTypeFilter = getIntent().getStringExtra(EXTRA_FILTER_LESSON_TYPE);
        }

        // Set up toolbar with custom title if filtering by lesson type
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (lessonTypeFilter != null && !lessonTypeFilter.isEmpty()) {
            toolbar.setTitle(getString(R.string.all_schedules) + " - " + lessonTypeFilter);
        }
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Initialize view model
        viewModel = new ViewModelProvider(this).get(LessonViewModel.class);

        // Find views
        scheduleRecyclerView = findViewById(R.id.recycler_schedules);
        progressIndicator = findViewById(R.id.progress_indicator);
        emptyStateView = findViewById(R.id.empty_state);
        emptyStateTitle = findViewById(R.id.text_empty_title);
        emptyStateDescription = findViewById(R.id.text_empty_description);
        sortSpinner = findViewById(R.id.spinner_sort);
        groupSpinner = findViewById(R.id.spinner_group);
        filterChipGroup = findViewById(R.id.filter_chip_group);
        chipFilterAll = findViewById(R.id.chip_filter_all);
        chipFilterUpcoming = findViewById(R.id.chip_filter_upcoming);
        chipFilterCompleted = findViewById(R.id.chip_filter_completed);
        chipFilterDate = findViewById(R.id.chip_filter_date);

        // Setup sorting options
        sortOptions = new String[]{
                getString(R.string.sort_date_asc),
                getString(R.string.sort_date_desc),
                getString(R.string.sort_lesson_type),
                getString(R.string.sort_lesson_number)
        };
        
        // Setup grouping options
        groupOptions = new String[]{
                getString(R.string.group_month),
                getString(R.string.group_week),
                getString(R.string.group_day),
                getString(R.string.group_type),
                getString(R.string.group_status)
        };

        // Setup spinners
        setupSpinners();
        
        // Setup RecyclerView
        setupRecyclerView();
        
        // Setup chip filters
        setupChipFilters();
        
        // Load lessons
        loadLessons();
    }

    private void setupSpinners() {
        // Sort spinner
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(this, 
                android.R.layout.simple_spinner_item, sortOptions);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(sortAdapter);
        sortSpinner.setSelection(currentSortOption);
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (currentSortOption != position) {
                    currentSortOption = position;
                    applyFiltersAndSort();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Group spinner
        ArrayAdapter<String> groupAdapter = new ArrayAdapter<>(this, 
                android.R.layout.simple_spinner_item, groupOptions);
        groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        groupSpinner.setAdapter(groupAdapter);
        groupSpinner.setSelection(currentGroupOption);
        groupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (currentGroupOption != position) {
                    currentGroupOption = position;
                    applyFiltersAndSort();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void setupRecyclerView() {
        // Create adapter
        adapter = new ScheduleAdapter(new ArrayList<>(), lesson -> {
            // Open lesson detail on click
            openLessonDetail(lesson);
        });
        
        // Set up RecyclerView
        scheduleRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        scheduleRecyclerView.setAdapter(adapter);
    }

    private void setupChipFilters() {
        // Set up filter chips
        chipFilterAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Select all
                showCompleted = true;
                showUpcoming = true;
                chipFilterCompleted.setChecked(true);
                chipFilterUpcoming.setChecked(true);
            } else {
                // If unchecking "all", make sure at least one other filter is checked
                if (!chipFilterCompleted.isChecked() && !chipFilterUpcoming.isChecked()) {
                    chipFilterUpcoming.setChecked(true);
                    showUpcoming = true;
                }
            }
            applyFiltersAndSort();
        });

        chipFilterCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            showCompleted = isChecked;
            updateAllChipState();
            applyFiltersAndSort();
        });

        chipFilterUpcoming.setOnCheckedChangeListener((buttonView, isChecked) -> {
            showUpcoming = isChecked;
            updateAllChipState();
            applyFiltersAndSort();
        });

        chipFilterDate.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                showDateRangePicker();
            } else {
                // Clear date filter
                startDate = null;
                endDate = null;
                applyFiltersAndSort();
            }
        });
    }

    private void updateAllChipState() {
        // Update the "All" chip state based on other chips
        boolean shouldBeChecked = showCompleted && showUpcoming;
        if (chipFilterAll.isChecked() != shouldBeChecked) {
            chipFilterAll.setChecked(shouldBeChecked);
        }
    }

    private void showDateRangePicker() {
        // Show date range picker to select start and end dates
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Start date picker
        DatePickerDialog startDateDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            // Set start date
            startDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay);
            
            // Show end date picker
            DatePickerDialog endDateDialog = new DatePickerDialog(this, (endView, endYear, endMonth, endDay) -> {
                // Set end date
                endDate = LocalDate.of(endYear, endMonth + 1, endDay);
                
                // Update chip text with date range
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd", Locale.getDefault());
                chipFilterDate.setText(String.format("%s - %s", 
                        startDate.format(formatter), 
                        endDate.format(formatter)));
                
                // Apply filters
                applyFiltersAndSort();
            }, year, month, day);
            
            // Set min date to start date
            Calendar minDate = Calendar.getInstance();
            minDate.set(selectedYear, selectedMonth, selectedDay);
            endDateDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
            
            endDateDialog.show();
        }, year, month, day);
        
        startDateDialog.show();
    }

    private void loadLessons() {
        // Show loading indicator
        progressIndicator.setVisibility(View.VISIBLE);
        emptyStateView.setVisibility(View.GONE);
        
        // Get all lessons from view model
        viewModel.getAllLessons().observe(this, lessons -> {
            progressIndicator.setVisibility(View.GONE);
            
            if (lessons != null && !lessons.isEmpty()) {
                // Apply filters and sorting
                applyFiltersAndSort();
            } else {
                // Show empty state
                showEmptyState(getString(R.string.no_schedules_found), 
                        getString(R.string.no_schedules_description));
            }
        });
    }

    private void applyFiltersAndSort() {
        List<Lesson> lessons = viewModel.getAllLessons().getValue();
        if (lessons == null || lessons.isEmpty()) {
            showEmptyState(getString(R.string.no_schedules_found), 
                    getString(R.string.no_schedules_description));
            return;
        }
        
        // Apply filters
        List<Lesson> filteredLessons = new ArrayList<>();
        LocalDate today = LocalDate.now();
        
        for (Lesson lesson : lessons) {
            boolean shouldInclude = false;
            
            // Apply lesson type filter first if present
            if (lessonTypeFilter != null && !lessonTypeFilter.isEmpty()) {
                if (lesson.getEventType() == null) {
                    continue; // Skip lessons with no event type
                }
                
                // Standardize both the filter and the lesson event type for comparison
                String standardizedFilter = AbbreviationHelper.standardizeAbbreviation(lessonTypeFilter);
                String standardizedEventType = AbbreviationHelper.standardizeAbbreviation(lesson.getEventType());
                
                if (!standardizedEventType.equals(standardizedFilter)) {
                    continue; // Skip lessons that don't match the type filter
                }
            }
            
            try {
                LocalDate lessonDate = LocalDate.parse(lesson.getDate());
                
                // Apply date range filter if active
                boolean isInDateRange = true;
                if (startDate != null && endDate != null) {
                    isInDateRange = !lessonDate.isBefore(startDate) && !lessonDate.isAfter(endDate);
                }
                
                // Apply completion status filters
                if (isInDateRange) {
                    if (lesson.isCompleted() && showCompleted) {
                        shouldInclude = true;
                    } else if (!lesson.isCompleted()) {
                        if (lessonDate.isBefore(today)) {
                            // Past uncompleted lessons are now considered completed
                            if (showCompleted) {
                                shouldInclude = true;
                            }
                        } else if (showUpcoming) {
                            shouldInclude = true;
                        }
                    }
                }
            } catch (Exception e) {
                // If unable to parse date, include based on completion status
                if ((lesson.isCompleted() && showCompleted) || 
                        (!lesson.isCompleted() && showUpcoming)) {
                    shouldInclude = true;
                }
            }
            
            if (shouldInclude) {
                filteredLessons.add(lesson);
            }
        }
        
        // Apply sorting
        sortLessons(filteredLessons);
        
        // Apply grouping
        groupLessons(filteredLessons);
        
        // Update UI
        if (filteredLessons.isEmpty()) {
            // Show custom empty state for lesson type filter
            if (lessonTypeFilter != null && !lessonTypeFilter.isEmpty()) {
                showEmptyState("No " + lessonTypeFilter + " lessons found", 
                        "There are no " + lessonTypeFilter + " lessons in your schedule");
            } else {
                showEmptyState(getString(R.string.no_matching_schedules), 
                        getString(R.string.try_different_filter));
            }
        } else {
            emptyStateView.setVisibility(View.GONE);
            scheduleRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void sortLessons(List<Lesson> lessons) {
        // Sort based on selected option
        switch (currentSortOption) {
            case 0: // Date ascending
                lessons.sort((l1, l2) -> {
                    int dateCompare = l1.getDate().compareTo(l2.getDate());
                    if (dateCompare == 0) {
                        return l1.getStartTime().compareTo(l2.getStartTime());
                    }
                    return dateCompare;
                });
                break;
            case 1: // Date descending
                lessons.sort((l1, l2) -> {
                    int dateCompare = l2.getDate().compareTo(l1.getDate());
                    if (dateCompare == 0) {
                        return l2.getStartTime().compareTo(l1.getStartTime());
                    }
                    return dateCompare;
                });
                break;
            case 2: // Lesson type
                lessons.sort((l1, l2) -> {
                    String type1 = l1.getEventType() != null ? l1.getEventType() : "";
                    String type2 = l2.getEventType() != null ? l2.getEventType() : "";
                    int typeCompare = type1.compareTo(type2);
                    if (typeCompare == 0) {
                        return l1.getDate().compareTo(l2.getDate());
                    }
                    return typeCompare;
                });
                break;
            case 3: // Lesson number
                lessons.sort((l1, l2) -> {
                    String num1 = l1.getEventNumber() != null ? l1.getEventNumber() : "";
                    String num2 = l2.getEventNumber() != null ? l2.getEventNumber() : "";
                    int numCompare = num1.compareTo(num2);
                    if (numCompare == 0) {
                        return l1.getDate().compareTo(l2.getDate());
                    }
                    return numCompare;
                });
                break;
        }
    }

    private void groupLessons(List<Lesson> lessons) {
        // Group based on selected option
        ScheduleGroup groupType;
        switch (currentGroupOption) {
            case 0: // Month
                groupType = ScheduleGroup.MONTH;
                break;
            case 1: // Week
                groupType = ScheduleGroup.WEEK;
                break;
            case 2: // Day
                groupType = ScheduleGroup.DAY;
                break;
            case 3: // Type
                groupType = ScheduleGroup.TYPE;
                break;
            case 4: // Status
                groupType = ScheduleGroup.STATUS;
                break;
            default:
                groupType = ScheduleGroup.MONTH;
                break;
        }
        
        // Update adapter with grouped lessons
        adapter.updateData(lessons, groupType);
    }

    private void showEmptyState(String title, String description) {
        emptyStateTitle.setText(title);
        emptyStateDescription.setText(description);
        emptyStateView.setVisibility(View.VISIBLE);
        scheduleRecyclerView.setVisibility(View.GONE);
    }

    private void openLessonDetail(Lesson lesson) {
        // Start lesson detail activity
        if (lesson != null) {
            LessonDetailActivity.start(this, lesson.getId());
        }
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