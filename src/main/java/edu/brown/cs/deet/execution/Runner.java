package edu.brown.cs.deet.execution;

import java.util.Map;

public interface Runner {

  /**
   * @param solutionPath
   * @param testDir
   * @return A map of <Input, Output> pairs to run results (null if the test
   *         passed, an error message otherwise).
   * @throws Exception
   */
  public Map<Pair<String, String>, String> runTests(String solutionPath,
      String testDir) throws Exception;

}
