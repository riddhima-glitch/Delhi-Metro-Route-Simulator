# Delhi Metro Route & Schedule Simulator

A terminal-based Java application that models the Delhi Metro network as a weighted graph, finds shortest routes using Dijkstra's algorithm, and estimates travel times based on line interchanges, walking transfer penalties, and peak/off-peak train frequencies.

I built this project for my 2nd-year Java / Data Structures coursework to practice working with graphs, priority queues, and file I/O.

---

## Features

- **Dynamic Route Finding**: Finds the fastest path between any two stations using Dijkstra's algorithm.
- **Interchange Detection**: Shows where line changes happen and adds a 5-minute walking transfer penalty per interchange.
- **Frequency & Waiting Time Simulation**:
  - Peak hours (08:00–10:00 and 17:00–19:00): 4-minute train frequency.
  - Off-peak hours: 8-minute train frequency.
  - Calculates estimated departure and arrival times based on the entered query time.
- **Station Search & Line Explorer**:
  - Case-insensitive search with partial name suggestions (e.g., typing `rajiv` or `botanical`).
  - View all 12 metro lines and list stations on any selected line.
- **CSV Data Loading**: Station connections, lines, and travel times are loaded from `data/metro_data.csv` rather than being hardcoded in Java.

---

## Tech Stack

- **Language**: Java (JDK 17+)
- **Data Structures**: Adjacency List (`Map<String, List<MetroEdge>>`), Min-Heap (`PriorityQueue`), `HashMap`, `ArrayList`
- **APIs**: `java.time.LocalTime`, `java.time.format.DateTimeFormatter`, `java.io.BufferedReader`, `java.util.Scanner`
- **Build Tool**: None required (uses standard `javac` and `java` commands)

---

## Project Structure

```text
DelhiMetroSimulator/
├── data/
│   └── metro_data.csv             # Delhi Metro lines, stations, and connection timings
├── src/
│   └── metro/
│       ├── Main.java              # Entry point
│       ├── MetroApplication.java   # Console menu loop and user prompts
│       ├── MetroStation.java       # Station node (name, lines served)
│       ├── MetroLine.java          # Line data (name, station list)
│       ├── MetroEdge.java          # Directed graph edge with travel duration
│       ├── MetroGraph.java         # Adjacency list graph and search methods
│       ├── MetroDataLoader.java    # CSV file parser
│       ├── MetroRouteFinder.java   # Dijkstra shortest path implementation
│       ├── RouteResult.java        # Route metrics and timing DTO
│       ├── RouteStep.java          # Individual step in route itinerary
│       ├── ScheduleCalculator.java # Headway and arrival time math
│       └── SimulationConfig.java   # Frequency, transfer penalty, and peak constants
├── test/
│   └── metro/
│       └── MetroSimulatorTest.java # Standalone verification tests
└── README.md
```

---

## How to Compile and Run

The project runs completely from the terminal and does not require Maven or Gradle.

### 1. Compile

Open a terminal in the project root directory and compile the source files into the `out` folder:

**Windows (Command Prompt / PowerShell):**
```cmd
javac -d out src/metro/*.java test/metro/*.java
```

**Linux / macOS:**
```bash
javac -d out src/metro/*.java test/metro/*.java
```

### 2. Run the Simulator

```cmd
java -cp out metro.Main
```

### 3. Run the Tests (Optional)

A test file is included to verify same-line routes, multi-interchange routes, invalid inputs, and timing logic:

```cmd
java -cp out test.metro.MetroSimulatorTest
```

---

## Sample Output

```text
==================================================
               FIND METRO ROUTE                   
==================================================
Enter source station: iit delhi
Enter destination station: rajiv chowk
Enter current time (HH:MM in 24-hr format, or press Enter for current system time): 09:30

Calculating optimal route via Dijkstra's algorithm...

--------------------------------------------------
                   ROUTE FOUND                    
--------------------------------------------------
From: IIT Delhi
To:   Rajiv Chowk
Query Time:       09:30 (Peak Hours: 4 min headway)
Next Departure:   09:32
Expected Arrival: 09:57
--------------------------------------------------
JOURNEY ITINERARY (11 stations, 1 interchange):
--------------------------------------------------
 1. IIT Delhi
    [Board Magenta Line]
 2. Hauz Khas
    ***********************************************
    >>> INTERCHANGE STATION <<<
    Change from Magenta Line -> Yellow Line
    (Estimated walking/transfer time: 5 mins)
    ***********************************************
 3. Green Park
 4. AIIMS
 5. Dilli Haat - INA
 6. Jor Bagh
 7. Lok Kalyan Marg
 8. Udyog Bhawan
 9. Central Secretariat
10. Patel Chowk
11. Rajiv Chowk
    [Destination Reached]
--------------------------------------------------
JOURNEY SUMMARY:
--------------------------------------------------
Lines Used:              Magenta Line, Yellow Line
Interchange Stations:    Hauz Khas
In-Train Travel Time:    20 minutes
Interchange Transfer:    5 minutes (1 x 5 min)
Expected Waiting Time:   2 minutes
Total Estimated Journey: 27 minutes
Departure Time:          09:32
Arrival Time:            09:57
--------------------------------------------------
```

---

## Dataset & Assumptions

- **Station and Line Data**: Includes Red, Yellow, Blue, Blue Branch, Green, Green Branch, Violet, Pink, Magenta, Grey, Airport Express, and Rapid Metro lines with approx 240 stations.
- **Travel Times**: Estimated at 2–3 minutes between consecutive stations.
- **Interchange Penalty**: Assumes an average 5-minute platform walking transfer time when changing lines.
- **Frequencies**: Fixed simulation headways (4 min peak, 8 min off-peak) rather than live DMRC tracking feeds.

---

## Limitations & Possible Improvements

- Interchange walking times are modeled as a uniform 5 minutes (some physical stations take longer to walk through than others).
- Does not integrate with live DMRC GPS tracking APIs.
- Fares are currently not calculated.
- Possible future additions: fare calculation based on station distance, platform exit gate information, and first/last train cutoff warnings.
