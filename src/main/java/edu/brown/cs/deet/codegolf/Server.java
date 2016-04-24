package edu.brown.cs.deet.codegolf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import spark.ExceptionHandler;
import spark.ModelAndView;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;
import spark.TemplateViewRoute;
import spark.template.freemarker.FreeMarkerEngine;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.Gson;

import edu.brown.cs.deet.database.ChallengeDatabase;
import edu.brown.cs.deet.execution.MyCompiler;
import edu.brown.cs.deet.execution.Runner;
import edu.brown.cs.deet.execution.python.PyCompiler;
import edu.brown.cs.deet.execution.python.PyRunner;
import edu.brown.cs.deet.pageHandler.AdminHandler;
import freemarker.template.Configuration;

final class Server {

  private static final Gson GSON = new Gson();
  private static AdminHandler admin;
  private static final int PORT = 4567;

  /**
   * Sets the AdminHandler for the Server.
   * @param a
   *          the AdminHandler
   */
  public static void setAdminHandler(AdminHandler a) {
    admin = a;
  }

  /**
   * Class to set up FreeMarker.
   * @return a FreeMarkerEngine
   */
  private static FreeMarkerEngine createEngine() {
    Configuration config = new Configuration();
    File templates = new File("src/main/resources/spark/template/freemarker");
    try {
      config.setDirectoryForTemplateLoading(templates);
    } catch (IOException ioe) {
      System.out.printf("ERROR: Unable use %s for template loading.\n",
          templates);
      System.exit(1);
    }
    return new FreeMarkerEngine(config);
  }

  /**
   * Runs the Spark Server.
   */
  public static void runSparkServer() {
    Spark.setPort(PORT);

    Spark.externalStaticFileLocation("src/main/resources/static");
    Spark.exception(Exception.class, new ExceptionPrinter());

    FreeMarkerEngine freeMarker = createEngine();

    // Setup Spark Routes
    Spark.get("/game", new GamePageHandler(), freeMarker);
    Spark.get("/admin_add", new AdminAddHandler(), freeMarker);
    Spark.post("/admin_add/results", new NewChallengeHandler());
    Spark.post("/namecheck", new NameCheckHandler());
    Spark.post("/categorycheck", new CategoryCheckHandler());
    Spark.post("/getallcategories", new AllCategoriesHandler());
    Spark.post("/game/run", new RunHandler());
  }

  /**
   * Runs a user's code on user-provided input and posts the corresponding
   * output.
   * @author dglauber
   */
  private static class RunHandler implements Route {
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Object handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      String language = qm.value("language");

      String fileType;
      Runner myRunner;
      MyCompiler myCompiler;
      switch (language) {
        case "python":
          fileType = ".py";
          myRunner = new PyRunner();
          myCompiler = new PyCompiler();
          break;
        default:
          System.out
          .println("Error in RunHandler: language must be either python, ruby, or javascript");
          Map<String, Object> variables = new ImmutableMap.Builder().put(
              "error", true).build();
          return GSON.toJson(variables);
      }

      Integer random = (int) (Math.random() * 1000000);
      String randomFileName = random.toString() + fileType;

      File file = new File("temporary/" + randomFileName);

