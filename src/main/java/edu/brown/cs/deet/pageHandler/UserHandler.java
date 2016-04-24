package edu.brown.cs.deet.pageHandler;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import edu.brown.cs.deet.database.LeaderboardDatabase;

public class UserHandler {
  private LeaderboardDatabase leaderboard;
  private static final int CHALLENGE_NAME = 0;
  private static final int PASSED = 2;
  /**
   * For a full row of the solution table.
   */
  private static final int CHALLENGE_LANGUAGE = 7;

  /**
   * Constructor for an UserHandler.
   * @param cdb
   *          A LeaderboardDatabase object
   */
  public UserHandler(LeaderboardDatabase cdb) {
    this.leaderboard = cdb;
  }

  /**
   * Gets all of the attempted challenge info for a user.
   * @param qName
   *          the username of the user
   * @return A List of a List of Strings, where each inner list contains
   *         information for one challenge for that user. The information will
   *         be in this order in the list: name of the challenge, whether they
   *         passed ("true" or "false"), their rank as a String (n/a if out of
   *         the top 20), solution language, and solution.
   * @throws SQLException
   *           when something with the database goes awry
   * @throws IOException
   *           when there is a file read error
   */
  public List<List<String>> getChallengeInfoForUser(String qName)
      throws SQLException, IOException {
    List<List<String>> toReturn = new ArrayList<>();

    List<List<String>> solutionsForUser = leaderboard
        .getSolutionsForUser(qName);

    for (List<String> solution : solutionsForUser) {
      // create a new inner list for the return list of lists
      List<String> oneChallengeInfo = new ArrayList<>();
      oneChallengeInfo.add(solution.get(CHALLENGE_NAME));
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
            oneChallengeInfo.add(((Integer) i).toString());
            break;
          }
        }

        if (i == topTwenty.size()) {
          oneChallengeInfo.add("n/a");
        }

        // add the language user's solution is in
        oneChallengeInfo.add(solution.get(CHALLENGE_LANGUAGE));

        // add the user's actual solution
        File solutionFile = new File("challenges/"
            + solution.get(CHALLENGE_NAME) + "/"
            + solution.get(CHALLENGE_LANGUAGE) + "/solutions/" + qName + "."
            + solution.get(CHALLENGE_LANGUAGE));
        String code = Files.toString(solutionFile, Charsets.UTF_8);
        oneChallengeInfo.add(code);
      } else {
        oneChallengeInfo.add("false");
        oneChallengeInfo.add("n/a");
        oneChallengeInfo.add("n/a");
        oneChallengeInfo.add("n/a");
      }
      toReturn.add(oneChallengeInfo);
    }

    return toReturn;
  }
}
