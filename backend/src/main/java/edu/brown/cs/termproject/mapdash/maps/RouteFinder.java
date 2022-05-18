package edu.brown.cs.termproject.mapdash.maps;

import edu.brown.cs.termproject.mapdash.kdTree.KDTree;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 ** The RouteFinder class executes all of the main maps commands (nearest, ways, route). It uses
 * the KDTree initialized in MapsInterpreter for getting nearest nodes and has a Guava cache to
 * store MapNodes that we have already created while building previous graphs.
 */
public class RouteFinder {

  private KDTree<MapNode> nodesTree;
  private Connection conn;
  private LazyDijkstraReader<MapNode, Way> reader;
  private Dijkstra<MapNode, Way> dijkstra;

  /**
   ** Constructor for the RouteFinder class. It initializes an empty instance of a KDTree to be
   * set later on, as well as a NodeReader which it passes to a new instance of the Dijkstra class.
   * The NodeReader is used as a proxy to query the database from the Dijkstra class.
   */
  public RouteFinder() {
    this.nodesTree = new KDTree<>();
    this.reader = new NodeReader();
    this.dijkstra = new Dijkstra<>(reader);
  }

  /**
   ** Helper method that takes in the same filename to a database as the maps command and
   * initializes a new Connection to that database.
   * @param filename - the String representing the path to a database file
   */
  public void setConn(String filename) {
    try {
      // create connection to the database
      Class.forName("org.sqlite.JDBC");
      String urlToDB = "jdbc:sqlite:" + filename;

      try {
        this.conn = DriverManager.getConnection(urlToDB);
        // Set the Connection for the LazyDijkstraReader to allow it to query the database too
        this.reader.setConn(filename);
      } catch (SQLException e) {
        System.out.println("ERROR: Database access error.");
      }
    } catch (ClassNotFoundException e) {
      System.out.println("ERROR: Class cannot be located.");
    }
  }

  /**
   ** Setter method that takes in a KDTree of MapNodes created in MapsInterpreter and sets it to
   * the KDTree instance in this class.
   * @param nodesTree - the KDTree of MapNodes created in MapsInterpreter
   */
  public void setNodesTree(KDTree<MapNode> nodesTree) {
    this.nodesTree = nodesTree;
  }

  /**
   ** Method that executes the nearest command. It does so by calling the getKNN() method in the
   * KDTree class with a k value of 1.
   * @param coordinates - a List of coordinates of the location for which we are getting the
   *                      nearest neighbor
   * @return a MapNode of the nearest neighbor or null if no neighbor was found
   */
  public MapNode nearestCommand(List<Double> coordinates) {
    List<MapNode> nearest = nodesTree.getKNN(coordinates, 1);
    if (!nearest.isEmpty()) {
      return nearest.get(0);
    } else {
      return null;
    }
  }

  /**
   ** Method that executes the ways command. It takes in two pairs of latitude and longitude
   * coordinates and finds all of the ways within a bounding box of these coordinates.
   * @param lat1 - a double representing the latitude of the first point
   * @param lon1 - a double representing the longitude of the first point
   * @param lat2 - a double representing the latitude of the second point
   * @param lon2 - a double representing the longitude of the second point
   * @return a List of Ways within a bounding box
   */
  public HashMap<String, HashMap<String, String>> waysCommand(double lat1, double lon1,
                                                               double lat2, double lon2) {
    HashMap<String, HashMap<String, String>> ways = new HashMap<>();
    PreparedStatement prep = null;
    ResultSet rs = null;
    try {
      // Gets all ways in a bounding box between (lat1, lon1) in the northwest corner and
      // (lat2, lon2) in the southeast corner
      prep = conn.prepareStatement(
          "SELECT way.id, way.type, node.latitude, node.longitude FROM way, node WHERE "
              + "(node.id = way.start AND node.latitude BETWEEN ? AND ? "
              + "AND node.longitude BETWEEN ? AND ?) "
              + "UNION "
              + "SELECT way.id, way.type, node.latitude, node.longitude FROM way, node WHERE "
              + "(node.id = way.end AND node.latitude BETWEEN ? AND ? "
              + "AND node.longitude BETWEEN ? AND ?) "
              + "ORDER BY way.id;"
      );

      prep.setDouble(1, lat2);
      prep.setDouble(2, lat1);
      prep.setDouble(3, lon1);
      prep.setDouble(4, lon2);
      prep.setDouble(5, lat2);
      prep.setDouble(6, lat1);
      prep.setDouble(7, lon1);
      prep.setDouble(8, lon2);
      rs = prep.executeQuery();

      while (rs.next()) {
        // If way is already in HashMap, then we only need to add end coords
        if (ways.containsKey(rs.getString(1))) {
          ways.get(rs.getString(1)).put("endLat", rs.getString(3));
          ways.get(rs.getString(1)).put("endLon", rs.getString(4));
        } else {
          // If way isn't in HashMap, create fields HashMap and add way
          HashMap<String, String> wayFields = new HashMap<>();
          wayFields.put("type", rs.getString(2));
          wayFields.put("startLat", rs.getString(3));
          wayFields.put("startLon", rs.getString(4));
          ways.put(rs.getString(1), wayFields);

          System.out.println(rs.getString(1));
        }
      }
      return ways;
    } catch (SQLException e) {
      System.out.println("ERROR: Invalid database content.");
      return null;
    } finally {
      if (prep != null) {
        try {
          prep.close();
        } catch (SQLException e) {
          System.out.println("ERROR: Error on closing PreparedStatement.");
        }
      }
      if (rs != null) {
        try {
          rs.close();
        } catch (SQLException e) {
          System.out.println("ERROR: Error on closing ResultSet.");
        }
      }
    }
  }

