package edu.brown.cs.deet.execution.javascript;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import jdk.nashorn.api.scripting.ClassFilter;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;

import edu.brown.cs.deet.execution.Runner;

public class JSRunner implements Runner {

  private ScriptEngine engine;

  public JSRunner() {
    NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
    this.engine = factory.getScriptEngine(new MyCF());
  }

  @Override
  public Map<String, String>
    run(String solutionPath, Collection<String> inputs) {
    Map<String, String> toReturn = new HashMap<>();
    try {
      engine.eval(new FileReader(solutionPath));
      for (String input : inputs) {
        int firstCommaIndex = input.indexOf(",");
        String func = input.substring(0, firstCommaIndex);
        String args = input.substring(firstCommaIndex + 1);
        Object returned;
        try {
          returned =
            engine.eval(String.format("%s.apply(null, %s)", func, args));
        } catch (ScriptException e) {
          toReturn.put(input, e.getMessage());
          continue;
        }
        toReturn.put(input, returned.toString());
      }
      return toReturn;
    } catch (FileNotFoundException e) {
      System.out.println(String.format(
        "ERROR: unable to find %s in JSCompiler", solutionPath));
      return toReturn;
    } catch (ScriptException e1) {
      System.out.println("ERROR: problem evaluating js script in JSRunner");
      return toReturn;
    }
  }

  class MyCF implements ClassFilter {
    @Override
    public boolean exposeToScripts(String s) {
      return false;
    }
  }
}
