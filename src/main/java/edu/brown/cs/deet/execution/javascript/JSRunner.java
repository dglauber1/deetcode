package edu.brown.cs.deet.execution.javascript;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import edu.brown.cs.deet.execution.Runner;

public class JSRunner implements Runner {

  private ScriptEngine engine = new ScriptEngineManager()
  .getEngineByName("nashorn");

  @Override
  public Map<String, String> run(String solutionPath, Collection<String> inputs) {
    Map<String, String> toReturn = new HashMap<>();
    try {
      engine.eval(new FileReader(solutionPath));
      for (String input : inputs) {
        int firstCommaIndex = input.indexOf(",");
        String func = input.substring(0, firstCommaIndex);
        String args = input.substring(firstCommaIndex + 1);
        Object returned = engine.eval(String.format("%s.apply(null, %s)", func,
            args));
        toReturn.put(input, returned.toString());
      }
      return toReturn;
    } catch (ScriptException e) {
      for (String s : inputs) {
        toReturn.put(s, e.getMessage());
      }
      return toReturn;
    } catch (FileNotFoundException e) {
      System.out.println(String.format(
          "ERROR: unable to find %s in JSCompiler", solutionPath));
      return toReturn;
    }
  }
}
