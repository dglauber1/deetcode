package edu.brown.cs.deet.codegolf;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import edu.brown.cs.deet.database.UserDatabase;
import edu.brown.cs.deet.pageHandler.AdminHandler;
import edu.brown.cs.deet.pageHandler.GamePageHandlers;
import edu.brown.cs.deet.pageHandler.LoginHandlers;
import edu.brown.cs.deet.pageHandler.UserHandler;
import freemarker.template.Configuration;
import spark.ExceptionHandler;
import spark.ModelAndView;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;
import spark.TemplateViewRoute;
import spark.template.freemarker.FreeMarkerEngine;

final class Server {

  private static final Gson GSON = new Gson();
  private static AdminHandler admin;
  private static UserHandler user;
  private static final int PORT = 4567;
  private static final String dbPath = "data/codegolf.db";
  private static String appID = "1559408461020162";
  private static String loginRedirectURL = "http://localhost:4567/fblogin";
  private static String appSecret = "9ffcf58f5f448a3e9e723537c476b5eb";

  /**
   * Sets the AdminHandler for the Server.
   * @param a
   *          the AdminHandler
   */
  public static void setAdminHandler(AdminHandler a) {
    admin = a;
  }

  /**
   * Sets the UserHandler for the Server.
   * @param a
   *          the UserHandler
   */
  public static void setUserHandler(UserHandler u) {
    user = u;
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
    Spark.get("/game", new GamePageHandlers.GamePageHandler(), freeMarker);
    Spark.get("/admin_add", new AdminAddHandler(), freeMarker);
    Spark.get("/user/:username", new UserPageHandler(), freeMarker);
    /*
     * TODO: get leaderboard associated with a particular challenge
     * Spark.get("/leaderboard/:challengeInfo", new GetLeaderboardHandler(),
     * freeMarker);
     */
    Spark.post("/admin_add/results", new NewChallengeHandler());
    Spark.post("/namecheck", new NameCheckHandler());
    Spark.post("/categorycheck", new CategoryCheckHandler());
    Spark.post("/getallcategories", new AllCategoriesHandler());
    Spark.post("/game/usertests", new GamePageHandlers.UserTestsHandler());
    Spark.post("/game/deettests", new GamePageHandlers.DeetTestsHandler());
    Spark.get("/categories", new LoginHandlers.CategoriesHandler(), freeMarker);
    Spark.get("/", new LoginHandlers.HomePageHandler(), freeMarker);
    Spark.get("/fblogin", new LoginHandlers.FBHandler());
    Spark.get("/logout", new LoginHandlers.LogoutHandler());
    Spark.post("/add-user", new LoginHandlers.AddUserHandler());
    
    // check authentication before every request
    Spark.before((request, response) -> {
      String url = request.url();

      Boolean validUser = validCookie(request);

      Boolean staticRequest = url.contains("css") || url.contains("js")
          || url.contains("favico");

      Boolean doesntNeedLogin = url.equals("http://localhost:4567/")
          || url.equals("http://localhost:4567/fblogin")
          || url.equals("http://localhost:4567/add-user");

      Boolean creatingAccount = url.equals("http://localhost:4567/categories")
          && (request.session().attribute("adding") != null);

      Boolean badRequest = !validUser && !(staticRequest || doesntNeedLogin);

      if (badRequest && !creatingAccount) {
        response.redirect("/");
      }
    });
  }

  private static Boolean validCookie(Request request) {
    String userID = request.cookie("user");
    Boolean noCookie = userID == null;
    Boolean badCookie = true;

    try (UserDatabase ud = new UserDatabase(dbPath)) {
      try {
        badCookie = !ud.doesUserExistWithID(userID);
      } catch (SQLException e) {
        System.out.println(e.getMessage());
        System.exit(1);
      }
    }

    return !(noCookie || badCookie);
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
   * Handles user pages.
   * @author el13
   */
  private static class UserPageHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      String username = req.params(":username");
      try {
        List<List<String>> results = user.getChallengeInfoForUser(username);

        Map<String, Object> variables = ImmutableMap.of("title", username
            + "'s Profile", "results", results);

        return new ModelAndView(variables, "user.ftl");
      } catch (SQLException e) {
        new ExceptionPrinter().handle(e, req, res);
      } catch (IOException e) {
        new ExceptionPrinter().handle(e, req, res);
      }
      return null; // shouldn't ever get here?
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
      String pName = GSON.fromJson(qm.value("pName"), String.class);
      String name = GSON.fromJson(qm.value("name"), String.class);
      String description = GSON.fromJson(qm.value("description"), String.class);

      Boolean success = false;

      try {
        success = admin.newBasicInfo(category, pName, name, description);
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
            admin.newTestInfo(pName, javaTestnames, javaInput, javaOutput,
                javaStub, "java");
          }

          if (!pythonInput.equals("")) {
            admin.newTestInfo(pName, pythonTestnames, pythonInput,
                pythonOutput, pythonStub, "python");
          }

          if (!rubyInput.equals("")) {
            admin.newTestInfo(pName, rubyTestnames, rubyInput, rubyOutput,
                rubyStub, "ruby");
          }

          if (!jsInput.equals("")) {
            admin.newTestInfo(pName, jsTestnames, jsInput, jsOutput, jsStub,
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