package edu.brown.cs.deet.execution.javascript;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import delight.nashornsandbox.NashornSandbox;
import delight.nashornsandbox.NashornSandboxes;
import edu.brown.cs.deet.execution.Runner;

public class JSRunner implements Runner {

  private ScriptEngine engine;
  private static final int TIMEOUT = 300;

  public JSRunner() {
    ScriptEngineManager engineManager = new ScriptEngineManager();
    engine = engineManager.getEngineByName("nashorn");
  }

  @Override
  public Map<String, String>
  run(String solutionPath, Collection<String> inputs) throws TimeoutException {
    Map<String, String> toReturn = new HashMap<>();
    NashornSandbox sandbox = NashornSandboxes.create();
    try {
      sandbox.setMaxCPUTime(TIMEOUT);
      byte[] encoded = Files.readAllBytes(Paths.get(solutionPath));
      String code = new String(encoded, StandardCharsets.UTF_8);
      sandbox.setExecutor(Executors.newSingleThreadExecutor());
      sandbox.eval(code);
      for (String input : inputs) {
        int firstCommaIndex = input.indexOf(",");
        String func = input.substring(0, firstCommaIndex);
        String args = input.substring(firstCommaIndex + 1);
        Object returned =
            sandbox.eval(String.format("%s.apply(null, %s)", func, args));
        if (returned == null) {
          try {
            engine.eval(new FileReader(solutionPath));
            engine.eval(String.format("%s.apply(null, %s)", func, args));
            toReturn.put(input, null);
            continue;
          } catch (FileNotFoundException e) {
            System.out.println(String.format(
              "ERROR: unable to find %s in JSRunner", solutionPath));
            return toReturn;
          } catch (ScriptException e) {
            throw new TimeoutException(e.getMessage());
          }
        }
        toReturn.put(input, returned.toString());
      }
      return toReturn;
      // } catch (ScriptException e) {
      // for (String s : inputs) {
      // toReturn.put(s, e.getMessage());
      // }
      // return toReturn;
    } catch (FileNotFoundException e) {
      System.out.println(String.format(
        "ERROR: unable to find %s in JSCompiler", solutionPath));
      return toReturn;
    } catch (IOException e) {
      System.out.println(String.format("ERROR: error reading %s in JSRunner",
        solutionPath));
      return toReturn;
    } catch (Exception e) {
      e.printStackTrace();
      throw new TimeoutException(e.getMessage());
    }
  }
}
