package edu.brown.cs.deet.deetcode;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

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
  private static final String dbPath = Main.dbLoc;

  /**
   * Class to set up FreeMarker.
   * 
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
    Spark.get("/game/:challenge-id", new GamePageHandlers.GamePageHandler(),
        freeMarker);
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
    Spark.get("/admin/edit/:challengeid",
        new AdminHandler.ShowChallengeHandler(), freeMarker);
    Spark.post("/admin/edit/results", new AdminHandler.EditChallengeHandler());
    Spark.post("/namecheck", new AdminHandler.NameCheckHandler());
    Spark.post("/categorycheck", new AdminHandler.CategoryCheckHandler());
    Spark.post("/getallcategories", new AdminHandler.AllCategoriesHandler());
    Spark.post("/game/usertests", new GamePageHandlers.UserTestsHandler());
    Spark.post("/game/deettests", new GamePageHandlers.DeetTestsHandler());
    Spark.post("/load", new GamePageHandlers.LoadSolutionHandler());
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
       
       Boolean unauthorizedAdminRequest = unauthorizedAdmin(url, request);
      
       if ((badRequest && !creatingAccount) || unauthorizedAdminRequest) {
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
  
  
  private static Boolean unauthorizedAdmin(String url, Request request) {
    
    // first check if they're an admin
    String userID = request.cookie("user");
    Boolean isAdmin = false;
    try (UserDatabase ud = new UserDatabase(dbPath)) {
      try {
        isAdmin = ud.isUserAdmin(userID);
      } catch (SQLException e) {
        System.out.println(e.getMessage());
        System.exit(1);
      }
    } catch (SQLException e1) {
      System.out.println(e1.getMessage());
      System.exit(1);
    }
    
    // don't need to check anything if they're an admin
    if (isAdmin) {
      return false;
    }
    
    // if they're not an admin, make sure they're not accessing restricted
    // urls
    String prefix = "http://localhost:4567";
    String[] restricted = {"/admin/edit/(.*?)",
        "/admin/add",
        "/admin/add/results",
        "/admin/delete/(.*?)",
        "/admin/edit/results"};
    List<String> restrictedList = Arrays.asList(restricted);
    for (String restrictedPattern : restrictedList) {
      if (url.matches(prefix + restrictedPattern)) {
        return true;
      }
    }
    return false;
  }
}
