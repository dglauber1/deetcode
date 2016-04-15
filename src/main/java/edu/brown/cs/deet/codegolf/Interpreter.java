package edu.brown.cs.deet.codegolf;

import java.io.File;

public interface Interpreter {

  /**
   *
   * @param pathToFile
   * @return
   */
  public RunOutput run(File solutionFile, File testInput, File testOutput);

}
