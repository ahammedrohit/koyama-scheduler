package com.koyama.scheduler.util;

import android.content.Context;

import com.koyama.scheduler.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper class for handling lesson type abbreviations consistently throughout the app
 */
public class AbbreviationHelper {

    /**
     * Standardizes the abbreviation format, handling all possible variants
     * @param abbreviation The original abbreviation
     * @return The standardized abbreviation
     */
    public static String standardizeAbbreviation(String abbreviation) {
        if (abbreviation == null) return null;
        
        // First check for exact matches of known variants
        if ("Apti.t".equals(abbreviation)) {
            return "APTIT";
        }
        
        // Handle other known variations by uppercase
        switch (abbreviation.toUpperCase()) {
            case "APTI.T":
            case "APTI.T.":
            case "APTI T":
            case "APTIT.":
                return "APTIT";
            case "EX&RD":
            case "EXRD":
            case "EX RD":
            case "EX-RD":
                return "EX&RD";
            default:
                // If the abbreviation matches a standard one except for case, use the standard case
                for (String std : getStandardAbbreviations()) {
                    if (std.equalsIgnoreCase(abbreviation)) {
                        return std;
                    }
                }
                return abbreviation;
        }
    }
    
    /**
     * Gets all the standard abbreviations used in the app
     * @return An array of standard abbreviations
     */
    public static String[] getStandardAbbreviations() {
        return new String[] {
            "AT",    // Automatic Transmission (1st step)
            "A50",   // Advanced 50 (AT 2nd step)
            "ATP",   // Automatic Transmission Parking
            "PT",    // Pre-test
            "CPR",   // Cardio Pulmonary Resuscitation
            "APTIT", // Aptitude Test
            "CDD",   // Combination of Driving Lesson and Discussion
            "EX&RD", // Expressway and Route Determination
            "APS",   // Special Item Lesson
            "EXT"    // Extra Lesson
        };
    }
    
    /**
     * Gets a map of all abbreviations and their full display names
     * @param context Context for string resources
     * @return Map of abbreviations to display names
     */
    public static Map<String, String> getAbbreviationDisplayNames(Context context) {
        Map<String, String> displayNames = new HashMap<>();
        displayNames.put("AT", context.getString(R.string.lesson_at));
        displayNames.put("A50", context.getString(R.string.lesson_a50));
        displayNames.put("ATP", context.getString(R.string.lesson_atp));
        displayNames.put("PT", context.getString(R.string.lesson_pt));
        displayNames.put("CPR", context.getString(R.string.lesson_cpr));
        displayNames.put("APTIT", context.getString(R.string.lesson_aptit));
        displayNames.put("CDD", context.getString(R.string.lesson_cdd));
        displayNames.put("EXT", context.getString(R.string.lesson_ext));
        displayNames.put("EX&RD", context.getString(R.string.lesson_exrd));
        displayNames.put("APS", context.getString(R.string.lesson_aps));
        return displayNames;
    }
    
    /**
     * Gets a map of all abbreviations and their full descriptions
     * @param context Context for string resources
     * @return Map of abbreviations to full descriptions
     */
    public static Map<String, String> getAbbreviationFullDescriptions(Context context) {
        Map<String, String> fullDescriptions = new HashMap<>();
        fullDescriptions.put("AT", context.getString(R.string.lesson_at_full));
        fullDescriptions.put("A50", context.getString(R.string.lesson_a50_full));
        fullDescriptions.put("ATP", context.getString(R.string.lesson_atp_full));
        fullDescriptions.put("PT", context.getString(R.string.lesson_pt_full));
        fullDescriptions.put("CPR", context.getString(R.string.lesson_cpr_full));
        fullDescriptions.put("APTIT", context.getString(R.string.lesson_aptit_full));
        fullDescriptions.put("CDD", context.getString(R.string.lesson_cdd_full));
        fullDescriptions.put("EXT", context.getString(R.string.lesson_ext_full));
        fullDescriptions.put("EX&RD", context.getString(R.string.lesson_exrd_full));
        fullDescriptions.put("APS", context.getString(R.string.lesson_aps_full));
        return fullDescriptions;
    }
} 