package edu.brown.cs.termproject.mapdash.maps;


import edu.brown.cs.termproject.mapdash.graph.Edge;
import edu.brown.cs.termproject.mapdash.graph.Vertex;
import edu.brown.cs.termproject.mapdash.graph.VertexComparator;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 ** The Dijkstra class is a generic implementation of Dijkstra's algorithm that uses the Vertex
 * and Edge interfaces.
 * @param <V> - the generic type for a vertex that must extend the Vertex interface
 * @param <E> - the generic type for an edge that must extend the Edge interface
 */
public class Dijkstra<V extends Vertex<V, E>, E extends Edge<V, E>> {

  private Set<V> visited;
  private Map<V, Double> distances;
  // map from child to edge between parent
  private Map<V, E> parents;
  private LazyDijkstraReader<V, E> reader;

  /**
   ** Constructor for the Dijkstra class. It initializes a Set for all visited vertices, a HashMap
   * between vertices and their shortest distances from the start, and another HashMap between
   * vertices and the edge between them and their parent along the shortest path. It also takes in
   * an instance of a LazyDijkstraReader, which serves as a proxy class for querying a database
   * to dynamically build a graph while finding the shortest path.
   * @param reader read
   */
  public Dijkstra(LazyDijkstraReader<V, E> reader) {
    this.visited = new HashSet<>();
    this.distances = new HashMap<>();
    this.parents = new HashMap<>();
    this.reader = reader;
  }

  /**
   ** Main method that implements Dijkstra's algorithm (modified to be A*).
   * Takes in a start and end vertex and runs A* using Java's PriorityQueue. A* is implemented by
   * calculating a heuristic of the Haversine distance between the current vertex and the end vertex
   * and adding this heuristic to the total distance for the current vertex.
   * @param start - a vertex representing the start of the graph
   * @param end - a vertex representing the end of the graph
   */
  private void findShortestPath(V start, V end) {
    // Clear all fields
    distances.clear();
    parents.clear();
    visited.clear();

    // Get first set of edges between start vertex and its neighbors
    Collection<E> startNeighbors = reader.getLazyEdges(start);
    // Initialize start vertex's distances as Haversine distance from start to end
    distances.put(start, start.getDistanceFromVertex(end));
    // Initialize a new PriorityQueue with a VertexComparator, which compares the distances
    // between two vertices. The VertexComparator takes in the distances HashMap for comparisons.
    PriorityQueue<V> pq = new
        PriorityQueue<V>(new VertexComparator<>(distances));

    // Initialize priority queue with start vertex
    pq.add(start);
    // Add all neighboring vertices to priority queue
    for (E edge: startNeighbors) {
      pq.add(edge.getEnd());
    }

    while (!pq.isEmpty()) {
      // Look at the shortest path currently
      V next = pq.poll();
      // If we hit the end vertex, don't iterate on it
      if (next.equals(end)) {
        break;
      }
      // Check that the vertex hasn't been visited
      if (!visited.contains(next)) {
        Collection<E> nextNeighbors = reader.getLazyEdges(next);
        // Cycle through the neighboring vertices
        for (E edge: nextNeighbors) {
          // Within this loop, update the distances map and add unvisited neighbor vertices to
          // priority queue

          // Get the current neighbor from the edge we are iterating on
          V currNeighbor = edge.getEnd();
          Double newDist = edge.getWeight() + distances.get(next);
          // Old distance is either the distance already stored in distances or null
          Double oldDist = distances.getOrDefault(currNeighbor, null);
          // If there is no distance or new distance is shorter, update distances and parents
          if (oldDist == null || (newDist.compareTo(oldDist) < 0)) {
            // Add Haversine distance heuristic for A*
            distances.put(currNeighbor, newDist + currNeighbor.getDistanceFromVertex(end));
            parents.put(currNeighbor, edge);
            pq.add(currNeighbor);
          }
        }
        visited.add(next);
      }
    }
  }

  /**
   ** Method that uses the parents HashMap to build a list of edges representing the shortest
   * path from start to end.
   * @param start - a vertex representing the start of the graph
   * @param end - a vertex representing the end of the graph
   * @return a list of edges representing the shortest path, an empty list if the path is between
   * a vertex and itself, or null if no shortest path was found
   */
  public List<E> getShortestPath(V start, V end) {
    // Calls findShortestPath to run Dijkstra's and populate parents HashMap
    findShortestPath(start, end);

    List<E> path = new LinkedList<>();
    // If the path is between a vertex and itself, return an empty list of edges
    if (start.equals(end)) {
      return path;
    } else if (parents.getOrDefault(end, null) == null) {
      // The end vertex is not in the map, meaning there was no path found
      return null;
    } else {

      // Add the last edge
      E endEdge = parents.get(end);
      path.add(endEdge);
      // Get the parent of the end vertex
      V currParent = endEdge.getStart();
      // Build list of edges until we reach start
      while (!currParent.equals(start)) {
        E currEdge = parents.get(currParent);
        if (currEdge == null) {
          path.clear();
          return null;
        }
        // Insert every new edge at beginning of list to maintain forward order despite backtracking
        path.add(0, currEdge);
        currParent = currEdge.getStart();
      }
      return path;
    }
  }

}
