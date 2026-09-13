package metro;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * MetroStation
 * 
 * Represents a metro station in the network.
 * A single station can be served by multiple metro lines (making it an interchange).
 */
public class MetroStation {
    private final String name;
    private final Set<String> lines;

    public MetroStation(String name) {
        this.name = name;
        this.lines = new HashSet<>();
    }

    public String getName() {
        return name;
    }

    public void addLine(String lineName) {
        if (lineName != null && !lineName.trim().isEmpty()) {
            this.lines.add(lineName.trim());
        }
    }

    public Set<String> getLines() {
        return Collections.unmodifiableSet(lines);
    }

    /**
     * If a station connects 2 or more lines, it is an interchange station.
     */
    public boolean isInterchange() {
        return lines.size() > 1;
    }

    @Override
    public String toString() {
        return name + " " + lines;
    }
}
