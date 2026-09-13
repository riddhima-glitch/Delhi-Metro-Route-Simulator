package metro;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MetroGraph {

    // Adjacency list: stationName -> outgoing track edges
    private final Map<String, List<MetroEdge>> adjacencyList;

    // Station lookup by lowercase name for case-insensitive matching
    private final Map<String, MetroStation> stationRegistry;

    // Line lookup preserving case-insensitive alphabetical ordering
    private final Map<String, MetroLine> lineRegistry;

    public MetroGraph() {
        this.adjacencyList = new HashMap<>();
        this.stationRegistry = new HashMap<>();
        this.lineRegistry = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    }

    public MetroStation addStation(String stationName, String lineName) {
        String key = stationName.trim().toLowerCase();
        MetroStation station = stationRegistry.get(key);
        if (station == null) {
            station = new MetroStation(stationName.trim());
            stationRegistry.put(key, station);
            adjacencyList.put(station.getName(), new ArrayList<>());
        }
        if (lineName != null) {
            station.addLine(lineName);
        }
        return station;
    }

    public MetroLine getOrCreateLine(String lineName) {
        String key = lineName.trim();
        MetroLine line = lineRegistry.get(key);
        if (line == null) {
            line = new MetroLine(key);
            lineRegistry.put(key, line);
        }
        return line;
    }

    public void addEdge(String fromStation, String toStation, String lineName, int travelTime) {
        MetroStation from = addStation(fromStation, lineName);
        MetroStation to = addStation(toStation, lineName);

        MetroEdge edge = new MetroEdge(from.getName(), to.getName(), lineName, travelTime);
        adjacencyList.get(from.getName()).add(edge);
    }

    public void addBidirectionalConnection(String station1, String station2, String lineName, int travelTime) {
        addEdge(station1, station2, lineName, travelTime);
        addEdge(station2, station1, lineName, travelTime);
    }

    public List<MetroEdge> getNeighbors(String stationName) {
        List<MetroEdge> edges = adjacencyList.get(stationName);
        return edges != null ? edges : Collections.emptyList();
    }

    public MetroStation findStation(String stationName) {
        if (stationName == null) {
            return null;
        }
        return stationRegistry.get(stationName.trim().toLowerCase());
    }

    /**
     * Searches for stations matching a user query:
     * - Returns an exact match if one exists
     * - Otherwise returns all stations containing the query substring
     */
    public List<MetroStation> searchStations(String query) {
        List<MetroStation> results = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) {
            return results;
        }

        String sanitized = query.trim().toLowerCase();
        MetroStation exact = findStation(sanitized);
        if (exact != null) {
            results.add(exact);
            return results;
        }

        for (MetroStation station : stationRegistry.values()) {
            if (station.getName().toLowerCase().contains(sanitized)) {
                results.add(station);
            }
        }
        return results;
    }

    public Map<String, MetroStation> getAllStations() {
        return Collections.unmodifiableMap(stationRegistry);
    }

    public Map<String, MetroLine> getAllLines() {
        return Collections.unmodifiableMap(lineRegistry);
    }

    public int getTotalStations() {
        return stationRegistry.size();
    }

    public int getTotalLines() {
        return lineRegistry.size();
    }
}
