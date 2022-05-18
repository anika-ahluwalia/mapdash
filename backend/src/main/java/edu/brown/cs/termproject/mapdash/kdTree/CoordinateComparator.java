package edu.brown.cs.termproject.mapdash.kdTree;

import edu.brown.cs.termproject.mapdash.stars.CoordinateObject;

import java.util.Comparator;

/**
 * Class to compare CoordinateObjects.
 */
public class CoordinateComparator implements Comparator<CoordinateObject> {
  private int coordPlane;

  /**
   * Constructor for a comparator of one index in a coordinate array.
   * @param coordPlane which index to compare
   */
  public CoordinateComparator(int coordPlane) {
    this.coordPlane = coordPlane;
  }

  /**
   * Compares two coordinate objects by one of the elements in their coordinates.
   * @param a the first coordinate object
   * @param b the second coordinate object
   * @return a positive if a is greater than, 0 if equal, negative if less
   */
  public int compare(CoordinateObject a, CoordinateObject b) {
    if (a.getCoords().get(coordPlane) < b.getCoords().get(coordPlane)) {
      return -1;
    } else if (a.getCoords().get(coordPlane) > b.getCoords().get(coordPlane)) {
      return 1;
    } else {
      return 0;
    }
  }
}
