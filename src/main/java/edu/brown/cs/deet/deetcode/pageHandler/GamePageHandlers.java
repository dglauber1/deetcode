package edu.brown.cs.deet.deetcode.pageHandler;

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
import java.util.concurrent.TimeoutException;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.Gson;

import edu.brown.cs.deet.database.ChallengeDatabase;
import edu.brown.cs.deet.database.LeaderboardDatabase;
import edu.brown.cs.deet.database.UserDatabase;
import edu.brown.cs.deet.deetcode.Main;
import edu.brown.cs.deet.deetcode.pageHandler.LeaderboardHandler.ExceptionPrinter;
import edu.brown.cs.deet.execution.CompilerRunnable;
import edu.brown.cs.deet.execution.MyCompiler;
import edu.brown.cs.deet.execution.Runner;
import edu.brown.cs.deet.execution.RunnerRunnable;
import edu.brown.cs.deet.execution.Tester;
import edu.brown.cs.deet.execution.javascript.JSCompiler;
import edu.brown.cs.deet.execution.javascript.JSRunner;
import edu.brown.cs.deet.execution.python.PyCompiler;
import edu.brown.cs.deet.execution.python.PyRunner;
import spark.ModelAndView;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.TemplateViewRoute;

public final class GamePageHandlers {
  private static final MyCompiler pyCompiler = new PyCompiler();
  private static final Runner pyRunner = new PyRunner();
  private static final MyCompiler jsCompiler = new JSCompiler();
  private static final Runner jsRunner = new JSRunner();
  private static final Gson GSON = new Gson();

