package edu.brown.cs.deet.codegolf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.ProcessBuilder.Redirect;

public final class REPL {

  public static void run() {

    InputStreamReader inputReader = null;
    try {
      inputReader = new InputStreamReader(System.in, "UTF-8");
    } catch (UnsupportedEncodingException e1) {
      System.out.println("ERROR: Encoding excpetion during read from "
        + "standard input.");
      return;
    }
    BufferedReader reader = new BufferedReader(inputReader);
    try {
      String input = "";
      while ((input = reader.readLine()) != null) {
        if (input.length() == 0) {
          break;
        }
        ProcessBuilder pb = new ProcessBuilder("python", input);
        // pb.redirectInput(file);
        // file should be some test INPUT file. pb.redirectOutput can be
        // directed to
        // some output file, which can be compared to some previously written
        // test OUTPUT file for program correctness, similar to how the
        // cs032_system_tester
        // used maps's repl to test the shortest path alg
        pb.redirectOutput(Redirect.INHERIT);
        pb.redirectError(Redirect.INHERIT);
        Process process;
        process = pb.start();
        int exitValue;
        try {
          exitValue = process.waitFor();
        } catch (InterruptedException e) {
          e.printStackTrace(); // TODO better error message
          continue;
        }
        if (exitValue == 0) {
          System.out.println("No error occurred");
        } else {
          System.out.println("Error occurred executing python file: " + input);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("ERROR: error occurred in reading input");
      return;
    }
  }

}
