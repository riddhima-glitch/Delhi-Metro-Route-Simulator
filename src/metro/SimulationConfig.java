package metro;

import java.time.LocalTime;

public class SimulationConfig {

    // Train frequency in minutes
    public static final int PEAK_FREQUENCY = 4;
    public static final int OFF_PEAK_FREQUENCY = 8;

    // Platform interchange transfer penalty (minutes)
    public static final int INTERCHANGE_TIME = 5;

    // Peak hour windows
    public static final LocalTime MORNING_PEAK_START = LocalTime.of(8, 0);
    public static final LocalTime MORNING_PEAK_END = LocalTime.of(10, 0);
    public static final LocalTime EVENING_PEAK_START = LocalTime.of(17, 0);
    public static final LocalTime EVENING_PEAK_END = LocalTime.of(19, 0);

    public static final String DEFAULT_DATA_PATH = "data/metro_data.csv";
}
