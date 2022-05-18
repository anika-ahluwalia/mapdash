package edu.brown.cs.termproject.mapdash.stars;

import java.util.List;

/**
 ** Interface that outlines a single method: getCoords(). This method is intended to
 * allow objects to be generically modeled as anything that has a set of coordinates,
 * regardless of the dimensions.
 */
public interface CoordinateObject {

  /**
   ** The getCoords() method allows a CoordinateObject class to return their own list
   * of coordinates, regardless of the dimensions.
   * @return the list of coordinates
   */
  List<Double> getCoords();

  /**
   * Method that calculates some sort of distance metric to compare different
   * CoordinateObjects in order to enable kd tree queries.
   * @param target the coordinates to compare distance from
   * @return distance/weight metric
   */
  double calculateDistanceApart(List<Double> target);
}
