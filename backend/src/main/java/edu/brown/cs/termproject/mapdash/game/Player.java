package edu.brown.cs.termproject.mapdash.game;

import edu.brown.cs.termproject.mapdash.maps.MapNode;
import edu.brown.cs.termproject.mapdash.maps.RouteFinder;
import edu.brown.cs.termproject.mapdash.maps.Way;

import java.util.ArrayList;
import java.util.List;

public class Player {

  private final String name;
  private MapNode node;
  private int gameId;
  private List<Way> routeToDestination;
  private double distToDest;

  public Player(String playerName) {
    name = playerName;
    this.routeToDestination = new ArrayList<>();
    this.distToDest = 0.0;
  }

  public String getName() {
    return name;
  }

  public void setNode(MapNode node) {
    this.node = node;
  }

  public MapNode getNode() {
    return node;
  }

  public void calculateRouteToDestination(RouteFinder finder, MapNode dest){

    try {
      if (node != null) {
        this.routeToDestination = finder.findRoute(node, dest);
        double routeLength = 0.0;
        for (Way way : this.routeToDestination) {
          routeLength += way.getWeight();
        }
        this.distToDest = routeLength;
      }
    } catch (Exception e) {
//      e.printStackTrace();
      System.out.println("weird dijkstra error");
    }

  }

  public double getDistToDest(){
    return this.distToDest;
  }

  public double getHeadingToNextNode() {
    System.out.println("ROUTE: " + this.routeToDestination);
    Way first = this.routeToDestination.get(0);
    double startLat = first.getStart().getCoords().get(0) * Math.PI / 180;
    double startLng = first.getStart().getCoords().get(1) * Math.PI / 180;
    double endLat = first.getEnd().getCoords().get(0) * Math.PI / 180;
    double endLng = first.getEnd().getCoords().get(1) * Math.PI / 180;

    double y = Math.sin(endLng - startLng) * Math.cos(endLat);
    double x = Math.cos(startLat) * Math.sin(endLat) - Math.sin(startLat) * Math.cos(endLat)
        * Math.cos(endLng - startLng);
    double theta = Math.atan2(y, x);
    return (theta * 180 / Math.PI + 360) % 360;
  }

  public int getGameId() { return gameId; }

  public void setGameId(int gameId) { this.gameId = gameId; }
}
