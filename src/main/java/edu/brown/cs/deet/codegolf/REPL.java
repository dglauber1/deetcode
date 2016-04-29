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
import edu.brown.cs.deet.execution.Runner;
import edu.brown.cs.deet.execution.Tester;
import edu.brown.cs.deet.execution.javascript.JSCompiler;
import edu.brown.cs.deet.execution.javascript.JSRunner;
import edu.brown.cs.deet.execution.python.PyCompiler;
import edu.brown.cs.deet.execution.python.PyRunner;
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
    Runner pyRunner = new PyRunner();
    MyCompiler jsCompiler = new JSCompiler();
    Runner jsRunner = new JSRunner();
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
        Runner myRunner;
        MyCompiler myCompiler;
        switch (language) {
          case "python":
            myRunner = pyRunner;
            myCompiler = pyCompiler;
            break;
          case "javascript":
            myCompiler = jsCompiler;
            myRunner = jsRunner;
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
        Collection<List<String>> testResults;
        try {
          testResults = Tester.test(solutionPath, testDir, myRunner);
        } catch (Exception e) {
          System.out.println(String.format(
              "ERROR: error occurred running %s on test directory %s",
              solutionPath, testDir));
          System.out.println();
          continue;
        }
        boolean passedAllTests = true;
        for (List<String> testResult : testResults) {
          String successOrFailure;
          if (testResult.get(1).equals(testResult.get(2))) {
            successOrFailure = "SUCCESS";
          } else {
            successOrFailure = "FAILURE";
            passedAllTests = false;
          }
          System.out.println(String.format(
              "%s on %s: on (%s), expected %s, got %s", successOrFailure,
              testResult.get(3), testResult.get(0), testResult.get(1),
              testResult.get(2)));
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
