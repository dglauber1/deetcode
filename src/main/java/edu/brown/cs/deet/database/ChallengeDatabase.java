package edu.brown.cs.deet.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains all SQL queries related to the challenge and test tables of the
 * database.
 * 
 * @author el13
 */
public class ChallengeDatabase implements AutoCloseable {
  /**
   * The Connection to the database.
   */
  private final Connection conn;

  /**
   * Constructs a new ChallengeDatabase.
   * 
   * @param db
   *          the path to the database
   * @throws SQLException
   *           When there is error with setting foreign_keys = on
   */
  public ChallengeDatabase(String db) throws SQLException {
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

    String foreignKeys = "PRAGMA FOREIGN_KEYS = ON;";
    try (PreparedStatement ps = conn.prepareStatement(foreignKeys)) {
      ps.execute();
    }
  }

  /**
   * Inserts a new challenge into the database.
   * 
   * @param pName
   *          the unique "path" name of the challenge
   * @param qName
   *          the name of the challenge
   * @param qPath
   *          the path to the directory that contains all challenge information
   * @param qCategory
   *          the category of the challenge
   * @return true if the challenge was successfully entered, false otherwise (if
   *         a challenge with that qName already exists in the database)
   * @throws SQLException
   *           when something has gone wrong with the database
   */
  public boolean insertNewChallenge(String pName, String qName, String qPath,
      String qCategory) throws SQLException {
    if (doesChallengeExist(pName)) {
      return false;
    }

    String query = "INSERT INTO challenge values(?, ?, ?, ?);";

    try (PreparedStatement ps = conn.prepareStatement(query)) {
      ps.setString(1, pName);
      ps.setString(2, qName);
      ps.setString(3, qPath);
      ps.setString(4, qCategory);

      ps.execute();

      return true;
    }
  }

