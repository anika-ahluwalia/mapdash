package edu.brown.cs.termproject.mapdash.graph;

/**
 ** Interface that requires three getter methods for an edge's weight, start vertex,
 * and end vertex.
 * @param <V> - the generic type for a vertex that must extend the Vertex interface
 * @param <E> - the generic type for an edge that must extend the Edge interface
 */
public interface Edge<V extends Vertex<V, E>, E extends Edge<V, E>> {
  /**
   ** The getWeight() method serves as a getter method for an Edge's weight.
   * @return a double representing the weight of an Edge
   */
  double getWeight();
  /**
   ** The getStart() method serves as a getter method for an Edge's start Vertex.
   * @return the Edge's start Vertex
   */
  V getStart();
  /**
   ** The getEnd() method serves as a getter method for an Edge's end Vertex.
   * @return the Edge's end Vertex
   */
  V getEnd();
}
