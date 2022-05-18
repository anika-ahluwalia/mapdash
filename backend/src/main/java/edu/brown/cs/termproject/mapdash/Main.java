package edu.brown.cs.termproject.mapdash;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Map;

import edu.brown.cs.termproject.mapdash.replAndParsing.REPL;
import edu.brown.cs.termproject.mapdash.maps.MapsInterpreter;
import edu.brown.cs.termproject.mapdash.websockets.GameWebSocket;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import spark.ExceptionHandler;
import spark.Request;
import spark.Response;
import spark.Spark;
import spark.Route;
import spark.template.freemarker.FreeMarkerEngine;

import com.google.common.collect.ImmutableMap;
import freemarker.template.Configuration;
import com.google.gson.Gson;

import java.util.ArrayList;

import org.json.JSONObject;



/**
 * The Main class of our project. This is where execution begins.
 *
 */
public final class Main {

  private static final int DEFAULT_PORT = 4567;
  private static final Gson GSON = new Gson();

  /**
   * The initial method called when execution begins.
   *
   * @param args
   *          An array of command line arguments
   */
  public static void main(String[] args) {
    new Main(args).run();
  }

  private String[] args;
  private MapsInterpreter mapsInterpreter;


  private Main(String[] args) {
    this.args = args;
    mapsInterpreter = new MapsInterpreter();
  }


  private void run() {
    // Parse command line arguments
    OptionParser parser = new OptionParser();
    parser.accepts("gui");
    parser.accepts("traffic");
    parser.accepts("port").withRequiredArg().ofType(Integer.class)
      .defaultsTo(DEFAULT_PORT);
    OptionSet options = parser.parse(args);

    if (options.has("gui")) {
      runSparkServer((int) options.valueOf("port"));
    }

    REPL repl = new REPL();
    repl.addAction("map", mapsInterpreter);
    repl.addAction("nearest", mapsInterpreter);
    repl.addAction("ways", mapsInterpreter);
    repl.addAction("route", mapsInterpreter);
    repl.run();
  }

  private static FreeMarkerEngine createEngine() {
    Configuration config = new Configuration();
    File templates = new File("src/main/resources/spark/template/freemarker");
    try {
      config.setDirectoryForTemplateLoading(templates);
    } catch (IOException ioe) {
      System.out.printf("ERROR: Unable use %s for template loading.%n", templates);
      System.exit(1);
    }
    return new FreeMarkerEngine(config);
  }

  private void runSparkServer(int port) {

    String localhost = "127.0.0.1";
    Spark.ipAddress(localhost);

    Spark.port(port);

    // We need to serve some simple static files containing CSS and JavaScript.
    // This tells Spark where to look for urls of the form "/static/*".
    Spark.externalStaticFileLocation("src/main/resources/static");

//    Spark.externalStaticFileLocation("src/main/resources/static");
//
//    Spark.options("/*", (request, response) -> {
//      String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
//      if (accessControlRequestHeaders != null) {
//        response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
//      }
//
//      String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
//
//      if (accessControlRequestMethod != null) {
//        response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
//      }
//      return "OK";
//    });
//    Spark.before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));

    // FreeMarkerEngine freeMarker = createEngine();

    Spark.webSocket("/game", GameWebSocket.class);

    Spark.exception(Exception.class, new ExceptionPrinter());

    // Setup Spark Routes
    Spark.post("/map", null);
  }

  /**
   * Display an error page when an exception occurs in the server.
   *
   */
  private static class ExceptionPrinter implements ExceptionHandler {
    @Override
    public void handle(Exception e, Request req, Response res) {
      res.status(500);
      StringWriter stacktrace = new StringWriter();
      try (PrintWriter pw = new PrintWriter(stacktrace)) {
        pw.println("<pre>");
        e.printStackTrace(pw);
        pw.println("</pre>");
      }
      res.body(stacktrace.toString());
    }
  }
}
