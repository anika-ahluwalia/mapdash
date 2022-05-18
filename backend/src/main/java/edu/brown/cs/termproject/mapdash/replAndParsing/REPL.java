package edu.brown.cs.termproject.mapdash.replAndParsing;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * REPL that takes implementations of TriggerActions to respond to commands.
 */
public class REPL {
  private final HashMap<String, TriggerAction> map = new HashMap<>();
  private final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

  /**
   * Add a TriggerAction to the REPL.
   * @param s Id string
   * @param ta TriggerAction
   */
  public void addAction(String s, TriggerAction ta) {
    map.put(s, ta);
  }

  /**
   * Executes the action method of a TriggerAction.
   * @param s Id of TriggerAction
   * @param input ArrayList of Strings to pass into action method
   */
  public void executeAction(String s, ArrayList<String> input) {
    if (map.containsKey(s)) {
      map.get(s).action(input);
    } else {
      System.err.println("ERROR: " + s + " is not a valid command");
    }
  }

  /**
   * Runs the REPL until EOF.
   */
  public void run() {
    while (true) {
      try {
        String userInput = reader.readLine();

        if (userInput == null) {
          // Received EOF, terminate REPL
          System.exit(0);
        } else if (userInput.length() == 0) {
          continue;
        }

        ArrayList<String> input = parse(userInput);
        if (input.size() > 0) {
          executeAction(input.get(0), input);
        }
      } catch (Exception e) {
        System.out.println(e.toString());
        System.out.println("ERROR: Issue reading REPL input");
      }
    }
  }

  private ArrayList<String> parse(String userInput) {
    // Split on whitespace
    String[] tokens = userInput.split("\\s+");

    // group tokens surrounded by double quotes together
    ArrayList<String> args = new ArrayList<>();
    int i = 0;
    while (i < tokens.length) {
      // Token does not start with " or ends with "
      if (tokens[i].charAt(0) == '"' && tokens[i].length() > 1
          && tokens[i].charAt(tokens[i].length() - 1) != '"') {
        String newArg = tokens[i];
        i++;

        while (true) {
          if (i < tokens.length) {
            if (tokens[i].charAt(tokens[i].length() - 1) == '"') {
              // close quotes
              newArg += " " + tokens[i];
              i++;
              args.add(newArg);
              break;
            } else {
              // not closed, keep adding
              newArg += " " + tokens[i];
              i++;
            }
          } else {
            System.err.println("ERROR: Opening quote not closed");
            return new ArrayList<>();
          }
        }
      } else {
        args.add(tokens[i]);
        i++;
      }
    }

    return args;
  }
}
