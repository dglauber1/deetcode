package edu.brown.cs.deet.deetcode.pageHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import edu.brown.cs.deet.database.ChallengeDatabase;
import edu.brown.cs.deet.database.LeaderboardDatabase;
import edu.brown.cs.deet.database.UserDatabase;
import spark.ExceptionHandler;
import spark.ModelAndView;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.TemplateViewRoute;

/**
 * Class that handles all Leaderboard-related requests.
 *
 * @author eddie
 */
public final class LeaderboardHandler {
  private static ChallengeDatabase challenge;
  private static LeaderboardDatabase leaderboard;
  private static UserDatabase user;
  private static final Gson GSON = new Gson();
  private static final int CHALLENGE_ID = 0;
  private static final int USERNAME = 1;
  private static final int SCORE = 2;
  private static final int LANGUAGE = 3;

  /**
   * Private Constructor for a LeaderboardHandler.
   */
  private LeaderboardHandler() {
  }

  /**
   * Shows the leaderboard page.
   *
   * @author el13
   */
  public static class ShowLeaderboardHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      List<List<String>> leaderboardInfo = null;
      List<String> languages = null;
      String name = "";

      try {
        // get challenge
        String challengeId = req.params(":challengeid");
        // get user viewing the page
        String currUserUsername = user.getUsernameFromID(req.cookie("user"));
        // get languages supported for this challenge
        languages = challenge.getLanguagesSupported(challengeId);

        String primary;
        if (languages.size() == 0) {
          primary = "";
        } else {
          primary = languages.get(0);
        }

        // get leaderboard info
        leaderboardInfo =
            getLeaderboardInfo(currUserUsername, challengeId, "aggregate",
              primary);
        
        System.out.println("leaderboardinfo: " + leaderboardInfo);

        name = challenge.getNameFromId(challengeId);
      } catch (SQLException e) {
        new ExceptionPrinter().handle(e, req, res);
      } catch (IOException e) {
        new ExceptionPrinter().handle(e, req, res);
      }

      Map<String, Object> variables = new HashMap<>();

      variables.put("title", "Leaderboard");
      variables.put("info", leaderboardInfo);
      variables.put("languages", languages);
      variables.put("name", name);

