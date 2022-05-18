package edu.brown.cs.termproject.mapdash.maps;

import edu.brown.cs.termproject.mapdash.graph.Edge;
import edu.brown.cs.termproject.mapdash.graph.Vertex;

import java.util.Collection;

/**
 * Proxy pattern interface to lazily load in edges of a graph.
 * @param <V> the generic type for a vertex that must extend the Vertex interface
 * @param <E> the generic type for an edge that must extend the Edge interface
 */
public interface LazyDijkstraReader<V extends Vertex<V, E>, E extends Edge<V, E>> {
  /**
   * Method to lazily retrieve the edges of a vertex.
   * @param vertex to get the edges from
   * @return a collection of vertex's edges
   */
  Collection<E> getLazyEdges(V vertex);

  /**
   * Method to set the connection to a database in which we can lazily load in from.
   * @param filename the database filename
   */
  void setConn(String filename);
}
