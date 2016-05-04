package edu.brown.cs.el13.pageHandler;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.junit.Test;

import edu.brown.cs.deet.database.ChallengeDatabase;
import edu.brown.cs.deet.deetcode.pageHandler.AdminHandler;

public class AdminHandlerTest {
  @Test
  public void getChallengeInfoTester() {
    try (ChallengeDatabase cdb = new ChallengeDatabase(
        "testdata/challengeDatabaseTester.sqlite3")) {

      // regular test case
      AdminHandler.setChallengeDatabase(cdb);

      try {
        List<List<String>> res = AdminHandler.getChallengeInfo("fib-fast");
        assertTrue(res.get(0).get(0).equals("integers"));
        assertTrue(res.get(0).get(1).equals("fib-fast"));
        assertTrue(res.get(0).get(2).equals("fib-fast"));
        assertTrue(res.get(0).get(3).equals("Find the nth fib in O(n) time.\n"));

        assertTrue(res.get(1).get(0)
            .equals("crap test name\ncrap test name 2\n"));
        assertTrue(res.get(1).get(1).equals("crap input\ncrap input2\n"));
        assertTrue(res.get(1).get(2).equals("crap output\ncrap output2\n"));

        assertTrue(res.get(2).get(0).equals(""));
        assertTrue(res.get(2).get(1).equals(""));
        assertTrue(res.get(2).get(2).equals(""));
        assertTrue(res.get(2).get(3).equals(""));

        assertTrue(res.get(3).get(0).equals(""));
        assertTrue(res.get(3).get(1).equals(""));
        assertTrue(res.get(3).get(2).equals(""));
        assertTrue(res.get(3).get(3).equals(""));

        assertTrue(res.get(4).get(0).equals(""));
        assertTrue(res.get(4).get(1).equals(""));
        assertTrue(res.get(4).get(2).equals(""));
        assertTrue(res.get(4).get(3).equals(""));
      } catch (IOException e) {
        assertTrue(false);
      } catch (SQLException e) {
        assertTrue(false);
      }

      // bad test case
      try {
        List<List<String>> res = AdminHandler.getChallengeInfo("not-exist");
        assertTrue(res == null);
      } catch (IOException e) {
        assertTrue(false);
      } catch (SQLException e) {
        assertTrue(false);
      }
    } catch (SQLException e1) {
      assertTrue(false);
    }
  }
}
