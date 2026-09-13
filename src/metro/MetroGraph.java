package metro;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * MetroGraph
 * 
 * Graph representation of the Delhi Metro network.
 * Nodes represent Metro stations, and edges represent rail connections between them.
 */
public class MetroGraph {

    // Adjacency list: stationName -> list of outgoing edges
    private final Map<String, List<MetroEdge>> adjacencyList;

    // Station registry keyed by lowercase name for fast, case-insensitive lookups
    private final Map<String, MetroStation> stationRegistry;

    // Line registry keyed by lowercase name
    private final Map<String, MetroLine> lineRegistry;

    public MetroGraph() {
        this.adjacencyList = new HashMap<>();
        this.stationRegistry = new HashMap<>();
        this.lineRegistry = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    }

    /**
     * Registers a station in the graph if it doesn't already exist.
     */
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

    /**
     * Registers a metro line.
     */
    public MetroLine getOrCreateLine(String lineName) {
        String key = lineName.trim();
        MetroLine line = lineRegistry.get(key);
        if (line == null) {
            line = new MetroLine(key);
            lineRegistry.put(key, line);
        }
        return line;
    }

    /**
     * Adds a directed edge between two stations.
     */
    public void addEdge(String fromStation, String toStation, String lineName, int travelTime) {
        MetroStation from = addStation(fromStation, lineName);
        MetroStation to = addStation(toStation, lineName);

        MetroEdge edge = new MetroEdge(from.getName(), to.getName(), lineName, travelTime);
        adjacencyList.get(from.getName()).add(edge);
    }

    /**
     * Adds a bidirectional edge between two consecutive stations on a line.
     */
    public void addBidirectionalConnection(String station1, String station2, String lineName, int travelTime) {
        addEdge(station1, station2, lineName, travelTime);
        addEdge(station2, station1, lineName, travelTime);
    }

    /**
     * Retrieves neighbors (outgoing edges) of a station.
     */
    public List<MetroEdge> getNeighbors(String stationName) {
        List<MetroEdge> edges = adjacencyList.get(stationName);
        if (edges == null) {
            return Collections.emptyList();
        }
        return edges;
    }

    /**
     * Finds a station by name (case-insensitive). Returns null if not found.
     */
    public MetroStation findStation(String stationName) {
        if (stationName == null) {
            return null;
        }
        return stationRegistry.get(stationName.trim().toLowerCase());
    }

    /**
     * Returns true if a station exists.
     */
    public boolean containsStation(String stationName) {
        return findStation(stationName) != null;
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
