package edu.brown.cs.deet.execution;

public class CompilerRunnable implements Runnable {
  private String filePath;
  private MyCompiler compiler;
  private String compilerOutput;
  private Exception toThrow;

  public CompilerRunnable(String filePath, MyCompiler compiler) {
    this.filePath = filePath;
    this.compiler = compiler;
  }

  @Override
  public void run() {
    try {
      compilerOutput = compiler.compile(filePath);
    } catch (Exception e) {
      toThrow = e;
    }
  }

  public String getCompilerOutput() throws Exception {
    if (toThrow != null) {
      throw toThrow;
    }
    return compilerOutput;
  }

}
