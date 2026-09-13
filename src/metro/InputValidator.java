package metro;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * InputValidator
 * 
 * Helper class for validating and sanitizing user inputs, including station
 * names and time strings. Provides suggestions for partial station name matches.
 */
public class InputValidator {

    /**
     * Finds matching stations for a user query.
     * 1. Exact match (case-insensitive) -> returns single station
     * 2. Substring match -> returns list of matching stations
     */
    public static List<MetroStation> searchStations(MetroGraph graph, String query) {
        List<MetroStation> results = new ArrayList<>();
        if (query == null || query.trim().isEmpty() || graph == null) {
            return results;
        }

        String sanitizedQuery = query.trim().toLowerCase();

        // Check exact match first
        MetroStation exact = graph.findStation(sanitizedQuery);
        if (exact != null) {
            results.add(exact);
            return results;
        }

        // Substring search across all stations
        for (MetroStation station : graph.getAllStations().values()) {
            if (station.getName().toLowerCase().contains(sanitizedQuery)) {
                results.add(station);
            }
        }

        return results;
    }

    /**
     * Validates and parses time in HH:mm format.
     */
    public static LocalTime validateTime(String timeStr) {
        if (timeStr == null || timeStr.trim().isEmpty()) {
            return null;
        }
        return ScheduleCalculator.parseTime(timeStr.trim());
    }

    /**
     * Validates whether a menu choice is within valid range [min, max].
     */
    public static int parseMenuChoice(String input, int min, int max) {
        if (input == null || input.trim().isEmpty()) {
            return -1;
        }
        try {
            int choice = Integer.parseInt(input.trim());
            if (choice >= min && choice <= max) {
                return choice;
            }
        } catch (NumberFormatException ignored) {
        }
        return -1;
    }
}
