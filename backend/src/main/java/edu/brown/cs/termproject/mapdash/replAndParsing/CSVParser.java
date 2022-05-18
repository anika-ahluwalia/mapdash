package edu.brown.cs.termproject.mapdash.replAndParsing;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.FileReader;

/**
 ** The CSVParser class uses a BufferedReader to parse a CSV file into tokens based on its
 * fields. It also does basic file validity and header format checking.
 */
public class CSVParser {

  private BufferedReader br;

  /**
   ** Constructor for the CSVParser class. It doesn't take any parameters.
   */
  public CSVParser() {

  }

  /**
   ** Method that takes in a filename and a header format that the CSV file must adhere to and
   * checks whether the file exists and whether the header matches. It also initializes a
   * BufferedReader that uses a FileReader.
   * @param filename - a String representing the file path to search for a valid CSV file
   * @param header - a String representing the header format that a CSV file must match to be valid
   * @return a boolean representing if the file is parsable or not
   */
  public boolean isParsable(String filename, String header) {
    try {
      br = new BufferedReader(new FileReader(filename));
      // First check that CSV header matches the intended header format
      if (!br.readLine().equals(header)) {
        System.out.println("ERROR: CSV header doesn't match intended header format.");
        return false;
      }
    } catch (FileNotFoundException e) {
      System.out.println("ERROR: File does not exist.");
      return false;
    } catch (IOException e) {
      System.out.println("ERROR: Invalid file input for parser.");
      return false;
    }
    return true;
  }

  /**
   ** Method that parses a CSV file line-by-line and splits based on commas.
   * @return a String[] with the tokenized CSV data or null if an error occurs
   */
  public String[] parseLine()  {
    String input;
    try {
      input = br.readLine();
      if (input != null) {
        String[] parsedInput = input.split(",", -1);
        return parsedInput;
      }
    } catch (Exception e) {
      System.out.println("ERROR: Invalid input for parser.");
    }
    return null;
  }
}
