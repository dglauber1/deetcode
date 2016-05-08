package edu.brown.cs.deet.deetcode.pageHandler;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import edu.brown.cs.deet.database.ChallengeDatabase;
import edu.brown.cs.deet.database.LeaderboardDatabase;
import edu.brown.cs.deet.database.UserDatabase;

/**
 * Class that contains all the handlers for getting the statistics needed to
 * draw visualizations.
 * 
 * @author eddie
 *
 */
public final class StatHandler {
  private static ChallengeDatabase challenges;
  private static UserDatabase user;
  private static LeaderboardDatabase leaderboard;
  private static final Gson GSON = new Gson();

  /**
   * Private Constructor for an StatHandler.
   */
  private StatHandler() {
  }

  /**
   * Statically changes the ChallengeDatabase of the StatHandler.
   *
   * @param cdb the ChallengeDatabase
   */
  public static void setChallengeDatabase(ChallengeDatabase cdb) {
    challenges = cdb;
  }

  /**
   * Statically changes the LeaderboardDatabase of the StatHandler.
   *
   * @param ldb the LeaderboardDatabase
   */
  public static void setLeaderboardDatabase(LeaderboardDatabase ldb) {
    leaderboard = ldb;
  }

  /**
   * Sets the UserDatabase.
   *
   * @param udb the UserDatabase.
   */
  public static void setUserDatabase(UserDatabase udb) {
    user = udb;
  }

  /**
   * Gets the information displayed on the User page showing user vs average vs
   * best of some statistic.
   *
   * @author el13
   */
  public static class UserVsAvgVsBestHandler implements Route {
    @Override
    public Object handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();

      // Get the user viewing the page
      String currUser = "";
      try {
        currUser = user.getUsernameFromID(req.cookie("user"));
      } catch (SQLException e2) {
        new AdminHandler.ExceptionPrinter().handle(e2, req, res);
      }

      // Get the GSON variables
      String username = GSON.fromJson(qm.value("user"), String.class);

      // get the id from challenge name
      String challengeId = null;
      try {
        challengeId = challenges.getIdFromName(GSON.fromJson(
            qm.value("challengeName"), String.class));
      } catch (JsonSyntaxException e1) {
        new AdminHandler.ExceptionPrinter().handle(e1, req, res);
      } catch (SQLException e1) {
        new AdminHandler.ExceptionPrinter().handle(e1, req, res);
      }

      String language = GSON.fromJson(qm.value("language"), String.class)
          .toLowerCase();
      String metric = GSON.fromJson(qm.value("metric"), String.class)
          .toLowerCase();

      Double average = null; // average score
      Double best = null; // best score
      Double userScore = null; // user score

      // Get the relevant information depending on the metric
      try {
        if (leaderboard.isChallengeAttempedByUser(challengeId, currUser)) {
          if (metric.equals("aggregate")) {
            try {
              average = leaderboard.averageAggregateGivenIdLanguage(
                  challengeId, language);

              List<List<String>> topTwenty = leaderboard
                  .topTwentyOfChallengeLanguage(challengeId, language);

              if (topTwenty.size() == 0) {
                best = 0.0; // dummy value in the odd edge case where topTwenty
                            // is
                            // empty
              } else {
                best = Double.parseDouble(topTwenty.get(0).get(2));
              }

              userScore = leaderboard.getAggregateForUserAndChallenge(
                  challengeId, username);
            } catch (SQLException e) {
              new AdminHandler.ExceptionPrinter().handle(e, req, res);
            }
          } else if (metric.equals("efficiency")) {
            try {
              average = leaderboard.averageEfficiencyGivenIdLanguage(
                  challengeId, language);
              List<List<String>> topTwenty = leaderboard
                  .topTwentyOfChallengeLanguageEfficiency(challengeId, language);

              if (topTwenty.size() == 0) {
                best = 0.0; // dummy value in the odd edge case where topTwenty
                            // is
                            // empty
              } else {
                best = Double.parseDouble(topTwenty.get(0).get(2));
              }

              userScore = leaderboard.getEfficiencyForUserAndChallenge(
                  challengeId, username);
            } catch (SQLException e) {
              new AdminHandler.ExceptionPrinter().handle(e, req, res);
            }
          } else if (metric.equals("brevity")) {
            try {
              average = leaderboard.averageBrevityGivenIdLanguage(challengeId,
                  language);

              List<List<String>> topTwenty = leaderboard
                  .topTwentyOfChallengeLanguageNumLines(challengeId, language);

              if (topTwenty.size() == 0) {
                best = 0.0; // dummy value in the odd edge case where topTwenty
                            // is
                            // empty
              } else {
                best = Double.parseDouble(topTwenty.get(0).get(2));
              }

              userScore = leaderboard.getBrevityForUserAndChallenge(
                  challengeId, username);
            } catch (SQLException e) {
              new AdminHandler.ExceptionPrinter().handle(e, req, res);
            }
          } else if (metric.equals("completion time")) {
            try {
              average = leaderboard.averageTimeGivenIdLanguage(challengeId,
                  language);

              List<List<String>> topTwenty = leaderboard
                  .topTwentyOfChallengeLanguageTimeSolve(challengeId, language);

              if (topTwenty.size() == 0) {
                best = 0.0; // dummy value in the odd edge case where topTwenty
                            // is
                            // empty
              } else {
                best = Double.parseDouble(topTwenty.get(0).get(2));
              }

              userScore = leaderboard.getTimeForUserAndChallenge(challengeId,
                  username);
            } catch (SQLException e) {
              new AdminHandler.ExceptionPrinter().handle(e, req, res);
            }
          }

          Map<String, Object> variables = new HashMap<>();
          variables.put("user", userScore);
          variables.put("average", average);
          variables.put("best", best);

          return GSON.toJson(variables);
        } else {
          Map<String, Object> variables = new HashMap<>();
          variables.put("user", "n/a");
          variables.put("average", "n/a");
          variables.put("best", "n/a");

          return GSON.toJson(variables);
        }
      } catch (NumberFormatException | SQLException e) {
        new AdminHandler.ExceptionPrinter().handle(e, req, res);
        return null; // shouldn't get here anyways
      }
    }
  }
}
