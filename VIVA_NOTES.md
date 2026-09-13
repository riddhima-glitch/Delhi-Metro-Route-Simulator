# Delhi Metro Route & Schedule Simulator - Viva Preparation Notes

This document provides concise, clear, and technically accurate answers to expected viva questions regarding the architecture, algorithms, data structures, and design choices of the project.

---

### Q1. Why is the Delhi Metro network represented as a graph?
**Answer:**
A metro transit network is naturally a graph:
- **Vertices (Nodes)** represent physical metro stations (e.g., Rajiv Chowk, Hauz Khas).
- **Edges (Links)** represent rail tracks connecting consecutive stations.
- **Weights** on edges represent travel duration (in minutes).

Representing the system as an adjacency list graph (`Map<String, List<MetroEdge>>`) allows us to dynamically explore connections and find optimal paths without hardcoding specific routes or special cases.

---

### Q2. What is a Node and an Edge in this project?
**Answer:**
- **Node (`MetroStation`)**: Holds the station's name, the set of lines passing through it (`Set<String> lines`), and a boolean method `isInterchange()` (which returns true if `lines.size() > 1`).
- **Edge (`MetroEdge`)**: Represents a directed track between a `source` station and a `destination` station on a specific `lineName`, storing `travelTimeMinutes`. Because trains run in both directions, bidirectional edges are added for every pair of adjacent stations.

---

### Q3. Why are the edges weighted?
**Answer:**
Edges are weighted by travel time in minutes because the goal is to find the **fastest route (least total journey time)** rather than simply the route with the fewest station stops. For example, a route with fewer stops on a slower track or with an extra line transfer might take longer than a direct line with more stops.

---

### Q4. Why did you use Dijkstra's Algorithm instead of Breadth-First Search (BFS)?
**Answer:**
- **BFS (Breadth-First Search)** only finds the shortest path in an **unweighted graph** (i.e., minimum number of station hops). It assumes every edge has equal weight (1).
- **Dijkstra's Algorithm** is designed for **weighted graphs with non-negative edge weights**. It allows us to incorporate edge travel times, line-transfer interchange penalties (e.g., 5 minutes for walking between platforms), and station waiting times to minimize total travel duration.

---

### Q5. What is the time complexity of your Dijkstra implementation?
**Answer:**
Using an adjacency list and a Min-Heap (`java.util.PriorityQueue`):
- **Time Complexity**: $\mathcal{O}((V + E) \log V)$
  - Where $V$ is the number of station states (~240 stations) and $E$ is the number of rail track edges (~500 directed edges).
  - Each station is inserted and extracted from the priority queue in $\mathcal{O}(\log V)$ time.
  - Each edge is relaxed once.
- **Space Complexity**: $\mathcal{O}(V + E)$ to store the graph adjacency list, distance map, and priority queue elements.

---

### Q6. Why did you use `PriorityQueue` in Dijkstra's algorithm?
**Answer:**
Dijkstra's algorithm is a greedy algorithm that must always extract the unvisited node with the smallest cumulative cost.
- `PriorityQueue` implements a Min-Heap in Java, providing $\mathcal{O}(1)$ retrieval and $\mathcal{O}(\log V)$ removal (`poll()`) of the minimum cost node.
- Without a priority queue, finding the minimum distance node in an array would take $\mathcal{O}(V)$ time, slowing the overall algorithm to $\mathcal{O}(V^2)$.

---

### Q7. How does the program detect and penalize line interchanges?
**Answer:**
In `MetroRouteFinder.java`:
1. The Dijkstra state tracks `(station, currentLine, cumulativeCost)`.
2. When relaxing an edge from the current station to a neighbor station on `edgeLine`:
   - If `currentLine != null` and `!currentLine.equalsIgnoreCase(edgeLine)`, the algorithm detects a line transfer.
   - An interchange penalty of `SimulationConfig.INTERCHANGE_TIME` (5 minutes) is added to the edge cost.
3. During path reconstruction, whenever consecutive steps have different line names, an interchange callout is added to the itinerary.

---

