# Delhi Metro Route & Schedule Simulator

A pure terminal-based Java application that models the Delhi Metro network as a weighted graph, calculates optimal travel routes using Dijkstra's Algorithm, and simulates departure schedules, platform waiting times, and interchange transfer penalties.

---

## 1. Project Overview

The **Delhi Metro Route & Schedule Simulator** is a 2nd-year college computer science project built using Core Java. It demonstrates practical applications of Object-Oriented Programming (OOP), Data Structures (Graphs, PriorityQueues), and Graph Algorithms (Dijkstra's Shortest Path) on a real-world public transit problem.

The application runs directly from the terminal or command prompt using standard `javac` and `java` commands. It does not require any build tools (Maven/Gradle), database servers, or external framework dependencies.

The metro network data is loaded dynamically from `data/metro_data.csv`.

---

## 2. Problem Statement

Navigating a massive urban transit system like the Delhi Metro (with over 240 stations across 12 lines) presents multiple challenges for commuters:
1. Finding the optimal route among multiple possible line combinations and transfer points.
2. Estimating total journey time including in-train travel, platform transfer walking times, and train departure waiting times.
3. Accounting for train frequency differences during peak rush hours versus off-peak hours.

This project solves this by modeling the network as an interconnected graph and providing an interactive, text-based terminal interface.

---

## 3. Key Features

- **Dynamic Route Finder**: Computes the optimal path between any two stations in the Delhi Metro network.
- **Interchange Detection**: Automatically highlights where passengers must switch trains, showing incoming and outgoing lines.
- **Schedule & Waiting Time Simulation**:
  - Peak hours (08:00–10:00 & 17:00–19:00): 4-minute train headway.
  - Off-peak hours: 8-minute train headway.
  - Dynamic departure and arrival time calculation.
- **Interchange Walking Penalty**: Adds an estimated 5-minute platform transfer penalty per interchange.
- **Case-Insensitive & Partial Matching**: Accepts inputs like `rajiv chowk`, `RAJIV CHOWK`, or `botanical` and matches automatically.
- **Network Explorer**:
  - View all 12 operational metro lines and terminal stations.
  - View ordered station lists for any specific line with interchange markers.
  - Search stations by keyword.
- **Multi-Search Support**: Perform repeated searches without restarting the application.
- **Configurable Architecture**: Simulation parameters (frequencies, penalties, peak hours) are centralized in `SimulationConfig.java`.

---

## 4. Technologies & Concepts

- **Language**: Java (JDK 17 or later)
- **Data Structures**:
  - Adjacency List Graph (`Map<String, List<MetroEdge>>`)
  - Min-Heap Priority Queue (`PriorityQueue<NodeState>`)
  - Hash Sets and Tree Maps (`HashSet`, `TreeMap`)
- **Algorithms**: Dijkstra's Shortest Path Algorithm ($\mathcal{O}((V + E) \log V)$)
- **Java APIs**: `java.time.LocalTime`, `java.time.format.DateTimeFormatter`, `java.io.BufferedReader`, `java.util.Scanner`

---

## 5. Project Structure

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
│       └── SimulationConfig.java   # Simulation constants and parameters
│
├── test/
│   └── metro/
│       └── MetroSimulatorTest.java # Standalone verification test suite
│
├── README.md                       # Project documentation
└── VIVA_NOTES.md                   # Viva exam preparation notes
```

---

## 6. How to Compile and Run (Terminal Execution)

The project is designed to be compiled and executed directly from the terminal without any external build tools or wrapper scripts.

### Step 1: Open Terminal & Navigate to Project Directory
```cmd
cd path/to/DelhiMetroSimulator
```

### Step 2: Compile the Java Source Files
Compile all source files into the `out` directory:

- **Windows Command Prompt (cmd)**:
  ```cmd
  javac -d out src\metro\*.java test\metro\*.java
  ```

- **Windows PowerShell**:
  ```powershell
  javac -d out src/metro/*.java test/metro/*.java
  ```

- **Linux / macOS Terminal**:
  ```bash
  javac -d out src/metro/*.java test/metro/*.java
  ```

### Step 3: Run the Application
Run the `Main` class from the project root:

- **Windows / Linux / macOS**:
  ```cmd
  java -cp out metro.Main
  ```

*Note: The application will automatically locate `data/metro_data.csv` in the current working directory.*

### Step 4: (Optional) Run the Test Suite
To run the automated verification test suite:

```cmd
java -cp out test.metro.MetroSimulatorTest
```

---

## 7. Sample Input and Output

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

## 8. Dataset Format

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

## 9. Simulation Assumptions vs Real DMRC Data

To maintain academic clarity:

| Aspect | Status | Details |
| :--- | :--- | :--- |
| **Stations & Lines** | Realistic | Names and topological order match operational Delhi Metro lines. |
| **Interchange Connections** | Realistic | Connecting stations match real-world interchange junctions. |
| **Travel Times** | Approximation | Average 2–3 minutes between adjacent stations. |
| **Train Headways** | Simulated | 4 mins for peak hours, 8 mins for off-peak hours. |
| **Interchange Walk Time** | Simulated | Fixed at 5 minutes per platform transfer. |
| **Live Train Tracking** | Simulated | Periodic headway model based on the user's entered time. |

---

## 10. Limitations & Future Improvements

### Current Limitations
- Does not connect to live DMRC real-time GPS feeds or dynamic delay APIs.
- Interchange walking times are modeled as a uniform 5 minutes (some physical stations have longer walkways than others).
- Fares are not calculated in the current version.

### Potential Future Improvements
- Fare calculation based on distance/station slabs.
- Station platform gate / exit directions (Gate 1, Gate 2).
- First/last train cutoff alerts late at night.
