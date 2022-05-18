package edu.brown.cs.termproject.mapdash.kdTree;

import edu.brown.cs.termproject.mapdash.stars.CoordinateObject;

import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.Random;

/**
 * Class that is an implementation of a KD Tree.
 * @param <T> Generic type that must have some type of coordinates doubles array
 */
public class KDTree<T extends CoordinateObject> {
  private KDNode<T> root;
  private int dims;

  /**
   * KDTree constructor.
   */
  public KDTree() {
    root = null;
    dims = 0;
  }

  /**
   * Root accessor.
   * @return root node
   */
  public KDNode<T> getRoot() {
    return root;
  }

  /**
   * Dims accessor.
   * @return dimensions of the KDTree
   */
  public int getDims() {
    return dims;
  }

  /**
   * Creates a balanced kdtree given a list of T objects.
   * Everything greater than or equal goes to the right of the root.
   * Everything less goes to the left.
   * @param tList a list of objects to convert into a tree
   */
  public void createBalancedFromList(List<T> tList) {
    Comparator<CoordinateObject> c = new CoordinateComparator(0);
    List<T> safeCopy = new ArrayList<>(tList);
    root = setFromList(safeCopy, c, 0);
  }

  private KDNode<T> setFromList(List<T> tList, Comparator<CoordinateObject> c, int depth) {
    // sort the list by a coordinate plane
    Collections.sort(tList, c);
    int medianInd = (tList.size() - 1) / 2;
    if (!tList.isEmpty()) {
      int relevantAxis;
      dims = tList.get(0).getCoords().size();
      relevantAxis = depth % dims;

      while (medianInd > 0) {
        double coord1 = tList.get(medianInd).getCoords().get(relevantAxis);
        double coord2 = tList.get(medianInd - 1).getCoords().get(relevantAxis);
        boolean notLeast = coord1 == coord2;
        if (notLeast) {
          medianInd--;
        } else {
          break;
        }
      }
      T median = tList.get(medianInd);
      KDNode<T> newRoot = new KDNode<>(median);
      KDNode<T> leftSubtree;
      KDNode<T> rightSubtree;

      if (median != null) {
        // split the next recursive call's lists by the next coordinate plane
        c = new CoordinateComparator((depth + 1) % dims);

        leftSubtree = setFromList(tList.subList(0, medianInd), c, depth + 1);
        rightSubtree = setFromList(tList.subList(medianInd + 1, tList.size()), c, depth + 1);

        newRoot.setLeft(leftSubtree);
        newRoot.setRight(rightSubtree);
        return newRoot;
      }
    }
    return null;
  }

  private List<T> getBestK(
          List<T> right,
          List<T> left,
          int k,
          List<Double> target) {
    // list to be filled with the k closest to target given two sorted right and left lists
    List<T> bestK = new ArrayList<>();
    int rightInd = 0;
    int leftInd = 0;
    int rightLen = right.size() - 1;
    int leftLen = left.size() - 1;
    // while there are not k in the best list
    while (bestK.size() < k) {
      // stop looping if you run out of stars in both lists
      if (rightInd > rightLen && leftInd > leftLen) {
        break;
      // only run out of right list stars, add left ones still
      } else if (rightInd > rightLen) {
        bestK.add(left.get(leftInd));
        leftInd++;
      // only out of left, add the right stars
      } else if (leftInd > leftLen) {
        bestK.add(right.get(rightInd));
        rightInd++;
      } else {
        // calculate to see which star from each list is closer
        double rightDist = right.get(rightInd).calculateDistanceApart(target);
        double leftDist = left.get(leftInd).calculateDistanceApart(target);
        if (rightDist < leftDist) {
          bestK.add(right.get(rightInd));
          rightInd++;
        } else if (rightDist > leftDist) {
          bestK.add(left.get(leftInd));
          leftInd++;
        } else {
          // if tied, add them both
          bestK.add(right.get(rightInd));
          rightInd++;
          bestK.add(left.get(leftInd));
          leftInd++;
        }
      }
    }

    double farthestDist = bestK.get(bestK.size() - 1).calculateDistanceApart(target);
    // add in all the stars tied at the end from the right list
    while (rightInd <= rightLen
        && right.get(rightInd).calculateDistanceApart(target) == farthestDist) {
      bestK.add(right.get(rightInd));
      rightInd++;
    }
    // do the same for the left
    while (leftInd <= leftLen
        && left.get(leftInd).calculateDistanceApart(target) == farthestDist) {
      bestK.add(left.get(leftInd));
      leftInd++;
    }
    return bestK;
  }

  private List<T> randomizeTies(List<T> nodeList, List<Double> target, int k) {
    // nodeList must be sorted already
    if (nodeList.size() <= k) {
      return nodeList;
    }
    List<T> topK = new ArrayList<>();
    int currentNum = nodeList.size();

    // if there is a tie
    double tieDist = nodeList.get(currentNum - 1).calculateDistanceApart(target);
    // i is the number of nontied stars
    int i = 0;
    while (i < currentNum && nodeList.get(i).calculateDistanceApart(target) < tieDist) {
      i++;
    }
    // take in all the nodes before the tie
    if (i > 0) {
      topK = nodeList.subList(0, i);
    }
    List<T> tied = nodeList.subList(i, currentNum);
    Collections.shuffle(tied);
    if (k > i) {
      topK.addAll(tied.subList(0, k - i));
    }
    return topK;
  }


