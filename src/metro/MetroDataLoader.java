package metro;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MetroDataLoader
 * 
 * Responsible for loading metro stations, lines, and connection timings from
 * an external CSV file into the MetroGraph.
 */
public class MetroDataLoader {

    // Helper record to temporarily hold CSV row data during sequential parsing
    private static class CsvRow {
        String lineName;
        String stationName;
        int stationOrder;
        int travelTimeToNext;

        CsvRow(String lineName, String stationName, int stationOrder, int travelTimeToNext) {
            this.lineName = lineName;
            this.stationName = stationName;
            this.stationOrder = stationOrder;
            this.travelTimeToNext = travelTimeToNext;
        }
    }

    /**
     * Loads the metro network graph from the specified CSV file path.
     * 
     * @param filePath Path to the CSV file
     * @return MetroGraph containing all parsed lines, stations, and edges
     * @throws IOException If file is missing or cannot be read
     */
    public static MetroGraph loadMetroData(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("Metro data file not found at: " + file.getAbsolutePath());
        }

        MetroGraph graph = new MetroGraph();
        Map<String, List<CsvRow>> lineRowsMap = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;

            while ((line = br.readLine()) != null) {
                lineNumber++;
                String trimmed = line.trim();

                // Skip blank lines or comments
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }

                // Skip header line
                if (lineNumber == 1 && trimmed.toLowerCase().startsWith("line_name")) {
                    continue;
                }

                String[] parts = trimmed.split(",");
                if (parts.length < 4) {
                    System.err.println("Warning: Skipping malformed line " + lineNumber + ": " + line);
                    continue;
                }

                String lineName = parts[0].trim();
                String stationName = parts[1].trim();

                int order;
                int travelTime;

                try {
                    order = Integer.parseInt(parts[2].trim());
                    travelTime = Integer.parseInt(parts[3].trim());
                } catch (NumberFormatException e) {
                    System.err.println("Warning: Invalid number on line " + lineNumber + ": " + line);
                    continue;
                }

                if (stationName.isEmpty() || lineName.isEmpty()) {
                    continue;
                }

                lineRowsMap.computeIfAbsent(lineName, k -> new ArrayList<>())
                           .add(new CsvRow(lineName, stationName, order, travelTime));
            }
        }

        if (lineRowsMap.isEmpty()) {
            throw new IOException("No valid metro data found in file: " + filePath);
        }

        // Process each line's sequential stations to create ordered lines and bidirectional graph edges
        for (Map.Entry<String, List<CsvRow>> entry : lineRowsMap.entrySet()) {
            String lineName = entry.getKey();
            List<CsvRow> rows = entry.getValue();

            // Sort rows by station_order
            rows.sort((r1, r2) -> Integer.compare(r1.stationOrder, r2.stationOrder));

            MetroLine metroLine = graph.getOrCreateLine(lineName);

            for (int i = 0; i < rows.size(); i++) {
                CsvRow current = rows.get(i);
                metroLine.addStation(current.stationName);
                graph.addStation(current.stationName, lineName);

                // Add connection to next station if exists
                if (i < rows.size() - 1) {
                    CsvRow next = rows.get(i + 1);
                    int travelTime = current.travelTimeToNext > 0 ? current.travelTimeToNext : 2;
                    graph.addBidirectionalConnection(current.stationName, next.stationName, lineName, travelTime);
                }
            }
        }

        return graph;
    }
}
