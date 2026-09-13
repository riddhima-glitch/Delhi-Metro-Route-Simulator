package test.metro;

import metro.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalTime;

public class MetroSimulatorTest {

    private static int testsRun = 0;
    private static int testsPassed = 0;

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("       RUNNING METRO SIMULATOR TEST SUITE         ");
        System.out.println("==================================================\n");

        String dataPath = "data/metro_data.csv";
        if (!new File(dataPath).exists() && new File("../data/metro_data.csv").exists()) {
            dataPath = "../data/metro_data.csv";
        }

        MetroGraph graph = null;
        try {
            graph = MetroDataLoader.loadMetroData(dataPath);
            System.out.println("[INIT] Graph loaded successfully with " + graph.getTotalLines() + " lines and " + graph.getTotalStations() + " stations.\n");
        } catch (IOException e) {
            System.err.println("[FAIL] Could not load dataset for testing: " + e.getMessage());
            System.exit(1);
        }

        MetroRouteFinder routeFinder = new MetroRouteFinder(graph);

        // Test 1: Same-line route
        testSameLineRoute(routeFinder);

        // Test 2: Single-interchange route
        testSingleInterchangeRoute(routeFinder);

        // Test 3: Multiple-interchange route
        testMultipleInterchangeRoute(routeFinder);

        // Test 4: Source equals destination
        testSourceEqualsDestination(routeFinder);

        // Test 5: Invalid station handling
        testInvalidStation(routeFinder);

        // Test 6: Invalid time format validation
        testTimeValidation();

        // Test 7: Peak-hour frequency and waiting time
        testPeakHourCalculation();

        // Test 8: Off-peak frequency and waiting time
        testOffPeakHourCalculation();

        // Test 9: Case-insensitive station lookup
        testCaseInsensitiveLookup(graph);

        // Test 10: Missing data file handling
        testMissingDataFile();

        // Summary
        System.out.println("\n==================================================");
        System.out.printf("TEST SUMMARY: %d/%d tests passed (%.1f%%)\n",
                testsPassed, testsRun, (testsPassed * 100.0 / testsRun));
        System.out.println("==================================================");

