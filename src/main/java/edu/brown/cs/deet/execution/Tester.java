package edu.brown.cs.deet.execution;

import java.util.Collection;
import java.util.List;

/**
 * Interface to specify the properties of a tester object.
 * @author dglauber
 */
public interface Tester {

  /**
   * Given a path to a solution file and path to a test directory that contains
   * corresponding input and output test files, returns a List of List of
   * Strings representing the test results.
   * @param solutionPath
   *          Path to a solution file.
   * @param testDir
   *          Path to a test directory that contains input and output test
   *          files.
   * @return A List of List of Strings. For each inner List, the four strings
   *         represent the input, the expected output, the solution-produced run
   *         output, and the test name, respectively.
   * @throws Exception
   *           Throws exception if there's an error in testing the solution
   *           against the tests.
   */
  public Collection<List<String>> test(String solutionPath, String testDir)
      throws Exception;
}
