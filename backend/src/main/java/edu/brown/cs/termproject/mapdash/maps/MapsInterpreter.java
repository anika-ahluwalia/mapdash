package edu.brown.cs.termproject.mapdash.maps;

import edu.brown.cs.termproject.mapdash.kdTree.KDTree;
import edu.brown.cs.termproject.mapdash.replAndParsing.TriggerAction;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 ** The MapInterpreter class implements the TriggerAction interface. It
 *  deals with loading map data from a database and processing different maps commands.
 */
public class MapsInterpreter implements TriggerAction {
  private List<MapNode> nodesList;
  private KDTree<MapNode> nodesTree;
  private RouteFinder finder;
  private Connection conn;
  private boolean isMapLoaded = false;
  private HashMap<String, HashMap<String, String>> waysForGUI;
  private List<HashMap<String, String>> routeForGUI;
  private List<Double> nearestForGUI;
  private String fileName;

  /**
   ** Constructor for the MapInterpreter class. Creates a new ArrayList of MapNodes that the KDTree
   * will use to build a new tree, initializes an instance of the RouteFinder class which handles
   * all of the main commands besides loading in map data, and initializes a new Connection to null
   * for database communication.
   */
  public MapsInterpreter() {
    this.nodesList = new ArrayList<>();
    this.nodesTree = new KDTree<>();
    this.finder = new RouteFinder();
    this.conn = null;
    this.waysForGUI = new HashMap<>();
    this.routeForGUI = new ArrayList<>();
    this.fileName = "";
  }

  public MapNode getNearest(List<Double> coordinates) {
    return finder.nearestCommand(coordinates);
  }

  @Override
  /**
   * MapInterpreter's implementation of the action() method required by the TriggerAction
   * interface. It deals with all command processing and delegation.
   * @param input - an ArrayList of Strings of parsed input from the REPl
   */
  public void action(ArrayList<String> input) {
    String command = input.get(0);
    List<String> args;

    switch (command) {
      case "map":
        if (hasArgs(input)) {
          args = input.subList(1, input.size());
          if (args.size() == 1) {
            String filename = args.get(0);
            // Load map data from path to database
            loadMapData(filename);
          } else {
            System.out.println("ERROR: map command argument must be of form <path/to/database>.");
          }
        }
        break;
      case "nearest":
        if (hasArgs(input) && isMapLoaded()) {
          args = input.subList(1, input.size());
          if (args.size() == 2) {
            // Check that coordinates are valid
            List<Double> coordinates = parseCoordinates(args);
            if (coordinates != null) {
              // Call RouteFinder's nearest command
              MapNode nearest = finder.nearestCommand(coordinates);
              // Nearest neighbor may be null if it does not exist
              if (nearest != null) {
                this.nearestForGUI = nearest.getCoords();
                System.out.println(nearest.getId());
              } else {
                System.out.println("ERROR: No nearest neighbor."
                    + " Ensure that database is not empty.");
              }
            }
          } else {
            System.out.println("ERROR: nearest command arguments must be of form <latitude> "
                + "<longitude>.");
          }
        }
        break;
      case "ways":
        if (hasArgs(input) && isMapLoaded()) {
          args = input.subList(1, input.size());
          if (args.size() == 4) {
            List<Double> coordinates = parseCoordinates(args);
            if (coordinates != null) {
              // Ensure that first two coordinates are NW corner and last two are SE corner
              if (coordinates.get(0) < coordinates.get(2)) {
                System.out.println("ERROR: <lat1> cannot be less than <lat2>");
              } else if (coordinates.get(1) > coordinates.get(3)) {
                System.out.println("ERROR: <lon1> cannot be greater than <lon2>");
              } else {
                // Call RouteFinder's ways command
                waysForGUI = finder.waysCommand(coordinates.get(0), coordinates.get(1),
                  coordinates.get(2), coordinates.get(3));
              }
            }
          } else {
            System.out.println("ERROR: ways command arguments must be of form <lat1> <lon1>"
                + " <lat2> <lon2>.");
          }
        }
        break;
      case "route":
        if (hasArgs(input) && isMapLoaded()) {
          args = input.subList(1, input.size());
          if (args.size() == 4) {
            // Try to extract street names from arguments
            List<String> streetNames = extractStreetNames(args);
            // Make sure that none of the street names are the empty string
            if (streetNames == null) {
              System.out.println("ERROR: Names cannot be empty.");
            } else if (!streetNames.isEmpty()) {
              // If we found four street names in quotes it's an intersections-based command
              convertRouteForGUI(finder.routeIntersectionsCommand(streetNames));
            } else {
              // If we see that route arguments don't have four street names in quotes, see if it's
              // a coordinates-based command
              List<Double> coordinates = parseCoordinates(args);
              if (coordinates != null) {
                // Call RouteFinder's coordinates-based route command
                convertRouteForGUI(finder.routeCoordinatesCommand(coordinates.get(0),
                    coordinates.get(1), coordinates.get(2), coordinates.get(3)));
              }
            }
          } else {
            System.out.println("ERROR: route command arguments must be of form <lat1> <lon1> <lat2>"
                + " <lon2> or \"Street 1\" \"Cross-street 1\" \"Street 2\" \"Cross-street 2\".");
          }
        }
        break;
      default:
        System.out.println("ERROR: Invalid command for REPL.");
    }
  }

