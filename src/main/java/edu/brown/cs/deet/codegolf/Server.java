package edu.brown.cs.deet.codegolf;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import edu.brown.cs.deet.pageHandler.AdminHandler;
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
  
  private static String appID = "1559408461020162";
  private static String loginRedirectURL = "http://localhost:4567/fblogin";
  private static String appSecret = "9ffcf58f5f448a3e9e723537c476b5eb";

  /**
   * Private constructor for a Server. Never gets called.
   */
  private Server() {
  }

  /**
   * Sets the AdminHandler for the Server.
   * @param a the AdminHandler
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
    Spark.externalStaticFileLocation("src/main/resources/static");
    Spark.exception(Exception.class, new ExceptionPrinter());

    FreeMarkerEngine freeMarker = createEngine();

    // Setup Spark Routes
    Spark.get("/game", new FrontHandler(), freeMarker);

    Spark.get("/categories", (request, response) -> {
      Map<String, Object> variables = ImmutableMap.of("title", "Categories");
      return new ModelAndView(variables, "categories.ftl");
    }, freeMarker);

    Spark.get("/", (request, response) -> {
      Map<String, Object> variables = ImmutableMap.of("title", "Home",
        "loginURL", getFBURL());
      return new ModelAndView(variables, "landing.ftl");
    }, freeMarker);
    
    Spark.get("/fblogin", (request, response) -> {
      String fbcode = request.queryParams("code");
      
      return handleFBLogin(fbcode, response);
    });
  }
  
  private static String handleFBLogin(String code, Response response) {
    String accessTokenURL =
      "https://graph.facebook.com/v2.3/oauth/access_token?client_id="
      + appID + "&redirect_uri=" + loginRedirectURL + "&client_secret="
      + appSecret + "&code=" + code;
    
    Map<String, String> tokenJSON = getJSONFromURL(accessTokenURL);
    
    if (!tokenJSON.containsKey("access_token")) {
      response.status(500);
      return "failed";
    }
          
    String graphDataURL = "https://graph.facebook.com/v2.3/me?access_token="
      + tokenJSON.get("access_token") + "&fields=id,name";
    
    Map<String, String> dataJSON = getJSONFromURL(graphDataURL);
    
    if (!dataJSON.containsKey("id")) {
      response.status(500);
      return "failed";
    }
    
    String toReturn = String.format("Name: %s, ID: %s",
      dataJSON.get("name"), dataJSON.get("id"));
    
    return toReturn;
  }

  /**
   * Handles loading the front page.
   * @author el13
   */
  private static class FrontHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      Map<String, Object> variables = ImmutableMap.of("title", "Game");
      return new ModelAndView(variables, "game.ftl");
    }
  }

  /**
   * Handles the input of a new challenge.
   * @author el13
   */
  private static class NewChallengeHandler implements Route {
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Object handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();

      // Basic info
      String category = GSON.fromJson(qm.value("category"), String.class);
      String name = GSON.fromJson(qm.value("name"), String.class);
      String description = GSON.fromJson(qm.value("description"), String.class);

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
        String pythonInput =
            GSON.fromJson(qm.value("pythonInput"), String.class);
        String pythonOutput =
            GSON.fromJson(qm.value("pythonOutput"), String.class);
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

      Map<String, Object> variables =
          new ImmutableMap.Builder().put("success", success).build();
      return GSON.toJson(variables);
    }
  }

  //TODO: deal with login and registration differently
  private static String getFBURL() {
    return "https://www.facebook.com/dialog/oauth?client_id="
      + appID + "&redirect_uri=" + loginRedirectURL;
  }
  
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
      Type stringStringMap = new TypeToken<Map<String, String>>(){}.getType();
      Map<String, String> json = new Gson().fromJson(line, stringStringMap);
      return json;
    } catch (IOException e) {
      System.out.println(e.getMessage());
      return ImmutableMap.of("error", e.getMessage());
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
