package edu.brown.cs.deet.deetcode.pageHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.TemplateViewRoute;

import com.google.common.base.Charsets;

import edu.brown.cs.deet.database.ChallengeDatabase;
import edu.brown.cs.deet.database.LeaderboardDatabase;
import edu.brown.cs.deet.database.UserDatabase;
import edu.brown.cs.deet.deetcode.pageHandler.AdminHandler.ExceptionPrinter;

public class UserHandler {
  private static LeaderboardDatabase leaderboard;
  private static UserDatabase user;
  private static ChallengeDatabase challenge;
  private static final int CHALLENGE_NAME = 0;
  private static final int PASSED = 2;
  /**
   * For a full row of the solution table.
   */
  private static final int CHALLENGE_LANGUAGE = 7;

  /**
   * Private constructor for an UserHandler.
   */
  private UserHandler() {
  }

  /**
   * Sets the LeaderboardDatabase.
   *
   * @param ldb the Leaderboard Database.
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
   * Sets the ChallengeDatabase.
   *
   * @param cdb the ChallengeDatabase.
   */
  public static void setChallengeDatabase(ChallengeDatabase cdb) {
    challenge = cdb;
  }

  /**
   * Handles user pages.
   *
   * @author el13
   */
  public static class UserPageHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      String username = req.params(":username");

      try {
        // Get the username of user viewing page to determine what to show
        String currUserUsername = user.getUsernameFromID(req.cookie("user"));
        // String currUserUsername = "dglauber";
        List<List<String>> results = getChallengeInfoForUser(username,
            currUserUsername);
        String name = getNameFromUsername(username);

        Map<String, Object> variables = new HashMap<>();

        variables.put("title", username);
        variables.put("name", name);
        variables.put("results", results);
        variables.put("username", currUserUsername);

        return new ModelAndView(variables, "user.ftl");
      } catch (SQLException e) {
        new ExceptionPrinter().handle(e, req, res);
      } catch (IOException e) {
        new ExceptionPrinter().handle(e, req, res);
      }
      return null; // shouldn't ever get here?
    }
  }

  /**
   * Gets all of the attempted challenge info for a user.
   *
   * @param qName the username of the user whose page is being requested
   * @param currUser the username of the user viewing the User Page
   * @return A List of a List of Strings, where each inner list contains
   *         information for one challenge for that user. The information will
   *         be in this order in the list: name of the challenge, whether they
   *         passed ("true" or "false"), their rank as a String (n/a if out of
   *         the top 20), solution language, and solution.
   * @throws SQLException when something with the database goes awry
   * @throws IOException when there is a file read error
   */
  public static List<List<String>> getChallengeInfoForUser(String qName,
      String currUser) throws SQLException, IOException {
    List<List<String>> toReturn = new ArrayList<>();

    List<List<String>> solutionsForUser = leaderboard
        .getSolutionsForUser(qName);

    for (List<String> solution : solutionsForUser) {
      // create a new inner list for the return list of lists
      List<String> oneChallengeInfo = new ArrayList<>();
      oneChallengeInfo
          .add(challenge.getNameFromId(solution.get(CHALLENGE_NAME)));
      if (solution.get(PASSED).equals("true")) {
        // add that the user has passed the challenge
        oneChallengeInfo.add("true");

        // get the top twenty entries for the current challenge
        List<List<String>> topTwenty = leaderboard
            .topTwentyOfChallengeLanguage(solution.get(CHALLENGE_NAME),
                solution.get(CHALLENGE_LANGUAGE));

        // add the rank, if it exists
        int i = 0;
        for (i = 0; i < topTwenty.size(); i++) {
          if (topTwenty.get(i).get(1).equals(qName)) {
            oneChallengeInfo.add(((Integer) (i + 1)).toString());
            break;
          }
        }

        if (i == topTwenty.size()) {
          oneChallengeInfo.add("n/a");
        }

        // add the language user's solution is in
        oneChallengeInfo.add(solution.get(CHALLENGE_LANGUAGE));

        // add the user's actual solution if "current user" has done the
        // challenge
        if (leaderboard.isChallengeAttempedByUser(solution.get(CHALLENGE_NAME),
            currUser)) {
          String suffix = (solution.get(CHALLENGE_LANGUAGE).equals("python")) ? "py"
              : "js";
          String solutionPath = "challenges/" + solution.get(CHALLENGE_NAME)
              + "/" + solution.get(CHALLENGE_LANGUAGE) + "/solutions/" + qName
              + "." + suffix;

          byte[] encoded = Files.readAllBytes(Paths.get(solutionPath));
          String code = new String(encoded, Charsets.UTF_8);
          oneChallengeInfo.add(code);
        } else {
          oneChallengeInfo
              .add("You must attempt the challenge before you can see the solution.");
        }
      } else {
        oneChallengeInfo.add("false");
        oneChallengeInfo.add("n/a");
        oneChallengeInfo.add(solution.get(CHALLENGE_LANGUAGE));
        oneChallengeInfo.add("n/a");
      }
      toReturn.add(oneChallengeInfo);
    }

    return toReturn;
  }

  /**
   * Returns the name of the user with a certain username.
   *
   * @param username The username of the user
   * @return the name of the user, or null if the username does not exist
   * @throws SQLException when something has gone wrong with the database
   */
  public static String getNameFromUsername(String username) throws SQLException {
    return user.getNameFromUsername(username);
  }
}
