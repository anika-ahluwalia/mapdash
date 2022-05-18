//package edu.brown.cs.termproject.mapdash.websockets;
//
//import com.google.gson.Gson;
//import com.google.gson.JsonObject;
//import org.eclipse.jetty.websocket.api.Session;
//import org.eclipse.jetty.websocket.api.RemoteEndpoint;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.FixMethodOrder;
//import org.junit.Test;
//import org.junit.runners.MethodSorters;
//import org.mockito.Mockito;
//import org.mockito.invocation.InvocationOnMock;
//import org.mockito.stubbing.Answer;
//
//import java.io.IOException;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertTrue;
//
///**
// * This suite of tests uses Mockito to mock client Session and RemoteEndpoints and then
// * checks that GameWebSocket properly responds to each message type and handles connecting, closing,
// * and socket errors. The test names have ascending letters after "test" so that JUnit runs all of
// * these tests in order because the GameWebSocket data structures are all static and are not
// * cleared between tests.
// */
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
//public class GameWebSocketTest {
//
//  private GameWebSocket socket;
//
//  /**
//   * Sets up a new GameWebSocket that mock clients can send messages to
//   */
//  @Before
//  public void setUp() {
//    this.socket = new GameWebSocket();
//  }
//
//  /**
//   * Resets all of the main instance variables.
//   */
//  @After
//  public void tearDown() {
//    this.socket = null;
//  }
//
//  /**
//   * Tests that the CONNECT message works properly and sends back the correct response when
//   * a client connects.
//   */
//  @Test
//  public void testAConnected() throws IOException {
//    Session s = Mockito.mock(Session.class);
//    RemoteEndpoint re = Mockito.mock(RemoteEndpoint.class);
//
//    // When session.getRemote() is called in GameWebSocket, return the mocked RemoteEndpoint
//    Mockito.when(s.getRemote()).thenReturn(re);
//
//    // When re.sendString() is called, extract the argument passed to the function, which is the
//    // message, and run asserts on this message
//    Mockito.doAnswer(invocation -> {
//      Gson g = new Gson();
//      String stringResponse = invocation.getArgument(0);
//      JsonObject response = g.fromJson(stringResponse, JsonObject.class);
//
//      // Assert that the message type is 0 (CONNECT)
//      assertEquals(response.get("type").getAsInt(), 0);
//      // Assert that the CONNECT message returns a session ID of 0 because this is the only client
//      assertEquals(response.get("payload").getAsJsonObject().get("id").getAsInt(), 0);
//
//      return null;
//    }).when(re).sendString(Mockito.anyString());
//
//    // Call the connected method in GameWebSocket
//    this.socket.connected(s);
//  }
//
//  /**
//   * Tests that the USER message works properly and sends back a message containing a success status
//   * if the client has a unique user name, otherwise it should send back a failure status. Also,
//   * ensures that the updateActivePlayers() method is being called when a new user successfully
//   * registers and that this method returns back a message containing all of the active players.
//   */
//  @Test
//  public void testBNewUser() throws IOException {
//    Session s = Mockito.mock(Session.class);
//    RemoteEndpoint re = Mockito.mock(RemoteEndpoint.class);
//
//    // When session.getRemote() is called in GameWebSocket, return the mocked RemoteEndpoint
//    Mockito.when(s.getRemote()).thenReturn(re);
//
//    Mockito.doAnswer(new Answer() {
//      Gson g = new Gson();
//      private boolean isFirstUser = true;
//
//      public Object answer(InvocationOnMock invocation) {
//        // When re.sendString() is called, extract the argument passed to the function, which is the
//        // message, and run asserts on this message
//        String stringResponse = invocation.getArgument(0);
//        JsonObject response = g.fromJson(stringResponse, JsonObject.class);
//
//        // Assert that the USER message returns a success status if this is the first user
//        // This check is necessary because we test attempting to connect a second user with the
//        // same name
//        if (isFirstUser) {
//          if (response.get("type").getAsInt() == 1) {
//            assertEquals(response.get("payload").getAsJsonObject().get("status").getAsString(),
//                "success");
//          } else if (response.get("type").getAsInt() == 5){
//            // Assert that the USER message updates the list of active players and returns an ACTIVE
//            // message (type 5) with the new user's name inside the list
//            assertTrue(response.get("payload").getAsJsonObject().get("players").getAsString().contains("player0"));
//            isFirstUser = false;
//          }
//        } else {
//          if (response.get("type").getAsInt() == 1) {
//            // Makes sure that when we try to connect a second user with the same name, we
//            // get a failure status
//            assertEquals(response.get("payload").getAsJsonObject().get("status").getAsString(),
//                "failure");
//          }
//        }
//        return null;
//      }
//    }).when(re).sendString(Mockito.anyString());
//
//    // The mocked message that the client would send to the socket
//    String userMessage = "{\"type\":1,\"payload\":{\"name\":\"player0\"}}";
//
//    // Call the newUser method for the first time in GameWebSocket
//    this.socket.message(s, userMessage);
//
//    // Call the newUser method for the second time in GameWebSocket with the same name to
//    // ensure that the user isn't added (see doAnswer function above)
//    this.socket.message(s, userMessage);
//  }
//
//  /**
//   * Tests that the REQUEST message takes a request from one player and correctly returns
//   * a message with the requesting and receiving players and the location/map that the requesting
//   * player is playing on.
//   */
//  @Test
//  public void testCNewRequest() throws IOException {
//    Session s1 = Mockito.mock(Session.class);
//    RemoteEndpoint re1 = Mockito.mock(RemoteEndpoint.class);
//    Session s2 = Mockito.mock(Session.class);
//    RemoteEndpoint re2 = Mockito.mock(RemoteEndpoint.class);
//
//    // When session.getRemote() is called in GameWebSocket, return the mocked RemoteEndpoint
//    Mockito.when(s1.getRemote()).thenReturn(re1);
//    Mockito.when(s2.getRemote()).thenReturn(re2);
//
//    // Send USER messages to add the two players
//    String userMessage1 = "{\"type\":1,\"payload\":{\"name\":\"player1\"}}";
//    String userMessage2 = "{\"type\":1,\"payload\":{\"name\":\"player2\"}}";
//    this.socket.message(s1, userMessage1);
//    this.socket.message(s2, userMessage2);
//
//    // When re2.sendString() is called, extract the argument passed to the function, which is the
//    // message, and run asserts on this message
//    Mockito.doAnswer(invocation -> {
//      Gson g = new Gson();
//      String stringResponse = invocation.getArgument(0);
//      JsonObject response = g.fromJson(stringResponse, JsonObject.class);
//
//      // Only assert on messages of type 2 (REQUEST)
//      if (response.get("type").getAsInt() == 2) {
//        // Assert that the REQUEST message returns the two players, with player1 being the player who sent
//        // the request and player2 being the player receiving the request
//        assertEquals(response.get("payload").getAsJsonObject().get("player1").getAsString(), "player1");
//        assertEquals(response.get("payload").getAsJsonObject().get("player2").getAsString(), "player2");
//        // Assert that the location (map) that player1 is requesting to play on, is returned correctly
//        assertEquals(response.get("payload").getAsJsonObject().get("location").getAsString(), "Providence");
//      }
//      return null;
//    }).when(re2).sendString(Mockito.anyString());
//
//    // Send a REQUEST message from player1 to player2
//    String requestMessage = "{\"type\":2,\"payload\":{\"player2\":\"player2\",\"location\":\"Providence\"}}";
//    this.socket.message(s1, requestMessage);
//  }
//
//  /**
//   * Tests that the ACCEPT message sends a message back to the requesting player that the
//   * receiving user has accepted their request and that the message contains all of the required
//   * information.
//   */
//  @Test
//  public void testDAcceptRequest() throws IOException {
//    // Players 3 and 4
//    Session s1 = Mockito.mock(Session.class);
//    RemoteEndpoint re1 = Mockito.mock(RemoteEndpoint.class);
//    Session s2 = Mockito.mock(Session.class);
//    RemoteEndpoint re2 = Mockito.mock(RemoteEndpoint.class);
//
//    // When session.getRemote() is called in GameWebSocket, return the mocked RemoteEndpoint
//    Mockito.when(s1.getRemote()).thenReturn(re1);
//    Mockito.when(s2.getRemote()).thenReturn(re2);
//
//    // Send USER messages to add the two players
//    String userMessage1 = "{\"type\":1,\"payload\":{\"name\":\"player3\"}}";
//    String userMessage2 = "{\"type\":1,\"payload\":{\"name\":\"player4\"}}";
//    this.socket.message(s1, userMessage1);
//    this.socket.message(s2, userMessage2);
//
//    // When re1.sendString() is called, extract the argument passed to the function, which is the
//    // message, and run asserts on this message
//    Mockito.doAnswer(invocation -> {
//      Gson g = new Gson();
//      String stringResponse = invocation.getArgument(0);
//      JsonObject response = g.fromJson(stringResponse, JsonObject.class);
//
//      // Only assert on messages of type 3 (ACCEPT)
//      if (response.get("type").getAsInt() == 3) {
//        // Assert that the ACCEPT message returns the two players, with player1 being the player who sent
//        // the request and player2 being the player who accepted it
//        assertEquals(response.get("payload").getAsJsonObject().get("player1").getAsString(), "player3");
//        assertEquals(response.get("payload").getAsJsonObject().get("player2").getAsString(), "player4");
//
//        // Assert that the destination and start fields in the message are the correct sizes
//        assertEquals(response.get("payload").getAsJsonObject().get("destination").getAsJsonArray().size(), 3);
//        assertEquals(response.get("payload").getAsJsonObject().get("start").getAsJsonArray().size(),2);
//
//        // Assert that the gameId is 0 because this is the first test that is initializing a Game
//        assertEquals(response.get("payload").getAsJsonObject().get("gameId").getAsInt(), 0);
//
//        // Assert that the location (map) is properly returned
//        assertEquals(response.get("payload").getAsJsonObject().get("location").getAsString(), "Providence");
//
//        // Assert that the playerNumber is correct for the player the message is being returned to
//        // In this case, it should be 1 since this is player1
//        assertEquals(response.get("payload").getAsJsonObject().get("playerNumber").getAsInt(), 1);
//      }
//      return null;
//    }).when(re1).sendString(Mockito.anyString());
//
//    // Send an ACCEPT message to initialize a game
//    String acceptMessage = "{\"type\":3,\"payload\":{\"player1\":\"player3\",\"destination\":"
//        + "[\"Roger Williams National Memorial\",[41.8313318,-71.4107403],\"https://testlink\"],"
//        + "\"start\":[\"panoID\", [41.82706546234948,-71.40819461143865]],\"location\":\"Providence\"}}";
//    this.socket.message(s2, acceptMessage);
//  }
//
//  /**
//   * Tests that the DECLINE message sends back a message to the requesting player from the
//   * receiving player who is declining the request and that the message contains all of the
//   * required information.
//   */
//  @Test
//  public void testEDeclineRequest() throws IOException {
//    Session s1 = Mockito.mock(Session.class);
//    RemoteEndpoint re1 = Mockito.mock(RemoteEndpoint.class);
//    Session s2 = Mockito.mock(Session.class);
//    RemoteEndpoint re2 = Mockito.mock(RemoteEndpoint.class);
//
//    // When session.getRemote() is called in GameWebSocket, return the mocked RemoteEndpoint
//    Mockito.when(s1.getRemote()).thenReturn(re1);
//    Mockito.when(s2.getRemote()).thenReturn(re2);
//
//    // Send USER messages to add the two players
//    String userMessage1 = "{\"type\":1,\"payload\":{\"name\":\"player5\"}}";
//    String userMessage2 = "{\"type\":1,\"payload\":{\"name\":\"player6\"}}";
//    this.socket.message(s1, userMessage1);
//    this.socket.message(s2, userMessage2);
//
//    // When re1.sendString() is called, extract the argument passed to the function, which is the
//    // message, and run asserts on this message
//    Mockito.doAnswer(invocation -> {
//      Gson g = new Gson();
//      String stringResponse = invocation.getArgument(0);
//      JsonObject response = g.fromJson(stringResponse, JsonObject.class);
//
//      // Only assert on messages of type 4 (DECLINE)
//      if (response.get("type").getAsInt() == 4) {
//        // Assert that the DECLINE message returns the correct name of the requesting player, which
//        // is player5 in this case, and the declining player, which is player6.
//        assertEquals(response.get("payload").getAsJsonObject().get("player1").getAsString(), "player5");
//        assertEquals(response.get("payload").getAsJsonObject().get("player2").getAsString(), "player6");
//      }
//      return null;
//    }).when(re1).sendString(Mockito.anyString());
//
//    // Send a DECLINE message to have the request receiving player decline the request
//    String declineMessage = "{\"type\":4,\"payload\":{\"player1\":\"player5\"}}";
//    this.socket.message(s2, declineMessage);
//  }
//
//  /**
//   * Tests that the CANCEL message sends back a message to both clients that the request has been
//   * canceled by either of the players.
//   */
//  @Test
//  public void testFCancelRequest() throws IOException {
//    Session s1 = Mockito.mock(Session.class);
//    RemoteEndpoint re1 = Mockito.mock(RemoteEndpoint.class);
//    Session s2 = Mockito.mock(Session.class);
//    RemoteEndpoint re2 = Mockito.mock(RemoteEndpoint.class);
//
//    // When session.getRemote() is called in GameWebSocket, return the mocked RemoteEndpoint
//    Mockito.when(s1.getRemote()).thenReturn(re1);
//    Mockito.when(s2.getRemote()).thenReturn(re2);
//
//    // Send USER messages to add the two players
//    String userMessage1 = "{\"type\":1,\"payload\":{\"name\":\"player7\"}}";
//    String userMessage2 = "{\"type\":1,\"payload\":{\"name\":\"player8\"}}";
//    this.socket.message(s1, userMessage1);
//    this.socket.message(s2, userMessage2);
//
//    // Send a CANCEL message to have the request receiving player decline the request
//    String cancelMessage = "{\"type\":9,\"payload\":{\"player2\":\"player8\"}}";
//    this.socket.message(s2, cancelMessage);
//
//    // Since the CANCEL message doesn't return anything in its payload, just verify that the clients
//    // are sent the desired number of messages throughout this test (via RemoteEndpoint's sendString() method).
//    Mockito.verify(re1, Mockito.times(4)).sendString(Mockito.anyString());
//    Mockito.verify(re2, Mockito.times(5)).sendString(Mockito.anyString());
//  }
//
//  /**
//   * Tests that the PLAY message functions properly and correctly sends back the game ID. This
//   * test requires some setup using the USER and ACCEPT messages to add two players and initialize
//   * a game, but those messages are tested elsewhere.
//   */
//  @Test
//  public void testGPlayGame() throws IOException {
//    Session s1 = Mockito.mock(Session.class);
//    RemoteEndpoint re1 = Mockito.mock(RemoteEndpoint.class);
//    Session s2 = Mockito.mock(Session.class);
//    RemoteEndpoint re2 = Mockito.mock(RemoteEndpoint.class);
//
//    // When session.getRemote() is called in GameWebSocket, return the mocked RemoteEndpoint
//    Mockito.when(s1.getRemote()).thenReturn(re1);
//    Mockito.when(s2.getRemote()).thenReturn(re2);
//
//    // Send USER messages to add the two players
//    String userMessage1 = "{\"type\":1,\"payload\":{\"name\":\"player8\"}}";
//    String userMessage2 = "{\"type\":1,\"payload\":{\"name\":\"player9\"}}";
//    this.socket.message(s1, userMessage1);
//    this.socket.message(s2, userMessage2);
//
//    // Send an ACCEPT message to initialize a game
//    String acceptMessage = "{\"type\":3,\"payload\":{\"player1\":\"player8\",\"destination\":"
//        + "[\"Roger Williams National Memorial\",[41.8313318,-71.4107403],\"https://testlink\"],"
//        + "\"start\":[\"panoID\", [41.82706546234948,-71.40819461143865]],\"location\":\"Providence\"}}";
//    this.socket.message(s2, acceptMessage);
//
//    // When re1.sendString() is called, extract the argument passed to the function, which is the
//    // message, and run asserts on this message
//    Mockito.doAnswer(invocation -> {
//      Gson g = new Gson();
//      String stringResponse = invocation.getArgument(0);
//      JsonObject response = g.fromJson(stringResponse, JsonObject.class);
//
//      // Only assert on messages of type 7 (PLAY)
//      if (response.get("type").getAsInt() == 7) {
//        // Assert that the PLAY message returns a game ID of 1 because this is the second game to
//        // be initialized (one was initialized in testAcceptRequest() )
//        assertEquals(response.get("payload").getAsJsonObject().get("gameId").getAsInt(), 1);
//      }
//      return null;
//    }).when(re1).sendString(Mockito.anyString());
//
////    // Send a PLAY message to start a game
////    String playMessage = "{\"type\":7,\"payload\":{\"gameId\":1}}";
////    this.socket.message(s1, playMessage);
//  }
//
//  /**
//   * Tests that the MOVE message actually moves the required player to a new location and sends
//   * back the new leader after that move.
//   */
//  @Test
//  public void testHMovePlayer() throws IOException {
//    Session s1 = Mockito.mock(Session.class);
//    RemoteEndpoint re1 = Mockito.mock(RemoteEndpoint.class);
//    Session s2 = Mockito.mock(Session.class);
//    RemoteEndpoint re2 = Mockito.mock(RemoteEndpoint.class);
//
//    // When session.getRemote() is called in GameWebSocket, return the mocked RemoteEndpoint
//    Mockito.when(s1.getRemote()).thenReturn(re1);
//    Mockito.when(s2.getRemote()).thenReturn(re2);
//
//    // Send USER messages to add the two players
//    String userMessage1 = "{\"type\":1,\"payload\":{\"name\":\"player10\"}}";
//    String userMessage2 = "{\"type\":1,\"payload\":{\"name\":\"player11\"}}";
//    this.socket.message(s1, userMessage1);
//    this.socket.message(s2, userMessage2);
//
//    // Send an ACCEPT message to initialize a game with the start at Thayer and Waterman and the
//    // destination at Thayer and Power
//    String acceptMessage = "{\"type\":3,\"payload\":{\"player1\":\"player10\",\"destination\":"
//        + "[\"Thayer and Power\",[41.822990,-71.400045],\"https://testlink\"],"
//        + "\"start\":[\"panoID\", [41.827276,-71.400571]],\"location\":\"Providence\"}}";
//    this.socket.message(s2, acceptMessage);
//
//    // When re1.sendString() is called, extract the argument passed to the function, which is the
//    // message, and run asserts on this message
//    Mockito.doAnswer(invocation -> {
//      Gson g = new Gson();
//      String stringResponse = invocation.getArgument(0);
//      JsonObject response = g.fromJson(stringResponse, JsonObject.class);
//
//      // Only assert on messages of type 8 (MOVE)
//      if (response.get("type").getAsInt() == 8) {
//        // Assert that the MOVE message returns a payload with the name of the leader and that this
//        // name isn't null and has a length > 0
//        assertTrue(response.get("payload").getAsJsonObject().get("leader").getAsString() != null);
//        assertTrue(response.get("payload").getAsJsonObject().get("leader").getAsString().length() > 0);
//      }
//      return null;
//    }).when(re1).sendString(Mockito.anyString());
//
//    // Send a MOVE message that moves player10 to the start location
//    String moveMessage = "{\"type\":8,\"payload\":{\"gameId\":2,\"playerName\":\"player10\","
//        + "\"newLat\":41.827276,\"newLong\":-71.400571}}";
//    this.socket.message(s1, moveMessage);
//  }
//
//  /**
//   * Tests that the HINT message sends back the new heading, lat, and lng to the player that
//   * is using the hint.
//   */
//  @Test
//  public void testIGiveHint() throws IOException {
//    Session s1 = Mockito.mock(Session.class);
//    RemoteEndpoint re1 = Mockito.mock(RemoteEndpoint.class);
//    Session s2 = Mockito.mock(Session.class);
//    RemoteEndpoint re2 = Mockito.mock(RemoteEndpoint.class);
//
//    // When session.getRemote() is called in GameWebSocket, return the mocked RemoteEndpoint
//    Mockito.when(s1.getRemote()).thenReturn(re1);
//    Mockito.when(s2.getRemote()).thenReturn(re2);
//
//    // Send USER messages to add the two players
//    String userMessage1 = "{\"type\":1,\"payload\":{\"name\":\"player12\"}}";
//    String userMessage2 = "{\"type\":1,\"payload\":{\"name\":\"player13\"}}";
//    this.socket.message(s1, userMessage1);
//    this.socket.message(s2, userMessage2);
//
//    // Send an ACCEPT message to initialize a game with the start at Thayer and Waterman and the
//    // destination at Thayer and Power
//    String acceptMessage = "{\"type\":3,\"payload\":{\"player1\":\"player12\",\"destination\":"
//        + "[\"Thayer and Power\",[41.822990,-71.400045],\"https://testlink\"],"
//        + "\"start\":[\"panoID\", [41.827276,-71.400571]],\"location\":\"Providence\"}}";
//    this.socket.message(s2, acceptMessage);
//
//    // Send a MOVE message that moves player12 to the start location
//    String moveMessage = "{\"type\":8,\"payload\":{\"gameId\":3,\"playerName\":\"player12\","
//        + "\"newLat\":41.827276,\"newLong\":-71.400571}}";
//    this.socket.message(s1, moveMessage);
//
//    // When re1.sendString() is called, extract the argument passed to the function, which is the
//    // message, and run asserts on this message
//    Mockito.doAnswer(invocation -> {
//      Gson g = new Gson();
//      String stringResponse = invocation.getArgument(0);
//      JsonObject response = g.fromJson(stringResponse, JsonObject.class);
//
//      // Only assert on messages of type 10 (HINT)
//      if (response.get("type").getAsInt() == 10) {
//        // Assert that the HINT message returns a payload with the heading where the player should
//        // be directed, and that heading is between 0 and 360 degrees
//        assertTrue(response.get("payload").getAsJsonObject().get("heading").getAsDouble() >= 0.0
//            && response.get("payload").getAsJsonObject().get("heading").getAsDouble() <= 360.0);
//
//        // Assert that the HINT message also returns lat and lng values that aren't the start location
//        assertTrue(response.get("payload").getAsJsonObject().get("lat").getAsDouble() != 41.827276);
//        assertTrue(response.get("payload").getAsJsonObject().get("lng").getAsDouble() != -71.400571);
//      }
//      return null;
//    }).when(re1).sendString(Mockito.anyString());
//
////    // Send a HINT message that generates a hint for player12
////    String hintMessage = "{\"type\":10,\"payload\":{\"gameId\":3,\"playerName\":\"player12\"}}";
////    this.socket.message(s1, hintMessage);
//  }
//
//  /**
//   * Tests that the GAMEOVER message sends the winner of the game to both players.
//   */
//  @Test
//  public void testJEndGame() throws IOException {
//    Session s1 = Mockito.mock(Session.class);
//    RemoteEndpoint re1 = Mockito.mock(RemoteEndpoint.class);
//    Session s2 = Mockito.mock(Session.class);
//    RemoteEndpoint re2 = Mockito.mock(RemoteEndpoint.class);
//
//    // When session.getRemote() is called in GameWebSocket, return the mocked RemoteEndpoint
//    Mockito.when(s1.getRemote()).thenReturn(re1);
//    Mockito.when(s2.getRemote()).thenReturn(re2);
//
//    // Send USER messages to add the two players
//    String userMessage1 = "{\"type\":1,\"payload\":{\"name\":\"player14\"}}";
//    String userMessage2 = "{\"type\":1,\"payload\":{\"name\":\"player15\"}}";
//    this.socket.message(s1, userMessage1);
//    this.socket.message(s2, userMessage2);
//
//    // Send an ACCEPT message to initialize a game with the start at Thayer and Waterman and the
//    // destination at Thayer and Power
//    String acceptMessage = "{\"type\":3,\"payload\":{\"player1\":\"player14\",\"destination\":"
//        + "[\"Thayer and Power\",[41.822990,-71.400045],\"https://testlink\"],"
//        + "\"start\":[\"panoID\", [41.827276,-71.400571]],\"location\":\"Providence\"}}";
//    this.socket.message(s2, acceptMessage);
//
//    // When re1.sendString() is called, extract the argument passed to the function, which is the
//    // message, and run asserts on this message
//    Mockito.doAnswer(invocation -> {
//      Gson g = new Gson();
//      String stringResponse = invocation.getArgument(0);
//      JsonObject response = g.fromJson(stringResponse, JsonObject.class);
//
//      // Only assert on messages of type 12 (GAMEOVER)
//      if (response.get("type").getAsInt() == 12) {
//        // Assert that the GAMEOVER message returns a payload with the winner and that the winner
//        // is not null and has a length > 0
//        assertTrue(response.get("payload").getAsJsonObject().get("winner").getAsString() != null);
//        assertTrue(response.get("payload").getAsJsonObject().get("winner").getAsString().length() > 0);
//      }
//      return null;
//    }).when(re1).sendString(Mockito.anyString());
//
//    // Send a GAMEOVER message to end the game
//    String gameoverMessage = "{\"type\":12,\"payload\":{\"gameId\":\"4\"}}";
//    this.socket.message(s1, gameoverMessage);
//  }
//
//  /**
//   * Tests that the ERROR message handles all socket-related errors and notifies the session
//   * who encountered the error.
//   */
//  @Test
//  public void testKError() throws IOException {
//    Session s = Mockito.mock(Session.class);
//    RemoteEndpoint re = Mockito.mock(RemoteEndpoint.class);
//    Throwable e = Mockito.mock(Throwable.class);
//
//    // When session.getRemote() is called in GameWebSocket, return the mocked RemoteEndpoint
//    Mockito.when(s.getRemote()).thenReturn(re);
//
//    // When re.sendString() is called, extract the argument passed to the function, which is the
//    // message, and run asserts on this message
//    Mockito.doAnswer(invocation -> {
//      Gson g = new Gson();
//      String stringResponse = invocation.getArgument(0);
//      JsonObject response = g.fromJson(stringResponse, JsonObject.class);
//
//      // Assert that the message type is 13 (ERROR)
//      assertEquals(response.get("type").getAsInt(), 13);
//      return null;
//    }).when(re).sendString(Mockito.anyString());
//
//    // Call the error() method in GameWebSocket
//    this.socket.error(s, e);
//  }
//
//  /**
//   * Tests that the socket handles closed connections correctly.
//   */
//  @Test
//  public void testLClosed() throws IOException {
//    Session s = Mockito.mock(Session.class);
//    RemoteEndpoint re = Mockito.mock(RemoteEndpoint.class);
//
//    // When session.getRemote() is called in GameWebSocket, return the mocked RemoteEndpoint
//    Mockito.when(s.getRemote()).thenReturn(re);
//
//    // Send USER messages to add one player
//    String userMessage = "{\"type\":1,\"payload\":{\"name\":\"player16\"}}";
//    this.socket.message(s, userMessage);
//
//    // Close the client with status code 1001, which is the standard code called when
//    // a user closes their tab/browser
//    this.socket.closed(s, 1001, null);
//
//    // Verify that the client only receives 2 messages, which are both from the earlier USER message.
//    // If the client wasn't properly closed and removed, they would receive more messages
//    // because the server immediately sends out an ACTIVE message after the connection is closed
//    // to notify other players that this client has left
//    Mockito.verify(re, Mockito.times(2)).sendString(Mockito.anyString());
//  }
//}
