package metro;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * MetroRouteFinder
 * 
 * Implements Dijkstra's Shortest Path Algorithm on the metro graph.
 * Calculates optimal routes considering:
 * - Direct station-to-station rail travel time
 * - Interchange line-transfer penalty (walking between platforms)
 * - Initial station waiting time based on peak/off-peak frequency
 */
public class MetroRouteFinder {

    private final MetroGraph graph;

    public MetroRouteFinder(MetroGraph graph) {
        this.graph = graph;
    }

    /**
     * Node used inside the Dijkstra PriorityQueue.
     * Represents the state of arriving at a station on a specific metro line.
     */
    private static class NodeState implements Comparable<NodeState> {
        final String station;
        final String currentLine;
        final int cumulativeCost;
        final NodeState previous;
        final MetroEdge edgeUsed;

        NodeState(String station, String currentLine, int cumulativeCost, NodeState previous, MetroEdge edgeUsed) {
            this.station = station;
            this.currentLine = currentLine;
            this.cumulativeCost = cumulativeCost;
            this.previous = previous;
            this.edgeUsed = edgeUsed;
        }

        @Override
        public int compareTo(NodeState other) {
            return Integer.compare(this.cumulativeCost, other.cumulativeCost);
        }
    }

    /**
     * Finds the fastest route between source and destination stations at the given departure time.
     * 
     * @param sourceStationName Source station (case-insensitive)
     * @param destStationName   Destination station (case-insensitive)
     * @param searchTime        User's query time
     * @return RouteResult containing detailed itinerary and journey metrics
     */
    public RouteResult findRoute(String sourceStationName, String destStationName, LocalTime searchTime) {
        MetroStation srcStation = graph.findStation(sourceStationName);
        MetroStation dstStation = graph.findStation(destStationName);

        if (srcStation == null || dstStation == null) {
            return RouteResult.emptyResult(sourceStationName, destStationName, searchTime);
        }

        String sourceCanonical = srcStation.getName();
        String destCanonical = dstStation.getName();

        // Handle case where source and destination are the same
        if (sourceCanonical.equalsIgnoreCase(destCanonical)) {
            List<RouteStep> singleStep = new ArrayList<>();
            String defaultLine = srcStation.getLines().isEmpty() ? "Metro" : srcStation.getLines().iterator().next();
            singleStep.add(new RouteStep(1, sourceCanonical, defaultLine, false, null, null));

            return new RouteResult(sourceCanonical, destCanonical, searchTime, searchTime, searchTime,
                    0, 0, 0, 0,
                    singleStep, Collections.emptyList(), Collections.singletonList(defaultLine), true);
        }

        PriorityQueue<NodeState> pq = new PriorityQueue<>();
        // Best known cost for state (stationName + "#" + lineName)
        Map<String, Integer> bestCosts = new HashMap<>();

        // Start from source station with no initial line (null line)
        pq.add(new NodeState(sourceCanonical, null, 0, null, null));
        bestCosts.put(stateKey(sourceCanonical, null), 0);

        NodeState destinationNode = null;

        while (!pq.isEmpty()) {
            NodeState current = pq.poll();

            // Reached destination station
            if (current.station.equalsIgnoreCase(destCanonical)) {
                destinationNode = current;
                break;
            }

            String currentKey = stateKey(current.station, current.currentLine);
            if (bestCosts.containsKey(currentKey) && bestCosts.get(currentKey) < current.cumulativeCost) {
                continue;
            }

            // Explore adjacent stations
            for (MetroEdge edge : graph.getNeighbors(current.station)) {
                String nextStation = edge.getDestination();
                String edgeLine = edge.getLineName();

                int edgeCost = edge.getTravelTimeMinutes();

                // Apply interchange transfer penalty if changing from an existing line to a different line
                if (current.currentLine != null && !current.currentLine.equalsIgnoreCase(edgeLine)) {
                    edgeCost += SimulationConfig.INTERCHANGE_TIME;
                }

                int newTotalCost = current.cumulativeCost + edgeCost;
                String nextKey = stateKey(nextStation, edgeLine);

                if (!bestCosts.containsKey(nextKey) || newTotalCost < bestCosts.get(nextKey)) {
                    bestCosts.put(nextKey, newTotalCost);
                    pq.add(new NodeState(nextStation, edgeLine, newTotalCost, current, edge));
                }
            }
        }

        if (destinationNode == null) {
            // No connected route found
            return RouteResult.emptyResult(sourceCanonical, destCanonical, searchTime);
        }

        // Reconstruct path from destination backwards to source
        return buildRouteResult(destinationNode, sourceCanonical, destCanonical, searchTime);
    }