  /**
   * Handles loading the game page.
   *
   * @author el51
   */
  public static class GamePageHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      try (ChallengeDatabase challenges = new ChallengeDatabase(Main.dbLoc);
        UserDatabase user = new UserDatabase(Main.dbLoc)) {
        String challengeId = req.params(":challenge-id");
        // String challengeName = "test";
        String promptPath = null;
        String difficulty = "easy";
        String questionName = null;
        String prompt =
          String.format("No challenge exists for %s!", challengeId);
        try {
          if (challenges.doesChallengeExist(challengeId)) {
            List<String> challengeData = challenges.getChallenge(challengeId);
            promptPath = challengeData.get(2).concat("/description.txt");
            difficulty = challengeData.get(4);
            questionName = challengeData.get(1);
            StringBuilder promptBuilder = new StringBuilder();
            try (BufferedReader r =
              new BufferedReader(new FileReader(promptPath))) {
              String line = r.readLine();
              while (line != null) {
                promptBuilder.append(line).append("\n");
                line = r.readLine();
              }
              prompt = promptBuilder.toString();
            } catch (FileNotFoundException e) {
              System.out.println("File not found: " + promptPath);
              System.exit(1);
            } catch (IOException e) {
              System.out.println("I/O Exception at: " + promptPath);
              System.exit(1);
            }
          } else {
            Map<String, Object> variables =
              ImmutableMap.of("errorMessage",
                String.format("No challenge exists for %s!", challengeId));
            return new ModelAndView(variables, "not-found.ftl");
          }
        } catch (SQLException e) {
          System.out.println("GamePageHandler");
          System.out.println(e.getMessage());
          System.exit(1);
        }
        String time = "0";
        if (difficulty.equals("easy")) {
          time = "300";
        } else if (difficulty.equals("medium")) {
          time = "600";
        } else if (difficulty.equals("hard")) {
          time = "900";
        } else {
          System.out
          .println("ERROR: (in GamePageHandler) the difficulty was not set properly");
        }
        Map<String, Object> variables =
          ImmutableMap.of("title", "Game", "prompt", prompt, "username",
            user.getUsernameFromID(req.cookie("user")), "questionName",
            questionName, "time", time);
        return new ModelAndView(variables, "game.ftl");
      } catch (SQLException e1) {
        System.out.println("GamePageHandler ChallengeDatabase");
        System.exit(1);
        return null;
      }
    }
  }

  /**
   * Loads available language options for a particular challenge.
   *
   * @author el51
   */
  public static class LoadLanguageHandler implements Route {
    @Override
    public Object handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      String challengeID = qm.value("challengeID");
      List<String> langs = null;
      try (ChallengeDatabase cd = new ChallengeDatabase(Main.dbLoc)) {
        langs = cd.getLanguagesSupported(challengeID);
      } catch (SQLException e) {
        e.printStackTrace();
        return GSON.toJson(ImmutableMap.of("status", "FAILURE", "message",
          e.getMessage()));
      }
      assert (langs != null);
      return GSON.toJson(ImmutableMap.of("status", "SUCCESS", "langs", langs));
    }
  }

  /**
   * Loads user solutions, if available, into the CodeMirror window. Else, loads
   * in the challenge stub.
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
      QueryParamsMap qm = req.queryMap();
      String challengeID = qm.value("challengeID");
      boolean isAttempted = false;
      boolean isAttemptedLang = false;
      String language = qm.value("language");
      // Solution is only available if the user has successfully solved
      // the problem or if time has run out. TODO clarify with group
      try (LeaderboardDatabase ld = new LeaderboardDatabase(Main.dbLoc)) {
        isAttempted = ld.isChallengeAttempedByUser(challengeID, username);
        isAttemptedLang =
          ld.isChallengeAttempedByUser(challengeID, username, language);
      } catch (SQLException e) {
        e.printStackTrace();
        return GSON.toJson(ImmutableMap.of("status", "FAILURE", "message",
          e.getMessage()));
      }

      if (isAttempted && isAttemptedLang) {
        StringBuilder userCode = new StringBuilder();
        String fileType;
        switch (language) {
        case "python":
          fileType = ".py";
          break;
        case "javascript":
          fileType = ".js";
          break;
        default:
          String msg =
            "Error in LoadSolutionHandler: "
              + "language must be either python, ruby, or javascript";
          Map<String, Object> variables =
            ImmutableMap.of("status", "FAILURE", "message", msg);
          return GSON.toJson(variables);
        }
        File file =
          new File(String.format("challenges/%s/%s/solutions/%s", challengeID,
            language, username + fileType));

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
        System.out.println("Load: subsequentf attempt");
        return GSON.toJson(ImmutableMap.of("status", "SUCCESS", "code",
          userCode.toString(), "language", language, "isFirstTime",
          !isAttempted));
      } else {
        StringBuilder stubCode = new StringBuilder();
        File file =
          new File(String.format("challenges/%s/%s/stub.txt", challengeID,
            language));
        try (BufferedReader rd = new BufferedReader(new FileReader(file))) {
          String line = rd.readLine();
          while (line != null) {
            stubCode.append(line).append("\n");
            line = rd.readLine();
          }
        } catch (FileNotFoundException e) {
          return GSON.toJson(ImmutableMap.of("status", "FAILURE", "message",
            e.getMessage()));
        } catch (IOException e) {
          return GSON.toJson(ImmutableMap.of("status", "FAILURE", "message",
            e.getMessage()));
        }
        System.out.println("Load: first attempt");
        return GSON.toJson(ImmutableMap.of("status", "SUCCESS", "code",
          stubCode.toString(), "language", language, "isFirstTime",
          !isAttempted));
      }

    }
  }

  /**
   * Removes the entry associated with the worst score in the leaderboard.
   *
   * @author el51
   */
  public static class RemoveWorstFromLeaderboardHandler implements Route {
    @Override
    public Object handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      String language = qm.value("language");
      String challengeID = qm.value("challengeID");

      try (LeaderboardDatabase ld = new LeaderboardDatabase(Main.dbLoc)) {
        List<List<String>> aggScores =
          ld.topTwentyOfChallengeLanguage(challengeID, language);
        if (!aggScores.isEmpty()) {
          int worstIndex = aggScores.size() - 1;
          String worstUser = aggScores.get(worstIndex).get(1);
          ld.deleteSolution(challengeID, worstUser, language);
        }
      } catch (SQLException e) {
        new ExceptionPrinter().handle(e, req, res);
        return GSON.toJson(ImmutableMap.of("status", "FAILURE", "message",
          e.getMessage()));
      }
      return GSON.toJson(ImmutableMap.of("status", "SUCCESS"));
    }
  }

  /**
   * Compares user stats to leaderboard stats.
   *
   * @author el51
   */
  public static class CompareToLeaderboardHandler implements Route {
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

      QueryParamsMap qm = req.queryMap();
      String language = qm.value("language");
      String challengeID = qm.value("challengeID");
      double aggregate = Double.parseDouble(qm.value("aggregate"));
      boolean isBetterAggScore = false;

      try (LeaderboardDatabase ld = new LeaderboardDatabase(Main.dbLoc)) {
        List<List<String>> aggScores =
          ld.topTwentyOfChallengeLanguage(challengeID, language);
        if (aggScores.size() < LeaderboardDatabase.LEADERBOARD_SIZE) {
          // leaderboard has room for more entries
          isBetterAggScore = true;
        } else {
          int worstIndex = aggScores.size() - 1;
          double worstScore =
            Double.parseDouble(aggScores.get(worstIndex).get(2));
          if (aggregate > worstScore) {
            // user beat the worst score
            isBetterAggScore = true;
          }
        }
      } catch (SQLException e) {
        new ExceptionPrinter().handle(e, req, res);
        return GSON.toJson(ImmutableMap.of("status", "FAILURE", "message",
          e.getMessage()));
      }

      return GSON.toJson(ImmutableMap.of("status", "SUCCESS", "isBetter",
        isBetterAggScore));
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
      assert (username != null);

      String language = qm.value("language");
      boolean passed = Boolean.parseBoolean(qm.value("passed"));
      double efficiency = Double.parseDouble(qm.value("efficiency"));
      int numLines = Integer.parseInt(qm.value("numLines"));
      double timeToSolve = Double.parseDouble(qm.value("timeToSolve"));
      int aggregate = Integer.parseInt(qm.value("aggregate"));
      try (LeaderboardDatabase ld = new LeaderboardDatabase(Main.dbLoc)) {
        // TODO DON'T HARDCODE TIMESTSAMP
        if (ld.isChallengeAttempedByUser(challengeID, username, language)) {
          ld.updateSolution(challengeID, username, passed, efficiency,
            numLines, timeToSolve, aggregate, language, 2.0);
        } else {
          ld.addSolution(challengeID, username, passed, efficiency, numLines,
            timeToSolve, aggregate, language, 2.0);
        }

      } catch (SQLException e) {
        e.printStackTrace();
        return GSON.toJson(ImmutableMap.of("status", "FAILURE", "message",
          e.getMessage()));
      }
      String fileType;
      switch (language) {
      case "python":
        fileType = ".py";
        break;
      case "javascript":
        fileType = ".js";
        break;
      default:
        String msg =
          "Error in SaveSolutionHandler: "
            + "language must be either python, ruby, or javascript";
        Map<String, Object> variables =
          ImmutableMap.of("status", "FAILURE", "message", msg);
        return GSON.toJson(variables);
      }

      String fileName = username + fileType;
      File file =
        new File(String.format("challenges/%s/%s/solutions/%s", challengeID,
          language, fileName));
      try (PrintWriter printWriter = new PrintWriter(file)) {
        String code = qm.value("input");
        printWriter.print(code);
        printWriter.close();
      } catch (FileNotFoundException e) {
        System.out.println(file.getAbsolutePath());
        String msg = "Error in SaveSolutionHandler: File not found.";
        Map<String, Object> variables =
          ImmutableMap.of("status", "FAILURE", "message", msg);
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
      case "javascript":
        fileType = ".js";
        myRunner = jsRunner;
        myCompiler = jsCompiler;
        break;
      default:
        System.out
          .println("Error in DeetTestsHandler: language must be either python, ruby, or javascript");
        Map<String, Object> variables =
          new ImmutableMap.Builder().put("error", true).build();
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
        CompilerRunnable compilerRunnable =
          new CompilerRunnable(file.getPath(), myCompiler, language);
        Thread compilerThread = new Thread(compilerRunnable);
        compilerThread.start();
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < 3000
          && compilerThread.isAlive()) {
        }
        if (compilerThread.isAlive()) {
          compilerThread.stop();
          Map<String, Object> variables =
            new ImmutableMap.Builder().put("error", false)
              .put("compiled", "Infinite loop detected").build();
          return GSON.toJson(variables);
        }
        String errorMessage = compilerRunnable.getCompilerOutput();
        if (errorMessage != null) {
          Map<String, Object> variables =
            new ImmutableMap.Builder().put("error", false)
              .put("compiled", errorMessage).build();
          return GSON.toJson(variables);
        }

        String testDir =
          String.format("challenges/%s/%s", challengeID, language);
        start = System.currentTimeMillis();
        Collection<List<String>> testResults =
          Tester.test(file.getPath(), testDir, myRunner);
        long finish = System.currentTimeMillis();
        long time = finish - start; /* in milliseconds */
        Map<String, Object> variables =
          new ImmutableMap.Builder().put("error", false)
            .put("compiled", "success").put("numLines", numLines)
            .put("testResults", testResults).put("timeToTest", time).build();
        return GSON.toJson(variables);
      } catch (IOException e) {
        System.out.println("ERROR: IOException in DeetTestsHandler");
        Map<String, Object> variables =
          new ImmutableMap.Builder().put("error", true).build();
        return GSON.toJson(variables);
      } catch (TimeoutException e) {
        String message = e.getMessage();
        Map<String, Object> variables =
          new ImmutableMap.Builder().put("error", false)
            .put("compiled", message).build();
        return GSON.toJson(variables);
      } catch (Exception e) {
        Map<String, Object> variables =
          new ImmutableMap.Builder().put("error", false)
            .put("compiled", e.getMessage()).build();
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
      case "javascript":
        fileType = ".js";
        myRunner = jsRunner;
        myCompiler = jsCompiler;
        break;
      default:
        System.out
          .println("Error in UserTestsHandler: language must be either python, ruby, or javascript");
        Map<String, Object> variables =
          new ImmutableMap.Builder().put("error", true).build();
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
        CompilerRunnable compilerRunnable =
            new CompilerRunnable(file.getPath(), myCompiler, language);
        Thread compilerThread = new Thread(compilerRunnable);
        compilerThread.start();
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < 3000
            && compilerThread.isAlive()) {
        }
        if (compilerThread.isAlive()) {
          compilerThread.stop();
          Map<String, Object> variables =
            new ImmutableMap.Builder().put("error", false)
              .put("compiled", "Infinite loop detected").build();
          return GSON.toJson(variables);
        }
        String errorMessage = compilerRunnable.getCompilerOutput();
        if (errorMessage != null) {
          Map<String, Object> variables =
            new ImmutableMap.Builder().put("error", false)
              .put("compiled", errorMessage).build();
          return GSON.toJson(variables);
        }

        String testInputs = qm.value("userTest");
        List<String> testInputList =
          Lists.newArrayList(Splitter.on(System.getProperty("line.separator"))
            .trimResults().omitEmptyStrings().split(testInputs));

        RunnerRunnable runnerRunnable =
            new RunnerRunnable(file.getPath(), testInputList, myRunner);
        Thread runnerThread = new Thread(runnerRunnable);
        runnerThread.start();
        start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < 3000
            && runnerThread.isAlive()) {
        }
        if (runnerThread.isAlive()) {
          runnerThread.stop();
          throw new TimeoutException("Infinite loop detected!");
        }
        Map<String, String> runResults = runnerRunnable.getRunOutputs();

        Map<String, Object> variables =
          new ImmutableMap.Builder().put("error", false)
            .put("compiled", "success").put("runResults", runResults).build();

        return GSON.toJson(variables);

      } catch (IOException e) {
        System.out.println("IOException in UserTestsHandler");
        Map<String, Object> variables =
          new ImmutableMap.Builder().put("error", true).build();
        return GSON.toJson(variables);
      } catch (Exception e) {
        Map<String, Object> variables =
          new ImmutableMap.Builder().put("error", false)
            .put("compiled", e.getMessage()).build();
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
