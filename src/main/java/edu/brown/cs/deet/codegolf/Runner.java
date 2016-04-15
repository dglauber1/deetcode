package edu.brown.cs.deet.codegolf;

import java.io.OutputStream;
import java.util.Map;

public interface Runner {

  /**
   *
   * @param solutionPath
   * @param testDir
   * @return A map of <Input, Output> pairs to run results (null if the test
   *         passed, an error message otherwise).
   * @throws Exception
   */
  public Map<Pair<String, String>, String> run(String solutionPath,
    String testDir) throws Exception;

}
