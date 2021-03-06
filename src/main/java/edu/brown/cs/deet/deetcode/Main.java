package edu.brown.cs.deet.deetcode;

import java.sql.SQLException;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import edu.brown.cs.deet.database.ChallengeDatabase;
import edu.brown.cs.deet.database.LeaderboardDatabase;
import edu.brown.cs.deet.database.UserDatabase;
import edu.brown.cs.deet.deetcode.pageHandler.AdminHandler;
import edu.brown.cs.deet.deetcode.pageHandler.LeaderboardHandler;
import edu.brown.cs.deet.deetcode.pageHandler.StatHandler;
import edu.brown.cs.deet.deetcode.pageHandler.UserHandler;

/**
 * Main class that launches Codegolf.
 *
 * @author el13
 */
public class Main {
  // default database location
  public static String dbLoc = "challengeDatabaseTester.sqlite3";

  /**
   * Constructs a new Main and runs it.
   *
   * @param args The arguments from the command line.
   */
  public static void main(String[] args) {
    try {
      new Main(args).run();
    } catch (RuntimeException e) {
      System.out.println(e.getMessage());
    }
  }

  private String[] args;

  /**
   * Creates a new instance of Main.
   *
   * @param args The arguments from the command line.
   */
  private Main(String[] args) {
    this.args = args;
  }

  /**
   * Runs Main.
   */
  private void run() {
    OptionParser parser = new OptionParser();
    parser.accepts("gui");
    OptionSpec<String> dbSpec = parser.accepts("db").withRequiredArg()
        .ofType(String.class);
    OptionSet options = parser.parse(args);

    if (options.has(dbSpec)) {
      dbLoc = options.valueOf(dbSpec);
    }

    if (options.has("gui")) {
      try {
        AdminHandler.setChallengeDatabase(new ChallengeDatabase(dbLoc));
        UserHandler.setLeaderboardDatabase(new LeaderboardDatabase(dbLoc));
        UserHandler.setUserDatabase(new UserDatabase(dbLoc));
        UserHandler.setChallengeDatabase(new ChallengeDatabase(dbLoc));
        LeaderboardHandler.setChallengeDatabase(new ChallengeDatabase(dbLoc));
        LeaderboardHandler
            .setLeaderboardDatabase(new LeaderboardDatabase(dbLoc));
        LeaderboardHandler.setUserDatabase(new UserDatabase(dbLoc));
        StatHandler.setLeaderboardDatabase(new LeaderboardDatabase(dbLoc));
        StatHandler.setChallengeDatabase(new ChallengeDatabase(dbLoc));
        StatHandler.setUserDatabase(new UserDatabase(dbLoc));
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
      Server.runSparkServer();
    } else {
      REPL.run();
    }
  }
}
