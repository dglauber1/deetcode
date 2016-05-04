package edu.brown.cs.el13.pageHandler;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.junit.Test;

import edu.brown.cs.deet.database.LeaderboardDatabase;
import edu.brown.cs.deet.database.UserDatabase;
import edu.brown.cs.deet.pageHandler.LeaderboardHandler;

public class LeaderboardHandlerTest {
  @Test
  public void getLeaderboardInfoTest() {
    try (LeaderboardDatabase ldb = new LeaderboardDatabase(
        "testdata/challengeDatabaseTester.sqlite3")) {
      try (UserDatabase udb = new UserDatabase(
          "testdata/challengeDatabaseTester.sqlite3")) {
        LeaderboardHandler.setLeaderboardDatabase(ldb);
        LeaderboardHandler.setUserDatabase(udb);

        // user has done the challenge
        List<List<String>> res = LeaderboardHandler.getLeaderboardInfo("jz63",
            "fib-fast", "aggregate", "java");
        assertTrue(res.size() == 20);
        assertTrue(res.get(0).get(0).equals("nhyde"));
        assertTrue(res.get(0).get(1).equals("java"));
        assertTrue(res.get(0).get(2).equals("21.0"));
        assertTrue(res.get(0).get(3).equals("nhyde solution\n"));

        assertTrue(res.get(1).get(0).equals("nhyde"));
        assertTrue(res.get(1).get(1).equals("java"));
        assertTrue(res.get(1).get(2).equals("21.0"));
        assertTrue(res.get(1).get(3).equals("nhyde solution\n"));

        assertTrue(res.get(2).get(0).equals("nhyde"));
        assertTrue(res.get(2).get(1).equals("java"));
        assertTrue(res.get(2).get(2).equals("21.0"));
        assertTrue(res.get(2).get(3).equals("nhyde solution\n"));

        assertTrue(res.get(3).get(0).equals("nhyde"));
        assertTrue(res.get(3).get(1).equals("java"));
        assertTrue(res.get(3).get(2).equals("21.0"));
        assertTrue(res.get(3).get(3).equals("nhyde solution\n"));

        assertTrue(res.get(4).get(0).equals("nhyde"));
        assertTrue(res.get(4).get(1).equals("java"));
        assertTrue(res.get(4).get(2).equals("21.0"));
        assertTrue(res.get(4).get(3).equals("nhyde solution\n"));

        assertTrue(res.get(5).get(0).equals("nhyde"));
        assertTrue(res.get(5).get(1).equals("java"));
        assertTrue(res.get(5).get(2).equals("21.0"));
        assertTrue(res.get(5).get(3).equals("nhyde solution\n"));

        assertTrue(res.get(6).get(0).equals("nhyde"));
        assertTrue(res.get(6).get(1).equals("java"));
        assertTrue(res.get(6).get(2).equals("21.0"));
        assertTrue(res.get(6).get(3).equals("nhyde solution\n"));

        assertTrue(res.get(7).get(0).equals("nhyde"));
        assertTrue(res.get(7).get(1).equals("java"));
        assertTrue(res.get(7).get(2).equals("21.0"));
        assertTrue(res.get(7).get(3).equals("nhyde solution\n"));

        assertTrue(res.get(8).get(0).equals("nhyde"));
        assertTrue(res.get(8).get(1).equals("java"));
        assertTrue(res.get(8).get(2).equals("21.0"));
        assertTrue(res.get(8).get(3).equals("nhyde solution\n"));

        assertTrue(res.get(9).get(0).equals("nhyde"));
        assertTrue(res.get(9).get(1).equals("java"));
        assertTrue(res.get(9).get(2).equals("21.0"));
        assertTrue(res.get(9).get(3).equals("nhyde solution\n"));

        assertTrue(res.get(10).get(0).equals("nhyde"));
        assertTrue(res.get(10).get(1).equals("java"));
        assertTrue(res.get(10).get(2).equals("21.0"));
        assertTrue(res.get(10).get(3).equals("nhyde solution\n"));

        assertTrue(res.get(11).get(0).equals("nhyde"));
        assertTrue(res.get(11).get(1).equals("java"));
        assertTrue(res.get(11).get(2).equals("21.0"));
        assertTrue(res.get(11).get(3).equals("nhyde solution\n"));

        assertTrue(res.get(12).get(0).equals("nhyde"));
        assertTrue(res.get(12).get(1).equals("java"));
        assertTrue(res.get(12).get(2).equals("21.0"));
        assertTrue(res.get(12).get(3).equals("nhyde solution\n"));

        assertTrue(res.get(13).get(0).equals("nhyde"));
        assertTrue(res.get(13).get(1).equals("java"));
        assertTrue(res.get(13).get(2).equals("21.0"));
        assertTrue(res.get(3).get(3).equals("nhyde solution\n"));

        assertTrue(res.get(14).get(0).equals("nhyde"));
        assertTrue(res.get(14).get(1).equals("java"));
        assertTrue(res.get(14).get(2).equals("21.0"));
        assertTrue(res.get(14).get(3).equals("nhyde solution\n"));

        assertTrue(res.get(15).get(0).equals("nhyde"));
        assertTrue(res.get(15).get(1).equals("java"));
        assertTrue(res.get(15).get(2).equals("21.0"));
        assertTrue(res.get(15).get(3).equals("nhyde solution\n"));

        assertTrue(res.get(16).get(0).equals("nhyde"));
        assertTrue(res.get(16).get(1).equals("java"));
        assertTrue(res.get(16).get(2).equals("21.0"));
        assertTrue(res.get(16).get(3).equals("nhyde solution\n"));

        assertTrue(res.get(17).get(0).equals("nhyde"));
        assertTrue(res.get(17).get(1).equals("java"));
        assertTrue(res.get(17).get(2).equals("21.0"));
        assertTrue(res.get(17).get(3).equals("nhyde solution\n"));

        assertTrue(res.get(18).get(0).equals("nhyde"));
        assertTrue(res.get(18).get(1).equals("java"));
        assertTrue(res.get(18).get(2).equals("21.0"));
        assertTrue(res.get(18).get(3).equals("nhyde solution\n"));

        assertTrue(res.get(19).get(0).equals("nhyde"));
        assertTrue(res.get(19).get(1).equals("java"));
        assertTrue(res.get(19).get(2).equals("21.0"));
        assertTrue(res.get(19).get(3).equals("nhyde solution\n"));

        // user has not done challenge
        res = LeaderboardHandler.getLeaderboardInfo("jz63", "reverse",
            "aggregate", "java");

        assertTrue(res.size() == 3);
        assertTrue(res.get(0).get(0).equals("dglauber"));
        assertTrue(res.get(0).get(1).equals("java"));
        assertTrue(res.get(0).get(2).equals("4.0"));
        assertTrue(res
            .get(0)
            .get(3)
            .equals(
                "You must attempt the challenge before you can see a solution."));

        assertTrue(res.get(1).get(0).equals("el51"));
        assertTrue(res.get(1).get(1).equals("java"));
        assertTrue(res.get(1).get(2).equals("3.0"));
        assertTrue(res
            .get(1)
            .get(3)
            .equals(
                "You must attempt the challenge before you can see a solution."));

        assertTrue(res.get(2).get(0).equals("el13"));
        assertTrue(res.get(2).get(1).equals("java"));
        assertTrue(res.get(2).get(2).equals("3.0"));
        assertTrue(res
            .get(2)
            .get(3)
            .equals(
                "You must attempt the challenge before you can see a solution."));

        // efficiency
        res = LeaderboardHandler.getLeaderboardInfo("el13", "reverse",
            "efficiency", "java");
        assertTrue(res.size() == 3);
        assertTrue(res.get(0).get(0).equals("el51"));
        assertTrue(res.get(0).get(1).equals("java"));
        assertTrue(res.get(0).get(2).equals("1.0"));

        assertTrue(res.get(1).get(0).equals("el13"));
        assertTrue(res.get(1).get(1).equals("java"));
        assertTrue(res.get(1).get(2).equals("2.0"));

        assertTrue(res.get(2).get(0).equals("dglauber"));
        assertTrue(res.get(2).get(1).equals("java"));
        assertTrue(res.get(2).get(2).equals("3.0"));

        // brevity
        res = LeaderboardHandler.getLeaderboardInfo("el13", "reverse",
            "brevity", "java");
        assertTrue(res.size() == 3);
        assertTrue(res.get(0).get(0).equals("dglauber"));
        assertTrue(res.get(0).get(1).equals("java"));
        assertTrue(res.get(0).get(2).equals("1.0"));

        assertTrue(res.get(1).get(0).equals("el13"));
        assertTrue(res.get(1).get(1).equals("java"));
        assertTrue(res.get(1).get(2).equals("2.0"));

        assertTrue(res.get(2).get(0).equals("el51"));
        assertTrue(res.get(2).get(1).equals("java"));
        assertTrue(res.get(2).get(2).equals("3.0"));

        // speed
        res = LeaderboardHandler.getLeaderboardInfo("el13", "reverse", "speed",
            "java");
        assertTrue(res.size() == 3);
        assertTrue(res.get(0).get(0).equals("el13"));
        assertTrue(res.get(0).get(1).equals("java"));
        assertTrue(res.get(0).get(2).equals("1.0"));

        assertTrue(res.get(1).get(0).equals("dglauber"));
        assertTrue(res.get(1).get(1).equals("java"));
        assertTrue(res.get(1).get(2).equals("2.0"));

        assertTrue(res.get(2).get(0).equals("el51"));
        assertTrue(res.get(2).get(1).equals("java"));
        assertTrue(res.get(2).get(2).equals("3.0"));
      } catch (IOException e) {
        assertTrue(false);
      }
    } catch (SQLException e) {
      assertTrue(false);
    }
  }
}