  /**
   * Method that finds the k nearest neighbors to target in the tree.
   * @param target the coordinates to find neighbors from
   * @param k the number of neighbors
   * @return a list of nodes that are the k (or less) nearest neighbors
   */
  public List<T> getKNN(List<Double> target, int k) {
    if (k == 0) {
      return new ArrayList<>();
    } else {
      List<T> bestK = getKNNHelper(target, root, k, 0);
      if (bestK.size() > k) {
        bestK = randomizeTies(bestK, target, k);
      }
      return bestK;
    }
  }

  private List<T> getKNNHelper(List<Double> target,
                              KDNode<T> node,
                              int k,
                              int depth) {
    if (node == null) {
      return new ArrayList<>();
    } else {
      // find the relevant axis
      int relevantAxis = depth % dims;
      double currentDist = target.get(relevantAxis) - node.getCoordinates().get(relevantAxis);
      KDNode<T> next;
      KDNode<T> other;
      // compare the relevant axis value of target to the KDNode
      if (currentDist >= 0) {
        next = node.getRight();
        other = node.getLeft();
      } else {
        next = node.getLeft();
        other = node.getRight();
      }
      List<T> bestK = getKNNHelper(target, next, k, depth + 1);
      List<T> singleNode = new ArrayList<>(1);
      singleNode.add(node.getData());
      bestK = getBestK(bestK, singleNode, k, target);
      // bestK is never empty, no need to bounds check
      double bestDist = bestK.get(bestK.size() - 1).calculateDistanceApart(target);
      // draw a circle around the target and see if it crosses a splitting plane
      // use >= not just > in order to include all tied stars
      if (bestDist >= currentDist * currentDist) {
        List<T> potentialBestK = getKNNHelper(target, other, k, depth + 1);
        bestK = getBestK(bestK, potentialBestK, k, target);
      }
      return bestK;
    }
  }

  /**
   * Method that finds all the tree nodes within r radius from target.
   * @param target the coordinates to look from
   * @param r the radius
   * @return a list of nodes within r from target
   */
  public List<T> radiusSearch(List<Double> target, double r) {
    return radiusSearchHelper(target, root, r, 0);
  }

  private List<T> radiusSearchHelper(List<Double> target,
                                    KDNode<T> node,
                                    double r,
                                    int depth) {
    double maxDist = r * r;
    if (node == null) {
      return new ArrayList<>();
    } else {
      // iterate through the tree to find where the target would be
      int relevantAxis = depth % dims;
      double currentDist = target.get(relevantAxis) - node.getCoordinates().get(relevantAxis);
      KDNode<T> next;
      KDNode<T> other;
      // compare the relevant axis value of target to the KDNode
      if (currentDist >= 0) {
        next = node.getRight();
        other = node.getLeft();
      } else {
        next = node.getLeft();
        other = node.getRight();
      }

      List<T> allWithinR = radiusSearchHelper(target, next, r, depth + 1);
      double currentRadius = node.calculateDistanceApart(target);
      if (currentRadius <= maxDist) {
        List<T> singleNode = new ArrayList<>();
        singleNode.add(node.getData());
        allWithinR = placeInOrder(allWithinR, singleNode, target);
      }
      if (maxDist >= currentDist * currentDist) {
        List<T> additionalWithin = radiusSearchHelper(target, other, r, depth + 1);
        allWithinR = placeInOrder(allWithinR, additionalWithin, target);
      }
      return allWithinR;
    }
  }

  private List<T> placeInOrder(List<T> left, List<T> right, List<Double> target) {
    // list to be filled with the k closest to target given right and left lists
    List<T> allWithin = new ArrayList<>();
    int rightInd = 0;
    int leftInd = 0;
    int rightLen = right.size() - 1;
    int leftLen = left.size() - 1;
    // while there are not k in the best list
    while (rightInd <= rightLen || leftInd <= leftLen) {
      // stop looping if you run out of stars in both lists
      if (rightInd > rightLen) {
        allWithin.add(left.get(leftInd));
        leftInd++;
      // only out of left, add the right stars
      } else if (leftInd > leftLen) {
        allWithin.add(right.get(rightInd));
        rightInd++;
      } else {
        // calculate to see which star from each list is closer
        double rightDist = right.get(rightInd).calculateDistanceApart(target);
        double leftDist = left.get(leftInd).calculateDistanceApart(target);
        if (rightDist < leftDist) {
          allWithin.add(right.get(rightInd));
          rightInd++;
        } else if (rightDist > leftDist) {
          allWithin.add(left.get(leftInd));
          leftInd++;
        } else {
          // if they are tied, randomly select
          Random rand = new Random();
          if (rand.nextInt(2) == 1) {
            allWithin.add(left.get(leftInd));
            leftInd++;
          } else {
            allWithin.add(right.get(rightInd));
            rightInd++;
          }
        }
      }
    }
    if (right.isEmpty()) {
      allWithin = left;
    } else if (left.isEmpty()) {
      allWithin = right;
    }
    return allWithin;
  }
}
