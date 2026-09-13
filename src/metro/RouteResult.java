package metro;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

public class RouteResult {
    private final String sourceStation;
    private final String destinationStation;
    private final LocalTime searchTime;
    private final LocalTime departureTime;
    private final LocalTime arrivalTime;
    private final int travelTimeMinutes;
    private final int interchangeTimeMinutes;
    private final int initialWaitingTimeMinutes;
    private final int totalEstimatedTimeMinutes;
    private final List<RouteStep> steps;
    private final List<String> interchanges;
    private final List<String> linesUsed;
    private final boolean routeFound;

    public RouteResult(String sourceStation, String destinationStation, LocalTime searchTime,
                       LocalTime departureTime, LocalTime arrivalTime, int travelTimeMinutes,
                       int interchangeTimeMinutes, int initialWaitingTimeMinutes,
                       int totalEstimatedTimeMinutes, List<RouteStep> steps,
                       List<String> interchanges, List<String> linesUsed, boolean routeFound) {
        this.sourceStation = sourceStation;
        this.destinationStation = destinationStation;
        this.searchTime = searchTime;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.travelTimeMinutes = travelTimeMinutes;
        this.interchangeTimeMinutes = interchangeTimeMinutes;
        this.initialWaitingTimeMinutes = initialWaitingTimeMinutes;
        this.totalEstimatedTimeMinutes = totalEstimatedTimeMinutes;
        this.steps = steps;
        this.interchanges = interchanges;
        this.linesUsed = linesUsed;
        this.routeFound = routeFound;
    }

    public static RouteResult emptyResult(String source, String destination, LocalTime searchTime) {
        return new RouteResult(source, destination, searchTime, searchTime, searchTime,
                0, 0, 0, 0,
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), false);
    }

    public String getSourceStation() {
        return sourceStation;
    }

    public String getDestinationStation() {
        return destinationStation;
    }

    public LocalTime getSearchTime() {
        return searchTime;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public LocalTime getArrivalTime() {
        return arrivalTime;
    }

    public int getTravelTimeMinutes() {
        return travelTimeMinutes;
    }

    public int getInterchangeTimeMinutes() {
        return interchangeTimeMinutes;
    }

    public int getInitialWaitingTimeMinutes() {
        return initialWaitingTimeMinutes;
    }

    public int getTotalEstimatedTimeMinutes() {
        return totalEstimatedTimeMinutes;
    }

    public List<RouteStep> getSteps() {
        return Collections.unmodifiableList(steps);
    }

    public List<String> getInterchanges() {
        return Collections.unmodifiableList(interchanges);
    }

    public List<String> getLinesUsed() {
        return Collections.unmodifiableList(linesUsed);
    }

    public boolean isRouteFound() {
        return routeFound;
    }

    public int getStationCount() {
        return steps.size();
    }

    public int getInterchangeCount() {
        return interchanges.size();
    }
}
