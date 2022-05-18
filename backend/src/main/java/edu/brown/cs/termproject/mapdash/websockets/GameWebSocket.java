package edu.brown.cs.termproject.mapdash.websockets;


import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import edu.brown.cs.termproject.mapdash.game.Game;
import edu.brown.cs.termproject.mapdash.game.Player;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;


/**
 * Websocket class that handles all messaging between the game's backend server and
 * the frontend for user sessions.
 */
@WebSocket(maxIdleTime = 1800000)
public class GameWebSocket {
  private static final Gson GSON = new Gson();
  private final ConcurrentMap<Session, Player> players = new ConcurrentHashMap<>();
  private final ConcurrentMap<String, Session> names = new ConcurrentHashMap<>();
  private final ConcurrentMap<Integer, Game> games = new ConcurrentHashMap<>();
  private final Queue<String> activePlayers = new ConcurrentLinkedQueue<>();
  private int sessionId = 0;
  private int latestGameID = 0;

  private enum MESSAGE_TYPE {
    CONNECT,
    USER,
    REQUEST,
    ACCEPT,
    DECLINE,
    ACTIVE,
    MAP,
    PLAY,
    MOVE,
    CANCEL,
    HINT,
    UPDATE,
    GAMEOVER,
    ERROR
  }

  @OnWebSocketConnect
  public void connected(Session session) throws IOException {
    // build connect message with unique session id
    JsonObject connect = new JsonObject();
    connect.addProperty("type", MESSAGE_TYPE.CONNECT.ordinal());
    JsonObject payload = new JsonObject();
    payload.addProperty("id", sessionId);
    sessionId += 1;
    connect.add("payload", payload);

    // send connect message back to client
    session.getRemote().sendString(GSON.toJson(connect));
  }

  @OnWebSocketClose
  public void closed(Session session, int statusCode, String reason) throws IOException {
    Player player = players.get(session);
    int gameId = player.getGameId();
    if (gameId != -1) {
      JsonObject cancel = new JsonObject();
      JsonObject payload = new JsonObject();
      cancel.addProperty("type", MESSAGE_TYPE.CANCEL.ordinal());
      cancel.add("payload", payload);

      if (games.get(gameId) != null) {
        for (String name : games.get(gameId).getPlayerNames()) {
          if (!name.equals(player.getName())) {
            names.get(name).getRemote().sendString(GSON.toJson(cancel));
            players.get(names.get(name)).setGameId(-1);
          }
        }
        games.remove(gameId);
      }
    }

    // remove session
    players.remove(session);
    String name = player.getName();
    names.remove(name);
    activePlayers.remove(name);

    // to let all other plays know that this player is no longer there
    this.updateActivePlayers();
  }

  @OnWebSocketMessage
  public void message(Session session, String message) throws IOException {
    // convert message to JsonObject and get message type
    JsonObject messageJson = GSON.fromJson(message, JsonObject.class);
    MESSAGE_TYPE type = MESSAGE_TYPE.values()[ messageJson.get("type").getAsInt()];
    JsonObject payload = messageJson.get("payload").getAsJsonObject();

//    System.out.println(type);
    System.out.println(message);
//    System.out.println(games);

    switch (type) {
      case USER:
        this.newUser(session, payload);
        break;
      case REQUEST:
        this.newRequest(session, payload);
        break;
      case ACCEPT:
        this.acceptRequest(session, payload);
        break;
      case DECLINE:
        this.declineRequest(session, payload);
        break;
      case MAP:
        break;
      case PLAY:
        this.playGame(session, payload);
        break;
      case MOVE:
        this.movePlayer(session, payload);
        break;
      case CANCEL:
        this.cancelRequest(session, payload);
        break;
      case HINT:
        this.giveHint(session, payload);
        break;
      case GAMEOVER:
        this.endGame(session, payload);
        break;
      default:
        break;
    }
  }

  @OnWebSocketError
  public void error(Session session, Throwable e) throws IOException {
    if (session.isOpen()) {
      JsonObject error = new JsonObject();
      error.addProperty("type", MESSAGE_TYPE.ERROR.ordinal());
      session.getRemote().sendString(GSON.toJson(error));
    }
    e.printStackTrace();
  }

