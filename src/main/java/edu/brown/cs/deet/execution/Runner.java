package edu.brown.cs.deet.execution;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Interface to specify the properties of a runner object.
 *
 * @author dglauber
 */
public interface Runner {

  /**
   * Given a path to a solution file and a collection of inputs, returns a map
   * of (input, runOutput) pairs.
   *
   * @param solutionPath
   *          Path to solution file to be tested on inputs.
   * @param inputs
   *          Collection of inputs.
   * @return A map of each input to its corresponding test output.
   * @throws TimeoutException
   *           If there is an infinite loop in user code.
   */
  Map<String, String> run(String solutionPath, Collection<String> inputs)
    throws TimeoutException;

}
