//package edu.brown.cs.termproject.mapdash.maps;
//
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
//public class RouteFinderTest {
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
//  /**
//   * Loads in the map.sqlite3
//   */
//  @Before
//  public void setUpLarge() {
//    _mapsInterpreter = new MapsInterpreter();
//    ArrayList<String> input = new ArrayList<>();
//    input.add("map");
//    input.add("data/maps.sqlite3");
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
//   * Tests getIntersectionNode through the routeIntersectionCommand method on
//   * a variety of different graphs and databases.
//   */
//  @Test
//  public void testRouteIntersectionCommand() {
//    List<String> intersectionStreets = new ArrayList<>();
//    intersectionStreets.add("Chihiro Ave");
//    intersectionStreets.add("Yubaba St");
//    intersectionStreets.add("Sootball Ln");
//    intersectionStreets.add("Kamaji Pl");
//    setUpEmpty();
//    List<Way> empty = _routeFinder.routeIntersectionsCommand(intersectionStreets);
//    assertTrue(empty == null);
//    tearDown();
//
//    setUpSequential();
//
//    List<String> sameIntersections = new ArrayList<>();
//    sameIntersections.add("Chihiro Ave");
//    sameIntersections.add("Yubaba St");
//    sameIntersections.add("Chihiro Ave");
//    sameIntersections.add("Yubaba St");
//
//    // test same node as start and end returns empty list
//    List<Way> sameStartAsEnd = _routeFinder.routeIntersectionsCommand(sameIntersections);
//    assertTrue(sameStartAsEnd.isEmpty());
//
//    // get a single path from n1 to n4
//    List<Way> singlePath = _routeFinder.routeIntersectionsCommand(intersectionStreets);
//    assertEquals(3, singlePath.size());
//    assertEquals("/w/1", singlePath.get(0).getId());
//    assertEquals("/w/2", singlePath.get(1).getId());
//    assertEquals("/w/3", singlePath.get(2).getId());
//    tearDown();
//
//    setUpGeneral();
//    List<String> intersectionsForThreePaths = new ArrayList<>();
//    intersectionsForThreePaths.add("Chihiro Ave");
//    intersectionsForThreePaths.add("Brooke St");
//    intersectionsForThreePaths.add("Charlesfield St");
//    intersectionsForThreePaths.add("Kamaji Pl");
//    // ensure the shortest path between n0 and n8 is chosen out of the 3
//    List<Way> shortestPath = _routeFinder.routeIntersectionsCommand(intersectionsForThreePaths);
//    assertEquals(4, shortestPath.size());
//    assertEquals("/w/5", shortestPath.get(0).getId());
//    assertEquals("/w/8", shortestPath.get(3).getId());
//    tearDown();
//  }
//
//  /**
//   * Tests whether the nearest command is time efficient with map.sqlite3.
//   */
//  @Test
//  public void testLargeNearest() {
//    setUpLarge();
//
//    List<Double> coords = new ArrayList<>();
//    coords.add(41.303986);
//    coords.add(-71.858414);
//    assertEquals(coords, _routeFinder.nearestCommand(coords).getCoords());
//
//    coords.clear();
//    coords.add(41.309216);
//    coords.add(-71.858025);
//    assertEquals(coords, _routeFinder.nearestCommand(coords).getCoords());
//
//    coords.clear();
//    coords.add(41.88884);
//    coords.add(-71.400208);
//    assertEquals(coords, _routeFinder.nearestCommand(coords).getCoords());
//
//    tearDown();
//  }
//
//  /**
//   * Tests whether coordinates-based route command is time efficient with map.sqlite3.
//   */
//  @Test
//  public void testLargeRouteCoordsCommand() {
//    setUpLarge();
//    List<Way> thayerToWickenden = _routeFinder.routeCoordinatesCommand(
//        41.819592,-71.396202,
//        41.820477,-71.399826);
//    assertTrue(!thayerToWickenden.isEmpty());
//    // check the start and end node ids are the ones corresponding to the inputted coordinates
//    assertEquals("/n/4181.7139.201313481",
//        thayerToWickenden.get(0).getStart().getId());
//    assertEquals("/n/4182.7139.201267765",
//        thayerToWickenden.get(thayerToWickenden.size() - 1).getEnd().getId());
//
//    tearDown();
//  }
//
//  /**
//   * Tests whether intersection-based route command is time efficient with map.sqlite3.
//   */
//  @Test
//  public void testLargeRouteIntersectionCommand() {
//    setUpLarge();
//    List<String> intersections = new ArrayList<>();
//    intersections.add("Brook St");
//    intersections.add("Wickenden St");
//    intersections.add("George Bennett Hwy");
//    intersections.add("Central Ave");
//
//    List<Way> longPath = _routeFinder.routeIntersectionsCommand(intersections);
//    assertTrue(!longPath.isEmpty());
//    // check the start and end node ids are the ones corresponding to the intersection nodes
//    assertEquals(longPath.get(0).getStart().getId(), "/n/4181.7139.201365725");
//    assertEquals(longPath.get(longPath.size() - 1).getEnd().getId(), "/n/4188.7136.201592414");
//
//    tearDown();
//  }
//}