  private void newUser(Session session, JsonObject messagePayload) throws IOException {
    String name = messagePayload.get("name").getAsString();
    if (names.containsKey(name) && !activePlayers.contains(name)) {
      activePlayers.add(name);
    } else {
      JsonObject user = new JsonObject();
      JsonObject payload = new JsonObject();
      user.addProperty("type", MESSAGE_TYPE.USER.ordinal());
      // if the name is unique, creates a new player
      if (!names.containsKey(name) && !activePlayers.contains(name)) {
        activePlayers.add(name);
        players.put(session, new Player(name));
        names.put(name, session);
        payload.addProperty("status", "success");
      } else {
        payload.addProperty("status", "failure");
      }
      user.add("payload", payload);
      // sends the status to the frontend
      session.getRemote().sendString(GSON.toJson(user));
    }
    this.updateActivePlayers();
  }

  // sends updated active players list to all players
  private void updateActivePlayers() throws IOException {
    JsonObject opponents = new JsonObject();
    JsonObject payload = new JsonObject();
    opponents.addProperty("type", MESSAGE_TYPE.ACTIVE.ordinal());
    String playersString = new Gson().toJson(activePlayers);
    payload.addProperty("players", playersString);
    opponents.add("payload", payload);

    for (Session session : players.keySet()) {
      session.getRemote().sendString(GSON.toJson(opponents));
      // System.out.println("updating list sent to: " + players.get(session).getName());
    }
  }

  private void newRequest(Session session, JsonObject messagePayload) throws IOException {
    String player2 = messagePayload.get("player2").getAsString();
    JsonObject request = new JsonObject();
    JsonObject payload = new JsonObject();
    request.addProperty("type", MESSAGE_TYPE.REQUEST.ordinal());
    request.add("payload", payload);
    payload.addProperty("player1", players.get(session).getName());
    payload.addProperty("player2", player2);
    payload.addProperty("location", messagePayload.get("location").getAsString());
    // removes both players from active players list
    activePlayers.remove(player2);
    activePlayers.remove(players.get(session).getName());
    this.updateActivePlayers();

    // sends request message to the requested user
    names.get(player2).getRemote().sendString(GSON.toJson(request));
  }

  private void acceptRequest(Session session, JsonObject messagePayload) throws IOException {
    String player1 = messagePayload.get("player1").getAsString();
    String location = messagePayload.get("location").getAsString();
    JsonArray destination = messagePayload.get("destination").getAsJsonArray();
    JsonArray start = messagePayload.get("start").getAsJsonArray();
    double destLat = destination.get(1).getAsJsonArray().get(0).getAsDouble();
    double destLng = destination.get(1).getAsJsonArray().get(1).getAsDouble();

    JsonObject accept = new JsonObject();
    JsonObject payload = new JsonObject();
    accept.addProperty("type", MESSAGE_TYPE.ACCEPT.ordinal());
    accept.add("payload", payload);
    payload.addProperty("player1", player1);
    payload.addProperty("player2", players.get(session).getName());
    payload.add("destination", destination);
    payload.add("start", start);

    games.put(latestGameID, new Game(players.get(names.get(player1)), players.get(session), latestGameID,
        destLat, destLng, location));
    payload.addProperty("gameId", latestGameID);
    latestGameID += 1;
    payload.addProperty("location", location);
    payload.addProperty("playerNumber", 1);
    // sends accept message to requesting user
    names.get(player1).getRemote().sendString(GSON.toJson(accept));

    // sends accept message to accepting user
    payload.remove("playerNumber");
    payload.addProperty("playerNumber", 2);
    session.getRemote().sendString(GSON.toJson(accept));
  }

  private void declineRequest(Session session, JsonObject messagePayload) throws IOException {
    String player1 = messagePayload.get("player1").getAsString();
    JsonObject decline = new JsonObject();
    JsonObject payload = new JsonObject();
    decline.addProperty("type", MESSAGE_TYPE.DECLINE.ordinal());
    decline.add("payload", payload);
    payload.addProperty("player1", player1);
    payload.addProperty("player2", players.get(session).getName());

    // adds both players back to active players
    if (!activePlayers.contains(player1)) {
      activePlayers.add(player1);
    }
    if (!activePlayers.contains(players.get(session).getName())) {
      activePlayers.add(players.get(session).getName());
    }
    this.updateActivePlayers();

    // sends decline message to requesting user
    names.get(player1).getRemote().sendString(GSON.toJson(decline));
  }

