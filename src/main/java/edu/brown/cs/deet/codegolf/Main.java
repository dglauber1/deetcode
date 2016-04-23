package edu.brown.cs.deet.codegolf;

import java.io.File;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import edu.brown.cs.deet.database.ChallengeDatabase;
import edu.brown.cs.deet.pageHandler.AdminHandler;

/**
 * Main class that launches Codegolf.
 * @author el13
 */
public class Main {
  /**
   * Constructs a new Main and runs it.
   * @param args
   *          The arguments from the command line.
   */
  public static void main(String[] args) {
    try {
      new Main(args).run();
    } catch (RuntimeException e) {
      System.out.println(e.getMessage());
    }
  }

  private String[] args;
  private File db;

  /**
   * Creates a new instance of Main.
   * @param args
   *          The arguments from the command line.
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
    OptionSet options = parser.parse(args);

    if (options.has("gui")) {
      AdminHandler a = new AdminHandler(new ChallengeDatabase(
          "testdata/challengeDatabaseTester.sqlite3"));
      Server.setAdminHandler(a);
      Server.runSparkServer();
    } else {
      REPL.run();
    }
  }
}
