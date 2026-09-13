package metro;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MetroLine {
    private final String name;
    private final List<String> stations;

    public MetroLine(String name) {
        this.name = name;
        this.stations = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void addStation(String stationName) {
        if (!stations.contains(stationName)) {
            stations.add(stationName);
        }
    }

    public List<String> getStations() {
        return Collections.unmodifiableList(stations);
    }

    public int getStationCount() {
        return stations.size();
    }

    public boolean containsStation(String stationName) {
        for (String station : stations) {
            if (station.equalsIgnoreCase(stationName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return name + " (" + stations.size() + " stations)";
    }
}
