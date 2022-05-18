package edu.brown.cs.termproject.mapdash.maps;


import edu.brown.cs.termproject.mapdash.graph.Edge;

/**
 ** The Way class models an edge object for our graph. It implements Edge so it can be used in
 * the Dijkstra class.
 */
public class Way implements Edge<MapNode, Way> {

  private String id;
  private MapNode start;
  private MapNode end;
  private double distance;

  /**
   ** Constructor for the Way class. It takes in an id, start MapNode, and end MapNode. It sets
   * all of these fields and also sets the distance field equal to the Haversine distance between
   * the two MapNodes.
   * @param id - the id of the Way
   * @param start - the start MapNode of the Way
   * @param end - the end MapNode of the Way
   */
  public Way(String id, MapNode start, MapNode end) {
    this.id = id;
    this.start = start;
    this.end = end;
    this.distance = start.getDistanceFromVertex(end);
  }

  /**
   ** Getter for the id variable.
   * @return id String
   */
  public String getId() {
    return this.id;
  }

  @Override
  /**
   ** Getter for the start vertex, in this case it's a MapNode.
   * @return the start MapNode
   */
  public MapNode getStart() {
    return this.start;
  }

  @Override
  /**
   ** Getter for the end vertex, in this case it's a MapNode.
   * @return the end MapNode
   */
  public MapNode getEnd() {
    return this.end;
  }

  @Override
  /**
   ** Getter for the weight of the edge, in this case it's the Haversine distance between the
   * start and end MapNodes.
   * @return the Haversine distance between the start and end MapNodes.
   */
  public double getWeight() {
    return this.distance;
  }
}
