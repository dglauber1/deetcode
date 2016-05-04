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
  /**
   * The Connection to the database.
   */
  private final Connection conn;

  /**
   * Constructs a new LeaderboardDatabase.
   *
   * @param db
   *          the path to the database
   * @throws SQLException
   *           when there is a problem with turning foreign_keys on
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
   * @param cID
   *          - the ID of the challenge.
   * @param uID
   *          - the username of the user.
   * @param language
   *          - the language to check
   * @return true if the challenge was attempted, false otherwise
   * @throws SQLException
   */
  public boolean isChallengeAttempedByUser(String cID, String uID,
    String language) throws SQLException {
    String query =
        "SELECT * FROM solution WHERE username = ? "
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
   * @param cID
   *          - the challenge ID.
   * @param uID
   *          - the user ID.
   * @return the language that the user used, or null if the challenge/user
   *         wasn't in the database
   * @throws SQLException
   */
  public String getUserChallengeLanguage(String cID, String uID)
      throws SQLException {
    String query =
        "SELECT DISTINCT language FROM solution WHERE username = ? "
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
   * @param qName
   *          the username of the user
   * @return a List of List of Strings that represent the solution information
   *         of the user, where each inner List represents information for one
   *         row
   * @throws SQLException
   *           when something with the database goes awry
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
   * @param data
   *          - the data associated with the solution. Contains challengeID,
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
   * Gets the top twenty entries of a particular challenge given a language.
   *
   * @param qName
   *          the name of the challenge
   * @param qLang
   *          the language to query for
   * @return A List of a List of Strings, where each inner List contains the
   *         follow information in this order: challenge name, username,
   *         cumulative score, language. The inner lists are ordered by
   *         cumulative score and there is a limit of 20.
   * @throws SQLException
   *           when something goes awry with the database
   */
  public List<List<String>> topTwentyOfChallengeLanguage(String qName,
    String qLang) throws SQLException {
    String query =
        "SELECT challenge_id, username, aggregate, language "
            + "FROM solution WHERE challenge_id = ? AND language = ? AND passed = 1 "
            + "ORDER BY aggregate DESC LIMIT 20;";

    try (PreparedStatement ps = conn.prepareStatement(query)) {
      ps.setString(1, qName);
      ps.setString(2, qLang);

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

  @Override
  public void close() throws SQLException {
    conn.close();
  }
}
