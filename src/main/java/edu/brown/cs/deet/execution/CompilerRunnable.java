package edu.brown.cs.deet.execution;

public class CompilerRunnable implements Runnable {
  private String filePath;
  private MyCompiler compiler;
  private String compilerOutput;

  public CompilerRunnable(String filePath, MyCompiler compiler) {
    this.filePath = filePath;
    this.compiler = compiler;
  }

  @Override
  public void run() {
    compilerOutput = compiler.compile(filePath);
  }

  public String getCompilerOutput() {
    return compilerOutput;
  }

}
