package edu.brown.cs.deet.codegolf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.List;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import edu.brown.cs.deet.execution.MyCompiler;
import edu.brown.cs.deet.execution.Tester;
import edu.brown.cs.deet.execution.Triple;
import edu.brown.cs.deet.execution.python.PyCompiler;
import edu.brown.cs.deet.execution.python.PyTester;

/**
 * Class to allow command line repl testing of codegolf project.
 * @author dglauber
 */
public final class REPL {

  /**
   * Runs the command line repl.
   */
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
    MyCompiler pyCompiler = new PyCompiler();
    Tester pyTester = new PyTester();
    try (BufferedReader reader = new BufferedReader(inputReader)) {
      while ((input = reader.readLine()) != null) {
        if (input.length() == 0) {
          break;
        }
        List<String> parsedInput = Lists.newArrayList(Splitter.onPattern("\\s")
            .trimResults().omitEmptyStrings().split(input));
        if (parsedInput.size() != 3) {
          System.out.println("Please enter an input of the following form: "
              + "language path/to/solution.file path/to/test/directory");
          System.out.println();
          continue;
        }
        String language = parsedInput.get(0);
        Tester myTester;
        MyCompiler myCompiler;
        switch (language) {
          case "python":
            myTester = pyTester;
            myCompiler = pyCompiler;
            break;
          default:
            System.out
            .println("language must be either python, ruby, or javascript");
            System.out.println();
            continue;
        }
        String solutionPath = parsedInput.get(1);
        String compileMessage = myCompiler.compile(solutionPath);
        if (compileMessage != null) {
          System.out.println(compileMessage);
          continue;
        }
        String testDir = parsedInput.get(2);
        Collection<Triple<String, String, String>> testResults;
        try {
          testResults = myTester.test(solutionPath, testDir);
        } catch (Exception e) {
          System.out.println(String.format(
              "ERROR: error occurred running %s on test directory %s",
              solutionPath, testDir));
          System.out.println();
          continue;
        }
        boolean passedAllTests = true;
        for (Triple<String, String, String> testResult : testResults) {
          String successOrFailure;
          if (testResult.getSecond().equals(testResult.getThird())) {
            successOrFailure = "SUCCESS";
          } else {
            successOrFailure = "FAILURE";
            passedAllTests = false;
          }
          System.out.println(String.format("%s : on (%s), expected %s, got %s",
              successOrFailure, testResult.getFirst(), testResult.getSecond(),
              testResult.getThird()));
        }
        if (passedAllTests) {
          System.out.println("All tests passed!");
        }
        System.out.println();
      }

    } catch (IOException e) {
      System.out.println("ERROR: error occurred in reading input");
      return;
    }
  }
}
