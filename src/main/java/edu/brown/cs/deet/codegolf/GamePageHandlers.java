package edu.brown.cs.deet.codegolf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import spark.ModelAndView;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.TemplateViewRoute;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.Gson;

import edu.brown.cs.deet.database.ChallengeDatabase;
import edu.brown.cs.deet.database.LeaderboardDatabase;
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

  /**
   * Handles loading the game page.
   * @author el51
   */
  static class GamePageHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      // TODO Currently set to the test database.
      String dbPath = "testdata/challengeDatabaseTester.sqlite3";
      try (ChallengeDatabase challenges = new ChallengeDatabase(dbPath)) {
        /*
         * TODO: This is currently hard-coded in because Tyler and I haven't yet
         * // set up a system to pass question names/ids from the categories
         * page // to the game page.
         */
        String challengeName = "test";
        String promptPath = null;
        try {
          if (challenges.doesChallengeExist(challengeName)) {
            List<String> challengeData = challenges.getChallenge(challengeName);
            promptPath = challengeData.get(2).concat("/description.txt");
          }
        } catch (SQLException e) {
          System.out.println("GamePageHandler");
          System.out.println(e.getMessage());
          System.exit(1);
        }

        StringBuilder promptBuilder = new StringBuilder();
        try (BufferedReader r = new BufferedReader(new FileReader(promptPath))) {
          String line = r.readLine();
          while (line != null) {
            promptBuilder.append(line).append("\n");
            line = r.readLine();
          }
        } catch (FileNotFoundException e) {
          System.out.println("File not found: " + promptPath);
          System.exit(1);
        } catch (IOException e) {
          System.out.println("I/O Exception at: " + promptPath);
          System.exit(1);
        }
        Map<String, Object> variables = ImmutableMap.of("title", "Game",
            "prompt", promptBuilder.toString());
        return new ModelAndView(variables, "game.ftl");
      }
    }
  }

  /**
   * Handlers saving the contents of the game page.
   * @author el51
   */
  static class SaveSolutionHandler implements Route {
    @Override
    public Object handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      String challengeID = qm.value("challengeID");
      String username = req.cookie("user");
      String language = qm.value("language");
      boolean passed = qm.value("passed").equals("true");
      double efficiency = Double.parseDouble(qm.value("efficiency"));
      double numLines = Double.parseDouble(qm.value("numLines"));
      double timeToSolve = Double.parseDouble(qm.value("timeToSolve"));
      double aggregate = Double.parseDouble(qm.value("aggregate"));

      // TODO Currently set to the test database.
      String dbPath = "testdata/solutionDatabaseTest";
      try (LeaderboardDatabase ld = new LeaderboardDatabase(dbPath)) {
        ld.addSolution(challengeID, username, passed, efficiency, numLines,
            timeToSolve, aggregate, language);
      } catch (SQLException e) {
        return ImmutableMap.of("status", "FAILURE", "message", e.getMessage());
      }

      String fileType;
      switch (language) {
        case "python":
          fileType = ".py";
          break;
        default:
          String msg = "Error in SaveSolutionHandler: "
              + "language must be either python, ruby, or javascript";
          Map<String, Object> variables = ImmutableMap.of("status", "FAILURE",
              "message", msg);
          return GSON.toJson(variables);
      }

      String fileName = username + fileType;
      File file = new File(String.format("challenges/%s/%s/solutions/%s",
          challengeID, language, fileName));
      try (PrintWriter printWriter = new PrintWriter(file)) {
        String code = qm.value("input");
        printWriter.print(code);
        printWriter.close();
      } catch (FileNotFoundException e) {
        String msg = "Error in SaveSolutionHandler: File not found.";
        Map<String, Object> variables = ImmutableMap.of("status", "FAILURE",
            "message", msg);
        return GSON.toJson(variables);
      }

      return GSON.toJson(ImmutableMap.of("status", "SUCCESS"));
    }
  }

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
        LineNumberReader lnr = new LineNumberReader(new FileReader(file));
        lnr.skip(Long.MAX_VALUE);
        int numLines = lnr.getLineNumber() + 1;
        lnr.close();
        String errorMessage = myCompiler.compile(file.getPath());
        if (errorMessage != null) {
          Map<String, Object> variables = new ImmutableMap.Builder()
              .put("error", false).put("compiled", errorMessage).build();
          return GSON.toJson(variables);
        }

        String testDir = String.format("challenges/%s/%s", challengeID,
            language);
        long start = System.currentTimeMillis();
        Collection<List<String>> testResults = myTester.test(file.getPath(),
            testDir);
        long finish = System.currentTimeMillis();
        long time = finish - start; /* in milliseconds */
        Map<String, Object> variables = new ImmutableMap.Builder()
            .put("error", false).put("compiled", "success")
            .put("numLines", numLines).put("testResults", testResults)
        .put("timeToSolve", time).build();
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
