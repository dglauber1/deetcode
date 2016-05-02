package edu.brown.cs.deet.pageHandler;

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
import edu.brown.cs.deet.database.UserDatabase;
import edu.brown.cs.deet.deetcode.Main;
import edu.brown.cs.deet.execution.MyCompiler;
import edu.brown.cs.deet.execution.Runner;
import edu.brown.cs.deet.execution.Tester;
import edu.brown.cs.deet.execution.python.PyCompiler;
import edu.brown.cs.deet.execution.python.PyRunner;

public final class GamePageHandlers {
  private static final MyCompiler pyCompiler = new PyCompiler();
  private static final Runner pyRunner = new PyRunner();
  private static final Gson GSON = new Gson();

  /**
   * Handles loading the game page.
   * 
   * @author el51
   */
  public static class GamePageHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      try (ChallengeDatabase challenges = new ChallengeDatabase(Main.dbLoc)) {
        String challengeId = req.params(":challenge-id");
        // String challengeName = "test";
        String promptPath = null;
        try {
          if (challenges.doesChallengeExist(challengeId)) {
            List<String> challengeData = challenges.getChallenge(challengeId);
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
      } catch (SQLException e1) {
        // Eddie added this, but code shouldn't get here either way
        System.out.println("GamePageHandler ChallengeDatabase");
        System.exit(1);

        return null; // ?
      }
    }
  }

  /**
   * Loads user solutions, if available, into the CodeMirror window.
   * 
   * @author el51
   */
  public static class LoadSolutionHandler implements Route {
    @Override
    public Object handle(Request req, Response res) {
      // Obtain username from Facebook id
      String username = null;
      try (UserDatabase ud = new UserDatabase(Main.dbLoc)) {
        username = ud.getUsernameFromID(req.cookie("user"));
      } catch (SQLException e) {
        e.printStackTrace();
        return GSON.toJson(ImmutableMap.of("status", "FAILURE", "message",
            e.getMessage()));
      }
      assert (username != null);
      username = "el13";
      QueryParamsMap qm = req.queryMap();
      String challengeID = qm.value("challengeID");
      boolean isAttempted = false;
      String language = null;
      // Solution is only available if the user has successfully solved
      // the problem or if time has run out. TODO clarify with group
      try (LeaderboardDatabase ld = new LeaderboardDatabase(Main.dbLoc)) {
        isAttempted = ld.isChallengeAttempedByUser(challengeID, username);
        language = ld.getUserChallengeLanguage(challengeID, username);
      } catch (SQLException e) {
        e.printStackTrace();
        return GSON.toJson(ImmutableMap.of("status", "FAILURE", "message",
            e.getMessage()));
      }

      StringBuilder userCode = new StringBuilder();
      if (isAttempted) {
        String fileType;
        switch (language) {
        case "python":
          fileType = ".py";
          break;
        case "javascript":
          fileType = ".js";
          break;
        default:
          String msg = "Error in SaveSolutionHandler: "
              + "language must be either python, ruby, or javascript";
          Map<String, Object> variables = ImmutableMap.of("status", "FAILURE",
              "message", msg);
          return GSON.toJson(variables);
        }
        File file = new File(String.format("challenges/%s/%s/solutions/%s",
            challengeID, language, username + fileType));

        try (BufferedReader rd = new BufferedReader(new FileReader(file))) {
          String line = rd.readLine();
          while (line != null) {
            userCode.append(line).append("\n");
            line = rd.readLine();
          }
        } catch (FileNotFoundException e) {
          return GSON.toJson(ImmutableMap.of("status", "FAILURE", "message",
              e.getMessage()));
        } catch (IOException e) {
          return GSON.toJson(ImmutableMap.of("status", "FAILURE", "message",
              e.getMessage()));
        }
      }

      return GSON.toJson(ImmutableMap.of("status", "SUCCESS", "code",
          userCode.toString()));
    }
  }

  /**
   * Handles saving the contents of the game page.
   * 
   * @author el51
   */
  public static class SaveSolutionHandler implements Route {
    @Override
    public Object handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      String challengeID = qm.value("challengeID");
      String username = null;
      try (UserDatabase ud = new UserDatabase(Main.dbLoc)) {
        username = ud.getUsernameFromID(req.cookie("user"));
      } catch (SQLException e) {
        return GSON.toJson(ImmutableMap.of("status", "FAILURE", "message",
            e.getMessage()));
      }
      // assert (username != null);
      // TODO currently hardcoding a username because tyler hasn't yet written
      // the code to populate the user table
      username = "el13";
      String language = qm.value("language");
      boolean passed = Boolean.parseBoolean(qm.value("passed"));
      double efficiency = Double.parseDouble(qm.value("efficiency"));
      int numLines = Integer.parseInt(qm.value("numLines"));
      double timeToSolve = Double.parseDouble(qm.value("timeToSolve"));
      int aggregate = Integer.parseInt(qm.value("aggregate"));
      // TODO Currently set to the test database.
      try (LeaderboardDatabase ld = new LeaderboardDatabase(Main.dbLoc)) {
        ld.addSolution(challengeID, username, passed, efficiency, numLines,
            timeToSolve, aggregate, language, 2.0); // TODO REMOVE TIMESTSAMP
      } catch (SQLException e) {
        return GSON.toJson(ImmutableMap.of("status", "FAILURE", "message",
            e.getMessage()));
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

  public static class DeetTestsHandler implements Route {
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Object handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      String challengeID = qm.value("challengeID");
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
            .println("Error in DeetTestsHandler: language must be either python, ruby, or javascript");
        Map<String, Object> variables = new ImmutableMap.Builder().put("error",
            true).build();
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
        Collection<List<String>> testResults = Tester.test(file.getPath(),
            testDir, myRunner);
        long finish = System.currentTimeMillis();
        long time = finish - start; /* in milliseconds */
        Map<String, Object> variables = new ImmutableMap.Builder()
            .put("error", false).put("compiled", "success")
            .put("numLines", numLines).put("testResults", testResults)
            .put("timeToTest", time).build();
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

  /**
   * Runs a user's code on user-provided input and posts the corresponding
   * output.
   * 
   * @author dglauber
   */
  public static class UserTestsHandler implements Route {
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
        Map<String, Object> variables = new ImmutableMap.Builder().put("error",
            true).build();
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

        System.out.println("here0");
        String testInputs = qm.value("userTest");
        List<String> testInputList = Lists.newArrayList(Splitter
            .on(System.getProperty("line.separator")).trimResults()
            .omitEmptyStrings().split(testInputs));
        Map<String, String> runResults = myRunner.run(file.getPath(),
            testInputList);

        System.out.println("here1");
        Map<String, Object> variables = new ImmutableMap.Builder()
            .put("error", false).put("compiled", "success")
            .put("runResults", runResults).build();

        System.out.println("here2");

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
