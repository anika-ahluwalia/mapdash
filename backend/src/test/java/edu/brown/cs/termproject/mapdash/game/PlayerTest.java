//package edu.brown.cs.termproject.mapdash.game;
//
//import edu.brown.cs.termproject.mapdash.maps.MapNode;
//import edu.brown.cs.termproject.mapdash.maps.MapsInterpreter;
//import edu.brown.cs.termproject.mapdash.maps.RouteFinder;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertTrue;
//
//public class PlayerTest {
//  private MapsInterpreter mapsInterpreter;
//  private RouteFinder routeFinder;
//
//  /**
//   * Set up a new MapsInterpreter and load in the Providence database. Also set up a new RouteFinder
//   * to be used in the Player class
//   */
//  @Before
//  public void setUp() {
//    this.mapsInterpreter = new MapsInterpreter();
//    this.mapsInterpreter.loadMapData("data/Providence.sqlite3");
//    this.routeFinder = this.mapsInterpreter.getFinder();
//  }
//
//  /**
//   * Resets the MapsInterpreter and RouteFinder after the tests have been run
//   */
//  @After
//  public void tearDown() {
//    this.mapsInterpreter = null;
//    this.routeFinder = null;
//  }
//
//  /**
//   * Tests the Player class's calculateRouteToDestination() method using a simple start and end
//   * location along Thayer St.
//   */
//  @Test
//  public void testRouteToDestination() {
//    setUp();
//    // Start location near Thayer and Waterman intersection
//    List<Double> startCoords = new ArrayList<>() {{ add(41.827276); add(-71.400571); }};
//    // End location almost directly south at Thayer and Power intersection
//    List<Double> destCoords = new ArrayList<>() {{ add(41.822990); add(-71.400045); }};
//    MapNode startNode = this.mapsInterpreter.getNearest(startCoords);
//    MapNode destNode = this.mapsInterpreter.getNearest(destCoords);
//
//    Player player = new Player("Player 1");
//    player.setNode(startNode);
//    player.calculateRouteToDestination(this.routeFinder, destNode);
//
//    double routeDistance =player.getDistToDest();
//    assertTrue(routeDistance > 0);
//    // Route distance should be about 0.3 miles (0.482803 km)
//    assertEquals(routeDistance, 0.482803, 0.01);
//  }
//
//  /**
//   * Tests the Player class's getHeadingToNextNode() method using a simple start and end
//   * location along Thayer St.
//   */
//  @Test
//  public void testGetHeadingToNextNode() {
//    // Start location near Thayer and Waterman intersection
//    List<Double> startCoords = new ArrayList<>() {{ add(41.827276); add(-71.400571); }};
//    // End location almost directly south at Thayer and Power intersection
//    List<Double> destCoords = new ArrayList<>() {{ add(41.822990); add(-71.400045); }};
//    MapNode startNode = this.mapsInterpreter.getNearest(startCoords);
//    MapNode destNode = this.mapsInterpreter.getNearest(destCoords);
//
//    Player player = new Player("Player 1");
//    player.setNode(startNode);
//    player.calculateRouteToDestination(this.routeFinder, destNode);
//
//    double heading = player.getHeadingToNextNode();
//    // The heading should be about 176 degrees because Thayer runs very slightly NW/SE, and 180
//    // degrees is directly south
//    assertEquals(heading, 176, 1);
//    // Check that the heading is between 0 and 360 degress
//    assertTrue(heading >= 0.0 && heading <= 360.0);
//
//    tearDown();
//  }
//
//}
