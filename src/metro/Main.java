package metro;

import java.io.File;
import java.io.IOException;

/**
 * Main
 * 
 * Entry point for the Delhi Metro Route & Schedule Simulator.
 */
public class Main {

    public static void main(String[] args) {
        String dataFilePath = SimulationConfig.DEFAULT_DATA_PATH;

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("--help") || args[0].equalsIgnoreCase("-h")) {
                System.out.println("Delhi Metro Route & Schedule Simulator");
                System.out.println("Usage: java -cp out metro.Main [optional_custom_data_path.csv]");
                return;
            }
            dataFilePath = args[0];
        }

        File dataFile = new File(dataFilePath);
        if (!dataFile.exists()) {
            // Check parent directory fallback if executed from a subfolder
            File fallback = new File("../" + dataFilePath);
            if (fallback.exists()) {
                dataFilePath = fallback.getPath();
            }
        }

        try {
            System.out.println("Initializing Delhi Metro network from: " + dataFilePath + " ...");
            MetroGraph graph = MetroDataLoader.loadMetroData(dataFilePath);

            MetroApplication app = new MetroApplication(graph);
            app.start();

        } catch (IOException e) {
            System.err.println("\n[ERROR] Metro data file could not be loaded.");
            System.err.println("Details: " + e.getMessage());
            System.err.println("Please ensure '" + dataFilePath + "' exists and is formatted properly.");
            System.err.println("Exiting application.\n");
            System.exit(1);
        }
    }
}
