import org.apache.commons.lang3.ArrayUtils;

import java.util.*;

public class Pathfinding {
    private static final int ROWS = 10;
    private static final int COLS = 10;
    private static final int NUMBER_OF_OBSTACLES = 20;

    // Check if a point is walkable (within grid bounds and not an obstacle)
    private static boolean isWalkable(int[][] grid, Point point) {
        if (point.y < 0 || point.y > grid.length - 1) return false; // Check row bounds
        if (point.x < 0 || point.x > grid[0].length - 1) return false; // Check column bounds
        return grid[point.y][point.x] == 0; // Return true if the cell is not an obstacle
    }

    // Find all valid (walkable) neighbours of a given point
    private static List<Point> findNeighbours(int[][] grid, Point point) {
        List<Point> neighbours = new ArrayList<>();

        // Possible movement directions (including diagonals)
        int[][] directions = {
                {1, 1},   // down-right
                {0, 1},   // down
                {1, 0},   // right
                {-1, 1},  // down-left
                {1, -1},  // up-right
                {-1, 0},  // left
                {0, -1},  // up
                {-1, -1}  // up-left
        };

        // Check each direction and add valid (walkable) neighbours
        for (int[] dir : directions) {
            Point neighbor = point.offset(dir[0], dir[1]);
            if (isWalkable(grid, neighbor)) neighbours.add(neighbor);
        }

        return neighbours;
    }

    // Perform a breadth-first search to find a path from start to end
    private static List<Point> findPath(int[][] grid, Point start, Point end) {
        List<Point> visited = new ArrayList<>();
        visited.add(start);

        List<Point> frontier = new ArrayList<>();
        frontier.add(start);

        // Map to keep track of predecessors for path reconstruction
        Map<Point, Point> predecessors = new HashMap<>();
        predecessors.put(start, null);

        while (!frontier.isEmpty()) {
            List<Point> nextFrontier = new ArrayList<>();

            for (Point point : frontier) {
                for (Point neighbor : findNeighbours(grid, point)) {
                    if (!visited.contains(neighbor) && !nextFrontier.contains(neighbor)) {
                        visited.add(neighbor);
                        nextFrontier.add(neighbor);
                        predecessors.put(neighbor, point);

                        // If the end point is reached, return the constructed path
                        if (neighbor.equals(end)) return buildPath(predecessors, end);
                    }
                }
            }

            // Move to the next frontier (next layer of BFS)
            frontier.clear();
            frontier.addAll(nextFrontier);
        }

        return null; // No path found
    }

    // Recursively build the path by following the predecessors map
    private static List<Point> buildPath(Map<Point, Point> predecessors, Point current) {
        List<Point> path = new ArrayList<>();

        // Base case: if the current point is null, return an empty list
        if (current == null) return path;

        // Recursively build the path from the predecessors
        path.addAll(buildPath(predecessors, predecessors.get(current)));
        path.add(current);

        return path;
    }

    // Find the minimal set of obstacles to remove to create a valid path
    private static List<Point> findObstaclesToRemove(int[][] grid, Point start, Point end, Point[] obstacles, int k) {
        if (k > obstacles.length) return null;  // No valid set of obstacles to remove found

        List<List<Point>> combinations = new ArrayList<>();
        generateCombinations(obstacles, new ArrayList<>(), 0, k, combinations);

        PathResult result = new PathResult(null, null);

        for (List<Point> combination : combinations) {
            // Temporarily remove the obstacles in the current combination
            for (Point obstacle : combination) grid[obstacle.y][obstacle.x] = 0;

            // Check if a valid path exists after removing the obstacles
            List<Point> path = findPath(grid, start, end);
            if (path != null && (result.shortestPath == null || path.size() < result.shortestPath.size())) {
                result.shortestPath = path;
                result.bestCombination = new ArrayList<>(combination);
            }

            // Re-add the obstacles after checking
            for (Point obstacle : combination) grid[obstacle.y][obstacle.x] = 1;
        }

        // If a valid combination was found with the shortest path, return it
        if (result.bestCombination != null) return result.bestCombination;

        // Recurse with the next size of combination (increase the number of obstacles to remove)
        return findObstaclesToRemove(grid, start, end, obstacles, k + 1);
    }

    // Generate all combinations of obstacles of size k
    private static void generateCombinations
            (Point[] obstacles, List<Point> current, int index, int k, List<List<Point>> combinations) {
        if (current.size() == k) {
            combinations.add(new ArrayList<>(current)); // Add the current combination
            return;
        }

        // Base case: end of array reached
        if (index == obstacles.length) return;

        // Include the current obstacle and move to the next
        current.add(obstacles[index]);
        generateCombinations(obstacles, current, index + 1, k, combinations);

        // Exclude the current obstacle and move to the next
        current.remove(current.size() - 1);
        generateCombinations(obstacles, current, index + 1, k, combinations);
    }

