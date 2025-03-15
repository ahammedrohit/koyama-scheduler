package com.koyama.scheduler.data.db;

import androidx.room.TypeConverter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Type converters for time values
 */
public class TimeConverters {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mm a", Locale.US);

    /**
     * Convert time string in "h:mm a" format (like "7:40 PM") to minutes since midnight
     * for easier comparison
     */
    @TypeConverter
    public static Integer timeStringToMinutes(String timeString) {
        if (timeString == null) {
            return null;
        }
        
        try {
            LocalTime time = LocalTime.parse(timeString, TIME_FORMATTER);
            return time.getHour() * 60 + time.getMinute();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Convert minutes since midnight back to time string
     */
    @TypeConverter
    public static String minutesToTimeString(Integer minutes) {
        if (minutes == null) {
            return null;
        }
        
        int hours = minutes / 60;
        int mins = minutes % 60;
        LocalTime time = LocalTime.of(hours, mins);
        return time.format(TIME_FORMATTER);
    }
}