      try (PrintWriter printWriter = new PrintWriter(file)) {
        String code = qm.value("input");
        printWriter.print(code);

        String errorMessage = myCompiler.compile(file.getPath());

        if (errorMessage != null) {
          Map<String, Object> variables = new ImmutableMap.Builder()
              .put("error", false).put("compiled", errorMessage).build();
          return GSON.toJson(variables);
        }

        String testInputs = qm.value("userTest");
        List<String> testInputList = Lists.newArrayList(Splitter
            .on(System.getProperty("line.separator")).trimResults()
            .omitEmptyStrings().split(testInputs));
        Map<String, String> runResults = myRunner.run(file.getPath(),
            testInputList);

        Map<String, Object> variables = new ImmutableMap.Builder()
            .put("error", false).put("compiled", "success")
            .put("runResults", runResults).build();
        Files.delete(file.toPath());
        return GSON.toJson(variables);

      } catch (IOException e) {
        System.out.println("IOException in RunHandler");
        Map<String, Object> variables = new ImmutableMap.Builder().put("error",
            true).build();
        return GSON.toJson(variables);
      }
    }
  }

  /**
   * Handles loading the game page.
   * @author el51
   */
  private static class GamePageHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      // TODO Currently set to the test database.
      String dbPath = "data/test.db";
      try (ChallengeDatabase challenges = new ChallengeDatabase(dbPath)) {
        /*
         * TODO: This is currently hard-coded in because Tyler and I haven't yet
         * // set up a system to pass question names/ids from the categories
         * page // to the game page.
         */
        String challengeName = "test";
        String promptPath = null;
        try {
          if (challenges.doesChallengeExist(challengeName)) {
            List<String> challengeData = challenges.getChallenge(challengeName);
            promptPath = challengeData.get(1).concat("description.txt");
          }
        } catch (SQLException e) {
          System.out.println(e.getMessage());
          System.exit(1);
        }

        StringBuilder promptBuilder = new StringBuilder();
        try (BufferedReader r = new BufferedReader(new FileReader(promptPath))) {
          String line = r.readLine();
          while (line != null) {
            promptBuilder.append(line).append("\n");
            line = r.readLine();
          }
        } catch (FileNotFoundException e) {
          System.out.println("File not found: " + promptPath);
          System.exit(1);
        } catch (IOException e) {
          System.out.println("I/O Exception at: " + promptPath);
          System.exit(1);
        }

        Map<String, Object> variables = ImmutableMap.of("title", "Game",
            "prompt", promptBuilder.toString());
        return new ModelAndView(variables, "game.ftl");
      }
    }
  }

  /**
   * Shows the Admin_add page.
   * @author el13
   */
  private static class AdminAddHandler implements TemplateViewRoute {
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
  private static class NewChallengeHandler implements Route {
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Object handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();

      // Basic info
      String category = GSON.fromJson(qm.value("category"), String.class);
      String name = GSON.fromJson(qm.value("name"), String.class);
      String description = GSON.fromJson(qm.value("description"), String.class);

      System.out.println(name);
      System.out.println(description);
      System.out.println(category);

      Boolean success = false;

      try {
        success = admin.newBasicInfo(category, name, description);
      } catch (SQLException e) {
        new ExceptionPrinter().handle(e, req, res);
      } catch (IOException e) {
        new ExceptionPrinter().handle(e, req, res);
      }

      // Java test cases and stub code
      if (success) {
        String javaInput = GSON.fromJson(qm.value("javaInput"), String.class);
        String javaOutput = GSON.fromJson(qm.value("javaOutput"), String.class);
        String javaStub = GSON.fromJson(qm.value("javaStub"), String.class);

        // Python test cases and stub code
        String pythonInput = GSON.fromJson(qm.value("pythonInput"),
            String.class);
        String pythonOutput = GSON.fromJson(qm.value("pythonOutput"),
            String.class);
        String pythonStub = GSON.fromJson(qm.value("pythonStub"), String.class);

        // Ruby test cases and stub code
        String rubyInput = GSON.fromJson(qm.value("rubyInput"), String.class);
        String rubyOutput = GSON.fromJson(qm.value("rubyOutput"), String.class);
        String rubyStub = GSON.fromJson(qm.value("rubyStub"), String.class);

        // Javascript test cases and stub code
        String jsInput = GSON.fromJson(qm.value("jsInput"), String.class);
        String jsOutput = GSON.fromJson(qm.value("jsOutput"), String.class);
        String jsStub = GSON.fromJson(qm.value("jsStub"), String.class);

        try {
          // putting all of the code into the directory, but only if something
          // was entered for those fields on the page
          if (!javaInput.equals("")) {
            admin.newTestInfo(name, javaInput, javaOutput, javaStub, "Java");
          }

          if (!pythonInput.equals("")) {
            admin.newTestInfo(name, pythonInput, pythonOutput, pythonStub,
                "Python");
          }

          if (!rubyInput.equals("")) {
            admin.newTestInfo(name, rubyInput, rubyOutput, rubyStub, "Ruby");
          }

          if (!jsInput.equals("")) {
            admin.newTestInfo(name, jsInput, jsOutput, jsStub, "Javascript");
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
   * Handler that checks if the entered name in the Admin page is already taken.
   * @author el13
   */
  private static class NameCheckHandler implements Route {
    @Override
    public Object handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      String name = GSON.fromJson(qm.value("textValue"), String.class);
      boolean exists = false;

      try {
        exists = admin.doesChallengeExist(name);
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
  private static class AllCategoriesHandler implements Route {
    @Override
    public Object handle(Request req, Response res) {
      List<String> categories = null;
      try {
        categories = admin.getAllCategories();
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
  private static class CategoryCheckHandler implements Route {
    @Override
    public Object handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      String name = GSON.fromJson(qm.value("textValue"), String.class);
      boolean exists = false;

      try {
        exists = admin.doesCategoryExist(name);
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
  private static class ExceptionPrinter implements ExceptionHandler {
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

}
