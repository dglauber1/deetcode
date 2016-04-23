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

  @Override
  public void close() throws SQLException {
    conn.close();
  }
}
