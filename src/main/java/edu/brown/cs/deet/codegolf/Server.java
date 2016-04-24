package edu.brown.cs.deet.codegolf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import edu.brown.cs.deet.database.ChallengeDatabase;
import edu.brown.cs.deet.database.UserDatabase;
import edu.brown.cs.deet.execution.MyCompiler;
import edu.brown.cs.deet.execution.Runner;
import edu.brown.cs.deet.execution.Tester;
import edu.brown.cs.deet.execution.python.PyCompiler;
import edu.brown.cs.deet.execution.python.PyRunner;
import edu.brown.cs.deet.execution.python.PyTester;
import edu.brown.cs.deet.pageHandler.AdminHandler;
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
    Spark.get("/game", new GamePageHandler(), freeMarker);
    Spark.get("/admin_add", new AdminAddHandler(), freeMarker);
    Spark.get("/user/:username", new UserPageHandler(), freeMarker);
    Spark.post("/admin_add/results", new NewChallengeHandler());
    Spark.post("/namecheck", new NameCheckHandler());
    Spark.post("/categorycheck", new CategoryCheckHandler());
    Spark.post("/getallcategories", new AllCategoriesHandler());
    Spark.post("/game/usertests", new GamePageHandlers.UserTestsHandler());
    Spark.post("/game/deettests", new GamePageHandlers.DeetTestsHandler());
    Spark.get(
        "/categories",
        (request, response) -> {
          Map<String, Object> variables = ImmutableMap.of("title",
              "Categories", "name", request.cookie("name"));
          return new ModelAndView(variables, "categories.ftl");
        }, freeMarker);

    // home page
    Spark.get(
        "/",
        (request, response) -> {
          // if they're already logged in, send them to the categories page
          if (validCookie(request)) {
            response.redirect("/categories");
          }

          Map<String, Object> variables = ImmutableMap.of("title", "Home",
              "loginURL", getFBURL());
          return new ModelAndView(variables, "landing.ftl");
        }, freeMarker);

    // Facebook authentication redirect
    Spark.get("/fblogin", (request, response) -> {
      String fbcode = request.queryParams("code");
      return handleFB(fbcode, request, response);
    });

    // logout request
    Spark.get("/logout", (request, response) -> {
      response.removeCookie("name");
      response.removeCookie("user");
      response.redirect("/");
      return "Should never get here";
    });

    // adding a user AJAX call
    Spark
    .post(
        "/add-user",
        (request, response) -> {
          String username = request.queryMap().value("username");
          try (UserDatabase ud = new UserDatabase(dbPath)) {
            try {
              Boolean usernameAlreadyExists = ud
                  .doesUserExistWithUsername(username);
              Boolean idAlreadyExists = ud.doesUserExistWithID(request
                  .cookie("user"));

              if (usernameAlreadyExists) {
                response.status(400);
                return GSON.toJson(ImmutableMap.of("error",
                    "That username is already taken."));
              } else if (idAlreadyExists) {
                response.status(400);
                return GSON.toJson(ImmutableMap
                    .of("error",
                        "There is already a user associated with this fb account."));
              } else {
                ud.addNewUser(username, request.cookie("user"), false,
                    request.cookie("name"));
                return GSON.toJson("Success!");
              }

            } catch (SQLException e) {
              System.out.println(e.getMessage());
              System.exit(1);
            }
            return "Should never get here";
          }
        });

    // check authentication before every request
    // Spark.before((request, response) -> {
    // String url = request.url();
    //
    // Boolean validUser = validCookie(request);
    //
    // Boolean staticRequest = url.contains("css") || url.contains("js");
    //
    // Boolean doesntNeedLogIn = url.equals("http://localhost:4567/")
    // || url.equals("http://localhost:4567/fblogin");
    //
    // System.out.println(url);
    // });
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
   * Handles FB requests for logins and registrations.
   * @param code
   *          the code returned from the initial authentication step
   * @param req
   *          the request object
   * @param res
   *          the response object
   * @return the string representing the desired response
   */
  private static String handleFB(String code, Request req, Response res) {
    String accessTokenURL = "https://graph.facebook.com/v2.3/oauth/access_token?client_id="
        + appID
        + "&redirect_uri="
        + loginRedirectURL
        + "&client_secret="
        + appSecret + "&code=" + code;

    Map<String, String> tokenJSON = getJSONFromURL(accessTokenURL);

    if (!tokenJSON.containsKey("access_token")) {
      res.status(500);
      return "Unable to get access token for the given FB account.";
    }

    String graphDataURL = "https://graph.facebook.com/v2.3/me?access_token="
        + tokenJSON.get("access_token") + "&fields=id,name";

    Map<String, String> dataJSON = getJSONFromURL(graphDataURL);

    if (!dataJSON.containsKey("id")) {
      res.status(500);
      return "Unable to get data for given FB account.";
    }

    String name = dataJSON.get("name");
    String fbID = dataJSON.get("id");

    try (UserDatabase ud = new UserDatabase(dbPath)) {
      try {
        Boolean alreadyExists = ud.doesUserExistWithID(fbID);

        res.cookie("name", name);
        res.cookie("user", fbID);

        if (alreadyExists) {
          res.redirect("/categories");
        } else {
          res.redirect("/categories#signup");
        }

      } catch (SQLException e) {
        System.out.println(e.getMessage());
        System.exit(1);
      }
    }
    ;

    // should never get here
    String toReturn = String.format("Name: %s, ID: %s", dataJSON.get("name"),
        dataJSON.get("id"));

    return toReturn;
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
            admin.newTestInfo(name, javaInput, javaOutput, javaStub, "java");
          }

          if (!pythonInput.equals("")) {
            admin.newTestInfo(name, pythonInput, pythonOutput, pythonStub,
                "python");
          }

          if (!rubyInput.equals("")) {
            admin.newTestInfo(name, rubyInput, rubyOutput, rubyStub, "ruby");
          }

          if (!jsInput.equals("")) {
            admin.newTestInfo(name, jsInput, jsOutput, jsStub, "javascript");
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

  // TODO: deal with login and registration differently
  private static String getFBURL() {
    return "https://www.facebook.com/dialog/oauth?client_id=" + appID
        + "&redirect_uri=" + loginRedirectURL;
  }

  /**
   * Takes a url, makes a get request, and then returns the JSON response in the
   * form of a Map
   * @param urlString
   *          the string of the url you want to hit
   * @return the JSON response as a Map<String, String>
   */
  private static Map<String, String> getJSONFromURL(String urlString) {
    try {
      // open a URL connection
      URL url = new URL(urlString);
      URLConnection connection = url.openConnection();
      connection.connect();

      // read the URL connection into a json object
      Reader reader = new InputStreamReader(connection.getInputStream());
      BufferedReader buff = new BufferedReader(reader);
      String line = buff.readLine();
      Type stringStringMap = new TypeToken<Map<String, String>>() {
      }.getType();
      Map<String, String> json = new Gson().fromJson(line, stringStringMap);
      return json;
    } catch (IOException e) {
      System.out.println(e.getMessage());
      return ImmutableMap.of("error", e.getMessage());
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
