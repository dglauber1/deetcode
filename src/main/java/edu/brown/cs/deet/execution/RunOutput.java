package edu.brown.cs.deet.execution;

public class RunOutput {
  private final boolean compiled;
  private final String compilerMessage;

  public RunOutput(boolean compiled, String compilerMessage, boolean passedTests) {
    this.compiled = compiled;
    this.compilerMessage = compilerMessage;
  }

  public boolean getCompiled() {
    return compiled;
  }

  public String getCompilerMessage() {
    return compilerMessage;
  }

  @Override
  public String toString() {
    // FOR REPL PURPOSES
    return null;
  }
}
