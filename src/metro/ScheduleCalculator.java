package metro;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * ScheduleCalculator
 * 
 * Handles time-based simulation logic including:
 * - Peak vs off-peak hour detection
 * - Train frequency determination
 * - Waiting time calculation for departing trains
 * - Arrival time calculation
 */
public class ScheduleCalculator {

    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Parses a string in HH:mm format into a LocalTime.
     * Returns null if parsing fails.
     */
    public static LocalTime parseTime(String timeString) {
        if (timeString == null || timeString.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalTime.parse(timeString.trim(), TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Formats a LocalTime object into HH:mm format string.
     */
    public static String formatTime(LocalTime time) {
        if (time == null) {
            return "--:--";
        }
        return time.format(TIME_FORMATTER);
    }

    /**
     * Checks if the given time falls within morning or evening peak hours.
     * Peak hours: 08:00 - 10:00 and 17:00 - 19:00
     */
    public static boolean isPeakHour(LocalTime time) {
        if (time == null) {
            return false;
        }
        boolean morningPeak = (!time.isBefore(SimulationConfig.MORNING_PEAK_START)) &&
                              (time.isBefore(SimulationConfig.MORNING_PEAK_END));
        boolean eveningPeak = (!time.isBefore(SimulationConfig.EVENING_PEAK_START)) &&
                              (time.isBefore(SimulationConfig.EVENING_PEAK_END));
        return morningPeak || eveningPeak;
    }

    /**
     * Returns the train frequency (headway) in minutes for a given time.
     */
    public static int getFrequency(LocalTime time) {
        return isPeakHour(time) ? SimulationConfig.PEAK_FREQUENCY : SimulationConfig.OFF_PEAK_FREQUENCY;
    }

    /**
     * Calculates the expected waiting time (in minutes) for the next train based on
     * the current minute and the line frequency.
     * 
     * Formula:
     * In this simulation, trains depart from terminals at regular intervals of 'frequency' minutes
     * (e.g. every 4 minutes: :00, :04, :08, :12... or every 8 minutes: :00, :08, :16...).
     * The wait time is the minutes until the next train slot. If user arrives exactly on slot,
     * wait is 0 (or immediate boarding).
     */
    public static int calculateWaitingTime(LocalTime currentTime) {
        if (currentTime == null) {
            return 0;
        }
        int freq = getFrequency(currentTime);
        int minute = currentTime.getMinute();
        int remainder = minute % freq;

        // If remainder is 0, train is arriving at current minute (0 min wait)
        // Otherwise, wait is (freq - remainder)
        if (remainder == 0) {
            return 0;
        }
        return freq - remainder;
    }

    /**
     * Calculates the exact departure time after accounting for initial platform waiting time.
     */
    public static LocalTime calculateDepartureTime(LocalTime currentTime, int waitingMinutes) {
        if (currentTime == null) {
            return LocalTime.now();
        }
        return currentTime.plusMinutes(waitingMinutes);
    }

    /**
     * Calculates the final arrival time given departure time and total duration.
     */
    public static LocalTime calculateArrivalTime(LocalTime departureTime, int totalDurationMinutes) {
        if (departureTime == null) {
            return LocalTime.now();
        }
        return departureTime.plusMinutes(totalDurationMinutes);
    }
}
