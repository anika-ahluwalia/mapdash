//package edu.brown.cs.termproject.mapdash.game;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertTrue;
//
//public class GameTest {
//
//  private Player player1;
//  private Player player2;
//  private Game game;
//  private double startLat;
//  private double startLng;
//  private double destLat;
//  private double destLng;
//
//  /**
//   * Sets up two Player instances, a start and destination, and then creates a new Game object
//   * using this information.
//   */
//  @Before
//  public void setUp() {
//    this.player1 = new Player("Player 1");
//    this.player2 = new Player("Player 2");
//    // Start location is in front of entrance to North Campus on Meeting St.
//    this.startLat = 41.829081;
//    this.startLng = -71.402023;
//    // Destination location is near Keeney at Charlesfield and Brown
//    this.destLat = 41.823767;
//    this.destLng = -71.402676;
//    this.game = new Game(this.player1, this.player2, 0, this.destLat, this.destLng, "Providence");
//  }
//
//  /**
//   * Resets all of the main instance variables.
//   */
//  @After
//  public void tearDown() {
//    this.player1 = null;
//    this.player2 = null;
//    this.game = null;
//    this.startLat = 0;
//    this.startLng = 0;
//    this.destLat = 0;
//    this.destLng = 0;
//  }
//
//  /**
//   * Tests the two main methods in the Game class associated with Player movement, which are
//   * movePlayer() and getLeader(). Starts two players at the same start location and then
//   * moves Player 1 into the lead, asserts that the locations, leaders, and distances were properly
//   * updated, and then repeats the process with Player 2.
//   */
//  @Test
//  public void testMovePlayerAndGetLeader() {
//    // Move both players to the start location
//    this.game.movePlayer(1, this.startLat,this.startLng);
//    this.game.movePlayer(2, this.startLat,this.startLng);
//
//    // Store the original distance from the start to destination for comparison after players have
//    // moved away from the start
//    double originalDistToDest = this.player1.getDistToDest();
//
//    // Move Player 1 to nearest node right in front of Faunce Arch, which is much closer to
//    // the destination
//    this.game.movePlayer(1, 41.827051,-71.403059);
//
//    // Assert that the player was actually moved away from the start location
//    assertTrue(player1.getNode().getCoords().get(0) != this.startLat);
//    assertTrue(player1.getNode().getCoords().get(1) != this.startLng);
//
//    // Assert that Player 1's new distance to the destination is less than the original distance
//    assertTrue(originalDistToDest > this.player1.getDistToDest());
//
//    // Check that the correct leader is returned by the Game
//    assertEquals(this.game.getLeader(), "Player 1");
//
//    // Move Player 2 to nearest node right in front of Wayland Arch, which is now closer than
//    // Player 2
//    this.game.movePlayer(2, 41.824621,-71.402721);
//
//    // Assert that the player was actually moved away from the start location
//    assertTrue(player2.getNode().getCoords().get(0) != this.startLat);
//    assertTrue(player2.getNode().getCoords().get(1) != this.startLng);
//
//    // Assert that Player 2's new distance to the destination is less than Player 1's distance
//    assertTrue(this.player1.getDistToDest() > this.player2.getDistToDest());
//
//    // Check that the correct leader is returned by the Game
//    assertEquals(this.game.getLeader(), "Player 2");
//  }
//}
