package metro;

/**
 * MetroEdge
 * 
 * Represents a directional track connection between two consecutive stations
 * on a specific metro line, along with the estimated travel time in minutes.
 */
public class MetroEdge {
    private final String source;
    private final String destination;
    private final String lineName;
    private final int travelTimeMinutes;

    public MetroEdge(String source, String destination, String lineName, int travelTimeMinutes) {
        this.source = source;
        this.destination = destination;
        this.lineName = lineName;
        this.travelTimeMinutes = travelTimeMinutes;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public String getLineName() {
        return lineName;
    }

    public int getTravelTimeMinutes() {
        return travelTimeMinutes;
    }

    @Override
    public String toString() {
        return source + " -> " + destination + " [" + lineName + ", " + travelTimeMinutes + " min]";
    }
}
