package edu.brown.cs.deet.execution.python;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import edu.brown.cs.deet.execution.Tester;
import edu.brown.cs.deet.execution.Triple;

public class PyTester implements Tester {

  private PyRunner runner;

  public PyTester() {
    runner = new PyRunner();
  }

  @Override
  public Collection<Triple<String, String, String>> test(String solutionPath,
      String testDir) throws Exception {

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
    Collection<Triple<String, String, String>> toReturn = new ArrayList<>();
    Map<String, String> runOutputs = runner.run(solutionPath, inputs);
    for (int i = 0; i < inputs.size(); i++) {
      String testInput = inputs.get(i);
      String testOutput = outputs.get(i);
      String runOutput = runOutputs.get(testInput);
      Triple<String, String, String> toAdd = new Triple<>(testInput,
          testOutput, runOutput);
      toReturn.add(toAdd);
    }
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
