package edu.brown.cs.deet.codegolf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import org.python.util.PythonInterpreter;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

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
    String input = "";
    PythonInterpreter interpreter = new PythonInterpreter();
    Compiler pyCompiler = new PyCompiler(interpreter);
    Runner pyRunner = new PyRunner(interpreter);
    try (BufferedReader reader = new BufferedReader(inputReader)) {
      while ((input = reader.readLine()) != null) {
        if (input.length() == 0) {
          break;
        }
        List<String> parsedInput =
            Lists.newArrayList(Splitter.onPattern("\\s").trimResults()
              .omitEmptyStrings().split(input));
        if (parsedInput.size() != 2) {
          System.out.println("usage"); // TODO
          continue;
        }
        String solutionPath = parsedInput.get(0);
        String compileMessage = pyCompiler.compile(solutionPath);
        if (compileMessage != null) {
          System.out.println(compileMessage);
          continue;
        }
        String testDir = parsedInput.get(1);
        Map<Pair<String, String>, String> runResults;
        try {
          runResults = pyRunner.run(solutionPath, testDir);
        } catch (Exception e) {
          System.out.println(String.format(
            "ERROR: error occurred running %s on test directory %s",
            solutionPath, testDir));
          e.printStackTrace();
          continue;
        }
        boolean passedAllTests = true;
        for (Pair<String, String> testIO : runResults.keySet()) {
          if (runResults.get(testIO) == null) {
            System.out.println(String.format(
              "SUCCESS : on %s, expected %s, got %s", testIO.getFirst(),
              testIO.getSecond(), testIO.getSecond()));
          } else {
            System.out.println(String.format(
              "FAILURE : on %s, expected %s, got %s", testIO.getFirst(),
              testIO.getSecond(), runResults.get(testIO)));
            passedAllTests = false;
          }
        }
        if (passedAllTests) {
          System.out.println("All tests passed!");
        }
      }

    } catch (IOException e) {
      System.out.println("ERROR: error occurred in reading input");
      return;
    }
  }
}
