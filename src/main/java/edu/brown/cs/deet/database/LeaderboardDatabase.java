package edu.brown.cs.deet.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A bunch of SQL Queries that deal with Leaderboard + User page information.
 * 
 * @author el13, el51
 */
public class LeaderboardDatabase implements AutoCloseable {
  public final static int LEADERBOARD_SIZE = 20;

  /**
   * The Connection to the database.
   */
  private final Connection conn;

  /**
   * Constructs a new LeaderboardDatabase.
   *
   * @param db the path to the database
   * @throws SQLException when there is a problem with turning foreign_keys on
   */
  public LeaderboardDatabase(String db) throws SQLException {
    try {
      // Set up the Connection
      Class.forName("org.sqlite.JDBC");
    } catch (ClassNotFoundException e) {
      throw new IllegalArgumentException("Bad database");
    }
    try {
      // Store the connection
      conn = DriverManager.getConnection("jdbc:sqlite:" + db);
    } catch (SQLException e) {
      throw new IllegalArgumentException("Bad database");
    }

    // turning foreign_keys on
    String foreignKeys = "PRAGMA FOREIGN_KEYS = ON;";
    try (PreparedStatement ps = conn.prepareStatement(foreignKeys)) {
      ps.execute();
    }
  }

  /**
   * Returns whether or not a challenge has been attempted by a user.
   *
   * @param cID - the ID of the challenge.
   * @param uID - the username of the user.
   * @return true if the challenge was attempted, false otherwise
   * @throws SQLException
   */
  public boolean isChallengeAttempedByUser(String cID, String uID)
      throws SQLException {
    String query = "SELECT * FROM solution WHERE username = ? "
        + "AND challenge_id = ?;";
    try (PreparedStatement ps = conn.prepareStatement(query)) {
      ps.setString(1, uID);
      ps.setString(2, cID);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          // User has attempted the challenge
          return true;
        }
        // User has not attempted the challenge
        return false;
      }
    }
  }

  /**
   * Returns whether or not a challenge has been attempted by a user.
   *
   * @param cID - the ID of the challenge.
   * @param uID - the username of the user.
   * @param language - the language to check
   * @return true if the challenge was attempted, false otherwise
   * @throws SQLException
   */
  public boolean isChallengeAttempedByUser(String cID, String uID,
      String language) throws SQLException {
    String query = "SELECT * FROM solution WHERE username = ? "
        + "AND challenge_id = ? AND language = ?;";
    try (PreparedStatement ps = conn.prepareStatement(query)) {
      ps.setString(1, uID);
      ps.setString(2, cID);
      ps.setString(3, language);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          // User has attempted the challenge
          return true;
        }
        // User has not attempted the challenge
        return false;
      }
    }
  }

  /**
   * Returns the language that a user used to solve a challenge.
   *
   * @param cID - the challenge ID.
   * @param uID - the user ID.
   * @return the language that the user used, or null if the challenge/user
   *         wasn't in the database
   * @throws SQLException
   */
  public String getUserChallengeLanguage(String cID, String uID)
      throws SQLException {
    String query = "SELECT DISTINCT language FROM solution WHERE username = ? "
        + "AND challenge_id = ?;";
    try (PreparedStatement ps = conn.prepareStatement(query)) {
      ps.setString(1, uID);
      ps.setString(2, cID);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return rs.getString(1);
        }
        return null;
      }
    }
  }

  /**
   * Gets all the solution information for a specific user.
   *
   * @param qName the username of the user
   * @return a List of List of Strings that represent the solution information
   *         of the user, where each inner List represents information for one
   *         row
   * @throws SQLException when something with the database goes awry
   */
  public List<List<String>> getSolutionsForUser(String qName)
      throws SQLException {
    String query = "SELECT * FROM solution WHERE username = ?;";
    try (PreparedStatement ps = conn.prepareStatement(query)) {
      ps.setString(1, qName);

      List<List<String>> solutions = new ArrayList<>();
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          List<String> solution = new ArrayList<>();
          solution.add(rs.getString(1));
          solution.add(rs.getString(2));
          solution.add(((Boolean) rs.getBoolean(3)).toString());
          solution.add(((Double) rs.getDouble(4)).toString());
          solution.add(((Double) rs.getDouble(5)).toString());
          solution.add(((Double) rs.getDouble(6)).toString());
          solution.add(((Double) rs.getDouble(7)).toString());
          solution.add(rs.getString(8));
          solution.add(((Double) rs.getDouble(9)).toString());

          solutions.add(solution);
        }
        return solutions;
      }
    }
  }

  /**
   * Inserts a solution into the Leaderboard.
   *
   * @param data - the data associated with the solution. Contains challengeID,
   *          username, passed, efficiency, numLines, timeToSolve, aggregate,
   *          language, and timestamp in that order.
   * @throws SQLException
   * @throws IllegalArgumentException
   */
  public void addSolution(String challengeID, String username, boolean passed,
      double efficiency, double numLines, double timeToSolve, double aggregate,
      String language, double timeStamp) throws SQLException {
    String query = "INSERT INTO solution VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
    try (PreparedStatement ps = conn.prepareStatement(query)) {
      ps.setString(1, challengeID);
      ps.setString(2, username);
      ps.setBoolean(3, passed);
      ps.setDouble(4, efficiency);
      ps.setDouble(5, numLines);
      ps.setDouble(6, timeToSolve);
      ps.setDouble(7, aggregate);
      ps.setString(8, language);
      ps.setDouble(9, timeStamp);
      ps.addBatch();
      ps.executeBatch();
    }
  }

  /**
   * Deletes solution from the table.
   * 
   * @param challengeID - the challenge ID
   * @param username - the username
   * @param language - the language
   * @throws SQLException if there's an exception
   */
  public void deleteSolution(String challengeID, String username,
      String language) throws SQLException {
    String query = "DELETE FROM solution WHERE "
        + "challenge_id = ? AND username = ? AND language = ?;";
    try (PreparedStatement ps = conn.prepareStatement(query)) {
      ps.setString(1, challengeID);
      ps.setString(2, username);
      ps.setString(3, language);
      ps.addBatch();
      ps.executeBatch();
    }
  }

  /**
   * Updates a solution in the Leaderboard.
   * 
   * @param data - the data associated with the solution. Contains challengeID,
   *          username, passed, efficiency, numLines, timeToSolve, aggregate,
   *          language, and timestamp in that order.
   * @throws SQLException
   * @throws IllegalArgumentException
   */
  public void updateSolution(String challengeID, String username,
      boolean passed, double efficiency, double numLines, double timeToSolve,
      double aggregate, String language, double timeStamp) throws SQLException {
    String query = "UPDATE solution SET challenge_id = ?, username = ?, passed = ?, efficiency = ?, "
        + "num_lines = ?, time_to_solve = ?, aggregate = ?, language = ?, timeStamp = ? "
        + "WHERE username = ? AND challenge_id = ? AND language = ?;";
    try (PreparedStatement ps = conn.prepareStatement(query)) {
      ps.setString(1, challengeID);
      ps.setString(2, username);
      ps.setBoolean(3, passed);
      ps.setDouble(4, efficiency);
      ps.setDouble(5, numLines);
      ps.setDouble(6, timeToSolve);
      ps.setDouble(7, aggregate);
      ps.setString(8, language);
      ps.setDouble(9, timeStamp);
      ps.setString(10, username);
      ps.setString(11, challengeID);
      ps.setString(12, language);
      ps.addBatch();
      ps.executeBatch();
    }
  }

  /**
   * Gets the top twenty entries of a particular challenge given a language.
   * They are ordered by the aggregate score.
   *
   * @param qName the name of the challenge
   * @param qLang the language to query for
   * @return A List of a List of Strings, where each inner List contains the
   *         follow information in this order: challenge name, username,
   *         cumulative score, language. The inner lists are ordered by
   *         cumulative score and there is a limit of 20.
   * @throws SQLException when something goes awry with the database
   */
  public List<List<String>> topTwentyOfChallengeLanguage(String qName,
      String qLang) throws SQLException {
    String query = "SELECT challenge_id, username, aggregate, language "
        + "FROM solution WHERE challenge_id = ? AND language = ? AND passed = 1 "
        + "ORDER BY aggregate DESC LIMIT ?;";

    try (PreparedStatement ps = conn.prepareStatement(query)) {
      ps.setString(1, qName);
      ps.setString(2, qLang);
      ps.setInt(3, LEADERBOARD_SIZE);

      List<List<String>> topTwenty = new ArrayList<>();
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          List<String> entry = new ArrayList<>();
          entry.add(rs.getString(1));
          entry.add(rs.getString(2));
          entry.add(((Double) rs.getDouble(3)).toString());
          entry.add(rs.getString(4));

          topTwenty.add(entry);
        }

        return topTwenty;
      }
    }
  }

  /**
   * Gets the top twenty entries of a particular challenge given a language.
   * They are ordered by the code efficiency score.
   *
   * @param qName the name of the challenge
   * @param qLang the language to query for
   * @return A List of a List of Strings, where each inner List contains the
   *         follow information in this order: challenge name, username,
   *         efficiency score, language. The inner lists are ordered by code
   *         efficiency score and there is a limit of 20.
   * @throws SQLException when something goes awry with the database
   */
  public List<List<String>> topTwentyOfChallengeLanguageEfficiency(
      String qName, String qLang) throws SQLException {
    String query = "SELECT challenge_id, username, efficiency, language "
        + "FROM solution WHERE challenge_id = ? AND language = ? AND passed = 1 "
        + "ORDER BY efficiency ASC LIMIT ?;";

    try (PreparedStatement ps = conn.prepareStatement(query)) {
      ps.setString(1, qName);
      ps.setString(2, qLang);
      ps.setInt(3, LEADERBOARD_SIZE);

      List<List<String>> topTwenty = new ArrayList<>();
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          List<String> entry = new ArrayList<>();
          entry.add(rs.getString(1));
          entry.add(rs.getString(2));
          entry.add(((Double) rs.getDouble(3)).toString());
          entry.add(rs.getString(4));

          topTwenty.add(entry);
        }

        return topTwenty;
      }
    }
  }

  /**
   * Gets the top twenty entries of a particular challenge given a language.
   * They are ordered by the code num_lines score.
   *
   * @param qName the name of the challenge
   * @param qLang the language to query for
   * @return A List of a List of Strings, where each inner List contains the
   *         follow information in this order: challenge name, username,
   *         num_lines score, language. The inner lists are ordered by num_lines
   *         score and there is a limit of 20.
   * @throws SQLException when something goes awry with the database
   */
  public List<List<String>> topTwentyOfChallengeLanguageNumLines(String qName,
      String qLang) throws SQLException {
    String query = "SELECT challenge_id, username, num_lines, language "
        + "FROM solution WHERE challenge_id = ? AND language = ? AND passed = 1 "
        + "ORDER BY num_lines ASC LIMIT ?;";

    try (PreparedStatement ps = conn.prepareStatement(query)) {
      ps.setString(1, qName);
      ps.setString(2, qLang);
      ps.setInt(3, LEADERBOARD_SIZE);

      List<List<String>> topTwenty = new ArrayList<>();
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          List<String> entry = new ArrayList<>();
          entry.add(rs.getString(1));
          entry.add(rs.getString(2));
          entry.add(((Double) rs.getDouble(3)).toString());
          entry.add(rs.getString(4));

          topTwenty.add(entry);
        }

        return topTwenty;
      }
    }
  }

  /**
   * Gets the top twenty entries of a particular challenge given a language.
   * They are ordered by the code time_to_solve score.
   *
   * @param challengeId the id of the challenge
   * @param qLang the language to query for
   * @return A List of a List of Strings, where each inner List contains the
   *         follow information in this order: challenge name, username,
   *         time_to_solve score, language. The inner lists are ordered by
   *         time_to_solve score and there is a limit of 20.
   * @throws SQLException when something goes awry with the database
   */
  public List<List<String>> topTwentyOfChallengeLanguageTimeSolve(
      String challengeId, String qLang) throws SQLException {
    String query = "SELECT challenge_id, username, time_to_solve, language "
        + "FROM solution WHERE challenge_id = ? AND language = ? AND passed = 1 "
        + "ORDER BY time_to_solve ASC LIMIT ?;";

    try (PreparedStatement ps = conn.prepareStatement(query)) {
      ps.setString(1, challengeId);
      ps.setString(2, qLang);
      ps.setInt(3, LEADERBOARD_SIZE);

      List<List<String>> topTwenty = new ArrayList<>();
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          List<String> entry = new ArrayList<>();
          entry.add(rs.getString(1));
          entry.add(rs.getString(2));
          entry.add(((Double) rs.getDouble(3)).toString());
          entry.add(rs.getString(4));

          topTwenty.add(entry);
        }

        return topTwenty;
      }
    }
  }

  /**
   * Calculates the average aggregate score given a challenge Id and language.
   * 
   * @param challengeId The challenge ID
   * @param language The language to look for
   * @return A value representing the average of the metric over all entries for
   *         that challenge in that given language. Returns 0.0 if there is some
   *         weird error with the database.
   * @throws SQLException if something goes awry with the database
   */
  public double averageAggregateGivenIdLanguage(String challengeId,
      String language) throws SQLException {
    String query = "SELECT AVG(aggregate) FROM solution WHERE challenge_id = ? "
        + "AND language = ? AND passed = 1;";

    try (PreparedStatement ps = conn.prepareStatement(query)) {
      ps.setString(1, challengeId);
      ps.setString(2, language);

      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return rs.getDouble(1);
        } else {
          return 0.0;
        }
      }
    }
  }

  /**
   * Calculates the average efficiency score given a challenge Id and language.
   * 
   * @param challengeId The challenge ID
   * @param language The language to look for
   * @return A value representing the average of the metric over all entries for
   *         that challenge in that given language. Returns 0.0 if there is some
   *         weird error with the database.
   * @throws SQLException if something goes awry with the database
   */
  public double averageEfficiencyGivenIdLanguage(String challengeId,
      String language) throws SQLException {
    String query = "SELECT AVG(efficiency) FROM solution WHERE challenge_id = ? AND language = ? "
        + "AND passed = 1;";

    try (PreparedStatement ps = conn.prepareStatement(query)) {
      ps.setString(1, challengeId);
      ps.setString(2, language);

      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return rs.getDouble(1);
        } else {
          return 0.0;
        }
      }
    }
  }

  /**
   * Calculates the average brevity score given a challenge Id and language.
   * 
   * @param challengeId The challenge ID
   * @param language The language to look for
   * @return A value representing the average of the metric over all entries for
   *         that challenge in that given language. Returns 0.0 if there is some
   *         weird error with the database.
   * @throws SQLException if something goes awry with the database
   */
  public double averageBrevityGivenIdLanguage(String challengeId,
      String language) throws SQLException {
    String query = "SELECT AVG(num_lines) FROM solution WHERE challenge_id = ? "
        + "AND language = ? AND passed = 1;";

    try (PreparedStatement ps = conn.prepareStatement(query)) {
      ps.setString(1, challengeId);
      ps.setString(2, language);

      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return rs.getDouble(1);
        } else {
          return 0.0;
        }
      }
    }
  }

  /**
   * Calculates the average time to completion given a challenge Id and
   * language.
   * 
   * @param challengeId The challenge ID
   * @param language The language to look for
   * @return A value representing the average of the metric over all entries for
   *         that challenge in that given language. Returns 0.0 if there is some
   *         weird error with the database.
   * @throws SQLException if something goes awry with the database
   */
  public double averageTimeGivenIdLanguage(String challengeId, String language)
      throws SQLException {
    String query = "SELECT AVG(time_to_solve) FROM solution WHERE challenge_id = ? "
        + "AND language = ? AND passed = 1;";

    try (PreparedStatement ps = conn.prepareStatement(query)) {
      ps.setString(1, challengeId);
      ps.setString(2, language);

      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return rs.getDouble(1);
        } else {
          return 0.0;
        }
      }
    }
  }

  /**
   * Gets a user's aggregate score for a challenge.
   * 
   * @param challengeId The challengeId of the challenge
   * @param username The username of the user
   * @return The score. Returns -1 if the user didn't pass the challenge.
   * @throws SQLException if something goes awry with the database
   */
  public double getAggregateForUserAndChallenge(String challengeId,
      String username) throws SQLException {
    String query = "SELECT aggregate FROM solution WHERE challenge_id = ? "
        + "AND username = ? AND passed = 1;";

    try (PreparedStatement ps = conn.prepareStatement(query)) {
      ps.setString(1, challengeId);
      ps.setString(2, username);

      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return rs.getDouble(1);
        } else {
          return -1.0;
        }
      }
    }
  }

  /**
   * Gets a user's efficiency score for a challenge.
   * 
   * @param challengeId The challengeId of the challenge
   * @param username The username of the user
   * @return The score. Returns -1 if the user didn't pass the challenge.
   * @throws SQLException if something goes awry with the database
   */
  public double getEfficiencyForUserAndChallenge(String challengeId,
      String username) throws SQLException {
    String query = "SELECT efficiency FROM solution WHERE challenge_id = ? "
        + "AND username = ? AND passed = 1;";

    try (PreparedStatement ps = conn.prepareStatement(query)) {
      ps.setString(1, challengeId);
      ps.setString(2, username);

      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return rs.getDouble(1);
        } else {
          return -1.0;
        }
      }
    }
  }

  /**
   * Gets a user's brevity score for a challenge.
   * 
   * @param challengeId The challengeId of the challenge
   * @param username The username of the user
   * @return The score. Returns -1 if the user didn't pass the challenge.
   * @throws SQLException if something goes awry with the database
   */
  public double getBrevityForUserAndChallenge(String challengeId,
      String username) throws SQLException {
    String query = "SELECT num_lines FROM solution WHERE challenge_id = ? "
        + "AND username = ? AND passed = 1;";

    try (PreparedStatement ps = conn.prepareStatement(query)) {
      ps.setString(1, challengeId);
      ps.setString(2, username);

      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return rs.getDouble(1);
        } else {
          return -1.0;
        }
      }
    }
  }

  /**
   * Gets a user's time score for a challenge.
   * 
   * @param challengeId The challengeId of the challenge
   * @param username The username of the user
   * @return The score. Returns -1 if the user didn't pass the challenge.
   * @throws SQLException if something goes awry with the database
   */
  public double getTimeForUserAndChallenge(String challengeId, String username)
      throws SQLException {
    String query = "SELECT time_to_solve FROM solution WHERE challenge_id = ? "
        + "AND username = ? AND passed = 1;";

    try (PreparedStatement ps = conn.prepareStatement(query)) {
      ps.setString(1, challengeId);
      ps.setString(2, username);

      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return rs.getDouble(1);
        } else {
          return -1.0;
        }
      }
    }
  }

  @Override
  public void close() throws SQLException {
    conn.close();
  }
}
