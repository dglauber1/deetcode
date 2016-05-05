package edu.brown.cs.deet.execution;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import com.google.common.collect.Lists;

/**
 * Class to specify the methods of a Tester object.
 *
 * @author dglauber
 */
public class Tester {

  /**
   * Given a path to a solution file and path to a test directory that contains
   * corresponding input and output test files, returns a List of List of
   * Strings representing the test results.
   *
   * @param solutionPath
   *          Path to a solution file.
   * @param testDir
   *          Path to a test directory that contains input and output test
   *          files.
   * @param runner
   *          Runner object to use to run tests.
   * @return A List of List of Strings. For each inner List, the four strings
   *         represent the input, the expected output, the solution-produced run
   *         output, and the test name, respectively.
   * @throws Exception
   *           Throws exception if there's an error in testing the solution
   *           against the tests.
   */
  public static Collection<List<String>> test(String solutionPath,
    String testDir, Runner runner) throws TimeoutException, Exception {
    List<String> inputs;
    List<String> outputs;
    List<String> testNames;
    try {
      inputs = getInputs(testDir);
      outputs = getOutputs(testDir);
      testNames = getTestNames(testDir);
    } catch (IOException e) {
      System.out.println("ERROR: error reading input.txt and output.txt");
      throw new Exception();
    }
    if (inputs.size() != outputs.size()) {
      System.out
      .println("ERROR: input.txt and output.txt files should have the same number of lines");
      throw new Exception();
    }
    Collection<List<String>> toReturn = new ArrayList<>();
    Map<String, String> runOutputs;
    try {
      runOutputs = runner.run(solutionPath, inputs);
    } catch (TimeoutException e) {
      throw e;
    }
    for (int i = 0; i < inputs.size(); i++) {
      String testInput = inputs.get(i);
      String testOutput = outputs.get(i);
      String runOutput = runOutputs.get(testInput);
      String testName = testNames.get(i);
      List<String> toAdd =
          Lists.newArrayList(testInput, testOutput, runOutput, testName);
      toReturn.add(toAdd);
    }
    return toReturn;
  }

  public static List<String> getOutputs(String testDir) throws IOException {
    String testOutputPath = testDir + "/output.txt";
    try (BufferedReader testOutputReader =
        new BufferedReader(new FileReader(testOutputPath))) {
      List<String> outputs = new ArrayList<>();
      String line;
      while ((line = testOutputReader.readLine()) != null) {
        outputs.add(line);
      }
      return outputs;
    }
  }

  public static List<String> getTestNames(String testDir) throws IOException {
    String testnamesPath = testDir + "/testnames.txt";
    try (BufferedReader testnamesReader =
        new BufferedReader(new FileReader(testnamesPath))) {
      List<String> testnames = new ArrayList<>();
      String line;
      while ((line = testnamesReader.readLine()) != null) {
        testnames.add(line);
      }
      return testnames;
    }
  }

  public static List<String> getInputs(String testDir) throws IOException {
    String testInputPath = testDir + "/input.txt";
    try (BufferedReader testInputReader =
        new BufferedReader(new FileReader(testInputPath))) {
      List<String> inputs = new ArrayList<>();
      String line;
      while ((line = testInputReader.readLine()) != null) {
        inputs.add(line);
      }
      return inputs;
    }
  }
}