      return new ModelAndView(variables, "leaderboard.ftl");
    }
  }

  /**
   * Handler to show a new part of the leaderboard.
   *
   * @author eddie
   *
   */
  public static class ChangeLeaderboardHandler implements Route {

    @Override
    public Object handle(Request req, Response res) {
      List<List<String>> leaderboardInfo = null;
      try {
        // get challenge
        String challengeId = req.params(":challengeid");
        // get user viewing the page
        String currUserUsername = user.getUsernameFromID(req.cookie("user"));

        QueryParamsMap qm = req.queryMap();
        String type = GSON.fromJson(qm.value("type"), String.class);
        String language =
            GSON.fromJson(qm.value("language"), String.class).toLowerCase();

        // get leaderboard info
        leaderboardInfo =
            getLeaderboardInfo(currUserUsername, challengeId, type, language);
      } catch (SQLException e) {
        new ExceptionPrinter().handle(e, req, res);
      } catch (IOException e) {
        new ExceptionPrinter().handle(e, req, res);
      }

      Map<String, Object> variables =
          ImmutableMap.of("title", "Leaderboard", "info", leaderboardInfo);
      return GSON.toJson(variables);
    }

  }

  /**
   * Handles Exceptions.
   *
   * @author el13
   */
  public static class ExceptionPrinter implements ExceptionHandler {
    @Override
    public void handle(Exception e, Request req, Response res) {
      res.status(500);
      StringWriter stacktrace = new StringWriter();
      try (PrintWriter pw = new PrintWriter(stacktrace)) {
        pw.println("<pre>");
        e.printStackTrace(pw);
        pw.println("</pre>");
      }
      res.body(stacktrace.toString());
    }
  }

  /**
   * Statically changes the LeaderboardDatabase of the LeaderboardHandler.
   *
   * @param ldb
   *          the LeaderboardDatabase
   */
  public static void setLeaderboardDatabase(LeaderboardDatabase ldb) {
    leaderboard = ldb;
  }

  /**
   * Statically changes the UserDatabase of the LeaderboardHandler.
   *
   * @param udb
   *          the UserDatabase
   */
  public static void setUserDatabase(UserDatabase udb) {
    user = udb;
  }

  /**
   * Statically changes the ChallengeDatabase of the LeaderboardHandler.
   *
   * @param cdb
   *          the ChallengeDatabase
   */
  public static void setChallengeDatabase(ChallengeDatabase cdb) {
    challenge = cdb;
  }

  /**
   * Gets top 20 leaderboard information based on the type of information
   * requested and the language it is being requested in.
   *
   * @param currUser
   *          the user currently viewing the leaderboard
   * @param challengeId
   *          the challenge id of the challenge
   * @param infoType
   *          The info type we want. Should either be "aggregate", "efficiency",
   *          "brevity", or "speed"
   * @param language
   *          The language we want to get the information for. For now, the only
   *          supported languages are "java", "python", "javascript", and "ruby"
   * @return A List of a List of Strings, where each inner List contains the
   *         following information in this order: username, language of
   *         solution, score of the solution, and the solution itself if the
   *         user has attempted the challenge. If the user has not attempted the
   *         challenge, there will be a message saying the user has not done the
   *         challenge.
   * @throws SQLException
   *           when there is an issue with the database at the time of
   *           invokation of this method
   * @throws IOException
   *           when there is an issue reading a solution file
   */
  public static List<List<String>> getLeaderboardInfo(String currUser,
    String challengeId, String infoType, String language) throws SQLException,
    IOException {
    List<List<String>> leaderboardInfo = new ArrayList<>();
    List<List<String>> sqlRes;
    if (infoType.equals("aggregate")) {
      System.out.println("1");
      sqlRes = leaderboard.topTwentyOfChallengeLanguage(challengeId, language);
      System.out.println(sqlRes);
    } else if (infoType.equals("efficiency")) {
      sqlRes =
          leaderboard.topTwentyOfChallengeLanguageEfficiency(challengeId,
          language);
    } else if (infoType.equals("brevity")) {
      sqlRes =
          leaderboard.topTwentyOfChallengeLanguageNumLines(challengeId, language);
    } else if (infoType.equals("speed")) {
      sqlRes =
          leaderboard
          .topTwentyOfChallengeLanguageTimeSolve(challengeId, language);
    } else {
      sqlRes = null;
    }

    // now get the actual solution information
    if (sqlRes == null) { // just return null if the res is null
      return sqlRes;
    } else {
      System.out.println("2");
      for (List<String> res : sqlRes) {
        List<String> newInfo = new ArrayList<>();
        newInfo.add(res.get(USERNAME));
        newInfo.add(res.get(LANGUAGE));
        newInfo.add(res.get(SCORE));

        // get the solution if necessary
        if (leaderboard.isChallengeAttempedByUser(res.get(CHALLENGE_ID),
          currUser)) {
          String suffix = (res.get(LANGUAGE).equals("python")) ? "py" : "js";
          String solutionPath =
              "challenges/" + res.get(CHALLENGE_ID) + "/" + res.get(LANGUAGE)
              + "/solutions/" + res.get(USERNAME) + "." + suffix;

          byte[] encoded = Files.readAllBytes(Paths.get(solutionPath));
          String code = new String(encoded, Charsets.UTF_8);
          newInfo.add(code);
        } else {
          newInfo
          .add("You must attempt the challenge before you can see a solution.");
        }
        leaderboardInfo.add(newInfo);
      }
    }
    System.out.println("leaderboardInfo: " + leaderboardInfo);

    return leaderboardInfo;
  }
}
