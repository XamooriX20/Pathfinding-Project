# Project Formulation

## Overview:
In this project, I implemented a Breadth-First Search pathfinding algorithm for self-driving delivery vehicles. The self-driving vehicle will need to create a path on a 2D-grid that contains a starting point (x, y), a delivery point (x, y), and a number of obstacles. The vehicle can navigate to any of the adjacent squares (even diagonally), as long as the squares are inbound and do not contain an obstacle.

## General notes:
The used language here is Java.

## 1st development phase:
- Implement a 10x10 grid that contains a starting point on (0, 0), the delivery point on (9, 9), and the following obstacles on locations (7, 7), (7, 8), (8, 7), (9, 7).
- The algorithm should calculate a valid path avoiding the obstacles and reaching the delivery point.
- The solution should print the path in the format of `[(x1, y1), (x2, y2)… . .]`.

## 2nd development phase:
- Add an additional 20 randomly placed obstacles and print their location using the format `[(x1, y1), (x2, y2)… . ]`.
- The obstacles should not overlap existing ones and should not be placed at the start and delivery points.
- The algorithm should calculate a valid path avoiding the obstacles and reaching the delivery point.
- The solution should print the path in the format of `[(x1, y1), (x2, y2)… . .]`.

## 3rd development phase:
- In the event that the vehicle is unable to reach its destination, the algorithm should print an appropriate message and identify which obstacles to be removed in order for the vehicle to reach its destination.
- The algorithm should suggest the least number of obstacles using the format `[(x1, y1), (x2, y2)… . ]` in order for the vehicle to reach its destination.

## Extra:
- In the case where a path could not be found initially, the algorithm should identify which obstacles to be removed in order for the vehicle to reach its destination in the least number of steps (while ensuring it is also the least number of obstacles).
- If the algorithm finds the least number of obstacles to be removed in order for the vehicle to find a valid path, the algorithm will keep searching for other valid obstacles (least number) to be removed in order for the vehicle to reach its destination in fewer steps, before suggesting the obstacle(s).