    private static String stateKey(String station, String line) {
        return station.toLowerCase() + "#" + (line == null ? "START" : line.toLowerCase());
    }

    private RouteResult buildRouteResult(NodeState destNode, String sourceName, String destName, LocalTime searchTime) {
        List<NodeState> pathNodes = new ArrayList<>();
        NodeState curr = destNode;
        while (curr != null) {
            pathNodes.add(curr);
            curr = curr.previous;
        }
        Collections.reverse(pathNodes);

        List<MetroEdge> edges = new ArrayList<>();
        for (int i = 1; i < pathNodes.size(); i++) {
            edges.add(pathNodes.get(i).edgeUsed);
        }

        // Calculate metrics
        int totalTravelTime = 0;
        int interchangeCount = 0;
        List<String> interchanges = new ArrayList<>();
        List<String> linesUsed = new ArrayList<>();
        Set<String> seenLines = new HashSet<>();

        String activeLine = null;

        for (int i = 0; i < edges.size(); i++) {
            MetroEdge edge = edges.get(i);
            totalTravelTime += edge.getTravelTimeMinutes();

            if (activeLine == null) {
                activeLine = edge.getLineName();
                linesUsed.add(activeLine);
                seenLines.add(activeLine);
            } else if (!activeLine.equalsIgnoreCase(edge.getLineName())) {
                // Line change happened at the source of this edge
                interchangeCount++;
                interchanges.add(edge.getSource());
                activeLine = edge.getLineName();
                if (!seenLines.contains(activeLine)) {
                    linesUsed.add(activeLine);
                    seenLines.add(activeLine);
                }
            }
        }

        int interchangeTime = interchangeCount * SimulationConfig.INTERCHANGE_TIME;
        int waitingTime = ScheduleCalculator.calculateWaitingTime(searchTime);
        int totalJourneyTime = totalTravelTime + interchangeTime + waitingTime;

        LocalTime departureTime = ScheduleCalculator.calculateDepartureTime(searchTime, waitingTime);
        LocalTime arrivalTime = ScheduleCalculator.calculateArrivalTime(departureTime, totalTravelTime + interchangeTime);

        // Build list of RouteStep objects for itinerary display
        List<RouteStep> steps = new ArrayList<>();
        int stepNumber = 1;

        for (int i = 0; i < pathNodes.size(); i++) {
            NodeState node = pathNodes.get(i);
            String station = node.station;

            if (i == 0) {
                // Source station
                String firstLine = (edges.isEmpty()) ? "" : edges.get(0).getLineName();
                steps.add(new RouteStep(stepNumber++, station, firstLine, false, null, null));
            } else if (i == pathNodes.size() - 1) {
                // Destination station
                String lastLine = edges.get(edges.size() - 1).getLineName();
                steps.add(new RouteStep(stepNumber++, station, lastLine, false, null, null));
            } else {
                // Intermediate station
                MetroEdge prevEdge = edges.get(i - 1);
                MetroEdge nextEdge = edges.get(i);

                boolean isInterchange = !prevEdge.getLineName().equalsIgnoreCase(nextEdge.getLineName());
                if (isInterchange) {
                    steps.add(new RouteStep(stepNumber++, station, nextEdge.getLineName(), true,
                            prevEdge.getLineName(), nextEdge.getLineName()));
                } else {
                    steps.add(new RouteStep(stepNumber++, station, prevEdge.getLineName(), false, null, null));
                }
            }
        }

        return new RouteResult(sourceName, destName, searchTime, departureTime, arrivalTime,
                totalTravelTime, interchangeTime, waitingTime, totalJourneyTime,
                steps, interchanges, linesUsed, true);
    }
}