  /**
   * Getter method for the current filename loaded into the REPL.
   * @return the filename
   */
  public String getFileName() {
    return this.fileName;
  }

  /**
   ** Method that reads in all traversable nodes from a database file. Creates a new MapNode for
   * each node and stores them in a temporary list to be passed into the KDTree class. The KDTree
   * class then creates a balanced kd-tree using this list and that tree is passed to our
   * RouteFinder class.
   * @param filename - the String representing the path to a database file
   */
  public void loadMapData(String filename) {
    try {
      // create connection to the database
      Class.forName("org.sqlite.JDBC");
      String urlToDB = "jdbc:sqlite:" + filename;
      File dbFile = new File(filename);

      // Only attempt to establish a connection if the database file exists. This prevents
      // getConnection() from creating a new file if the file does not exist.
      if (dbFile.exists()) {
        PreparedStatement prep = null;
        ResultSet rs = null;
        try {
          conn = DriverManager.getConnection(urlToDB);
          finder.setConn(filename);

          this.fileName = filename;

          // Select all traversable nodes to build a KD tree
          prep = conn.prepareStatement("SELECT node.id, node.latitude, "
            + "node.longitude FROM node, way WHERE node.id = way.start AND way.type NOT IN "
            + "('', \"unclassified\") UNION SELECT node.id, node.latitude, node.longitude FROM "
            + "node, way WHERE node.id = way.end AND way.type NOT IN ('', \"unclassified\");");
          rs = prep.executeQuery();

          // Create new MapNodes from data read in from database
          while (rs.next()) {
            nodesList.add(new MapNode(
                rs.getString(1),
                rs.getDouble(2),
                rs.getDouble(3)));
          }

          // Create new KDTree from list of MapNodes
          nodesTree.createBalancedFromList(nodesList);
          isMapLoaded = true;
          // Pass the newly-created tree into the RouteFinder class
          finder.setNodesTree(this.nodesTree);

          // Once KD tree has been built with nodesList, clear it because it's not needed elsewhere
          nodesList.clear();

          System.out.println("map set to " + filename);
        } catch (SQLException e) {
          isMapLoaded = false;
          nodesList.clear();
          System.out.println("ERROR: Invalid database format.");
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
      } else {
        System.out.println("ERROR: Database file does not exist.");
      }
    } catch (ClassNotFoundException e) {
      System.out.println("ERROR: Class cannot be located.");
    }
  }

  /**
   ** Helper method to ensure that a command has arguments.
   * @param parsedInput - a List of Strings that contains the parsed command line input
   * @return a boolean representing whether the command also has arguments
   */
  private boolean hasArgs(List<String> parsedInput) {
    if (parsedInput.size() < 2) {
      System.out.println("ERROR: No command arguments provided.");
      return false;
    }
    return true;
  }

  /**
   ** Helper method to determine if Strings of coordinates are valid doubles.
   * @param coords - a List of Strings to be parsed into doubles
   * @return a List of doubles containing the parsed coordinates or null if any of the
   * Strings cannot be parsed as doubles
   */
  private List<Double> parseCoordinates(List<String> coords) {
    List<Double> parsedCoords = new ArrayList<>();
    for (String coord : coords) {
      try {
        parsedCoords.add(Double.parseDouble(coord));
      } catch (NumberFormatException e) {
        System.out.println("ERROR: Check that all latitude and longitude "
            + "arguments are numeric values or all street names are in quotes.");
        return null;
      }
    }
    return parsedCoords;
  }

  /**
   ** Helper method to check if any map data has been loaded.
   * @return a boolean representing whether data has been loaded
   */
  private boolean isMapLoaded() {
    if (!isMapLoaded) {
      System.out.println("ERROR: No map has been loaded.");
      return false;
    }
    return true;
  }

  /**
   ** Helper method to extract street names from a route command's arguments if they have quotes
   *  around them.
   * @param commandArgs - the List of Strings of command line arguments with quotes around them if
   *                      applicable
   * @return a List of Strings containing the street names without quotes or an empty list if
   * the arguments didn't match the form of "Street 1" "Cross-street 1" "Street 2" "Cross-street 2"
   */
  private List<String> extractStreetNames(List<String> commandArgs) {
    // street names extracted, with quotes surrounding names still
    String street1 = commandArgs.get(0);
    String street2 = commandArgs.get(1);
    String street3 = commandArgs.get(2);
    String street4 = commandArgs.get(3);

    List<String> streetNames = new ArrayList<>();

    // Checks if every street is encased in quotes. If they are, add them to the list
    if (street1.substring(0, 1).equals("\"")
        && street1.substring(street1.length() - 1).equals("\"")
        && street2.substring(0, 1).equals("\"")
        && street2.substring(street2.length() - 1).equals("\"")
        && street3.substring(0, 1).equals("\"")
        && street3.substring(street3.length() - 1).equals("\"")
        && street4.substring(0, 1).equals("\"")
        && street4.substring(street4.length() - 1).equals("\"")) {
      String s1 = street1.substring(1, street1.length() - 1);
      String s2 = street2.substring(1, street2.length() - 1);
      String s3 = street3.substring(1, street3.length() - 1);
      String s4 = street4.substring(1, street4.length() - 1);

      if (s1.isEmpty() || s2.isEmpty() || s3.isEmpty() || s4.isEmpty()) {
        // If any street name is the empty string, return null
        return null;
      }
      streetNames.add(s1);
      streetNames.add(s2);
      streetNames.add(s3);
      streetNames.add(s4);
      return streetNames;
    }
    // If there are not valid quotes surrounding the names, return empty list
    return streetNames;
  }

  /**
   * Accessor method for the route finder instance.
   * @return finder instance variable
   */
  public RouteFinder getFinder() {
    return this.finder;
  }

  /**
   * Getter method for a List of nearest node coordinates for the Maps GUI.
   * @param input - the input nearest command to run
   * @return a List of the nearest node's coordinates to be returned to the GUI
   */
  public List<Double> getNearestForGUI(ArrayList<String> input) {
    // Evaluate command input from GUI. Will delegate a nearest command to RouteFinder and store
    // results in nearestForGUI list
    action(input);

    return this.nearestForGUI;
  }

  /**
   * Getter method for a List of Ways for the Maps GUI.
   * @param input - the input ways command to run
   * @return a HashMap of HashMaps of the Ways within a bounding box to be returned to the GUI
   */
  public HashMap<String, HashMap<String, String>> getWaysForGUI(ArrayList<String> input) {
    this.waysForGUI.clear();
    // Evaluate command input from GUI. Will delegate a ways or route command to RouteFinder
    // and store results in nearestForGUI list
    action(input);
    return this.waysForGUI;
  }

  /**
   * Getter method for a List of route Ways for the Maps GUI.
   * @param input - the input route command to run
   * @return a List of HashMaps of Ways that make up a route to be returned to the GUI
   */
  public List<HashMap<String, String>> getRouteForGUI(ArrayList<String> input) {
    // Evaluate command input from GUI. Will delegate a ways or route command to RouteFinder
    // and store results in nearestForGUI list
    action(input);
    return this.routeForGUI;
  }

  /**
   * Helper method to convert a List of Ways to a List of HashMaps (one for each Way) to help format
   * the data better for the GUI.
   * @param ways - the List of Ways representing the route
   */
  private void convertRouteForGUI(List<Way> ways) {
    if (ways == null) {
      this.routeForGUI = new ArrayList<>();
    } else {
      this.routeForGUI.clear();
      for (Way way : ways) {
        HashMap<String, String> wayFields =  new HashMap<>();
        wayFields.put("id", way.getId());
        wayFields.put("startLat", Double.toString(way.getStart().getCoords().get(0)));
        wayFields.put("startLon", Double.toString(way.getStart().getCoords().get(1)));
        wayFields.put("endLat", Double.toString(way.getEnd().getCoords().get(0)));
        wayFields.put("endLon", Double.toString(way.getEnd().getCoords().get(1)));
        this.routeForGUI.add(wayFields);
      }
    }
  }

  /**
   * Getter method for a List of coordinates at an intersection.
   * @param street - the street name
   * @param cross - the cross-street name
   * @return a List of the coordinates at the intersection or null if no intersection exists
   */
  public List<Double> getIntersectionCoords(String street, String cross) {
    List<Double> intersectionCoords = new ArrayList<>();
    MapNode intersection = finder.getIntersectionNode(street, cross);
    if (intersection != null) {
      intersectionCoords.add(intersection.getCoords().get(0));
      intersectionCoords.add(intersection.getCoords().get(1));
    }
    return intersectionCoords;
  }

}
