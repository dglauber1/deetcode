package edu.brown.cs.deet.codegolf;

public interface Compiler {

  /**
   *
   * @param filePath
   * @return An error message if the file failed to compile, and null otherwise.
   */
  public String compile(String filePath);
}
