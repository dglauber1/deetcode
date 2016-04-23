package edu.brown.cs.deet.pageHandler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import edu.brown.cs.deet.database.ChallengeDatabase;

/**
 * Handles all of the Admin-related requests, such as adding a new challenge or
 * editing a pre-existing challenge.
 * @author el13
 */
public class AdminHandler {
  private ChallengeDatabase challenges;

  /**
   * Constructor for an AdminHandler.
   * @param cdb A ChallengeDatabase object
   */
  public AdminHandler(ChallengeDatabase cdb) {
    this.challenges = cdb;
  }

  /**
   * Processes the new "basic" information for a new challenge.
   * @param category The new category
   * @param name The new name
   * @param description The new description
   * @return True if the information was successfully edited, false otherwise
   * @throws SQLException if the database is messed up somehow
   * @throws IOException If an I/O error occurred with creating a file
   */
  public boolean newBasicInfo(String category, String name, String description)
      throws SQLException, IOException {
    String path = "challenges/" + name;

    // check if an insert to the database is successful
    if (challenges.insertNewChallenge(name, path, category)) {
      // make the directory for the challenge
      File challengeDir = new File(path);
      challengeDir.mkdir();

      // Create the file for the description
      File challengeDesc = new File(path + "/description.txt");
      challengeDesc.createNewFile();

      try (BufferedWriter bw =
          new BufferedWriter(new FileWriter(challengeDesc))) {
        bw.write(description);
      }

      // Create the solutions folder
      File solutionsDir = new File(path + "/solutions");
      solutionsDir.mkdir();
      return true;
    }

    return false;
  }

  /**
   * Puts the test input/output and stub code into the challenge directory. If a
   * challenge directory has input, output, or stub files already present, the
   * behavior of this function will be undefined. For example, an input file can
   * be created with the new input for tests, but the output file may not be
   * created with the new output information for tests because an output.txt
   * file may already exist (and will then also skip the creation of the
   * stub.txt file). Therefore, call this ONLY after newBasicInfo is called (and
   * IMMEDIATELY afterwards).
   * @param name The name of the challenge
   * @param input The input for the test cases
   * @param output The output for the test cases
   * @param stub The stub code for the test cases
   * @param language The language that this information is related to
   * @return True if all the test info was properly entered into the challenges
   * directory, false otherwise. A False may occur when the challenge directory
   * with name "name" doesn't exist. Or a file for some txt file/directory for
   * the Language already exists.
   * @throws IOException If an I/O error occurred with creating a file
   * @throws SQLException When the database screws up
   */
  public boolean newTestInfo(String name, String input, String output,
      String stub, String language) throws IOException, SQLException {

    File directory = new File("challenges/" + name);

    // first checks to see if the directory exists
    if (directory.exists()) {
      // Adding the information to the database
      challenges.insertTestsForChallenge(name, language);

      // Make the directory for Language-related stuff
      String path = "challenges/" + name + "/" + language;
      File javaPath = new File(path);

      if (!javaPath.mkdir()) {
        return false;
      }

      // Create test input file and write the input to it
      File inputFile = new File(path + "/input.txt");

      if (!inputFile.createNewFile()) {
        return false;
      }

      try (BufferedWriter bw = new BufferedWriter(new FileWriter(inputFile))) {
        bw.write(input);
      }

      // Create test output file and write the output to it
      File outputFile = new File(path + "/output.txt");

      if (!outputFile.createNewFile()) {
        return false;
      }

      try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {
        bw.write(output);
      }

      // Create stub file and write the stub to it
      File stubFile = new File(path + "/stub.txt");

      if (!stubFile.createNewFile()) {
        return false;
      }

      try (BufferedWriter bw = new BufferedWriter(new FileWriter(stubFile))) {
        bw.write(stub);
      }

      return true;
    }

    return false;
  }

  /**
   * Determines if a challenge by a certain name already exists.
   * @param qName the name of the challenge
   * @return True if the challenge already exists, false otherwise.
   * @throws SQLException if something with the database goes awry
   */
  public boolean doesChallengeExist(String qName) throws SQLException {
    return challenges.doesChallengeExist(qName);
  }

  /**
   * Determines if a category already exists.
   * @param qCategory the category
   * @return True if the category already exists, false otherwise.
   * @throws SQLException if something with the database goes awry
   */
  public boolean doesCategoryExist(String qCategory) throws SQLException {
    return challenges.doesCategoryExist(qCategory);
  }

  /**
   * Gets all the categories that exist.
   * @return all the categories in a List
   * @throws SQLException if something with the database goes awry
   */
  public List<String> getAllCategories() throws SQLException {
    return challenges.getAllCategories();
  }
}
