package edu.brown.cs.deet.execution.javascript;

import java.io.FileNotFoundException;
import java.io.FileReader;
//import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Executors;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import jdk.nashorn.api.scripting.ClassFilter;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import edu.brown.cs.deet.execution.MyCompiler;
import edu.brown.cs.deet.execution.javascript.JSRunner.MyCF;

public class JSCompiler implements MyCompiler {

  private ScriptEngine engine;

  public JSCompiler() {
    NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
    engine = factory.getScriptEngine(new MyCF());
  }

  @Override
  public String compile(String filePath) {
    try {
      engine.eval(new FileReader(filePath));
      return null;
    } catch (FileNotFoundException e) {
      System.out.println(String.format(
        "ERROR: unable to find %s in JSCompiler", filePath));
      return null;
    } catch (ScriptException e) {
      return e.getMessage();
    } catch (Exception e) {
      e.printStackTrace();
      return String.format("Exception caught: ", e.getMessage());
    }
  }

  class MyCF implements ClassFilter {
    @Override
    public boolean exposeToScripts(String s) {
      return false;
    }
  }
}
