package edu.brown.cs.deet.pageHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.util.Map;

import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.TemplateViewRoute;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import edu.brown.cs.deet.database.UserDatabase;

public final class LoginHandlers {

  private static String appID = "1559408461020162";
  private static String loginRedirectURL = "http://localhost:4567/fblogin";
  private static String appSecret = "9ffcf58f5f448a3e9e723537c476b5eb";
  private static final String dbPath = "data/codegolf.db";
  private static final Gson GSON = new Gson();

  public static class CategoriesHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {

      String username = "Not yet defined";
      try (UserDatabase ud = new UserDatabase(dbPath)) {
        String id = req.cookie("user");
        try {
          if (ud.doesUserExistWithID(id)) {
            username = ud.getUsernameFromID(req.cookie("user"));
          }
        } catch (SQLException e) {
          System.out.println(e.getMessage());
          System.exit(1);
        }
      } catch (SQLException e1) {
        // Eddie added this
        System.out.println(e1.getMessage());
        System.exit(1);
      }

      Map<String, Object> variables = ImmutableMap.of("title", "Categories",
          "name", req.cookie("name"), "username", username);
      return new ModelAndView(variables, "categories.ftl");
    }
  }

  public static class HomePageHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      Map<String, Object> variables = ImmutableMap.of("title", "Home",
          "loginURL", getFBURL());
      return new ModelAndView(variables, "landing.ftl");
    }
  }

  public static class FBHandler implements Route {
    @Override
    public Object handle(Request req, Response res) {
      String fbcode = req.queryParams("code");
      return handleFB(fbcode, req, res);
    }
  }

  public static class LogoutHandler implements Route {
    @Override
    public Object handle(Request req, Response res) {
      res.removeCookie("name");
      res.removeCookie("user");
      res.redirect("/");
      return "Should never get here";
    }
  }

  public static class AddUserHandler implements Route {
    @Override
    public Object handle(Request req, Response res) {
      String username = req.queryMap().value("username");
      try (UserDatabase ud = new UserDatabase(dbPath)) {
        try {
          Boolean usernameAlreadyExists = ud
              .doesUserExistWithUsername(username);
          Boolean idAlreadyExists = ud.doesUserExistWithID(req.cookie("user"));

          if (usernameAlreadyExists) {
            res.status(400);
            return GSON.toJson(ImmutableMap.of("error",
                "That username is already taken."));
          } else if (idAlreadyExists) {
            res.status(400);
            return GSON.toJson(ImmutableMap.of("error",
                "There is already a user associated with this fb account."));
          } else {
            req.session().removeAttribute("adding");
            ud.addNewUser(username, req.cookie("user"), false,
                req.cookie("name"));
            return GSON.toJson("Success!");
          }

        } catch (SQLException e) {
          System.out.println(e.getMessage());
          System.exit(1);
        }
        return "Should never get here";
      } catch (SQLException e1) {
        // Eddie added this
        System.out.println(e1.getMessage());
        System.exit(1);

        return "Should never get here";
      }
    }
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
  static String handleFB(String code, Request req, Response res) {
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
          req.session(true);
          req.session().attribute("adding", "true");
          res.redirect("/categories#signup");
        }

      } catch (SQLException e) {
        System.out.println(e.getMessage());
        System.exit(1);
      }
    } catch (SQLException e1) {
      // Eddie added this
      System.out.println(e1.getMessage());
      System.exit(1);
    }

    // should never get here
    String toReturn = String.format("Name: %s, ID: %s", dataJSON.get("name"),
        dataJSON.get("id"));

    return toReturn;
  }

  // TODO: deal with login and registration differently
  static String getFBURL() {
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
  static Map<String, String> getJSONFromURL(String urlString) {
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

}