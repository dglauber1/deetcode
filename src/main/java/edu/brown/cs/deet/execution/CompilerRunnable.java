package edu.brown.cs.deet.execution;

public class CompilerRunnable implements Runnable {
  private String filePath;
  private MyCompiler compiler;
  private String compilerOutput;
  private Exception toThrow;
  private String language = "notJava";

  // public CompilerRunnable(String filePath, MyCompiler compiler) {
  // this.filePath = filePath;
  // this.compiler = compiler;
  // }

  public CompilerRunnable(String filePath, MyCompiler compiler, String language) {
    this.filePath = filePath;
    this.compiler = compiler;
    this.language = language;
  }

  @Override
  public void run() {
    SecurityManager old = System.getSecurityManager();
    SecurityManager sm = new MySecurityManager(language);
    System.setSecurityManager(sm);
    try {
      compilerOutput = compiler.compile(filePath);
    } catch (Exception e) {
      toThrow = e;
    }
    System.setSecurityManager(old);
  }

  public String getCompilerOutput() throws Exception {
    if (toThrow != null) {
      throw toThrow;
    }
    return compilerOutput;
  }

}
