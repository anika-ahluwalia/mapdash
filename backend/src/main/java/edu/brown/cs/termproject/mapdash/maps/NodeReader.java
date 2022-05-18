package edu.brown.cs.termproject.mapdash.maps;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * Class that implements LazyDijkstraReader that houses the cache from map node ids to corresponding
 * map node with its edges/ways field populated. This class queries the database to get edges for
 * a particular map node. These edges are then used to dynamically build a graph in Dijkstra.
 */
public class NodeReader implements LazyDijkstraReader<MapNode, Way> {
  private Connection conn;
  private Cache<String, MapNode> cache;
  private static final int MAX_SIZE = 10000;

  /**
   * Constructor that initializes the Guava cache to be maintained.
   */
  public NodeReader() {
    this.cache = CacheBuilder.newBuilder()
        .maximumSize(MAX_SIZE)
        .expireAfterWrite(5, TimeUnit.MINUTES)
        .build();
    this.conn = null;
  }

  /**
   * Method that lazily returns the edges of a node.
   * If the node is not already in the cache, query the database.
   * If the node is, then simply return from cache.
   * @param node - the node from which you want to find all edges leading from it
   * @return the Collection of Edges leading from the node.
   */
  public Collection<Way> getLazyEdges(MapNode node) {
    PreparedStatement prep = null;
    ResultSet rs = null;
    try {
      // Check if current node is already in cache
      if (cache.getIfPresent(node.getId()) == null) {
        // If not in cache, read in all neighbor nodes to the current node
        prep = this.conn.prepareStatement(
            "SELECT node.id, node.latitude, node.longitude, way.id FROM node, way"
                + " WHERE way.start = ? and node.id = way.end AND way.type NOT"
                + " IN ('', \"unclassified\");");
        prep.setString(1, node.getId());
        rs = prep.executeQuery();

        // Iterate through all connected neighbor nodes
        while (rs.next()) {
          String neighborId = rs.getString(1);
          // If the neighbor node isn't already in the cache, create a new one and cache it
          if (cache.getIfPresent(neighborId) == null) {
            MapNode neighborNode = new MapNode(rs.getString(1), rs.getDouble(2),
                rs.getDouble(3));
            // Create new Way between passed in node and neighbor node
            Way way1 = new Way(rs.getString(4), node, neighborNode);
            Way way2 = new Way(rs.getString(4)+"-"+2, neighborNode, node);
            node.addWay(neighborNode.getId(), way1);
            neighborNode.addWay(node.getId(), way2);
            // Cache passed in node
            cache.put(node.getId(), node);
          } else {
            // If the neighbor node is already in the cache, retrieve it and add a new way to
            // the current node
            MapNode neighborNode = cache.getIfPresent(neighborId);
            Way way1 = new Way(rs.getString(4), node, neighborNode);
            Way way2 = new Way(rs.getString(4)+"-"+2, neighborNode, node);
            node.addWay(neighborId, way1);
            neighborNode.addWay(node.getId(), way2);
            cache.put(node.getId(), node);
          }
        }
        return node.getEdges();
      }
      // If in cache, just return because the only way a node is in the cache is after all
      // the neighbors are queried already
      return cache.getIfPresent(node.getId()).getEdges();
    } catch (SQLException e) {
      System.out.println("ERROR: Database access error.");
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
        // Clear cache if we are loading a new database
        clearCache();
      } catch (SQLException e) {
        System.out.println("ERROR: Database access error.");
      }
    } catch (ClassNotFoundException e) {
      System.out.println("ERROR: Class cannot be located.");
    }
  }

  /**
   ** Helper method that clears the Guava cache.
   */
  public void clearCache() {
    this.cache.invalidateAll();
  }
}