  /**
   * Gets the basic information for a challenge, such as name, path, and
   * category.
   * 
   * @param pName
   *          The "path name" of the challenge
   * @return A List of Strings containing the path name, real name, path, and
   *         category of the challenge, in that order.
   * @throws SQLException
   *           when something goes wrong with the database
   */
  public List<String> getChallenge(String pName) throws SQLException {
    String query = "SELECT * from challenge WHERE question_id = ?;";

    try (PreparedStatement ps = conn.prepareStatement(query)) {
      ps.setString(1, pName);

      List<String> challengeInfo = new ArrayList<>();
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          challengeInfo.add(rs.getString(1));
          challengeInfo.add(rs.getString(2));
          challengeInfo.add(rs.getString(3));
          challengeInfo.add(rs.getString(4));
        }

        return challengeInfo;
      }
    }
  }

  /**
   * Deletes the basic information for a challenge, specifically name, path and
   * category, from the challenge table.
   * 
   * @param qName
   *          the "path name" of the challenge
   * @throws SQLException
   *           when something goes wrong with the database
   */
  public void deleteChallenge(String qName) throws SQLException {
    String query = "DELETE FROM challenge WHERE question_id = ?;";

    try (PreparedStatement ps = conn.prepareStatement(query)) {
      ps.setString(1, qName);

      ps.execute();
    }
  }

  /**
   * Edits a challenge currently in the database.
   * 
   * @param origPName
   *          the original unique name of the challenge
   * @param pName
   *          the "path name" of the challenge
   * @param qName
   *          the unique name of the challenge
   * @param qPath
   *          the path to the directory that contains all challenge information
   * @param qCategory
   *          the category of the challenge
   * @return true if the challenge was successfully entered, false otherwise (if
   *         a challenge with the name qName already exists in the database)
   * @throws SQLException
   *           when something has gone wrong with the database
   */
  public boolean editChallenge(String origPName, String pName, String qName,
      String qPath, String qCategory) throws SQLException {

    // check if pName already exists or not
    if (doesChallengeExist(pName) && !origPName.equals(pName)) {
      return false;
    }

    String query = "UPDATE challenge SET question_id = ?, question_name = ?, "
        + "path = ?, category = ? where question_id = ?;";

    try (PreparedStatement ps = conn.prepareStatement(query)) {
      ps.setString(1, pName);
      ps.setString(2, qName);
      ps.setString(3, qPath);
      ps.setString(4, qCategory);
      ps.setString(5, origPName);

      ps.execute();

      return true;
    }
  }

  /**
   * Determines if a challenge already exists in the database.
   * 
   * @param pName
   *          the "path name" of the challenge
   * @return true if already exists, false otherwise
   * @throws SQLException
   *           when something has gone wrong with the database
   */
  public boolean doesChallengeExist(String pName) throws SQLException {
    String query = "SELECT * FROM challenge WHERE question_id = ?;";

    try (PreparedStatement ps = conn.prepareStatement(query)) {
      ps.setString(1, pName);
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next();
      }
    }
  }

  /**
   * Determines if a category already exists in the database.
   * 
   * @param qCategory
   *          the category in question
   * @return True if the category already exists, False otherwise
   * @throws SQLException
   *           when something has gone wrong with the database
   */
  public boolean doesCategoryExist(String qCategory) throws SQLException {
    String query = "SELECT DISTINCT category FROM challenge WHERE category = ?;";

    try (PreparedStatement ps = conn.prepareStatement(query)) {
      ps.setString(1, qCategory);
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next();
      }
    }
  }

  /**
   * Gets all the Categories that exist on the site.
   * 
   * @return A List of Strings each representing a category
   * @throws SQLException
   *           when something has gone wrong with the database
   */
  public List<String> getAllCategories() throws SQLException {
    String query = "SELECT DISTINCT category FROM challenge;";

    try (PreparedStatement ps = conn.prepareStatement(query)) {
      List<String> categories = new ArrayList<>();
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          categories.add(rs.getString(1));
        }
      }
      return categories;
    }
  }

  public List<Map<String, String>> categoryToChallenges(String category)
      throws SQLException {
    String query = "SELECT question_id, question_name FROM challenge WHERE "
        + "category=?;";

    try (PreparedStatement ps = conn.prepareStatement(query)) {
      ps.setString(1, category);
      try (ResultSet rs = ps.executeQuery()) {
        List<Map<String, String>> challenges = new ArrayList<>();
        while (rs.next()) {
          Map<String, String> singleChallenge = new HashMap<>();
          singleChallenge.put("id", rs.getString(1));
          singleChallenge.put("name", rs.getString(2));
          challenges.add(singleChallenge);
        }
        return challenges;
      }
    }
  }

  public Boolean hasUserSolvedChallenge(String username, String challenge_id)
      throws SQLException {
    String query = "SELECT * FROM solution WHERE challenge_id=? AND username=? "
        + "AND passed=?;";
    try (PreparedStatement ps = conn.prepareStatement(query)) {
      ps.setString(1, challenge_id);
      ps.setString(2, username);
      ps.setBoolean(3, true);
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next();
      }
    }
  }

  /**
   * Inserts a row into the test table for the challenge and the language
   * corresponding to the challenge.
   * 
   * @param pName
   *          the "path name" of the challenge
   * @param language
   *          the language
   * @throws SQLException
   *           when something has gone wrong with the database
   */
  public void insertTestsForChallenge(String pName, String language)
      throws SQLException {
    String query = "INSERT INTO test values(?, ?);";

    try (PreparedStatement ps = conn.prepareStatement(query)) {
      ps.setString(1, pName);
      ps.setString(2, language);

      ps.execute();
    }
  }

  /**
   * Gets all the languages that are supported for a particular challenge.
   * 
   * @param pName
   *          the "path name" of the challenge
   * @return a List of the languages that are supported for the challenge
   * @throws SQLException
   *           when the database screws up
   */
  public List<String> getLanguagesSupported(String pName) throws SQLException {
    String query = "SELECT language FROM test WHERE challenge_name = ?;";

    try (PreparedStatement ps = conn.prepareStatement(query)) {
      ps.setString(1, pName);

      List<String> languages = new ArrayList<>();
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          languages.add(rs.getString(1));
        }
        return languages;
      }
    }
  }

  /**
   * Deletes a supported language for a challenge.
   * 
   * @param oName
   *          the "path name" of the challenge
   * @param language
   *          the supported language
   * @throws SQLException
   *           when the database screws up
   */
  public void deleteLanguageSupported(String pName, String language)
      throws SQLException {
    String query = "DELETE FROM test WHERE challenge_name = ? AND language = ?;";

    try (PreparedStatement ps = conn.prepareStatement(query)) {
      ps.setString(1, pName);
      ps.setString(2, language);

      ps.execute();
    }
  }

  @Override
  public void close() {
    try {
      conn.close();
    } catch (SQLException e) {
      System.out.println("ERROR: Couldn't close the connection "
          + "associated with ChallengeDatabase");
      System.exit(1);
    }
  }
}
