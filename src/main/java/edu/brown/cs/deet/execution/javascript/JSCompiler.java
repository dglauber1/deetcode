package edu.brown.cs.deet.execution.javascript;

import java.io.FileNotFoundException;
import java.io.FileReader;
//import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Executors;

//import javax.script.ScriptEngine;
//import javax.script.ScriptException;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

//import jdk.nashorn.api.scripting.ClassFilter;
//import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import delight.nashornsandbox.NashornSandbox;
import delight.nashornsandbox.NashornSandboxes;
import edu.brown.cs.deet.execution.MyCompiler;

public class JSCompiler implements MyCompiler {

  private ScriptEngine engine;
  private static final int TIMEOUT = 300;

  public JSCompiler() {
    ScriptEngineManager engineManager = new ScriptEngineManager();
    engine = engineManager.getEngineByName("nashorn");
  }

  @Override
  public String compile(String filePath) {
    NashornSandbox sandbox = NashornSandboxes.create();
    try {
      sandbox.setMaxCPUTime(TIMEOUT);
      byte[] encoded = Files.readAllBytes(Paths.get(filePath));
      String code = new String(encoded, StandardCharsets.UTF_8);
      sandbox.setExecutor(Executors.newSingleThreadExecutor());
      sandbox.eval(code);
    } catch (FileNotFoundException e) {
      System.out.println(String.format(
        "ERROR: unable to find %s in JSCompiler", filePath));
      return null;
    } catch (IOException e) {
      System.out.println(String.format("ERROR: error reading %s in JSCompiler",
        filePath));
      return null;
    } catch (IllegalMonitorStateException e) {
      return "Your program timed out! Check your code for possible infinite loops.";
    } catch (Exception e) {
      return e.getMessage();
      // return String.format(
      // "Your code timed out! It took more than %d milliseconds of CPU time. "
      // + "Check for infinite loops in your code", TIMEOUT);
    }
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
      return String.format("Exception caught: ", e.getMessage());
    }
  }

  // class MyCF implements ClassFilter {
  // @Override
  // public boolean exposeToScripts(String s) {
  // return false;
  // }
  // }
}
