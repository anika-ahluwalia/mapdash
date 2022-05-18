package edu.brown.cs.termproject.mapdash.graph;

import java.util.Comparator;
import java.util.Map;

/**
 ** The VertexComparator class implements Comparator and compares Vertex objects
 * based on their distances. When used in the Dijkstra class, the distances are the
 * shortest distance between a Vertex and the start.
 * @param <V> - the generic type for a vertex that must extend the Vertex interface
 * @param <E> - the generic type for an edge that must extend the Edge interface
 */
public class VertexComparator<V extends Vertex<V, E>, E extends Edge<V, E>>
    implements Comparator<V> {
  private Map<V, Double> distances;

  /**
   ** Constructor for the VertexComparator class.
   * @param distances - a Map with a Vertex as the key and its distance as the value.
   */
  public VertexComparator(Map<V, Double> distances) {
    this.distances = distances;
  }

  @Override
  /**
   ** VertexComparator's implementation of the compare method. It compares the distances of two
   * Vertex objects. It uses the Map of distances to get these distances or chooses infinity if
   * there is no distance for the chosen vertex.
   * @param v1 - the first Vertex to be compared
   * @param v2 - the second Vertex to be compared
   * @return int representing if v1 is greater than (1) less than (-1), or equal to (0) v2.
   */
  public int compare(V v1, V v2) {
    if (distances.getOrDefault(v1, Double.POSITIVE_INFINITY)
        < distances.getOrDefault(v2, Double.POSITIVE_INFINITY)) {
      return -1;
    } else if (distances.getOrDefault(v1, Double.POSITIVE_INFINITY)
        > distances.getOrDefault(v2, Double.POSITIVE_INFINITY)) {
      return 1;
    } else {
      return 0;
    }
  }
}
