package metro;

public class RouteStep {
    private final int stepNumber;
    private final String stationName;
    private final String lineName;
    private final boolean isInterchange;
    private final String interchangeFromLine;
    private final String interchangeToLine;

    public RouteStep(int stepNumber, String stationName, String lineName, boolean isInterchange,
                     String interchangeFromLine, String interchangeToLine) {
        this.stepNumber = stepNumber;
        this.stationName = stationName;
        this.lineName = lineName;
        this.isInterchange = isInterchange;
        this.interchangeFromLine = interchangeFromLine;
        this.interchangeToLine = interchangeToLine;
    }

    public int getStepNumber() {
        return stepNumber;
    }

    public String getStationName() {
        return stationName;
    }

    public String getLineName() {
        return lineName;
    }

    public boolean isInterchange() {
        return isInterchange;
    }

    public String getInterchangeFromLine() {
        return interchangeFromLine;
    }

    public String getInterchangeToLine() {
        return interchangeToLine;
    }
}
