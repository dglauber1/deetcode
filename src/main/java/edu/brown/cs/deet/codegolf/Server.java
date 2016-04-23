package edu.brown.cs.deet.codegolf;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
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

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import edu.brown.cs.deet.pageHandler.AdminHandler;
import freemarker.template.Configuration;

final class Server {
  private static final Gson GSON = new Gson();
  private static AdminHandler admin;
  private static final int PORT = 4567;

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
    Spark.setPort(PORT);

    Spark.externalStaticFileLocation("src/main/resources/static");
    Spark.exception(Exception.class, new ExceptionPrinter());

    FreeMarkerEngine freeMarker = createEngine();

    // Setup Spark Routes
    Spark.get("/game", new FrontHandler(), freeMarker);
    Spark.get("/admin_add", new AdminAddHandler(), freeMarker);
    Spark.post("/admin_add/results", new NewChallengeHandler());
    Spark.post("/namecheck", new NameCheckHandler());
    Spark.post("/categorycheck", new CategoryCheckHandler());
    Spark.post("/getallcategories", new AllCategoriesHandler());
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
   * Shows the Admin_add page.
   * @author el13
   */
  private static class AdminAddHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      Map<String, Object> variables =
          ImmutableMap.of("title", "Add a Challenge");
      return new ModelAndView(variables, "newChallenge.ftl");
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

  /**
   * Handler that checks if the entered name in the Admin page is already taken.
   * @author el13
   */
  @SuppressWarnings("unused")
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

      @SuppressWarnings({"rawtypes", "unchecked"})
      Map<String, Object> variables =
          new ImmutableMap.Builder().put("exists", exists).build();
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

      @SuppressWarnings({"rawtypes", "unchecked"})
      Map<String, Object> variables =
          new ImmutableMap.Builder().put("categories", categories).build();
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

      @SuppressWarnings({"rawtypes", "unchecked"})
      Map<String, Object> variables =
          new ImmutableMap.Builder().put("exists", exists).build();
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
