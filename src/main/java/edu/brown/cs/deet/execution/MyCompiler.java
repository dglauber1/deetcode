package edu.brown.cs.deet.execution;

/**
 * Interface to specify the properties of a compiler object.
 * @author dglauber
 */
public interface MyCompiler {

  /**
   * Compiles a file specified by filePath parameter. Returns null if it
   * successfully compiled, and an error message otherwise.
   * @param filePath
   *          The path to the file to compile.
   * @return An error message if the file failed to compile, and null otherwise.
   */
  public String compile(String filePath);
}
