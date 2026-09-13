package metro;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ScheduleCalculator {

    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

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

    public static String formatTime(LocalTime time) {
        if (time == null) {
            return "--:--";
        }
        return time.format(TIME_FORMATTER);
    }

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

    public static int getFrequency(LocalTime time) {
        return isPeakHour(time) ? SimulationConfig.PEAK_FREQUENCY : SimulationConfig.OFF_PEAK_FREQUENCY;
    }

    /**
     * Calculates the expected waiting time in minutes for the next train
     * based on headway intervals.
     */
    public static int calculateWaitingTime(LocalTime currentTime) {
        if (currentTime == null) {
            return 0;
        }
        int freq = getFrequency(currentTime);
        int minute = currentTime.getMinute();
        int remainder = minute % freq;

        if (remainder == 0) {
            return 0;
        }
        return freq - remainder;
    }

    public static LocalTime calculateDepartureTime(LocalTime currentTime, int waitingMinutes) {
        if (currentTime == null) {
            return LocalTime.now();
        }
        return currentTime.plusMinutes(waitingMinutes);
    }

    public static LocalTime calculateArrivalTime(LocalTime departureTime, int totalDurationMinutes) {
        if (departureTime == null) {
            return LocalTime.now();
        }
        return departureTime.plusMinutes(totalDurationMinutes);
    }
}
