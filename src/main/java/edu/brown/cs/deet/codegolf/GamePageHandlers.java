package edu.brown.cs.deet.codegolf;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.Gson;

import edu.brown.cs.deet.execution.MyCompiler;
import edu.brown.cs.deet.execution.Runner;
import edu.brown.cs.deet.execution.Tester;
import edu.brown.cs.deet.execution.python.PyCompiler;
import edu.brown.cs.deet.execution.python.PyRunner;
import edu.brown.cs.deet.execution.python.PyTester;

public final class GamePageHandlers {

  private static final MyCompiler pyCompiler = new PyCompiler();
  private static final Runner pyRunner = new PyRunner();
  private static final Tester pyTester = new PyTester();
  private static final Gson GSON = new Gson();

  static class DeetTestsHandler implements Route {
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Object handle(Request req, Response res) {
      String userID = req.cookie("user");
      QueryParamsMap qm = req.queryMap();
      String challengeID = qm.value("challengeID");
      String language = qm.value("language");

      String fileType;
      Tester myTester;
      MyCompiler myCompiler;
      switch (language) {
        case "python":
          fileType = ".py";
          myTester = pyTester;
          myCompiler = pyCompiler;
          break;
        default:
          System.out
          .println("Error in DeetTestsHandler: language must be either python, ruby, or javascript");
          Map<String, Object> variables = new ImmutableMap.Builder().put(
              "error", true).build();
          return GSON.toJson(variables);
      }

      String fileName = userID + fileType;
      System.out.println(challengeID);
      File file = new File(String.format("challenges/%s/%s/solutions/%s",
          challengeID, language, fileName));
      try (PrintWriter printWriter = new PrintWriter(file)) {
        String code = qm.value("input");
        printWriter.print(code);
        printWriter.close();
        String errorMessage = myCompiler.compile(file.getPath());
        if (errorMessage != null) {
          Map<String, Object> variables = new ImmutableMap.Builder()
          .put("error", false).put("compiled", errorMessage).build();
          return GSON.toJson(variables);
        }

        String testDir = String.format("challenges/%s/%s", challengeID,
            language);
        Collection<List<String>> testResults = myTester.test(file.getPath(),
            testDir);
        Map<String, Object> variables = new ImmutableMap.Builder()
        .put("error", false).put("compiled", "success")
        .put("testResults", testResults).build();
        return GSON.toJson(variables);

      } catch (IOException e) {
        System.out.println("ERROR: IOException in DeetTestsHandler");
        Map<String, Object> variables = new ImmutableMap.Builder().put("error",
            true).build();
        return GSON.toJson(variables);
      } catch (Exception e) {
        System.out.println("ERROR: Tester error occurred in DeetTestsHandler");
        Map<String, Object> variables = new ImmutableMap.Builder().put("error",
            true).build();
        return GSON.toJson(variables);
      } finally {
        try {
          Files.delete(file.toPath());
        } catch (IOException e) {
          System.out
          .println("error deleting temporary directory in DeetTestsHandler");
        }
      }
    }
  }

  /**
   * Runs a user's code on user-provided input and posts the corresponding
   * output.
   * @author dglauber
   */
  static class UserTestsHandler implements Route {
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Object handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      String language = qm.value("language");

      String fileType;
      Runner myRunner;
      MyCompiler myCompiler;
      switch (language) {
        case "python":
          fileType = ".py";
          myRunner = pyRunner;
          myCompiler = pyCompiler;
          break;
        default:
          System.out
          .println("Error in UserTestsHandler: language must be either python, ruby, or javascript");
          Map<String, Object> variables = new ImmutableMap.Builder().put(
              "error", true).build();
          return GSON.toJson(variables);
      }

      Integer random = (int) (Math.random() * 1000000);
      String randomFileName = "temp" + random.toString() + fileType;
      File tempDir = new File("temporary");
      tempDir.mkdir();
      File file = new File("temporary/" + randomFileName);

      try (PrintWriter printWriter = new PrintWriter(file)) {
        String code = qm.value("input");
        printWriter.print(code);
        printWriter.close();
        String errorMessage = myCompiler.compile(file.getPath());
        if (errorMessage != null) {
          Map<String, Object> variables = new ImmutableMap.Builder()
          .put("error", false).put("compiled", errorMessage).build();
          return GSON.toJson(variables);
        }

        String testInputs = qm.value("userTest");
        List<String> testInputList = Lists.newArrayList(Splitter
            .on(System.getProperty("line.separator")).trimResults()
            .omitEmptyStrings().split(testInputs));
        Map<String, String> runResults = myRunner.run(file.getPath(),
            testInputList);

        Map<String, Object> variables = new ImmutableMap.Builder()
        .put("error", false).put("compiled", "success")
        .put("runResults", runResults).build();
        return GSON.toJson(variables);

      } catch (IOException e) {
        System.out.println("IOException in UserTestsHandler");
        Map<String, Object> variables = new ImmutableMap.Builder().put("error",
            true).build();
        return GSON.toJson(variables);
      } finally {
        for (File f : tempDir.listFiles()) {
          f.delete();
        }
        try {
          Files.delete(tempDir.toPath());
        } catch (IOException e) {
          System.out
          .println("error deleting temporary directory in UserTestsHandler");
        }
      }
    }
  }

}
