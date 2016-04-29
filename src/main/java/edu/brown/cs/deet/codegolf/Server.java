package edu.brown.cs.deet.codegolf;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import edu.brown.cs.deet.database.UserDatabase;
import edu.brown.cs.deet.pageHandler.AdminHandler;
import edu.brown.cs.deet.pageHandler.AdminHandler.ExceptionPrinter;
import edu.brown.cs.deet.pageHandler.GamePageHandlers;
import edu.brown.cs.deet.pageHandler.LoginHandlers;
import edu.brown.cs.deet.pageHandler.UserHandler;
import freemarker.template.Configuration;
import spark.Request;
import spark.Spark;
import spark.template.freemarker.FreeMarkerEngine;

final class Server {
  private static final int PORT = 4567;
  private static final String dbPath = "data/codegolf.db";
  private static String appID = "1559408461020162";
  private static String loginRedirectURL = "http://localhost:4567/fblogin";
  private static String appSecret = "9ffcf58f5f448a3e9e723537c476b5eb";

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
    Spark.get("/admin/add", new AdminHandler.AdminAddHandler(), freeMarker);
    Spark.get("/user/:username", new UserHandler.UserPageHandler(), freeMarker);
    /*
     * TODO: get leaderboard associated with a particular challenge
     * Spark.get("/leaderboard/:challengeInfo", new GetLeaderboardHandler(),
     * freeMarker);
     */
    Spark.post("/admin/add/results", new AdminHandler.NewChallengeHandler());
    Spark.post("/admin/delete/:challengeid",
        new AdminHandler.DeleteChallengeHandler());
    Spark.post("/namecheck", new AdminHandler.NameCheckHandler());
    Spark.post("/categorycheck", new AdminHandler.CategoryCheckHandler());
    Spark.post("/getallcategories", new AdminHandler.AllCategoriesHandler());
    Spark.post("/game/usertests", new GamePageHandlers.UserTestsHandler());
    Spark.post("/game/deettests", new GamePageHandlers.DeetTestsHandler());
    Spark.post("/save", new GamePageHandlers.SaveSolutionHandler());
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
    } catch (SQLException e1) {
      // Eddie added this
      System.out.println(e1.getMessage());
      System.exit(1);
    }

    return !(noCookie || badCookie);
  }
}