  /**
   ** Method that executes the coordinates-based route command. It takes in two pairs of
   * latitude and longitude coordinates and finds the shortest path between the closest nodes to
   * these two points.
   * @param lat1 - a double representing the latitude of the first point
   * @param lon1 - a double representing the longitude of the first point
   * @param lat2 - a double representing the latitude of the second point
   * @param lon2 - a double representing the longitude of the second point
   * @return List of ways that is the shortest path between the coordinates inputted.
   */
  public List<Way> routeCoordinatesCommand(double lat1, double lon1, double lat2, double lon2) {
    List<Double> startCoords = new ArrayList<>();
    startCoords.add(lat1);
    startCoords.add(lon1);
    List<Double> endCoords = new ArrayList<>();
    endCoords.add(lat2);
    endCoords.add(lon2);

    // Use nearestCommand() to find the start and end nodes of the route
    MapNode startNode = nearestCommand(startCoords);
    MapNode endNode = nearestCommand(endCoords);
    if (startNode != null && endNode != null) {
      return findRoute(startNode, endNode);
    } else {
      // if the nodes are null, that means there are no nodes in the nodesTree,
      // meaning that there are no traversable nodes
      System.out.println("ERROR: No traversable nodes in database.");
      return null;
    }
  }

  /**
   ** Method that executes the intersections-based route command. It takes in a List of street
   * names and finds the shortest path between the intersection between Street 1 and Cross-street 1
   * and Street 2 and Cross-street 2.
   * @param streetNames - a List of street names from which we must find the intersections and
   *                    the shortest path between these intersections
   * @return List of ways that is the shortest path between the coordinates inputted.
   */
  public List<Way> routeIntersectionsCommand(List<String> streetNames) {
    MapNode intersectionNode1 = getIntersectionNode(streetNames.get(0), streetNames.get(1));
    MapNode intersectionNode2 = getIntersectionNode(streetNames.get(2), streetNames.get(3));
    if (intersectionNode1 != null && intersectionNode2 != null) {
      // since getIntersectionNode creates a new node on return, must use .equals to check
      // here if they are the same because they are different references in memory
      if (intersectionNode1.getId().equals(intersectionNode2.getId())) {
        return new ArrayList<>();
      } else {
        return findRoute(intersectionNode1, intersectionNode2);
      }
    } else {
      System.out.println("ERROR: Intersections not found.");
      return null;
    }
  }

  /**
   ** Method that finds a route between two MapNodes by first building a graph between the two nodes
   * and then running Dijkstra's on that graph. Dijkstra's returns a List of Ways representing the
   * shortest path which we use to print the route.
   * @param start - a MapNode representing the start of the route
   * @param end - a MapNode representing the end of the route
   * @return List of ways that is the shortest path between the coordinates inputted.
   */
  public List<Way> findRoute(MapNode start, MapNode end) {
    List<Way> ways = dijkstra.getShortestPath(start, end);
    //printShortestPath(ways, start, end);
    return ways;
  }

  /**
   ** Helper method that finds the node at an intersection between a street and cross-street by
   * reading from the database.
   * @param street - a String representing the street name
   * @param crossStreet - a String representing the cross-street name
   * @return a MapNode representing the intersection or null if no intersection was found
   */
  public MapNode getIntersectionNode(String street, String crossStreet) {
    PreparedStatement prep = null;
    ResultSet rs = null;
    try {
      // Get node at intersection between a street and a cross-street
      prep = this.conn.prepareStatement(
          "SELECT node.id, node.latitude, node.longitude FROM way, node "
              + "WHERE way.name = ? "
              + "AND (node.id = way.start or node.id = way.end) "
              + "INTERSECT "
              + "SELECT node.id, node.latitude, node.longitude FROM way, node "
              + "WHERE way.name = ? AND (node.id = way.start or node.id = way.end) "
              + "AND node.id NOT IN "
              + "(SELECT node.id FROM way, node "
              + "WHERE way.name = ? and way.type in ('', \"unclassified\") "
              + "AND (node.id = way.start or node.id = way.end) "
              + "INTERSECT "
              + "SELECT node.id FROM way, node "
              + "WHERE way.name = ? and way.type in ('', \"unclassified\") "
              + "AND (node.id = way.start or node.id = way.end));");

      prep.setString(1, street);
      prep.setString(2, crossStreet);
      prep.setString(3, street);
      prep.setString(4, crossStreet);
      rs = prep.executeQuery();

      // If such a node exists, create a new MapNode for this intersection
      while (rs.next()) {
        return new MapNode(rs.getString(1), rs.getDouble(2), rs.getDouble(3));
      }
    } catch (SQLException e) {
      System.out.println("ERROR: Database access error.");
    } finally {
      if (prep != null) {
        try {
          prep.close();
        } catch (SQLException e) {
          System.out.println("ERROR: Error on closing PreparedStatement.");
        }
      }
      if (rs != null) {
        try {
          rs.close();
        } catch (SQLException e) {
          System.out.println("ERROR: Error on closing ResultSet.");
        }
      }
    }
    return null;
  }

  /**
   ** Helper method that prints the route between two nodes using the list of Ways returned from
   * Dijkstra's.
   * @param ways - a List of Ways representing the shortest path
   * @param start - a MapNode representing the start of the route
   * @param end - a MapNode representing the end of the route
   */
  private void printShortestPath(List<Way> ways, MapNode start, MapNode end) {
    // If ways is null, Dijkstra's could not find a path between start and end
    if (ways == null) {
      System.out.println(start.getId() + " -/- " + end.getId());
    } else {
      // Print out path
      for (Way way: ways) {
        String wayId = way.getId();
        String startId = way.getStart().getId();
        String endId = way.getEnd().getId();
        System.out.println(startId + " -> " + endId + " : " + wayId);
      }
    }
  }

}
