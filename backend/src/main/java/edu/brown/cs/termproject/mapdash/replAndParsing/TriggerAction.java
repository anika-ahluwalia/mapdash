package edu.brown.cs.termproject.mapdash.replAndParsing;

import java.util.ArrayList;

/**
 * Interface used for adding commands to REPL.
 */
public interface TriggerAction {
  /**
   * Action to execute.
   * @param input - List of strings of input from the REPL
   */
  void action(ArrayList<String> input);
}
