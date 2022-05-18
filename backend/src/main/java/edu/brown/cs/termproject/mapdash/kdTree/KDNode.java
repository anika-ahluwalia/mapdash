package edu.brown.cs.termproject.mapdash.kdTree;

import edu.brown.cs.termproject.mapdash.stars.CoordinateObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to represent the node of a kd tree.
 * @param <T> Generic type that must have some type of coordinates doubles array
 */
public class KDNode<T extends CoordinateObject> {
  private KDNode<T> left;
  private KDNode<T> right;
  private final T nodeObject;
  private final List<Double> coords;

  /**
   * KDNode constructor.
   * @param nodeObject the object to construct a node with
   */
  public KDNode(T nodeObject) {
    this.nodeObject = nodeObject;
    this.coords = new ArrayList<>(nodeObject.getCoords());
    this.left = null;
    this.right = null;
  }

  /**
   * left child accessor.
   * @return the left child
   */
  public KDNode<T> getLeft() {
    return left;
  }

  /**
   * right child accessor.
   * @return the right child
   */
  public KDNode<T> getRight() {
    return right;
  }

  /**
   * coordinates accessor.
   * @return the node's coordinates
   */
  public List<Double> getCoordinates() {
    return new ArrayList<>(coords);
  }

  /**
   * the actual object within the node accessor.
   * @return the node object
   */
  public T getData() {
    return nodeObject;
  }

  /**
   * Left child modifier.
   * @param left node to set the left child as
   */
  public void setLeft(KDNode<T> left) {
    this.left = left;
  }

  /**
   * Right child modifier.
   * @param right node to set the right child as
   */
  public void setRight(KDNode<T> right) {
    this.right = right;
  }

  /**
   * Finds the distance between coordinates and the node object as defined by the
   * node object.
   * @param otherCoords the coordinates to find distance from
   * @return double representing distance, returns a -1 if an error
   */
  public double calculateDistanceApart(List<Double> otherCoords) {
    // check if the coordinate dimensions match
    if (nodeObject.getCoords().size() == otherCoords.size()) {
      return nodeObject.calculateDistanceApart(otherCoords);
    } else {
      System.out.println("ERROR: Invalid dimensions");
      return -1;
    }
  }
}
