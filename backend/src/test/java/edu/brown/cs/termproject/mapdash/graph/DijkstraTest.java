//package edu.brown.cs.termproject.mapdash.graph;
//
//import edu.brown.cs.termproject.mapdash.maps.MapsInterpreter;
//import edu.brown.cs.termproject.mapdash.maps.RouteFinder;
//import edu.brown.cs.termproject.mapdash.maps.Way;
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
//public class DijkstraTest {
//  private MapsInterpreter _mapsInterpreter;
//  private RouteFinder _routeFinder;
//
//  /**
//   * Loads in database with no traversable ways.
//   */
//  @Before
//  public void setUpEmpty() {
//    _mapsInterpreter = new MapsInterpreter();
//    ArrayList<String> input = new ArrayList<>();
//    input.add("map");
//    input.add("data/empty.sqlite3");
//    _mapsInterpreter.action(input);
//    _routeFinder = _mapsInterpreter.getFinder();
//  }
//
//  /**
//   * Loads in database with just a single edge that is traversable.
//   */
//  @Before
//  public void setUpSingleTraversable() {
//    _mapsInterpreter = new MapsInterpreter();
//    ArrayList<String> input = new ArrayList<>();
//    input.add("map");
//    input.add("data/onlyOneTraversable.sqlite3");
//    _mapsInterpreter.action(input);
//    _routeFinder = _mapsInterpreter.getFinder();
//  }
//
//  /**
//   * Loads in database with only one single path.
//   * n0 -> n1 -> n2 -> n3 -> n4 -> n5
//   */
//  @Before
//  public void setUpSequential() {
//    _mapsInterpreter = new MapsInterpreter();
//    ArrayList<String> input = new ArrayList<>();
//    input.add("map");
//    input.add("data/sequential.sqlite3");
//    _mapsInterpreter.action(input);
//    _routeFinder = _mapsInterpreter.getFinder();
//  }
//
//  /**
//   * Loads in database with 3 paths, one of which is non-traversable.
//   *    n1 -> n2 -> n3 -> n4
//   *   /                   |
//   * n0     -----/---->   n8
//   *   \                  /
//   *    n5  ->  n6  ->  n7
//   */
//  @Before
//  public void setUpGeneral() {
//    _mapsInterpreter = new MapsInterpreter();
//    ArrayList<String> input = new ArrayList<>();
//    input.add("map");
//    input.add("data/threePaths.sqlite3");
//    _mapsInterpreter.action(input);
//    _routeFinder = _mapsInterpreter.getFinder();
//  }
//
//  @After
//  public void tearDown() {
//    _mapsInterpreter = null;
//    _routeFinder = null;
//  }
//
//  /**
//   * Tests getShortestPath through the routeCoordinatesCommand method on
//   * a variety of different graphs and databases.
//   */
//  @Test
//  public void testShortestPath() {
//    setUpEmpty();
//    List<Way> empty = _routeFinder.routeCoordinatesCommand(0, -71.3, 42, 0);
//    assertTrue(empty == null);
//    tearDown();
//
//    setUpSingleTraversable();
//    // must find that single possible edge since there are only two traversable nodes
//    List<Way> singleEdge = _routeFinder.routeCoordinatesCommand(0, 0, 100, 0);
//    assertEquals(1, singleEdge.size());
//    assertEquals("/n/4", singleEdge.get(0).getStart().getId());
//    assertEquals("/n/5", singleEdge.get(0).getEnd().getId());
//
//    // test same node as start and end returns empty list
//    List<Way> sameStartAsEnd = _routeFinder.routeCoordinatesCommand(0, 0, 0, 0);
//    assertTrue(sameStartAsEnd.isEmpty());
//    tearDown();
//
//    setUpSequential();
//    // get a single path
//    List<Way> singlePath = _routeFinder.routeCoordinatesCommand(41.82, -71.4, 41.8206, -71.4003);
//    assertEquals(5, singlePath.size());
//    assertEquals("/w/0", singlePath.get(0).getId());
//    assertEquals("/w/1", singlePath.get(1).getId());
//    assertEquals("/w/2", singlePath.get(2).getId());
//    assertEquals("/w/3", singlePath.get(3).getId());
//    assertEquals("/w/4", singlePath.get(4).getId());
//    tearDown();
//
//    setUpGeneral();
//    // ensure the shortest path between n0 and n8 is chosen out of the 3
//    List<Way> shortestPath = _routeFinder.routeCoordinatesCommand(41.82, 71.4, 41.82, 71.45);
//    assertEquals(4, shortestPath.size());
//    assertEquals("/w/5", shortestPath.get(0).getId());
//    assertEquals("/w/8", shortestPath.get(3).getId());
//
//    tearDown();
//  }
//
//}
