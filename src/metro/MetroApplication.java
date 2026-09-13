package metro;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class MetroApplication {

    private final MetroGraph graph;
    private final MetroRouteFinder routeFinder;
    private final Scanner scanner;

    public MetroApplication(MetroGraph graph) {
        this.graph = graph;
        this.routeFinder = new MetroRouteFinder(graph);
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        boolean running = true;
        System.out.println("==================================================");
        System.out.println("       DELHI METRO ROUTE & SCHEDULE SIMULATOR     ");
        System.out.println("==================================================");
        System.out.println("Loaded " + graph.getTotalLines() + " metro lines and " + graph.getTotalStations() + " stations.\n");

        while (running) {
            printMainMenu();
            System.out.print("Enter your choice (1-6): ");
            String input = scanner.nextLine();
            int choice = parseMenuChoice(input, 1, 6);

            switch (choice) {
                case 1:
                    handleFindRoute();
                    break;
                case 2:
                    handleViewLines();
                    break;
                case 3:
                    handleViewStationsOnLine();
                    break;
                case 4:
                    handleSearchStation();
                    break;
                case 5:
                    handleProjectInfo();
                    break;
                case 6:
                    System.out.println("\nThank you for using Delhi Metro Route & Schedule Simulator. Safe travels!");
                    running = false;
                    break;
                default:
                    System.out.println("\n[!] Invalid selection. Please enter a number between 1 and 6.\n");
            }
        }
    }

    private void printMainMenu() {
        System.out.println("--------------------------------------------------");
        System.out.println("                    MAIN MENU                     ");
        System.out.println("--------------------------------------------------");
        System.out.println("1. Find Route & Journey Schedule");
        System.out.println("2. View All Metro Lines");
        System.out.println("3. View Stations on a Selected Line");
        System.out.println("4. Search for a Station");
        System.out.println("5. Project Information & Simulation Assumptions");
        System.out.println("6. Exit");
        System.out.println("--------------------------------------------------");
    }

    private void handleFindRoute() {
        System.out.println("\n==================================================");
        System.out.println("               FIND METRO ROUTE                   ");
        System.out.println("==================================================");

        MetroStation source = promptForStation("Enter source station: ");
        if (source == null) return;

        MetroStation destination = promptForStation("Enter destination station: ");
        if (destination == null) return;

        LocalTime searchTime = promptForTime();
        if (searchTime == null) return;

        System.out.println("\nCalculating optimal route via Dijkstra's algorithm...\n");

        RouteResult result = routeFinder.findRoute(source.getName(), destination.getName(), searchTime);
        displayRouteResult(result);
    }

    public void displayRouteResult(RouteResult result) {
        if (!result.isRouteFound()) {
            System.out.println("--------------------------------------------------");
            System.out.println("[!] No connected metro route found between:");
            System.out.println("    Source: " + result.getSourceStation());
            System.out.println("    Destination: " + result.getDestinationStation());
            System.out.println("--------------------------------------------------\n");
            return;
        }

        boolean isPeak = ScheduleCalculator.isPeakHour(result.getSearchTime());
        int frequency = ScheduleCalculator.getFrequency(result.getSearchTime());

        System.out.println("--------------------------------------------------");
        System.out.println("                   ROUTE FOUND                    ");
        System.out.println("--------------------------------------------------");
        System.out.println("From: " + result.getSourceStation());
        System.out.println("To:   " + result.getDestinationStation());
        System.out.println("Query Time:       " + ScheduleCalculator.formatTime(result.getSearchTime()) +
                (isPeak ? " (Peak Hours: " + frequency + " min headway)" : " (Off-Peak: " + frequency + " min headway)"));
        System.out.println("Next Departure:   " + ScheduleCalculator.formatTime(result.getDepartureTime()));
        System.out.println("Expected Arrival: " + ScheduleCalculator.formatTime(result.getArrivalTime()));
        System.out.println("--------------------------------------------------");
        System.out.println("JOURNEY ITINERARY (" + result.getStationCount() + " stations, " + result.getInterchangeCount() + " interchange" + (result.getInterchangeCount() == 1 ? "" : "s") + "):");
        System.out.println("--------------------------------------------------");

        List<RouteStep> steps = result.getSteps();
        for (int i = 0; i < steps.size(); i++) {
            RouteStep step = steps.get(i);

            if (i == 0) {
                System.out.printf("%2d. %s\n", step.getStepNumber(), step.getStationName());
                System.out.println("    [Board " + step.getLineName() + "]");
            } else if (step.isInterchange()) {
                System.out.printf("%2d. %s\n", step.getStepNumber(), step.getStationName());
                System.out.println("    ***********************************************");
                System.out.println("    >>> INTERCHANGE STATION <<<");
                System.out.println("    Change from " + step.getInterchangeFromLine() + " -> " + step.getInterchangeToLine());
                System.out.println("    (Estimated walking/transfer time: " + SimulationConfig.INTERCHANGE_TIME + " mins)");
                System.out.println("    ***********************************************");
            } else if (i == steps.size() - 1) {
                System.out.printf("%2d. %s\n", step.getStepNumber(), step.getStationName());
                System.out.println("    [Destination Reached]");
            } else {
                System.out.printf("%2d. %s\n", step.getStepNumber(), step.getStationName());
            }
        }

        System.out.println("--------------------------------------------------");
        System.out.println("JOURNEY SUMMARY:");
        System.out.println("--------------------------------------------------");
        System.out.println("Lines Used:              " + String.join(", ", result.getLinesUsed()));
        if (!result.getInterchanges().isEmpty()) {
            System.out.println("Interchange Stations:    " + String.join(", ", result.getInterchanges()));
        } else {
            System.out.println("Interchanges:            Direct route (0 interchanges)");
        }
        System.out.println("In-Train Travel Time:    " + result.getTravelTimeMinutes() + " minutes");
        System.out.println("Interchange Transfer:    " + result.getInterchangeTimeMinutes() + " minutes (" + result.getInterchangeCount() + " x " + SimulationConfig.INTERCHANGE_TIME + " min)");
        System.out.println("Expected Waiting Time:   " + result.getInitialWaitingTimeMinutes() + " minutes");
        System.out.println("Total Estimated Journey: " + result.getTotalEstimatedTimeMinutes() + " minutes");
        System.out.println("Departure Time:          " + ScheduleCalculator.formatTime(result.getDepartureTime()));
        System.out.println("Arrival Time:            " + ScheduleCalculator.formatTime(result.getArrivalTime()));
        System.out.println("--------------------------------------------------\n");
    }

    private MetroStation promptForStation(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("cancel") || input.equalsIgnoreCase("back")) {
                System.out.println("[Action cancelled]\n");
                return null;
            }

            if (input.isEmpty()) {
                System.out.println("[!] Station name cannot be blank. Type 'cancel' to return to menu.");
                continue;
            }

            List<MetroStation> matches = graph.searchStations(input);

            if (matches.isEmpty()) {
                System.out.println("[!] Station '" + input + "' not found in the Delhi Metro network.");
                System.out.println("    Please check spelling or type 'cancel' to return.\n");
                continue;
            }

            // Exact match found
            if (matches.size() == 1 && matches.get(0).getName().equalsIgnoreCase(input)) {
                return matches.get(0);
            }

            // Single partial match
            if (matches.size() == 1) {
                MetroStation match = matches.get(0);
                System.out.println("    Selected: " + match.getName() + " (" + String.join(", ", match.getLines()) + ")");
                return match;
            }

            // Multiple matches found - prompt user to select
            System.out.println("Multiple matching stations found for '" + input + "':");
            for (int i = 0; i < matches.size(); i++) {
                MetroStation st = matches.get(i);
                System.out.printf("  %d. %s [%s]\n", i + 1, st.getName(), String.join(", ", st.getLines()));
            }
            System.out.print("Please select station number (1-" + matches.size() + ") or 0 to re-enter: ");
            String selStr = scanner.nextLine();
            int sel = parseMenuChoice(selStr, 0, matches.size());
            if (sel > 0) {
                return matches.get(sel - 1);
            }
            System.out.println();
        }
    }

    private LocalTime promptForTime() {
        while (true) {
            System.out.print("Enter current time (HH:MM in 24-hr format, or press Enter for current system time): ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                LocalTime now = LocalTime.now().withSecond(0).withNano(0);
                System.out.println("Using current system time: " + ScheduleCalculator.formatTime(now));
                return now;
            }

            if (input.equalsIgnoreCase("cancel") || input.equalsIgnoreCase("back")) {
                System.out.println("[Action cancelled]\n");
                return null;
            }

            LocalTime parsed = ScheduleCalculator.parseTime(input);
            if (parsed != null) {
                return parsed;
            }

            System.out.println("[!] Invalid time format. Please enter in HH:MM format (e.g., 09:30 or 17:45).");
        }
    }

    private void handleViewLines() {
        System.out.println("\n==================================================");
        System.out.println("              DELHI METRO LINES                   ");
        System.out.println("==================================================");
        Map<String, MetroLine> lines = graph.getAllLines();
        int idx = 1;
        for (MetroLine line : lines.values()) {
            List<String> stations = line.getStations();
            String terminalA = stations.isEmpty() ? "?" : stations.get(0);
            String terminalB = stations.isEmpty() ? "?" : stations.get(stations.size() - 1);
            System.out.printf("%2d. %-20s (%2d stations) [ %s <-> %s ]\n",
                    idx++, line.getName(), line.getStationCount(), terminalA, terminalB);
        }
        System.out.println("--------------------------------------------------\n");
    }

    private void handleViewStationsOnLine() {
        System.out.println("\n==================================================");
        System.out.println("            VIEW STATIONS ON A LINE               ");
        System.out.println("==================================================");

        List<MetroLine> linesList = new ArrayList<>(graph.getAllLines().values());
        for (int i = 0; i < linesList.size(); i++) {
            System.out.printf("%2d. %s\n", i + 1, linesList.get(i).getName());
        }
        System.out.print("\nSelect line number (1-" + linesList.size() + ") or 0 to go back: ");
        String selStr = scanner.nextLine();
        int sel = parseMenuChoice(selStr, 0, linesList.size());

        if (sel <= 0) {
            System.out.println();
            return;
        }

        MetroLine selectedLine = linesList.get(sel - 1);
        System.out.println("\n--------------------------------------------------");
        System.out.println("Line: " + selectedLine.getName() + " (" + selectedLine.getStationCount() + " stations)");
        System.out.println("--------------------------------------------------");

        List<String> stations = selectedLine.getStations();
        for (int i = 0; i < stations.size(); i++) {
            String stName = stations.get(i);
            MetroStation st = graph.findStation(stName);
            String interchangeInfo = "";
            if (st != null && st.isInterchange()) {
                List<String> otherLines = new ArrayList<>(st.getLines());
                otherLines.remove(selectedLine.getName());
                interchangeInfo = "  [⇄ Interchange: " + String.join(", ", otherLines) + "]";
            }
            System.out.printf("%2d. %-35s%s\n", i + 1, stName, interchangeInfo);
        }
        System.out.println("--------------------------------------------------\n");
    }

    private void handleSearchStation() {
        System.out.println("\n==================================================");
        System.out.println("               SEARCH STATION                     ");
        System.out.println("==================================================");
        System.out.print("Enter station name or keyword: ");
        String query = scanner.nextLine().trim();

        if (query.isEmpty()) {
            System.out.println("[!] Search query cannot be empty.\n");
            return;
        }

        List<MetroStation> matches = graph.searchStations(query);
        if (matches.isEmpty()) {
            System.out.println("[!] No stations found matching '" + query + "'.\n");
            return;
        }

        System.out.println("\nFound " + matches.size() + " matching station" + (matches.size() == 1 ? "" : "s") + ":");
        System.out.println("--------------------------------------------------");
        for (int i = 0; i < matches.size(); i++) {
            MetroStation st = matches.get(i);
            String interchangeTag = st.isInterchange() ? " [Interchange Station]" : "";
            System.out.printf("%2d. %-30s | Lines: %s%s\n",
                    i + 1, st.getName(), String.join(", ", st.getLines()), interchangeTag);
        }
        System.out.println("--------------------------------------------------\n");
    }

    private void handleProjectInfo() {
        System.out.println("\n==================================================");
        System.out.println("           ABOUT DELHI METRO SIMULATOR            ");
        System.out.println("==================================================");
        System.out.println("Project: Delhi Metro Route & Schedule Simulator");
        System.out.println("Course:  Object-Oriented Programming & Data Structures (Java)");
        System.out.println();
        System.out.println("KEY CONCEPTS & ALGORITHMS:");
        System.out.println("1. Graph Representation:");
        System.out.println("   - Stations are Graph Nodes (Vertices).");
        System.out.println("   - Track segments are Weighted Edges with travel time in minutes.");
        System.out.println("   - Bidirectional adjacency list representation.");
        System.out.println();
        System.out.println("2. Route Search Algorithm:");
        System.out.println("   - Dijkstra's Algorithm using Java's PriorityQueue.");
        System.out.println("   - State tracking: (Station, Current Line, Cumulative Time).");
        System.out.println("   - Graph automatically discovers single-line & multi-interchange routes.");
        System.out.println();
        System.out.println("3. Simulation Assumptions:");
        System.out.println("   - Peak Hours:    08:00 - 10:00 & 17:00 - 19:00 (Frequency: " + SimulationConfig.PEAK_FREQUENCY + " mins)");
        System.out.println("   - Off-Peak:      All other times (Frequency: " + SimulationConfig.OFF_PEAK_FREQUENCY + " mins)");
        System.out.println("   - Transfer Time: " + SimulationConfig.INTERCHANGE_TIME + " minutes per line change (walking penalty).");
        System.out.println("   - Data Source:   External CSV (data/metro_data.csv).");
        System.out.println("--------------------------------------------------\n");
    }

    private int parseMenuChoice(String input, int min, int max) {
        if (input == null || input.trim().isEmpty()) {
            return -1;
        }
        try {
            int choice = Integer.parseInt(input.trim());
            if (choice >= min && choice <= max) {
                return choice;
            }
        } catch (NumberFormatException ignored) {
        }
        return -1;
    }
}
