# Delhi Metro Route & Schedule Simulator

A Java terminal-based application that models the Delhi Metro network as a weighted graph, calculates optimal travel routes using Dijkstra's Algorithm, and simulates departure schedules, platform waiting times, and interchange transfer penalties.

---

## 1. Project Overview

The **Delhi Metro Route & Schedule Simulator** is a 2nd-year college computer science project built using Core Java. It demonstrates practical applications of Object-Oriented Programming (OOP), Data Structures (Graphs, PriorityQueues), and Graph Algorithms (Dijkstra's Shortest Path) on a real-world public transit problem.

Instead of hardcoding route paths, the system loads Delhi Metro lines and stations dynamically from an external CSV file, constructs an adjacency-list graph, and dynamically calculates the shortest and fastest journeys between any two stations.

---

## 2. Problem Statement

Navigating a massive urban transit system like the Delhi Metro (with over 240 stations across 12 lines) presents multiple challenges for commuters:
1. Finding the optimal route among multiple possible line combinations and transfer points.
2. Estimating total journey time including in-train travel, platform transfer walking times, and train departure waiting times.
3. Accounting for train frequency differences during peak rush hours versus off-peak hours.

This project solves this by modeling the network as an interconnected graph and providing a simple, user-friendly terminal interface.

---

## 3. Key Objectives

- Implement a graph-based transit model where stations are vertices and track segments are weighted edges.
- Use Dijkstra's Algorithm to compute the least-cost path (incorporating travel time and interchange penalties).
- Maintain clean separation between the data layer (CSV file) and core application logic.
- Accurately identify and display interchange stations and line transitions.
- Simulate schedule headways based on peak (08:00–10:00, 17:00–19:00) and off-peak time intervals using Java's `LocalTime` API.
- Provide robust error handling, case-insensitive station lookup, and partial name suggestions.

---

## 4. Key Features

- **Dynamic Route Finder**: Computes the optimal path between any two stations in the Delhi Metro network.
- **Interchange Detection**: Automatically highlights where passengers must switch trains, showing the incoming and outgoing lines.
- **Schedule & Waiting Time Simulation**:
  - Peak hours: 4-minute train headway.
  - Off-peak hours: 8-minute train headway.
  - Dynamic departure and arrival time calculation.
- **Interchange Walking Penalty**: Adds an estimated 5-minute platform transfer penalty per interchange.
- **Case-Insensitive & Partial Matching**: Users can type `rajiv chowk`, `RAJIV CHOWK`, or `botanical` and get matched effortlessly.
- **Network Explorer**:
  - View all 12 operational metro lines and terminal stations.
  - View ordered station lists for any specific line with interchange markers.
  - Search stations by keyword.
- **Multi-Search Support**: Perform repeated searches without restarting the application.
- **Configurable Architecture**: Simulation parameters (frequencies, penalties, peak hours) are centralized in `SimulationConfig.java`.

---

## 5. Technologies & Tools Used

- **Language**: Java (JDK 17 or later)
- **Paradigm**: Object-Oriented Programming (OOP)
- **Data Structures**:
  - `Map<String, List<MetroEdge>>` (Adjacency List Graph)
  - `PriorityQueue<NodeState>` (Dijkstra's Min-Heap)
  - `Set<String>` (Station line membership)
  - `List<RouteStep>` (Itinerary representation)
- **Standard Libraries**:
  - `java.time.LocalTime` and `java.time.format.DateTimeFormatter`
  - `java.io.BufferedReader`, `java.io.FileReader`, `java.io.File`
  - `java.util.Scanner`, `java.util.Collections`
- **Build / Run**: Standard `javac` and `java` commands (or `run.bat` on Windows).

---

## 6. Concepts Demonstrated

| Concept | Implementation in Project |
| :--- | :--- |
| **Object-Oriented Programming** | Modularity via `MetroStation`, `MetroLine`, `MetroEdge`, `MetroGraph`, `RouteResult`. |
| **Graph Data Structure** | Adjacency list storing bidirectional connections between adjacent metro stations. |
| **Dijkstra's Algorithm** | Shortest path search with custom state tracking `(station, line, cumulativeCost)`. |
| **Collections Framework** | `HashMap`, `TreeMap`, `ArrayList`, `HashSet`, `PriorityQueue`. |
| **File I/O & Exception Handling** | Reading and validating CSV data; graceful handling of missing or malformed records. |
| **Modern Date/Time API** | `java.time.LocalTime` for headway simulation, departure, and arrival calculations. |
| **Input Validation** | Sanitizing inputs, case-insensitive string normalization, and range checks. |

---

## 7. Algorithms & Time Complexity

### Dijkstra's Shortest Path Algorithm
- **Graph Nodes ($V$)**: 241 stations.
- **Graph Edges ($E$)**: Bidirectional connections between adjacent stations.
- **State Representation**: `(Station, ActiveLine, CumulativeCost)`
- **Cost Function**:
  $$\text{Total Cost} = \text{Travel Time} + (\text{Number of Transfers} \times \text{Interchange Penalty}) + \text{Initial Waiting Time}$$
- **Time Complexity**: $\mathcal{O}((V + E) \log V)$ using a Min-Heap (`PriorityQueue`).
- **Space Complexity**: $\mathcal{O}(V + E)$ for adjacency list storage and Dijkstra state tracking.

---

## 8. Project Structure

```
DelhiMetroSimulator/
│
├── data/
│   └── metro_data.csv             # Delhi Metro lines, stations, and connection timings
│
├── src/
│   └── metro/
│       ├── Main.java              # Application entry point
│       ├── MetroApplication.java   # Terminal CLI menu loop and presentation layer
│       ├── MetroStation.java       # Station entity (name, lines served, interchange flag)
│       ├── MetroLine.java          # Line entity (name, ordered station sequence)
│       ├── MetroEdge.java          # Directional track edge (source, dest, line, travel time)
│       ├── MetroGraph.java         # Adjacency list graph with station & line registries
│       ├── MetroDataLoader.java    # CSV loader and graph builder with validation
│       ├── MetroRouteFinder.java   # Dijkstra algorithm implementation
│       ├── RouteResult.java        # Route metrics DTO (times, durations, steps)
│       ├── RouteStep.java          # Individual step in route itinerary
│       ├── ScheduleCalculator.java # Headway, peak-hour, and arrival time math
│       ├── SimulationConfig.java   # Simulation constants and parameters
│       └── InputValidator.java     # Time parsing, menu checks, station name search
│
├── test/
│   └── metro/
│       └── MetroSimulatorTest.java # Standalone verification test suite
│
├── run.bat                         # Windows 1-click build and run script
├── README.md                       # Project documentation
└── VIVA_NOTES.md                   # Viva exam preparation questions and answers
```

---

## 9. Dataset Format

The metro network is defined in `data/metro_data.csv`:

```csv
line_name,station_name,station_order,travel_time_to_next
Red Line,Shaheed Sthal,1,2
Red Line,Hindon River,2,2
...
Yellow Line,Samaypur Badli,1,2
Yellow Line,Kashmere Gate,12,2
...
Blue Line,Rajiv Chowk,29,2
...
```

- **`line_name`**: Name of the metro line (e.g. `Red Line`, `Yellow Line`, `Magenta Line`).
- **`station_name`**: Name of the station. Interchange stations share the exact same name across lines.
- **`station_order`**: Sequence index of the station on the line.
- **`travel_time_to_next`**: Estimated transit time in minutes to the next sequential station (0 for terminal stations).

### Lines Covered in Dataset:
1. Red Line (Shaheed Sthal $\leftrightarrow$ Rithala)
2. Yellow Line (Samaypur Badli $\leftrightarrow$ Millennium City Centre Gurugram)
3. Blue Line Main (Dwarka Sector 21 $\leftrightarrow$ Noida Electronic City)
4. Blue Line Branch (Yamuna Bank $\leftrightarrow$ Vaishali)
5. Green Line (Brigadier Hoshiar Singh $\leftrightarrow$ Inderlok)
6. Green Line Branch (Ashok Park Main $\leftrightarrow$ Kirti Nagar)
7. Violet Line (Kashmere Gate $\leftrightarrow$ Raja Nahar Singh)
8. Pink Line (Majlis Park $\leftrightarrow$ Shiv Vihar)
9. Magenta Line (Janakpuri West $\leftrightarrow$ Botanical Garden)
10. Grey Line (Dwarka $\leftrightarrow$ Dhansa Bus Stand)
11. Airport Express Line (New Delhi $\leftrightarrow$ Yashobhoomi Dwarka Sector 25)
12. Rapid Metro Gurugram (Sector 55-56 $\leftrightarrow$ Phase 3)

---

## 10. How to Compile and Run

### Prerequisites
- Java Development Kit (JDK) 17 or newer installed.
- Terminal / Command Prompt / PowerShell.

### Option 1: Using the Windows Batch Script
Double-click `run.bat` or execute in Command Prompt:
```cmd
run.bat
```

### Option 2: Manual Compilation via Command Line
1. **Compile the source and test files**:
   ```cmd
   javac -d bin src/metro/*.java test/metro/*.java
   ```

2. **Run the Simulator**:
   ```cmd
   java -cp bin metro.Main
   ```

3. **Run the Test Suite**:
   ```cmd
   java -cp bin test.metro.MetroSimulatorTest
   ```

---

## 11. Sample Run & Output

### Finding a Route (Single Interchange)
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

## 12. Verification & Test Scenarios

The included automated test suite (`test/metro/MetroSimulatorTest.java`) validates 10 core scenarios:

1. **Same-Line Route**: Verified on Yellow Line (`Samaypur Badli` to `Rajiv Chowk` $\rightarrow$ 0 interchanges).
2. **Single-Interchange Route**: Verified on `IIT Delhi` to `Rajiv Chowk` (Transfer at `Hauz Khas`).
3. **Multi-Interchange Route**: Verified on `IIT Delhi` to `Vaishali` (Multiple transfers across Magenta, Yellow/Pink, Blue Branch).
4. **Source Equals Destination**: Handles identity journeys with 0 duration and 1 step.
5. **Invalid Station Handling**: Gracefully reports non-existent stations without exceptions.
6. **Time Format Validation**: Validates 24-hour `HH:mm` format and rejects invalid inputs like `25:00`.
7. **Peak-Hour Calculation**: Confirms 4-minute frequency during 08:00–10:00 and calculates wait time modulo 4.
8. **Off-Peak Calculation**: Confirms 8-minute frequency during off-peak hours and calculates wait time modulo 8.
9. **Case-Insensitive Lookup**: Confirms identical station resolution for `kashmere gate`, `KASHMERE GATE`, and mixed casing.
10. **Missing Data File Handling**: Validates that missing files trigger clear error messages via `IOException`.

---

## 13. Simulation Assumptions vs Real DMRC Data

To maintain academic honesty, the following distinguishes realistic data from simulation assumptions:

| Aspect | Status | Details |
| :--- | :--- | :--- |
| **Stations & Lines** | Realistic | Names and topological order match operational Delhi Metro lines. |
| **Interchange Connections** | Realistic | Connecting stations match real-world interchange junctions. |
| **Travel Times** | Approximation | Average 2–3 minutes between adjacent stations. |
| **Train Headways** | Simulated | 4 mins for peak hours, 8 mins for off-peak hours. |
| **Interchange Walk Time** | Simulated | Fixed at 5 minutes per platform transfer. |
| **Live Train Tracking** | Simulated | Periodic headway model based on the user's entered time. |

---

## 14. Limitations & Future Improvements

### Current Limitations
- Does not connect to live DMRC real-time GPS feeds or dynamic delay APIs.
- Interchange walking times are modeled as a uniform 5 minutes (some physical stations like Hauz Khas have longer walkways than others).
- Fares are not calculated in the current version.

### Potential Future Improvements
- Fare calculation based on distance/station slabs.
- Station platform gate / exit directions (Gate 1, Gate 2).
- First/last train cutoff alerts late at night.
