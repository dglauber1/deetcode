package edu.brown.cs.deet.execution;

import java.util.Collection;

public interface Tester {

  /**
   * Given a path to a solution file and path to a test directory that contains
   * corresponding input and output test files, returns a List of String Triples
   * representing the test results.
   * @param solutionPath
   *          Path to a solution file.
   * @param testDir
   *          Path to a test directory that contains input and output test
   *          files.
   * @return A List of String Triples. For each Triple, the three strings
   *         represent the input, the expected output, and the solution-produced
   *         run output, respectively.
   * @throws Exception
   *           Throws exception if there's an error in testing the solution
   *           against the tests.
   */
  public Collection<Triple<String, String, String>> test(String solutionPath,
      String testDir) throws Exception;
}
