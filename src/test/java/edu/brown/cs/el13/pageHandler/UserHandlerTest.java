package edu.brown.cs.el13.pageHandler;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.junit.Test;

import edu.brown.cs.deet.database.LeaderboardDatabase;
import edu.brown.cs.deet.pageHandler.UserHandler;

public class UserHandlerTest {
  @Test
  public void getChallengeForUserTest() {
    try (LeaderboardDatabase db = new LeaderboardDatabase(
        "testdata/challengeDatabaseTester.sqlite3")) {
      UserHandler handler = new UserHandler(db);
      List<List<String>> res = handler.getChallengeInfoForUser("el51");

      assertTrue(res.size() == 2);
      assertTrue(res.get(0).get(0).equals("reverse"));
      assertTrue(res.get(0).get(1).equals("true"));
      assertTrue(res.get(0).get(2).equals("1")); // because thakamor is python
      assertTrue(res.get(0).get(3).equals("java"));

      assertTrue(res.get(1).get(0).equals("fibonacci"));
      assertTrue(res.get(1).get(1).equals("false"));
      assertTrue(res.get(1).get(2).equals("n/a")); // because thakamor is python
      assertTrue(res.get(1).get(3).equals("n/a"));
      assertTrue(res.get(1).get(4).equals("n/a"));

      res = handler.getChallengeInfoForUser("jz63");
      assertTrue(res.get(0).get(0).equals("fib-fast"));
      assertTrue(res.get(0).get(1).equals("true"));
      assertTrue(res.get(0).get(2).equals("n/a"));
      assertTrue(res.get(0).get(3).equals("java"));
      assertTrue(res.get(0).get(4).equals(""));
      assertTrue(res.size() == 1);
    } catch (SQLException e) {
      assertTrue(false);
    } catch (IOException e) {
      assertTrue(false);
    }
  }
}