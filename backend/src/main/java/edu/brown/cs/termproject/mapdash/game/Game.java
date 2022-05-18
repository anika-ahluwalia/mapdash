package edu.brown.cs.termproject.mapdash.game;

import edu.brown.cs.termproject.mapdash.maps.MapNode;
import edu.brown.cs.termproject.mapdash.maps.MapsInterpreter;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class Game {

  private Player player1;
  private Player player2;
  private int id;
  private double destLat;
  private double destLng;
  private MapNode nearestDestNode;
  private MapsInterpreter mapsInterpreter;
  private static final double WIN_CONDITION_RADIUS = 0.0000005;

  public Game(Player p1, Player p2, int id, double destLat, double destLng, String location) {
    this.player1 = p1;
    player1.setGameId(id);
    this.player2 = p2;
    player2.setGameId(id);
    this.id = id;
    this.destLat = destLat;
    this.destLng = destLng;

    mapsInterpreter = new MapsInterpreter();
    String database = "data/" + location + ".sqlite3";
    mapsInterpreter.loadMapData(database);
    this.nearestDestNode = mapsInterpreter.getNearest(Arrays.asList(this.destLat, this.destLng));
  }

  public int getId() {
    return id;
  }

  public MapNode getNearestDestNode() {
    return this.nearestDestNode;
  }

  public String getLeader() {

    // any time either player moves, get the leader and send it to
    // front end for both players to display / update
    if (player1.getDistToDest() > player2.getDistToDest()) {
      return player2.getName();
    } else if (player1.getDistToDest() < player2.getDistToDest()) {
      return player1.getName();
    }
    return "Tied";
  }

  public boolean isGameOver(double newLat, double newLong) {
    MapNode currentNode = new MapNode(null, newLat, newLong);
    return currentNode.calculateDistanceApart(Arrays.asList(destLat, destLng))
        < WIN_CONDITION_RADIUS;
  }

  public String movePlayer(int playerNumber, double newLat, double newLong) {
    List<Double> coordinates = new ArrayList<>() {{ add(newLat); add(newLong); }};
    MapNode nearest = mapsInterpreter.getNearest(coordinates);
    if (playerNumber == 1) {
      player1.setNode(nearest);
      player1.calculateRouteToDestination(mapsInterpreter.getFinder(), nearestDestNode);
    } else {
      player2.setNode(nearest);
      player2.calculateRouteToDestination(mapsInterpreter.getFinder(), nearestDestNode);
    }
    return this.getLeader();
  }

  public double givePlayerHeading(int playerNumber) {
    if (playerNumber == 1) {
      return player1.getHeadingToNextNode();
    } else {
      return player2.getHeadingToNextNode();
    }
  }

  public List<Double> getPlayerNode(int playerNumber) {
    if (playerNumber == 1) {
      return player1.getNode().getCoords();
    } else {
      return player2.getNode().getCoords();
    }
  }

  public String[] getPlayerNames() {
    return new String[]{player1.getName(), player2.getName()};
  }

  public String getPlayerNameFromNumber(int playerNumber) {
    if (playerNumber == 1) {
      return player1.getName();
    } else {
      return player2.getName();
    }
  }

  public int getPlayerNumberFromName(String name) {
    if (player1.getName().equals(name)) {
      return 1;
    } else {
      return 2;
    }
  }
}
