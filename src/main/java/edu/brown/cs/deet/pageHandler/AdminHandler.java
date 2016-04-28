package edu.brown.cs.deet.pageHandler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
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

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import edu.brown.cs.deet.database.ChallengeDatabase;

/**
 * Handles all of the Admin-related requests, such as adding a new challenge or
 * editing a pre-existing challenge.
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
   * @param cdb
   *          the ChallengeDatabase
   */
  public static void setChallengeDatabase(ChallengeDatabase cdb) {
    challenges = cdb;
  }

  /**
   * Shows the Admin_add page.
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
   * Handler that checks if the entered name in the Admin page is already taken.
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
   * @param category
   *          The new category
   * @param pName
   *          The "path name" of the challenge
   * @param name
   *          The new name
   * @param description
   *          The new description
   * @return True if the information was successfully edited, false otherwise
   * @throws SQLException
   *           if the database is messed up somehow
   * @throws IOException
   *           If an I/O error occurred with creating a file
   */
  public static boolean newBasicInfo(String category, String pName,
      String name, String description) throws SQLException, IOException {
    String path = "challenges/" + pName;

    // check if an insert to the database is successful
    if (challenges.insertNewChallenge(pName, name, path, category)) {
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
   * @param pName
   *          The "path name" of the challenge
   * @param testnames
   *          The names of each of the tests
   * @param input
   *          The input for the test cases
   * @param output
   *          The output for the test cases
   * @param stub
   *          The stub code for the test cases
   * @param language
   *          The language that this information is related to
   * @return True if all the test info was properly entered into the challenges
   *         directory, false otherwise. A False may occur when the challenge
   *         directory with name "name" doesn't exist. Or a file for some txt
   *         file/directory for the Language already exists.
   * @throws IOException
   *           If an I/O error occurred with creating a file
   * @throws SQLException
   *           When the database screws up
   */
  public static boolean newTestInfo(String pName, String testnames,
      String input, String output, String stub, String language)
      throws IOException, SQLException {

    File directory = new File("challenges/" + pName);

    // first checks to see if the directory exists
    if (directory.exists()) {
      // Adding the information to the database
      challenges.insertTestsForChallenge(pName, language);

      // Make the directory for Language-related stuff
      String path = "challenges/" + pName + "/" + language;
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
   * Determines if a challenge by a certain name already exists.
   * @param pName
   *          the "path name" of the challenge
   * @return True if the challenge already exists, false otherwise.
   * @throws SQLException
   *           if something with the database goes awry
   */
  public static boolean doesChallengeExist(String pName) throws SQLException {
    return challenges.doesChallengeExist(pName);
  }

  /**
   * Determines if a category already exists.
   * @param qCategory
   *          the category
   * @return True if the category already exists, false otherwise.
   * @throws SQLException
   *           if something with the database goes awry
   */
  public static boolean doesCategoryExist(String qCategory) throws SQLException {
    return challenges.doesCategoryExist(qCategory);
  }

  /**
   * Gets all the categories that exist.
   * @return all the categories in a List
   * @throws SQLException
   *           if something with the database goes awry
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
   * @param challengeName
   *          the name of the challenge as seen in the challenges directory (NOT
   *          the one seen by a user)
   * @throws SQLException
   *           if something with the database goes awry.
   * @throws IOException
   *           when there is an error with deleting the directory associated
   *           with the challenge
   */
  public static void deleteChallenge(String challengeName) throws SQLException,
      IOException {
    String directory = "challenges/" + challengeName;
    File challengeDirectory = new File(directory);

    // delete the directory that contains all the challenge info
    FileUtils.deleteDirectory(challengeDirectory);

    // delete the challenges from the database
    challenges.deleteChallenge(challengeName);
  }

  public static void main(String[] args) throws SQLException {
    ChallengeDatabase cdb = new ChallengeDatabase(
        "testdata/deleteChallengeTest.sqlite3");
    AdminHandler.setChallengeDatabase(cdb);

    try {
      deleteChallenge("delete-test");
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}