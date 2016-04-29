package edu.brown.cs.deet.execution.javascript;

import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import edu.brown.cs.deet.execution.MyCompiler;

public class JSCompiler implements MyCompiler {

  private ScriptEngine engine = new ScriptEngineManager()
  .getEngineByName("nashorn");

  @Override
  public String compile(String filePath) {
    try {
      engine.eval(new FileReader(filePath));
      return null;
    } catch (ScriptException e) {
      return e.getMessage();
    } catch (FileNotFoundException e) {
      System.out.println(String.format(
          "ERROR: unable to find %s in JSCompiler", filePath));
      return null;
    }
  }
}
