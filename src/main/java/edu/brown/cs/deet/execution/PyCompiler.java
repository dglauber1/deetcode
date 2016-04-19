package edu.brown.cs.deet.execution;

import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

public class PyCompiler implements Compiler {

  private PythonInterpreter interpreter;

  public PyCompiler(PythonInterpreter interp) {
    this.interpreter = interp;
    interpreter.setErr(System.err);
    interpreter.exec("import sys");
    interpreter.exec("if not 'bin' in sys.path : sys.path.append('bin')");
    interpreter.exec("from my_compiler import *");
  }

  @Override
  public String compile(String filePath) {
    PyObject compileOutput =
        interpreter.eval(String.format("compile_mod('%s')", filePath));
    if (compileOutput.isInteger()) {
      // compile_mod() returned 0, code successfully compiled
      return null;
    } else {
      return compileOutput.asString();
    }
  }

}
