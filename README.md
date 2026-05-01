# Graph Algorithms Visualizer

A Java Swing desktop app for building weighted graphs and stepping through four classic graph algorithms in real time.

---

## How to run

**Requirements:** JDK 25, Maven

```bash
# Clone the repo
git clone <repo-url>
cd Algorithms-Visualizer

# Build and run
mvn clean compile
mvn exec:java -Dexec.mainClass="visualizer.ApplicationRunner"
```

---

## Algorithms

Select an algorithm from the **Algorithms** menu. The graph switches to read-only mode, then asks you to click a starting vertex.

| Algorithm                | What it shows                                         |
|--------------------------|-------------------------------------------------------|
| **Depth-First Search**   | Traversal order, e.g. `DFS : A -> B -> D -> C`        |
| **Breadth-First Search** | Traversal order, e.g. `BFS : A -> B -> C -> D`        |
| **Dijkstra's Algorithm** | Shortest distances from source, e.g. `A=0, B=5, C=10` |
| **Prim's Algorithm**     | Minimum spanning tree edges, e.g. `B=A, C=B, D=C`     |

Steps are animated at 500 ms intervals. Visited vertices and edges are selected as the algorithm processes them. **Selected elements are highlighted white.** The final result appears in the status bar at the bottom.

---

## Using the app

1. Start in *Add a Vertex* mode. Click the canvas to place vertices, each with a single-character label. Labels must be unique. Switch to *Add an Edge*, click two vertices, and enter a weight to connect them.

2. Open the Algorithms menu and pick one. Click the starting vertex when prompted. Watch the traversal animate, then read the result in the status bar.

3. Use *Remove a Vertex* or *Remove an Edge* to delete elements. The verteces/edges selected are deleted once they are selected. **Make sure you unselect the elements you don't want to delete before choosing any deletion mode.** Use **File → New** to wipe the canvas and start over.

---

## Controls

| Action        | How                                                       |
|---------------|-----------------------------------------------------------|
| Create vertex | Mode: *Add a Vertex* → click canvas                       |
| Create edge   | Mode: *Add an Edge* → select (click) two vertices         |
| Delete vertex | Mode: *Remove a Vertex* → select (click) vertex           |
| Delete edge   | Mode: *Remove an Edge* → select (click) edge              |
| Run algorithm | Algorithms menu → select → select (click) starting vertex |
| Reset graph   | File → New                                                |
| Exit          | File → Exit                                               |

---

## Project structure

```
src/main/java/visualizer/
├── ApplicationRunner.java        # Entry point
├── gui/
│   ├── MainFrame.java            # Window and menus
│   ├── Graph.java                # Canvas and mode logic
│   ├── Vertex.java               # Vertex component
│   ├── Edge.java                 # Edge component
│   └── AppColors.java            # Color constants
└── logic/
    ├── AlgorithmsManager.java    # Algorithm orchestration
    ├── GraphManager.java         # Graph state (thread-safe)
    └── workers/
        ├── DepthFirstTraverser.java
        ├── BreadthFirstTraverser.java
        ├── DijkstraTraverser.java
        ├── PrimTraverser.java
        ├── VertexCreator.java
        ├── EdgeCreator.java
        ├── VertexEraser.java
        └── EdgeEraser.java
```
