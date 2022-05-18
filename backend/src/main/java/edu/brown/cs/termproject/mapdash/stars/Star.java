package edu.brown.cs.termproject.mapdash.stars;

import java.util.ArrayList;
import java.util.List;

/**
 ** The Star class models a star object using the fields of star CSV data.
 */
public class Star implements CoordinateObject {

  private int id;
  private String name;
  private ArrayList<Double> coords;
  private double distance;

  /**
   ** Constructor for the Star class. Default value of distance is infinity so that when
   * a Star's distance isn't compared to itself, it's automatically sorted to the back of the
   * list of neighboring stars.
   * @param id - the id of the Star
   * @param name - the name of the Star
   * @param coords - a list of coordinates for the Star
   */
  public Star(int id, String name, List<Double> coords) {
    this.id = id;
    this.name = name;
    this.coords = new ArrayList<>(coords);
    this.distance = Double.POSITIVE_INFINITY;
  }

  /**
   ** Getter for the id variable.
   * @return id variable
   */
  public int getID() {
    return id;
  }

  /**
   ** Getter for the name variable.
   * @return name variable
   */
  public String getName() {
    return name;
  }

  /**
   ** Getter for the distance variable.
   * @return distance variable
   */
  public double getDistance() {
    return distance;
  }

  /**
   ** Setter for the firstName variable.
   * @param distance - a new distance value to be set
   */
  public void setDistance(double distance) {
    this.distance = distance;
  }

  /**
   * Star's implementation of squared euclidean distance for kd tree.
   * @param target the coordinates to calculate distance from
   * @return the distance
   */
  @Override
  public double calculateDistanceApart(List<Double> target) {
    double squaredDist = 0;
    for (int i = 0; i < 3; i++) {
      double delta = coords.get(i) - target.get(i);
      squaredDist += delta * delta;
    }
    return squaredDist;
  }

  /**
   ** The Star class's implementation of the getCoords() method that it must
   * implement as a CoordinateObject.
   * @return the list of coordinates
   */
  @Override
  public List<Double> getCoords() {
    return coords;
  }
}
