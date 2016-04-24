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
      res = db.getSolutionsForUser("bbauer");
      assertTrue(res.size() == 0);
    } catch (SQLException e) {
      assertTrue(false);
    }
  }

  @Test
  public void topTwentyOfChallengeLanguageTest() {
    try (LeaderboardDatabase db = new LeaderboardDatabase(
        "testdata/challengeDatabaseTester.sqlite3")) {
      // challenge exists
      List<List<String>> res = db.topTwentyOfChallengeLanguage("reverse",
          "java");
      assertTrue(res.size() == 3);
      assertTrue(res.get(0).get(0).equals("reverse"));
      assertTrue(res.get(0).get(1).equals("dglauber"));
      assertTrue(res.get(0).get(2).equals("4.0"));
      assertTrue(res.get(0).get(3).equals("java"));

      assertTrue(res.get(1).get(0).equals("reverse"));
      assertTrue(res.get(1).get(1).equals("el51"));
      assertTrue(res.get(1).get(2).equals("3.0"));
      assertTrue(res.get(1).get(3).equals("java"));

      assertTrue(res.get(2).get(0).equals("reverse"));
      assertTrue(res.get(2).get(1).equals("el13"));
      assertTrue(res.get(2).get(2).equals("3.0"));
      assertTrue(res.get(2).get(3).equals("java"));

      // challenge or language DNE
      res = db.topTwentyOfChallengeLanguage("ordering", "java");
      assertTrue(res.size() == 0);

      res = db.topTwentyOfChallengeLanguage("reverse", "haskell");
      assertTrue(res.size() == 0);

      // more than 20
      res = db.topTwentyOfChallengeLanguage("fib-fast", "java");
      assertTrue(res.size() == 20);
      // jz63 should never be present
      assertTrue(res.get(0).get(1).equals("nhyde"));
      assertTrue(res.get(1).get(1).equals("nhyde"));
      assertTrue(res.get(2).get(1).equals("nhyde"));
      assertTrue(res.get(3).get(1).equals("nhyde"));
      assertTrue(res.get(4).get(1).equals("nhyde"));
      assertTrue(res.get(5).get(1).equals("nhyde"));
      assertTrue(res.get(6).get(1).equals("nhyde"));
      assertTrue(res.get(7).get(1).equals("nhyde"));
      assertTrue(res.get(8).get(1).equals("nhyde"));
      assertTrue(res.get(9).get(1).equals("nhyde"));
      assertTrue(res.get(10).get(1).equals("nhyde"));
      assertTrue(res.get(11).get(1).equals("nhyde"));
      assertTrue(res.get(12).get(1).equals("nhyde"));
      assertTrue(res.get(13).get(1).equals("nhyde"));
      assertTrue(res.get(14).get(1).equals("nhyde"));
      assertTrue(res.get(15).get(1).equals("nhyde"));
      assertTrue(res.get(16).get(1).equals("nhyde"));
      assertTrue(res.get(17).get(1).equals("nhyde"));
      assertTrue(res.get(18).get(1).equals("nhyde"));
      assertTrue(res.get(19).get(1).equals("nhyde"));
    } catch (SQLException e) {
      assertTrue(false);
    }
  }
}
