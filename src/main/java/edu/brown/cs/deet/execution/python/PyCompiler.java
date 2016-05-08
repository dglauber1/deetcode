package edu.brown.cs.deet.execution.python;

import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

import edu.brown.cs.deet.execution.MyCompiler;
import edu.brown.cs.deet.execution.MySecurityManager;

/**
 * Implementation of a MyCompiler for the python language.
 *
 * @author dglauber
 */
public class PyCompiler implements MyCompiler {

  private PythonInterpreter interpreter;
  private MySecurityManager sm = new MySecurityManager();

  /**
   * Constructor for a PyCompiler.
   */
  public PyCompiler() {
    this.interpreter = new PythonInterpreter();
    interpreter.setErr(System.err);
    interpreter.exec("import sys");
    interpreter.exec("if not 'my_bin' in sys.path : sys.path.append('my_bin')");
    interpreter.exec("from my_compiler import *");
  }

  @Override
  public String compile(String filePath) {
    // SecurityManager old = System.getSecurityManager();
    // sm.disable();
    // System.setSecurityManager(sm);
    PyObject compileOutput =
        interpreter.eval(String.format("compile_mod('%s')", filePath));
    // System.out.println("na");
    // sm.enable();
    // System.setSecurityManager(old);
    if (compileOutput.isInteger()) {
      // compile_mod() returned 0, code successfully compiled
      return null;
    } else {
      return compileOutput.asString();
    }
  }

}
