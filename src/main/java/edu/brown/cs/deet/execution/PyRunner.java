package edu.brown.cs.deet.execution;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

public class PyRunner implements Runner {

  private PythonInterpreter interpreter;

  public PyRunner(PythonInterpreter interp) {
    this.interpreter = interp;
    interpreter.setErr(System.err);
    interpreter.exec("import sys");
    interpreter.exec("if not 'bin' in sys.path : sys.path.append('bin')");
    interpreter.exec("from runner import *");
  }

  @Override
  public Map<Pair<String, String>, String> run(String solutionPath,
      String testDir) throws Exception {
    Path userInputFile = Paths.get(solutionPath);
    String file = userInputFile.getFileName().toString();
    String module = file.replaceAll("\\..*", "");
    String inputFileDir = userInputFile.getParent().toString();
    interpreter.exec(String.format("sys.path.append('%s')", inputFileDir));
    interpreter.exec(String.format("from %s import *", module));

    List<String> inputs;
    List<String> outputs;
    try {
      inputs = getInputs(testDir);
      outputs = getOutputs(testDir);
    } catch (IOException e) {
      System.out.println("ERROR: error reading input.txt and output.txt");
      throw new Exception();
    }
    if (inputs.size() != outputs.size()) {
      System.out
      .println("ERROR: input.txt and output.txt files should have the same number of lines");
      throw new Exception();
    }
    Map<Pair<String, String>, String> toReturn = new HashMap<>();
    for (int i = 0; i < inputs.size(); i++) {
      String testInput = inputs.get(i);
      String testOutput = outputs.get(i);
      Pair<String, String> toAdd = new Pair<>(testInput, testOutput);
      PyObject runOutput = interpreter.eval("run(" + testInput + ")");
      if (testOutput.equals(runOutput.toString())) {
        toReturn.put(toAdd, null);
      } else {
        toReturn.put(toAdd, runOutput.toString());
      }
    }
    interpreter.exec(String.format("sys.path.remove('%s')", inputFileDir));
    return toReturn;
  }

  private static List<String> getInputs(String testDir) throws IOException {
    String testInputPath = testDir + "/input.txt";
    try (BufferedReader testInputReader = new BufferedReader(new FileReader(
        testInputPath))) {
      List<String> inputs = new ArrayList<>();
      String line;
      while ((line = testInputReader.readLine()) != null) {
        inputs.add(line);
      }
      return inputs;
    }
  }

  private static List<String> getOutputs(String testDir) throws IOException {
    String testOutputPath = testDir + "/output.txt";
    try (BufferedReader testOutputReader = new BufferedReader(new FileReader(
        testOutputPath))) {
      List<String> outputs = new ArrayList<>();
      String line;
      while ((line = testOutputReader.readLine()) != null) {
        outputs.add(line);
      }
      return outputs;
    }
  }
}
