package edu.brown.cs.el13.database;

import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.List;

import org.junit.Test;

import edu.brown.cs.deet.database.LeaderboardDatabase;

public class LeaderboardDatabaseTest {
  @Test
  public void getSolutionsForUserTest() {
    try (LeaderboardDatabase db = new LeaderboardDatabase(
        "testdata/challengeDatabaseTester.sqlite3")) {
      // Two solutions
      List<List<String>> res = db.getSolutionsForUser("el51");
      assertTrue(res.size() == 2);
      assertTrue(res.get(0).get(0).equals("reverse"));
      assertTrue(res.get(0).get(1).equals("el51"));
      assertTrue(res.get(0).get(2).equals("true"));
      assertTrue(res.get(0).get(3).equals("1.0"));
      assertTrue(res.get(0).get(4).equals("1.0"));
      assertTrue(res.get(0).get(5).equals("1.0"));
      assertTrue(res.get(0).get(6).equals("3.0"));
      assertTrue(res.get(0).get(7).equals("java"));

      assertTrue(res.get(1).get(0).equals("fibonacci"));
      assertTrue(res.get(1).get(1).equals("el51"));
      assertTrue(res.get(1).get(2).equals("false"));
      assertTrue(res.get(1).get(3).equals("1.0"));
      assertTrue(res.get(1).get(4).equals("1.0"));
      assertTrue(res.get(1).get(5).equals("1.0"));
      assertTrue(res.get(1).get(6).equals("3.0"));
      assertTrue(res.get(1).get(7).equals("java"));

      // One solution
      res = db.getSolutionsForUser("el13");
      assertTrue(res.size() == 1);

      assertTrue(res.get(0).get(0).equals("reverse"));
      assertTrue(res.get(0).get(1).equals("el13"));
      assertTrue(res.get(0).get(2).equals("true"));
      assertTrue(res.get(0).get(3).equals("1.0"));
      assertTrue(res.get(0).get(4).equals("1.0"));
      assertTrue(res.get(0).get(5).equals("1.0"));
      assertTrue(res.get(0).get(6).equals("3.0"));
      assertTrue(res.get(0).get(7).equals("java"));
      // No solution
      res = db.getSolutionsForUser("dglauber");
      assertTrue(res.size() == 0);
    } catch (SQLException e) {
      assertTrue(false);
    }
  }
}