        if (testsPassed != testsRun) {
            System.exit(1);
        }
    }

    private static void assertTrue(String testName, boolean condition, String details) {
        testsRun++;
        if (condition) {
            testsPassed++;
            System.out.println("[PASS] " + testName + (details != null ? " - " + details : ""));
        } else {
            System.err.println("[FAIL] " + testName + (details != null ? " - " + details : ""));
        }
    }

    private static void testSameLineRoute(MetroRouteFinder finder) {
        // Samaypur Badli to Rajiv Chowk (both on Yellow Line)
        RouteResult result = finder.findRoute("Samaypur Badli", "Rajiv Chowk", LocalTime.of(9, 30));

        boolean passed = result.isRouteFound() &&
                result.getInterchangeCount() == 0 &&
                result.getLinesUsed().size() == 1 &&
                result.getLinesUsed().contains("Yellow Line");

        assertTrue("Test 1: Same-line route (Samaypur Badli -> Rajiv Chowk)", passed,
                "Duration: " + result.getTotalEstimatedTimeMinutes() + " mins, Interchanges: " + result.getInterchangeCount());
    }

    private static void testSingleInterchangeRoute(MetroRouteFinder finder) {
        // IIT Delhi (Magenta) to Rajiv Chowk (Yellow/Blue) via Hauz Khas
        RouteResult result = finder.findRoute("IIT Delhi", "Rajiv Chowk", LocalTime.of(9, 30));

        boolean passed = result.isRouteFound() &&
                result.getInterchangeCount() == 1 &&
                result.getInterchanges().contains("Hauz Khas") &&
                result.getLinesUsed().contains("Magenta Line") &&
                result.getLinesUsed().contains("Yellow Line");

        assertTrue("Test 2: Single-interchange route (IIT Delhi -> Rajiv Chowk)", passed,
                "Interchange at: " + result.getInterchanges() + ", Lines: " + result.getLinesUsed());
    }

    private static void testMultipleInterchangeRoute(MetroRouteFinder finder) {
        // IIT Delhi (Magenta) to Vaishali (Blue Line Branch)
        RouteResult result = finder.findRoute("IIT Delhi", "Vaishali", LocalTime.of(9, 30));

        boolean passed = result.isRouteFound() &&
                result.getInterchangeCount() >= 2 &&
                result.getLinesUsed().contains("Magenta Line") &&
                result.getLinesUsed().contains("Blue Line Branch");

        assertTrue("Test 3: Multi-interchange route (IIT Delhi -> Vaishali)", passed,
                "Interchanges: " + result.getInterchanges() + ", Lines: " + result.getLinesUsed());
    }

    private static void testSourceEqualsDestination(MetroRouteFinder finder) {
        RouteResult result = finder.findRoute("Rajiv Chowk", "Rajiv Chowk", LocalTime.of(10, 0));

        boolean passed = result.isRouteFound() &&
                result.getTravelTimeMinutes() == 0 &&
                result.getInterchangeCount() == 0 &&
                result.getStationCount() == 1;

        assertTrue("Test 4: Source equals destination (Rajiv Chowk -> Rajiv Chowk)", passed,
                "Total Duration: " + result.getTotalEstimatedTimeMinutes() + " min");
    }

    private static void testInvalidStation(MetroRouteFinder finder) {
        RouteResult result = finder.findRoute("NonExistentStationXYZ", "Rajiv Chowk", LocalTime.of(10, 0));

        boolean passed = !result.isRouteFound() && result.getSteps().isEmpty();
        assertTrue("Test 5: Invalid station handling", passed, "Non-existent station correctly handled");
    }

    private static void testTimeValidation() {
        LocalTime validTime = ScheduleCalculator.parseTime("09:30");
        LocalTime invalidTime1 = ScheduleCalculator.parseTime("25:00");
        LocalTime invalidTime2 = ScheduleCalculator.parseTime("abc");

        boolean passed = (validTime != null && validTime.getHour() == 9 && validTime.getMinute() == 30) &&
                (invalidTime1 == null) &&
                (invalidTime2 == null);

        assertTrue("Test 6: Time format validation (HH:mm)", passed, "Valid: 09:30 accepted, 25:00 & abc rejected");
    }

    private static void testPeakHourCalculation() {
        LocalTime morningPeak = LocalTime.of(8, 30);
        LocalTime eveningPeak = LocalTime.of(18, 15);

        boolean isMorningPeak = ScheduleCalculator.isPeakHour(morningPeak);
        boolean isEveningPeak = ScheduleCalculator.isPeakHour(eveningPeak);
        int morningFreq = ScheduleCalculator.getFrequency(morningPeak);
        int waitTime = ScheduleCalculator.calculateWaitingTime(LocalTime.of(9, 30)); // 30 % 4 = 2, wait = 2 min

        boolean passed = isMorningPeak && isEveningPeak &&
                morningFreq == SimulationConfig.PEAK_FREQUENCY &&
                waitTime == 2;

        assertTrue("Test 7: Peak-hour frequency & wait calculation", passed,
                "Peak freq: " + morningFreq + " min, Wait at 09:30: " + waitTime + " min");
    }

    private static void testOffPeakHourCalculation() {
        LocalTime offPeakTime = LocalTime.of(14, 15);

        boolean isPeak = ScheduleCalculator.isPeakHour(offPeakTime);
        int offPeakFreq = ScheduleCalculator.getFrequency(offPeakTime);
        int waitTime = ScheduleCalculator.calculateWaitingTime(LocalTime.of(14, 15)); // 15 % 8 = 7, wait = 1 min

        boolean passed = !isPeak &&
                offPeakFreq == SimulationConfig.OFF_PEAK_FREQUENCY &&
                waitTime == 1;

        assertTrue("Test 8: Off-peak frequency & wait calculation", passed,
                "Off-peak freq: " + offPeakFreq + " min, Wait at 14:15: " + waitTime + " min");
    }

    private static void testCaseInsensitiveLookup(MetroGraph graph) {
        MetroStation st1 = graph.findStation("kashmere gate");
        MetroStation st2 = graph.findStation("KASHMERE GATE");
        MetroStation st3 = graph.findStation("KaShMeRe GaTe");

        boolean passed = (st1 != null) && (st1 == st2) && (st2 == st3);
        assertTrue("Test 9: Case-insensitive station lookup", passed, "Station: " + (st1 != null ? st1.getName() : "null"));
    }

    private static void testMissingDataFile() {
        boolean exceptionThrown = false;
        try {
            MetroDataLoader.loadMetroData("invalid_path_to_file_123.csv");
        } catch (IOException e) {
            exceptionThrown = true;
        }

        assertTrue("Test 10: Missing dataset error handling", exceptionThrown, "IOException caught as expected");
    }
}
