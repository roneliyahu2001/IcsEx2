Ex2 – Basic Object-Oriented Programming & 2D Maze Algorithms

Course: Introduction to Computer Science
Institution: Ariel University, School of Computer Science
Year: 2026

Overview

This project implements a 2D map (maze / raster image) using Object-Oriented Programming in Java.
The map is represented as a 2D integer matrix and supports drawing operations and graph algorithms based on Breadth-First Search (BFS).

The main goals of the assignment are:
	•	Practice OOP design and interfaces
	•	Work with 2D arrays
	•	Implement BFS-based algorithms
	•	Write proper JUnit tests
	•	Build a simple GUI using StdDraw

⸻

Project Structure
	•	Pixel2D.java
Interface representing a 2D coordinate.
	•	Index2D.java
Concrete implementation of Pixel2D.
	•	Map2D.java
Interface defining all required map operations and algorithms.
	•	Map.java
Implementation of Map2D.
Includes drawing functions, flood fill, shortest path, and distance map algorithms.
	•	Ex2_GUI.java
Graphical User Interface for interacting with the map using StdDraw.
	•	Index2DTest.java
JUnit tests for the Index2D class.
	•	MapTest.java
JUnit tests for the Map class and all core functionalities.

⸻

Implemented Features

Basic Map Operations
	•	Initialize map with given size and value
	•	Initialize map from a 2D array (deep copy)
	•	Get and set pixel values
	•	Check bounds (isInside)
	•	Compare map dimensions
	•	Map addition and scalar multiplication
	•	Rescaling the map

Drawing Algorithms
	•	Draw a single point
	•	Draw a line between two points
	•	Draw a rectangle
	•	Draw a circle

BFS-Based Algorithms
	•	Flood Fill
Fills a connected component starting from a given pixel.
	•	Shortest Path
Finds the shortest path between two pixels while avoiding obstacles.
	•	All Distances
Computes the shortest distance from a source pixel to all other reachable pixels.

Both cyclic and non-cyclic maps are supported.

⸻

Graphical User Interface (GUI)

The GUI allows:
	•	Drawing on the map using mouse clicks
	•	Choosing colors
	•	Drawing shapes (point, line, rectangle, circle)
	•	Running algorithms (fill, shortest path)
	•	Loading and saving maps from/to text files

The GUI is implemented using StdDraw.
Example of shortest path implemented in GUI:
![DED8CB18-0A01-4AD3-8037-69CB6123E592](https://github.com/user-attachments/assets/cf63ec83-7b97-4394-90a6-ca696499ed91)
The blue represent the shortest path and black represent obsticles.

⸻

Testing

JUnit tests were written for:
	•	Index2D
	•	Map core methods
	•	BFS algorithms
	•	Edge cases (bounds, obstacles, deep copy, etc.)

All tests pass successfully.

⸻

How to Run
	1.	Open the project in IntelliJ IDEA.
	2.	Make sure StdDraw.java is included in the project.
	3.	Run Ex2_GUI.java to launch the GUI.
	4.	Run MapTest and Index2DTest to execute unit tests.

⸻

Notes
	•	The interfaces Map2D and Pixel2D were not modified.
	•	The implementation follows the assignment instructions.
	•	Code was written and tested independently.
