package edu.brown.cs.deet.execution.python;

import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

import edu.brown.cs.deet.execution.MyCompiler;

/**
 * Implementation of a MyCompiler for the python language.
 * @author dglauber
 */
public class PyCompiler implements MyCompiler {

  private PythonInterpreter interpreter;

  /**
   * Constructor for a PyCompiler.
   */
  public PyCompiler() {
    this.interpreter = new PythonInterpreter();
    interpreter.setErr(System.err);
    interpreter.exec("import sys");
    interpreter.exec("if not 'bin' in sys.path : sys.path.append('bin')");
    interpreter.exec("from my_compiler import *");
  }

  @Override
  public String compile(String filePath) {
    PyObject compileOutput = interpreter.eval(String.format(
        "compile_mod('%s')", filePath));
    if (compileOutput.isInteger()) {
      // compile_mod() returned 0, code successfully compiled
      return null;
    } else {
      return compileOutput.asString();
    }
  }

}
