package edu.brown.cs.deet.execution.javascript;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import edu.brown.cs.deet.execution.Runner;
import edu.brown.cs.deet.execution.Tester;

public class JSTester {// implements Tester {

  private Runner runner;

  public JSTester() {
    runner = new JSRunner();
  }

  // @Override
  // public Collection<List<String>> test(String solutionPath, String testDir)
  // throws Exception {
  // List<String> inputs;
  // List<String> outputs;
  // List<String> testNames;
  // try {
  // inputs = getInputs(testDir);
  // outputs = getOutputs(testDir);
  // testNames = getTestNames(testDir);
  // } catch (IOException e) {
  // System.out.println("ERROR: error reading input.txt and output.txt");
  // throw new Exception();
  // }
  // if (inputs.size() != outputs.size()) {
  // System.out
  // .println("ERROR: input.txt and output.txt files should have the same number of lines");
  // throw new Exception();
  // }
  // Collection<List<String>> toReturn = new ArrayList<>();
  // Map<String, String> runOutputs = runner.run(solutionPath, inputs);
  // for (int i = 0; i < inputs.size(); i++) {
  // String testInput = inputs.get(i);
  // String testOutput = outputs.get(i);
  // String runOutput = runOutputs.get(testInput);
  // String testName = testNames.get(i);
  // List<String> toAdd = Lists.newArrayList(testInput, testOutput, runOutput,
  // testName);
  // toReturn.add(toAdd);
  // }
  // return toReturn;
  // }

}
