package edu.brown.cs.deet.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDatabase implements AutoCloseable {
  /**
   * The Connection to the database.
   */
  private final Connection conn;

  /**
   * Constructs a new ChallengeDatabase.
   * @param db
   *          the path to the database
   * @throws SQLException
   *           when there's an issue with turning foreign_keys on
   */
  public UserDatabase(String db) throws SQLException {
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

  public boolean addNewUser(String username, String id, Boolean is_admin,
      String name) throws SQLException {

    String query = "INSERT INTO user values(?, ?, ?, ?);";

    try (PreparedStatement ps = conn.prepareStatement(query)) {
      ps.setString(1, username);
      ps.setString(2, id);
      ps.setBoolean(3, is_admin);
      ps.setString(4, name);

      ps.execute();

      return true;
    }
  }

  /**
   * Determines if a user already exists in the database.
   * @param fbid
   *          the facebook id of the potential user
   * @return True if the user already exists, False otherwise
   * @throws SQLException
   *           when something has gone wrong with the database
   */
  public boolean doesUserExistWithID(String fbID) throws SQLException {
    String query = "SELECT DISTINCT name FROM user WHERE id = ?;";

    try (PreparedStatement ps = conn.prepareStatement(query)) {
      ps.setString(1, fbID);
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next();
      }
    }
  }

  /**
   * Determines if a user already exists in the database.
   * @param username
   *          the username of the potential user
   * @return True if the user already exists, False otherwise
   * @throws SQLException
   *           when something has gone wrong with the database
   */
  public boolean doesUserExistWithUsername(String username) throws SQLException {
    String query = "SELECT DISTINCT name FROM user WHERE username = ?;";

    try (PreparedStatement ps = conn.prepareStatement(query)) {
      ps.setString(1, username);
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next();
      }
    }
  }

  public String getUsernameFromID(String id) throws SQLException {
    String query = "SELECT DISTINCT username FROM user WHERE id = ?;";

    try (PreparedStatement ps = conn.prepareStatement(query)) {
      ps.setString(1, id);
      try (ResultSet rs = ps.executeQuery()) {
        return rs.getString(1);
      }
    }
  }

  /**
   * Returns the name of the user with a certain username.
   * @param username
   *          The username of the user
   * @return the name of the user, or null if the username does not exist
   * @throws SQLException
   *           when something has gone wrong with the database
   */
  public String getNameFromUsername(String username) throws SQLException {
    String query = "SELECT name FROM user WHERE username = ?;";

    try (PreparedStatement ps = conn.prepareStatement(query)) {
      ps.setString(1, username);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return rs.getString(1);
        }

        return null;
      }
    }
  }

  @Override
  public void close() {
    try {
      conn.close();
    } catch (SQLException e) {
      System.out.println("ERROR: Couldn't close the connection "
          + "associated with UserDatabase");
      System.exit(1);
    }
  }
}
