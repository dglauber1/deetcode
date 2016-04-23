package edu.brown.cs.deet.execution.python;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

import edu.brown.cs.deet.execution.Runner;

public class PyRunner implements Runner {

  private PythonInterpreter interpreter;

  public PyRunner() {
    this.interpreter = new PythonInterpreter();
    interpreter.setErr(System.err);
    interpreter.exec("import sys");
    interpreter.exec("if not 'bin' in sys.path : sys.path.append('bin')");
    interpreter.exec("from runner import *");
  }

  @Override
  public Map<String, String> run(String solutionPath, Collection<String> inputs) {
    Path userInputFile = Paths.get(solutionPath);
    String file = userInputFile.getFileName().toString();
    String module = file.replaceAll("\\..*", "");
    String inputFileDir = userInputFile.getParent().toString();
    interpreter.exec(String.format("sys.path.append('%s')", inputFileDir));
    interpreter.exec(String.format("from %s import *", module));

    Map<String, String> toReturn = new HashMap<>();
    for (String input : inputs) {
      PyObject runOutput = interpreter.eval("run(" + input + ")");
      toReturn.put(input, runOutput.toString());
    }
    interpreter.exec(String.format("sys.path.remove('%s')", inputFileDir));
    return toReturn;
  }
}
