package metro;

import java.time.LocalTime;

/**
 * SimulationConfig
 * 
 * Central configuration class holding all simulation assumptions and constants.
 * Keeping these values in one place allows easy tuning without scattering
 * magic numbers throughout the codebase.
 */
public class SimulationConfig {

    // Train frequency in minutes during peak rush hours
    public static final int PEAK_FREQUENCY = 4;

    // Train frequency in minutes during regular off-peak hours
    public static final int OFF_PEAK_FREQUENCY = 8;

    // Estimated walking and transfer time between platforms at an interchange station
    public static final int INTERCHANGE_TIME = 5;

    // Morning peak hour window: 08:00 to 10:00
    public static final LocalTime MORNING_PEAK_START = LocalTime.of(8, 0);
    public static final LocalTime MORNING_PEAK_END = LocalTime.of(10, 0);

    // Evening peak hour window: 17:00 to 19:00
    public static final LocalTime EVENING_PEAK_START = LocalTime.of(17, 0);
    public static final LocalTime EVENING_PEAK_END = LocalTime.of(19, 0);

    // Operational hours of Delhi Metro simulation
    public static final LocalTime METRO_OPEN_TIME = LocalTime.of(5, 30);
    public static final LocalTime METRO_CLOSE_TIME = LocalTime.of(23, 30);

    // Path to the external CSV dataset
    public static final String DEFAULT_DATA_PATH = "data/metro_data.csv";
}