    // Recursively populate the grid with random obstacles
    private static void populateObstacles(int[][] grid, Point start, Point end, Point[] addedObstacles, int index) {
        // Base case: stop when the array is fully populated
        if (index == addedObstacles.length) return;

        Random random = new Random();
        int y = random.nextInt(grid.length);
        int x = random.nextInt(grid[0].length);

        // Ensure the random point is not already an obstacle and is not the start or end point
        if (grid[y][x] == 0 && !(x == start.x && y == start.y) && !(x == end.x && y == end.y)) {
            grid[y][x] = 1; // Mark the cell as an obstacle
            addedObstacles[index] = new Point(x, y, null); // Store the obstacle point in the array
            populateObstacles(grid, start, end, addedObstacles, index + 1); // Recurse to the next index
        } else populateObstacles(grid, start, end, addedObstacles, index); // Retry the same index
    }

    public static void main(String[] args) {
        int[][] grid = new int[ROWS][COLS];
        for (int[] row : grid) Arrays.fill(row, 0); // Initialise grid with 0 (walkable cells)

        Point start = new Point(0, 0, null); // Start point at (0, 0)
        Point end = new Point(9, 9, null);   // End point at (9, 9)

        Point[] initialObstacles = new Point[4];

        // Populate the grid and fill the array with some predefined obstacles
        // Each obstacle is stored with its corresponding index in each row for ease,
        // where each row's numbers represent the x-coordinate, the y-coordinate, and
        // the index (to be used when filling the array) respectively.
        int[][] indexedObstacles = {{7, 7, 0}, {7, 8, 1}, {8, 7, 2}, {9, 7, 3}};
        for (int[] obstacle : indexedObstacles) {
            grid[obstacle[1]][obstacle[0]] = 1; // Mark obstacle in grid
            initialObstacles[obstacle[2]] = new Point(obstacle[0], obstacle[1], null); // Store in array
        }

        // Array to hold randomly added obstacles
        Point[] addedObstacles = new Point[NUMBER_OF_OBSTACLES];

        // Populate the grid and fill the array with random obstacles using recursion
        populateObstacles(grid, start, end, addedObstacles, 0);

        System.out.println("Location of the 20 randomly placed obstacles: \n" + Arrays.toString(addedObstacles) + "\n");

        // Combine initial and added obstacles into a single array
        Point[] obstacles = ArrayUtils.addAll(initialObstacles, addedObstacles);

        // Print the grid
        for (int[] rows : grid) {
            for (int columns : rows) System.out.print(columns + " ");
            System.out.println();
        }

        // Attempt to find a direct path
        List<Point> initialPath = findPath(grid, start, end);
        if (initialPath != null) System.out.println("Path found: " + initialPath);
        else {
            System.out.println("No path found initially. Attempting to find obstacles to remove...");

            // Attempt to find the minimal obstacles to remove
            List<Point> obstaclesToRemove = findObstaclesToRemove(grid, start, end, obstacles, 1);
            if (obstaclesToRemove != null) {
                System.out.println("Obstacles to remove: " + obstaclesToRemove);

                // Remove the identified obstacles and find the path again
                for (Point obstacle : obstaclesToRemove) grid[obstacle.y][obstacle.x] = 0;
                List<Point> finalPath = findPath(grid, start, end);
                if (finalPath != null) System.out.println("Path after removing obstacles: " + finalPath);
                else System.out.println("Still no path found after removing obstacles.");
            } else System.out.println("No valid path could be created, even by removing obstacles.");
        }
    }

    // Point class to represent a cell in the grid
    private static class Point {
        public int x;          // x-coordinate (column index)
        public int y;          // y-coordinate (row index)
        public Point previous; // Pointer to the previous point in the path

        public Point(int x, int y, Point previous) {
            this.x = x;
            this.y = y;
            this.previous = previous;
        }

        @Override
        public String toString() {
            return String.format("(%d, %d)", x, y);
        }

        @Override
        public boolean equals(Object o) {
            if (o == null) return false;
            if (this.getClass() != o.getClass()) return false;

            Point point = (Point) o;
            return x == point.x && y == point.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

        // Create a new point offset from the current point
        public Point offset(int ox, int oy) {
            return new Point(x + ox, y + oy, this);
        }
    }

    // Helper class to store the result of pathfinding
    private static class PathResult {
        List<Point> shortestPath;    // The shortest path found
        List<Point> bestCombination; // The best combination of obstacles to remove

        PathResult(List<Point> shortestPath, List<Point> bestCombination) {
            this.shortestPath = shortestPath;
            this.bestCombination = bestCombination;
        }
    }
}