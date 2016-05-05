package edu.brown.cs.deet.pageHandler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import spark.ExceptionHandler;
import spark.ModelAndView;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.TemplateViewRoute;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import edu.brown.cs.deet.database.ChallengeDatabase;

/**
 * Handles all of the Admin-related requests, such as adding a new challenge or
 * editing a pre-existing challenge.
 * 
 * @author el13
 */
public final class AdminHandler {
  private static ChallengeDatabase challenges;
  private static final Gson GSON = new Gson();

  /**
   * Private Constructor for an AdminHandler.
   */
  private AdminHandler() {
  }

  /**
   * Statically changes the ChallengeDatabase of the AdminHandler.
   * 
   * @param cdb the ChallengeDatabase
   */
  public static void setChallengeDatabase(ChallengeDatabase cdb) {
    challenges = cdb;
  }

  /**
   * Shows the Admin_add page.
   * 
   * @author el13
   */
  public static class AdminAddHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      Map<String, Object> variables = ImmutableMap.of("title",
          "Add a Challenge");
      return new ModelAndView(variables, "newChallenge.ftl");
    }
  }

  /**
   * Handles the input of a new challenge.
   * 
   * @author el13
   */
  public static class NewChallengeHandler implements Route {
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Object handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();

      // Basic info
      String category = GSON.fromJson(qm.value("category"), String.class);
      String pName = GSON.fromJson(qm.value("pName"), String.class);
      String name = GSON.fromJson(qm.value("name"), String.class);
      String description = GSON.fromJson(qm.value("description"), String.class);

      Boolean success = false;

      try {
        success = newBasicInfo(category, pName, name, description);
      } catch (SQLException e) {
        new ExceptionPrinter().handle(e, req, res);
      } catch (IOException e) {
        new ExceptionPrinter().handle(e, req, res);
      }

      // Java test cases and stub code
      if (success) {
        String javaTestnames = GSON.fromJson(qm.value("javaTestName"),
            String.class);
        String javaInput = GSON.fromJson(qm.value("javaInput"), String.class);
        String javaOutput = GSON.fromJson(qm.value("javaOutput"), String.class);
        String javaStub = GSON.fromJson(qm.value("javaStub"), String.class);

        // Python test cases and stub code
        String pythonTestnames = GSON.fromJson(qm.value("pythonTestName"),
            String.class);
        String pythonInput = GSON.fromJson(qm.value("pythonInput"),
            String.class);
        String pythonOutput = GSON.fromJson(qm.value("pythonOutput"),
            String.class);
        String pythonStub = GSON.fromJson(qm.value("pythonStub"), String.class);

        // Ruby test cases and stub code
        String rubyTestnames = GSON.fromJson(qm.value("rubyTestName"),
            String.class);
        String rubyInput = GSON.fromJson(qm.value("rubyInput"), String.class);
        String rubyOutput = GSON.fromJson(qm.value("rubyOutput"), String.class);
        String rubyStub = GSON.fromJson(qm.value("rubyStub"), String.class);

        // Javascript test cases and stub code
        String jsTestnames = GSON
            .fromJson(qm.value("jsTestName"), String.class);
        String jsInput = GSON.fromJson(qm.value("jsInput"), String.class);
        String jsOutput = GSON.fromJson(qm.value("jsOutput"), String.class);
        String jsStub = GSON.fromJson(qm.value("jsStub"), String.class);

        try {
          // putting all of the code into the directory, but only if something
          // was entered for those fields on the page
          if (!javaInput.equals("")) {
            newTestInfo(pName, javaTestnames, javaInput, javaOutput, javaStub,
                "java");
          }

          if (!pythonInput.equals("")) {
            newTestInfo(pName, pythonTestnames, pythonInput, pythonOutput,
                pythonStub, "python");
          }

          if (!rubyInput.equals("")) {
            newTestInfo(pName, rubyTestnames, rubyInput, rubyOutput, rubyStub,
                "ruby");
          }

          if (!jsInput.equals("")) {
            newTestInfo(pName, jsTestnames, jsInput, jsOutput, jsStub,
                "javascript");
          }
        } catch (IOException e) {
          new ExceptionPrinter().handle(e, req, res);
        } catch (SQLException e) {
          new ExceptionPrinter().handle(e, req, res);
        }
      }

      Map<String, Object> variables = new ImmutableMap.Builder().put("success",
          success).build();
      return GSON.toJson(variables);
    }
  }

  /**
   * Handler that deletes a challenge.
   * 
   * @author el13
   */
  public static class DeleteChallengeHandler implements Route {
    @SuppressWarnings("rawtypes")
    @Override
    public Object handle(Request req, Response res) {
      String challengeId = req.params(":challengeid");
      try {
        deleteChallenge(challengeId);

        @SuppressWarnings("unchecked")
        Map<String, Object> variables = new ImmutableMap.Builder().put(
            "success", true).build();
        return GSON.toJson(variables);
      } catch (SQLException e) {
        new ExceptionPrinter().handle(e, req, res);
        @SuppressWarnings("unchecked")
        Map<String, Object> variables = new ImmutableMap.Builder().put(
            "success", false).build();
        return GSON.toJson(variables);
      } catch (IOException e) {
        new ExceptionPrinter().handle(e, req, res);
        @SuppressWarnings("unchecked")
        Map<String, Object> variables = new ImmutableMap.Builder().put(
            "success", false).build();
        return GSON.toJson(variables);
      }
    }
  }

  /**
   * Handler that edits a challenge.
   * 
   * @author eddie
   */
  public static class EditChallengeHandler implements Route {
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Object handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();

      // Basic info
      String category = GSON.fromJson(qm.value("category"), String.class);
      String pName = GSON.fromJson(qm.value("pName"), String.class);
      String origPName = GSON.fromJson(qm.value("origPName"), String.class);
      String name = GSON.fromJson(qm.value("name"), String.class);
      String description = GSON.fromJson(qm.value("description"), String.class);

      Boolean success = false;

      try {
        success = editBasicInfo(category, pName, name, description, origPName);
      } catch (SQLException e) {
        new ExceptionPrinter().handle(e, req, res);
      } catch (IOException e) {
        new ExceptionPrinter().handle(e, req, res);
      }

      // Java test cases and stub code
      if (success) {
        String javaTestnames = GSON.fromJson(qm.value("javaTestName"),
            String.class);
        String javaInput = GSON.fromJson(qm.value("javaInput"), String.class);
        String javaOutput = GSON.fromJson(qm.value("javaOutput"), String.class);
        String javaStub = GSON.fromJson(qm.value("javaStub"), String.class);

        // Python test cases and stub code
        String pythonTestnames = GSON.fromJson(qm.value("pythonTestName"),
            String.class);
        String pythonInput = GSON.fromJson(qm.value("pythonInput"),
            String.class);
        String pythonOutput = GSON.fromJson(qm.value("pythonOutput"),
            String.class);
        String pythonStub = GSON.fromJson(qm.value("pythonStub"), String.class);

        // Ruby test cases and stub code
        String rubyTestnames = GSON.fromJson(qm.value("rubyTestName"),
            String.class);
        String rubyInput = GSON.fromJson(qm.value("rubyInput"), String.class);
        String rubyOutput = GSON.fromJson(qm.value("rubyOutput"), String.class);
        String rubyStub = GSON.fromJson(qm.value("rubyStub"), String.class);

        // Javascript test cases and stub code
        String jsTestnames = GSON
            .fromJson(qm.value("jsTestName"), String.class);
        String jsInput = GSON.fromJson(qm.value("jsInput"), String.class);
        String jsOutput = GSON.fromJson(qm.value("jsOutput"), String.class);
        String jsStub = GSON.fromJson(qm.value("jsStub"), String.class);

        try {
          // putting all of the code into the directory, but only if something
          // was entered for those fields on the page
          if (!javaInput.equals("")) {
            editTestInfo(pName, javaTestnames, javaInput, javaOutput, javaStub,
                "java");
          }

          if (!pythonInput.equals("")) {
            editTestInfo(pName, pythonTestnames, pythonInput, pythonOutput,
                pythonStub, "python");
          }

          if (!rubyInput.equals("")) {
            editTestInfo(pName, rubyTestnames, rubyInput, rubyOutput, rubyStub,
                "ruby");
          }

          if (!jsInput.equals("")) {
            editTestInfo(pName, jsTestnames, jsInput, jsOutput, jsStub,
                "javascript");
          }
        } catch (IOException e) {
          new ExceptionPrinter().handle(e, req, res);
        } catch (SQLException e) {
          new ExceptionPrinter().handle(e, req, res);
        }
      }

      Map<String, Object> variables = new ImmutableMap.Builder().put("success",
          success).build();
      return GSON.toJson(variables);
    }
  }

  /**
   * Handles showing all challenge information when editing one.
   * 
   * @author eddie
   */
  public static class ShowChallengeHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      String challengeId = req.params(":challengeid");
      Map<String, Object> variables = null;
      try {
        Map<String, Object> orig = new HashMap<>();
        orig.put("title", "Edit a Challenge");
        orig.put("info", getChallengeInfo(challengeId));
        orig.put("categories", getAllCategories());
        variables = Collections.unmodifiableMap(orig);
      } catch (IOException e) {
        // shouldn't get here?
        new ExceptionPrinter().handle(e, req, res);
      } catch (SQLException e) {
        // shouldn't get here?
        new ExceptionPrinter().handle(e, req, res);
      }
      return new ModelAndView(variables, "editChallenge.ftl");
    }
  }

  /**
   * Handler that checks if the entered name in the Admin page is already taken.
   * 
   * @author el13
   */
  public static class NameCheckHandler implements Route {
    @Override
    public Object handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      String name = GSON.fromJson(qm.value("textValue"), String.class);
      boolean exists = false;

      try {
        exists = doesChallengeExist(name);
      } catch (SQLException e) {
        new ExceptionPrinter().handle(e, req, res);
      }

      @SuppressWarnings({ "rawtypes", "unchecked" })
      Map<String, Object> variables = new ImmutableMap.Builder().put("exists",
          exists).build();
      return GSON.toJson(variables);
    }
  }

  /**
   * Handler that gets all of the Categories.
   * 
   * @author el13
   */
  public static class AllCategoriesHandler implements Route {
    @Override
    public Object handle(Request req, Response res) {
      List<String> categories = null;
      try {
        categories = getAllCategories();
      } catch (SQLException e) {
        new ExceptionPrinter().handle(e, req, res);
      }

      @SuppressWarnings({ "rawtypes", "unchecked" })
      Map<String, Object> variables = new ImmutableMap.Builder().put(
          "categories", categories).build();
      return GSON.toJson(variables);
    }

  }

  /**
   * Handler that checks if the entered new category already exists.
   * 
   * @author el13
   */
  public static class CategoryCheckHandler implements Route {
    @Override
    public Object handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      String name = GSON.fromJson(qm.value("textValue"), String.class);
      boolean exists = false;

      try {
        exists = doesCategoryExist(name);
      } catch (SQLException e) {
        new ExceptionPrinter().handle(e, req, res);
      }

      @SuppressWarnings({ "rawtypes", "unchecked" })
      Map<String, Object> variables = new ImmutableMap.Builder().put("exists",
          exists).build();
      return GSON.toJson(variables);
    }
  }

  /**
   * Handles Exceptions.
   * 
   * @author el13
   */
  public static class ExceptionPrinter implements ExceptionHandler {
    @Override
    public void handle(Exception e, Request req, Response res) {
      res.status(500);
      StringWriter stacktrace = new StringWriter();
      try (PrintWriter pw = new PrintWriter(stacktrace)) {
        pw.println("<pre>");
        e.printStackTrace(pw);
        pw.println("</pre>");
      }
      res.body(stacktrace.toString());
    }
  }

  /**
   * Processes the new "basic" information for a new challenge.
   * 
   * @param category The new category
   * @param pName The "path name" of the challenge
   * @param name The new name
   * @param description The new description
   * @return True if the information was successfully edited, false otherwise
   * @throws SQLException if the database is messed up somehow
   * @throws IOException If an I/O error occurred with creating a file
   */
  public static boolean newBasicInfo(String category, String challengeId,
      String name, String description) throws SQLException, IOException {
    String path = "challenges/" + challengeId;

    // check if an insert to the database is successful
    if (challenges.insertNewChallenge(challengeId, name, path, category)) {
      // make the directory for the challenge
      File challengeDir = new File(path);
      challengeDir.mkdir();

      // Create the file for the description
      File challengeDesc = new File(path + "/description.txt");
      challengeDesc.createNewFile();

      try (BufferedWriter bw = new BufferedWriter(new FileWriter(challengeDesc))) {
        bw.write(description);
      }

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
   * 
   * @param challengeId The "path name" of the challenge
   * @param testnames The names of each of the tests
   * @param input The input for the test cases
   * @param output The output for the test cases
   * @param stub The stub code for the test cases
   * @param language The language that this information is related to
   * @return True if all the test info was properly entered into the challenges
   *         directory, false otherwise. A False may occur when the challenge
   *         directory with name "name" doesn't exist. Or a file for some txt
   *         file/directory for the Language already exists.
   * @throws IOException If an I/O error occurred with creating a file
   * @throws SQLException When the database screws up
   */
  public static boolean newTestInfo(String challengeId, String testnames,
      String input, String output, String stub, String language)
      throws IOException, SQLException {

    File directory = new File("challenges/" + challengeId);

    // first checks to see if the directory exists
    if (directory.exists()) {
      // Adding the information to the database
      challenges.insertTestsForChallenge(challengeId, language);

      // Make the directory for Language-related stuff
      String path = "challenges/" + challengeId + "/" + language;
      File languagePath = new File(path);

      if (!languagePath.mkdir()) {
        return false;
      }

      // Create the solutions folder
      File solutionsDir = new File(path + "/solutions");
      solutionsDir.mkdir();

      // Create the testnames file and write to it
      File testnamesFile = new File(path + "/testnames.txt");

      if (!testnamesFile.createNewFile()) {
        return false;
      }

      try (BufferedWriter bw = new BufferedWriter(new FileWriter(testnamesFile))) {
        bw.write(testnames);
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
   * Edits the basic information for a challenge in the directories and in the
   * database.
   * 
   * @param category The (new) category
   * @param challengeId The (new) challengeId
   * @param name The (new) name
   * @param description The (new) description
   * @param originalChallengeId The old challengeId
   * @return True if successfully edited, false otherwise.
   * @throws IOException when there is an issue writing to the description.txt
   *           file
   * @throws SQLException when something goes awry with the database in editing
   *           the challenge
   */
  public static boolean editBasicInfo(String category, String challengeId,
      String name, String description, String originalChallengeId)
      throws IOException, SQLException {
    String newChallengeDir = "challenges/" + challengeId;

    if (challenges.editChallenge(originalChallengeId, challengeId, name,
        newChallengeDir, category)) {
      // rename to new name (does this even if the id name is the same as
      // before)
      String origChallengeDir = "challenges/" + originalChallengeId;

      File origDir = new File(origChallengeDir);
      File newDir = new File(newChallengeDir);

      origDir.renameTo(newDir);

      File challengeDesc = new File(newChallengeDir + "/description.txt");

      try (BufferedWriter bw = new BufferedWriter(new FileWriter(challengeDesc))) {
        bw.write(description);
      }
      return true;
    }

    return false;
  }

  /**
   * Edits Test Info for a challenge. If a directory for a specifically language
   * for that test already exists, then the test info is updated per the user's
   * entry.
   * 
   * @param challengeId The new ID of the challenge
   * @param testnames All the test names
   * @param input All the inputs
   * @param output All the outputs
   * @param stub The stub code
   * @param language The language of the test
   * @return True if successfully changed or added, false otherwise.
   * @throws IOException If there is an issue writing to some file.
   * @throws SQLException If the database goes awry with adding to the Test
   *           table, if an add was necessary.
   */
  public static boolean editTestInfo(String challengeId, String testnames,
      String input, String output, String stub, String language)
      throws IOException, SQLException {
    File directory = new File("challenges/" + challengeId);

    if (directory.exists()) {
      String path = "challenges/" + challengeId + "/" + language;
      File languagePath = new File(path);

      if (languagePath.exists()) { // this challenge already supported this new
                                   // language
        // Overwrite the testnames file
        File testnamesFile = new File(path + "/testnames.txt");

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(
            testnamesFile))) {
          bw.write(testnames);
        }

        // Overwrite the input file
        File inputFile = new File(path + "/input.txt");

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(inputFile))) {
          bw.write(input);
        }

        // Overwrite the output file
        File outputFile = new File(path + "/output.txt");

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {
          bw.write(output);
        }

        // Overwrite the stub file
        File stubFile = new File(path + "/stub.txt");

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(stubFile))) {
          bw.write(stub);
        }
      } else { // this challenge now supports this new language
        newTestInfo(challengeId, testnames, input, output, stub, language);
      }

      return true;
    }
    return false;
  }

  /**
   * Determines if a challenge by a certain name already exists.
   * 
   * @param challengeId the "path name" of the challenge
   * @return True if the challenge already exists, false otherwise.
   * @throws SQLException if something with the database goes awry
   */
  public static boolean doesChallengeExist(String challengeId)
      throws SQLException {
    return challenges.doesChallengeExist(challengeId);
  }

  /**
   * Determines if a category already exists.
   * 
   * @param qCategory the category
   * @return True if the category already exists, false otherwise.
   * @throws SQLException if something with the database goes awry
   */
  public static boolean doesCategoryExist(String qCategory) throws SQLException {
    return challenges.doesCategoryExist(qCategory);
  }

  /**
   * Gets all the categories that exist.
   * 
   * @return all the categories in a List
   * @throws SQLException if something with the database goes awry
   */
  public static List<String> getAllCategories() throws SQLException {
    return challenges.getAllCategories();
  }

  /**
   * Deletes a challenge from the Database, including all entries in the test
   * and solutions tables. This method rests on the assumption that the test and
   * solution tables have the "ON CASCADE DELETE" options on for its
   * "challenge_id" foreign keys. Otherwise, there will be zombie entries
   * leftover in the test and solution tables.
   * 
   * @param challengeId the name of the challenge as seen in the challenges
   *          directory (NOT the one seen by a user)
   * @throws SQLException if something with the database goes awry.
   * @throws IOException when there is an error with deleting the directory
   *           associated with the challenge
   */
  public static void deleteChallenge(String challengeId) throws SQLException,
      IOException {
    String directory = "challenges/" + challengeId;
    File challengeDirectory = new File(directory);

    // delete the directory that contains all the challenge info
    FileUtils.deleteDirectory(challengeDirectory);

    // delete the challenges from the database
    challenges.deleteChallenge(challengeId);
  }

  /**
   * Returns all of the information related to a challenge for the edit
   * challenge handler. This method assumes that the description.txt file
   * exists. It also assumes that if a directory for the challenge exists then
   * there is an entry in the challenge table in the database for this
   * challenge. For other cases, the results of this method are undefined.
   * 
   * @param challengeId the name of the challenge as seen in the challenges
   *          directory (NOT the one seen by a user)
   * @return A List of a List of Objects where: the first list consists of the
   *         "basic information" like category, challengeId, actual challenge
   *         name, and description, in that order, the second list consists of
   *         all the Java information with test name, input, output, and stub in
   *         that order, and the third, fourth, and fifth lists contain Python,
   *         Ruby, and Javascript information, in that order. Returns null if
   *         the requested challengeId does not exist.
   * @throws IOException when there is an error with deleting the directory
   *           associated with the challenge
   * @throws SQLException if something with the database goes awry
   */
  public static List<List<String>> getChallengeInfo(String challengeId)
      throws IOException, SQLException {
    List<List<String>> ret = new ArrayList<>();
    // GET BASIC INFORMATION
    String directory = "challenges/" + challengeId;
    File directoryFile = new File(directory);

    if (directoryFile.exists()) { // if the directory for the challenge exists
      List<String> basic = new ArrayList<>();

      // Category, challengeId and actual challenge name are assumed to exist
      List<String> challengeInfo = challenges.getChallenge(challengeId);

      basic.add(challengeInfo.get(3)); // category
      basic.add(challengeId);
      basic.add(challengeInfo.get(1)); // real name
      System.out.println(basic);

      // Description
      String descriptionFile = directory + "/description.txt";
      byte[] encoded = Files.readAllBytes(Paths.get(descriptionFile));
      String encodedString = new String(encoded, Charsets.UTF_8);

      basic.add(encodedString); // description

      ret.add(basic);

      // GET JAVA, PYTHON, RUBY, JAVASCRIPT INFORMATION
      ret.add(getTestInfo(challengeId, "java"));
      ret.add(getTestInfo(challengeId, "python"));
      ret.add(getTestInfo(challengeId, "ruby"));
      ret.add(getTestInfo(challengeId, "javascript"));

      System.out.println("edit");
      System.out.println(ret);

      return ret;
    } else {
      return null;
    }
  }

  /**
   * Returns the Test Info for some challenge and language
   * 
   * @param challengeId The Id of the challenge
   * @param language The language of the challenge
   * @return A List of test information information containing test name, input,
   *         output, and stub in that order, as strings. If the test directory
   *         does not exist, each entry is an empty String.
   * @throws IOException when there is an error with deleting the directory
   *           associated with the challenge
   */
  private static List<String> getTestInfo(String challengeId, String language)
      throws IOException {
    List<String> ret = new ArrayList<>();

    String testPath = "challenges/" + challengeId + "/" + language;
    File testDirectory = new File(testPath);

    if (testDirectory.exists()) { // get all info if it exists
      // Test names
      String testNamePath = testPath + "/testnames.txt";
      byte[] encoded = Files.readAllBytes(Paths.get(testNamePath));
      String encodedString = new String(encoded, Charsets.UTF_8);

      ret.add(encodedString);

      // Inputs
      String inputsPath = testPath + "/input.txt";
      encoded = Files.readAllBytes(Paths.get(inputsPath));
      encodedString = new String(encoded, Charsets.UTF_8);

      ret.add(encodedString);

      // Outputs
      String outputsPath = testPath + "/output.txt";
      encoded = Files.readAllBytes(Paths.get(outputsPath));
      encodedString = new String(encoded, Charsets.UTF_8);

      ret.add(encodedString);

      // Stub
      String stubPath = testPath + "/stub.txt";
      encoded = Files.readAllBytes(Paths.get(stubPath));
      encodedString = new String(encoded, Charsets.UTF_8);

      ret.add(encodedString);
    } else { // otherwise just return empty lists
      ret.add("");
      ret.add("");
      ret.add("");
      ret.add("");
    }

    return ret;
  }

  public static void main(String[] args) throws SQLException {
    ChallengeDatabase cdb = new ChallengeDatabase(
        "testdata/challengeDatabaseTester.sqlite3");
    AdminHandler.setChallengeDatabase(cdb);

    try {
      List<List<String>> res = getChallengeInfo("fib-fast");
      System.out.println(res.get(0));
      System.out.println(res.get(1));
      System.out.println(res.get(2));
      System.out.println(res.get(3));
      System.out.println(res.get(4));
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
