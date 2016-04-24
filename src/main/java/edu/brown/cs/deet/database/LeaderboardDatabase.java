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
 * @author el13
 */
public class LeaderboardDatabase implements AutoCloseable {
  /**
   * The Connection to the database.
   */
  private final Connection conn;

  /**
   * Constructs a new LeaderboardDatabase.
   * @param db
   *          the path to the database
   */
  public LeaderboardDatabase(String db) {
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
  }

  /**
   * Gets all the solution information for a specific user.
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

          solutions.add(solution);
        }
        return solutions;
      }
    }
  }

  /**
   * Gets the top twenty entries of a particular challenge given a language.
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
    String query = "SELECT challenge_id, username, aggregate, language "
        + "FROM solution WHERE challenge_id = ? AND language = ? "
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