  private void cancelRequest(Session session, JsonObject messagePayload) throws IOException {
    String player2 = messagePayload.get("player2").getAsString();
    JsonObject cancel = new JsonObject();
    JsonObject payload = new JsonObject();
    cancel.addProperty("type", MESSAGE_TYPE.CANCEL.ordinal());
    cancel.add("payload", payload);

    // adds both players back to active players
    if (!activePlayers.contains(player2)) {
      activePlayers.add(player2);
    }
    if (!activePlayers.contains(players.get(session).getName())) {
      activePlayers.add(players.get(session).getName());
    }
    this.updateActivePlayers();

    // sends decline message to requesting user
    names.get(player2).getRemote().sendString(GSON.toJson(cancel));
    names.get(players.get(session).getName()).getRemote().sendString(GSON.toJson(cancel));
  }

  private void playGame(Session session, JsonObject messagePayload) throws IOException {
    Game game = games.get(messagePayload.get("gameId").getAsInt());
    JsonObject play = new JsonObject();
    JsonObject payload = new JsonObject();
    play.addProperty("type", MESSAGE_TYPE.PLAY.ordinal());
    payload.addProperty("gameId", game.getId());
    play.add("payload", payload);

    // send message to all players with status of game
    for (String name : game.getPlayerNames()) {
      names.get(name).getRemote().sendString(GSON.toJson(play));
    }
  }

  private void movePlayer(Session session, JsonObject messagePayload) throws IOException {
    Game game = games.get(messagePayload.get("gameId").getAsInt());
    if (game != null) {
      String playerName = messagePayload.get("playerName").getAsString();
      int playerNumber = game.getPlayerNumberFromName(playerName);
      String leader = game.movePlayer(playerNumber, messagePayload.get("newLat").getAsDouble(),
          messagePayload.get("newLong").getAsDouble());
      boolean isGameOver = game.isGameOver(messagePayload.get("newLat").getAsDouble(),
          messagePayload.get("newLong").getAsDouble());
      JsonObject move = new JsonObject();
      JsonObject payload = new JsonObject();
      move.addProperty("type", MESSAGE_TYPE.MOVE.ordinal());
      payload.addProperty("leader", leader);
      payload.addProperty("isGameOver", isGameOver);
      move.add("payload", payload);

      // send message to all players with status of game
      for (String name : game.getPlayerNames()) {
        names.get(name).getRemote().sendString(GSON.toJson(move));
      }
    }
  }

  private void endGame(Session session, JsonObject messagePayload) throws IOException {
    int id = messagePayload.get("gameId").getAsInt();
    JsonObject gameover = new JsonObject();
    JsonObject payload = new JsonObject();
    if (id == -1) {
      String player = messagePayload.get("winner").getAsString();
      gameover.addProperty("type", MESSAGE_TYPE.GAMEOVER.ordinal());
      payload.addProperty("winner", player);
      gameover.add("payload", payload);
      names.get(player).getRemote().sendString(GSON.toJson(gameover));
    } else {
      Game game = games.get(id);
      if (game != null) {
        String winner = game.getLeader();
        gameover.addProperty("type", MESSAGE_TYPE.GAMEOVER.ordinal());
        payload.addProperty("winner", winner);
        gameover.add("payload", payload);

        // send message to all players with the winner
        for (String name : game.getPlayerNames()) {
          names.get(name).getRemote().sendString(GSON.toJson(gameover));
          players.get(names.get(name)).setGameId(-1);
        }
        games.remove(id);
      }
    }
  }

  private void giveHint(Session session, JsonObject messagePayload) throws IOException {
    Game game = games.get(messagePayload.get("gameId").getAsInt());
    String playerName = messagePayload.get("playerName").getAsString();
    int playerNumber = game.getPlayerNumberFromName(playerName);
    double lat = game.getPlayerNode(playerNumber).get(0);
    double lng = game.getPlayerNode(playerNumber).get(1);
    double heading = game.givePlayerHeading(playerNumber);

    JsonObject hint = new JsonObject();
    JsonObject payload = new JsonObject();
    hint.addProperty("type", MESSAGE_TYPE.HINT.ordinal());
    payload.addProperty("heading", heading);
    payload.addProperty("lat", lat);
    payload.addProperty("lng", lng);
    hint.add("payload", payload);

    String name = game.getPlayerNameFromNumber(playerNumber);

    names.get(name).getRemote().sendString(GSON.toJson(hint));

  }
}
