package edu.brown.cs.deet.codegolf;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.python.util.PythonInterpreter;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

public final class REPL {

  public static void run() {

    InputStreamReader inputReader = null;
    try {
      inputReader = new InputStreamReader(System.in, "UTF-8");
    } catch (UnsupportedEncodingException e1) {
      System.out.println("ERROR: Encoding excpetion during read from "
        + "standard input.");
      return;
    }
    BufferedReader reader = new BufferedReader(inputReader);
    try (PythonInterpreter interpreter = new PythonInterpreter()) {
      String input = "";
      interpreter.exec("import sys");
      interpreter.setErr(System.err);
      interpreter.setOut(System.out);
      while ((input = reader.readLine()) != null) {
        if (input.length() == 0) {
          break;
        }
        List<String> parsedInput =
          Lists.newArrayList(Splitter.onPattern("\\s").trimResults()
            .omitEmptyStrings().split(input));
        if (parsedInput.size() != 2) {
          System.out.println("usage"); // TODO
          continue;
        }
        // file name of user input file
        String userInputFile = parsedInput.get(0);
        int lastSlashIndex = userInputFile.lastIndexOf("/");
        String inputFileDir = userInputFile.substring(0, lastSlashIndex);
        String module =
          userInputFile.substring(lastSlashIndex + 1,
            userInputFile.indexOf('.'));
        interpreter.exec(String.format("sys.path = ['%s']", inputFileDir));
        interpreter.exec("from " + module + " import *");

        // directory of test suite
        String testDir = parsedInput.get(1);
        List<String> inputs = getInputs(testDir);
        List<String> outputs = getOutputs(testDir);
        if (inputs.size() != outputs.size()) {
          System.out
          .println("ERROR: PYINPUT and PYOUTPUT files should have the same number of lines");
          continue;
        }
        String functionName = testDir.substring(testDir.lastIndexOf('/') + 1);
        for (int i = 0; i < inputs.size(); i++) {
          String testInput = inputs.get(i);
          String testOutput = outputs.get(i);
          String userOutput =
            interpreter.eval(functionName + "(" + testInput + ")").toString();
          if (!userOutput.equals(testOutput)) {
            System.out
              .println(String
                .format(
                  "%s produced INCORRECT output %s with input = %s. Output should have been %s",
                  functionName, userOutput, testInput, testOutput));
          } else {
            System.out.println(String.format(
              "%s produced the correct output, %s, with input = %s",
              functionName, testOutput, testInput));
          }
        }
      }
    } catch (IOException e) {
      System.out.println("ERROR: error occurred in reading input");
      return;
    }
  }

  private static List<String> getInputs(String testDir) throws IOException {
    String testInputPath = testDir + "/PYINPUT";
    try (BufferedReader testInputReader =
        new BufferedReader(new FileReader(testInputPath))) {
      List<String> inputs = new ArrayList<>();
      String line;
      while ((line = testInputReader.readLine()) != null) {
        inputs.add(line);
      }
      return inputs;
    }
  }

  private static List<String> getOutputs(String testDir) throws IOException {
    String testOutputPath = testDir + "/PYOUTPUT";
    try (BufferedReader testOutputReader =
        new BufferedReader(new FileReader(testOutputPath))) {
      List<String> outputs = new ArrayList<>();
      String line;
      while ((line = testOutputReader.readLine()) != null) {
        outputs.add(line);
      }
      return outputs;
    }
  }
}
