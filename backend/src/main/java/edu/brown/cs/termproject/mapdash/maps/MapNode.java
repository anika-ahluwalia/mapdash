package edu.brown.cs.termproject.mapdash.maps;

import edu.brown.cs.termproject.mapdash.graph.Vertex;
import edu.brown.cs.termproject.mapdash.stars.CoordinateObject;
import edu.brown.cs.termproject.mapdash.graph.*;
import edu.brown.cs.termproject.mapdash.stars.*;
//TODO : fix these imports

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 ** The MapNode class models a node/vertex object for our KDTree and graph. It implements
 * CoordinateObject so it can be used in KDTree and Vertex so that it can be used in Dijkstra.
 */
public class MapNode implements CoordinateObject, Vertex<MapNode, Way> {
  private String id;
  private List<Double> coords;
  private HashMap<String, Way> ways;
  // Radius of Earth in km, for Haversine distance
  private final double r = 6371;

  /**
   ** Constructor for the MapNode class. It takes in an id, latitude, and longitude. It then creates
   * a coordinates List using the latitude and longitude and initializes a HashMap to store all
   * Ways that this node is a part of. The key of the HashMap is the ID of the other node in the
   * Way and the value is the Way itself.
   * @param id - the id of the MapNode
   * @param latitude - a double representing the latitude of the MapNode
   * @param longitude - a double representing the longitude of the MapNode
   */
  public MapNode(String id, double latitude, double longitude) {
    this.id = id;
    this.coords = new ArrayList<>();
    this.coords.add(latitude);
    this.coords.add(longitude);
    this.ways = new HashMap<>();
  }

  /**
   ** Getter for the list of coordinates.
   * @return List of coordinates containing the latitude and longitude
   */
  public List<Double> getCoords() {
    return new ArrayList<>(this.coords);
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
   ** Getter for the HashMap of edges that this MapNode is a part of. In this case, the edges are
   * Ways.
   * @return Collection of Ways, which are the values in the ways HashMap.
   */
  public Collection<Way> getEdges() {
    return this.ways.values();
  }

  /**
   ** Helper method to add a new Way to the HashMap of ways.
   * @param endId - a String representing the ID of the end node in this Way
   * @param way - the Way object itself.
   */
  public void addWay(String endId, Way way) {
    ways.put(endId, way);
  }

  @Override
  /**
   * Calculates Haversine distance from this node to another. Used for heuristic in A* search.
   * @param node - the other node to calculate the distance from.
   * @return Haversine distance between this node and the other node.
   */
  public double getDistanceFromVertex(MapNode node) {
    List<Double> otherCoords = node.getCoords();
    double delLat = Math.toRadians(this.coords.get(0) - otherCoords.get(0));
    double delLon = Math.toRadians(this.coords.get(1) - otherCoords.get(1));

    double dist = Math.pow(Math.sin(delLat / 2), 2)
        + Math.pow(Math.sin(delLon / 2), 2)
        * Math.cos(Math.toRadians(this.coords.get(0)))
        * Math.cos(Math.toRadians(otherCoords.get(0)));
    return 2 * r * Math.asin(Math.sqrt(dist));
  }

  @Override
  /**
   * Method that is required by the CoordinateObject interface.
   * Calculates the Euclidean distance between two pairs of latitude and longitude coordinates.
   * These distances are used when constructing the KDTree and when setting the weights/distances
   * of Ways for the graph.
   * @param otherCoords - a List containing the latitude and longitude of a different MapNode
   * @return a double representing the Euclidean distance between two pairs of latitude and
   * longitude coordinates.
   */
  public double calculateDistanceApart(List<Double> otherCoords) {
    double squaredDist = 0;
    for (int i = 0; i < 2; i++) {
      double delta = this.coords.get(i) - otherCoords.get(i);
      squaredDist += delta * delta;
    }
    return squaredDist;
  }

  @Override
  /**
   * We override equals() for MapNode since the Vertex interface requires us to do so to allow for
   * easier comparison in the Dijkstra class.
   * @param o - the MapNode to compare if it is equal with this MapNode
   * @return boolean representing whether a MapNode is equal to another MapNode or not
   */
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    } else if (!(o instanceof MapNode)) {
      return false;
    } else {
      String otherId = ((MapNode) o).getId();
      return otherId.equals(this.id);
    }
  }

  @Override
  /**
   * We override hashCode() using the MapNode ID because we overrode equals() for the Vertex
   * interface.
   * @return the hashcode of a MapNode
   */
  public int hashCode() {
    return Objects.hash(this.id);
  }
}