### Q8. How is peak hour determined and how does it affect train frequency?
**Answer:**
In `ScheduleCalculator.java`:
- Morning Peak: `08:00` to `10:00` (`SimulationConfig.MORNING_PEAK_START` to `END`)
- Evening Peak: `17:00` to `19:00` (`SimulationConfig.EVENING_PEAK_START` to `END`)
- We use `LocalTime.isBefore()` and `!time.isBefore()` to check if the user's query time falls within these intervals.
- If in peak hours: train frequency is **4 minutes** (`PEAK_FREQUENCY`).
- If off-peak: train frequency is **8 minutes** (`OFF_PEAK_FREQUENCY`).

---

### Q9. How is expected waiting time calculated?
**Answer:**
Assuming trains depart on regular headway intervals from terminals:
$$\text{Minute Remainder} = \text{currentMinute} \pmod{\text{frequency}}$$
- If remainder is 0: train is departing now (wait time = 0 min).
- Otherwise: $\text{Wait Time} = \text{frequency} - \text{remainder}$.
- Example: At `09:30` during peak hours (frequency = 4 min): $30 \pmod 4 = 2$. Next train is at `09:32` (2 minutes wait).

---

### Q10. Why use Java's `java.time.LocalTime` instead of manual integer math?
**Answer:**
- `LocalTime` handles 24-hour clock wrapping, minute addition (`plusMinutes()`), time comparisons (`isBefore()`), and formatted parsing/printing cleanly with `DateTimeFormatter`.
- It eliminates bugs caused by manual hour/minute modulo arithmetic (e.g. crossing midnight or invalid inputs like `25:70`).

---

### Q11. Why use an external CSV file instead of hard-coding the data inside Java files?
**Answer:**
- **Separation of Concerns**: Data is kept separate from code logic.
- **Maintainability**: If a new station or line is opened, anyone can add a row in `metro_data.csv` without modifying or recompiling Java source files.
- **Scalability**: Allows loading networks of any size dynamically through `MetroDataLoader`.

---

### Q12. How does the program handle invalid station inputs and case variations?
**Answer:**
1. **Case-Insensitive Indexing**: Stations are registered in `MetroGraph` using lowercase keys in a `HashMap`.
2. **Partial Search / Suggestions**: In `MetroGraph.java` (`searchStations`), if an exact match is not found, a substring search is executed across all station names, presenting matching candidates to the user.
3. **Graceful Error Messages**: If no match exists, the user is notified with friendly suggestions instead of crashing.

---

### Q13. What happens if source and destination stations are identical?
**Answer:**
`MetroRouteFinder` checks if `sourceCanonical.equalsIgnoreCase(destCanonical)`. If so, it immediately returns a 0-duration `RouteResult` with 1 station step and 0 interchanges, without running Dijkstra unnecessarily.

---

### Q14. What design patterns or OOP principles did you apply?
**Answer:**
- **Single Responsibility Principle (SRP)**:
  - `MetroDataLoader` only handles file reading.
  - `MetroRouteFinder` only executes Dijkstra search.
  - `ScheduleCalculator` only handles time math.
  - `MetroApplication` handles CLI user interaction.
- **Encapsulation**: Class fields are private/final with getters, preventing external mutation.
- **Data Transfer Object (DTO)**: `RouteResult` and `RouteStep` bundle the computed route metrics for clean transfer to the UI.

---

### Q15. What are the limitations of this simulation?
**Answer:**
1. **Uniform Transfer Times**: Uses a standard 5-minute interchange walk time across all stations, though physical transfer distances vary between stations.
2. **Static Headway**: Simulates train arrivals using fixed periodic schedules rather than real-time GPS feeds from DMRC.
3. **No Dynamic Delay Tracking**: Does not account for sudden service delays or maintenance disruptions.

---

### Q16. How would you improve or expand this project in the future?
**Answer:**
1. **Fare Calculation**: Calculate fare based on DMRC distance/slab rules and smart card discounts.
2. **First / Last Train Schedules**: Add terminus departure and cutoff time validation.
3. **Wheelchair / Accessibility Flags**: Store lift and escalator accessibility per station.
4. **GUI / Web Dashboard**: Build a desktop GUI using JavaFX or a lightweight visual map.
