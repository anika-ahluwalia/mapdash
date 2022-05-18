package edu.brown.cs.termproject.mapdash.graph;

import java.util.Collection;

/**
 ** Interface that outlines a single method: getEdges(). This gets all edges associated with
 * a given Vertex.
 * @param <E> - the generic type for an edge that must extend the Edge interface
 * @param <V> - the generic type for a vertex that must extend the Vertex interface
 */
public interface Vertex<V extends Vertex<V, E>, E extends Edge<V, E>> {
  /**
   ** The getEdges() method serves as a getter method for a Vertex object. It returns a Collection
   * of Edges that a Vertex is associated with.
   * @return the Collection of Edges
   */
  Collection<E> getEdges();

  /**
   * Method that finds the distance between one vertex and this vertex.
   * @param vertex the vertex to calculate distance from.
   * @return distance as a double with whatever metric chosen.
   */
  double getDistanceFromVertex(V vertex);

  @Override
  /**
   ** We override equals for Vertex for easier Vertex comparison in the Dijkstra class.
   * @return a boolean representing if a Vertex is equal to another Vertex
   */
  boolean equals(Object o);
}
