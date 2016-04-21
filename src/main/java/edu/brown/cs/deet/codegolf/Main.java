package edu.brown.cs.deet.codegolf;

import java.io.File;

import edu.brown.cs.deet.execution.REPL;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

/**
 * Main class that launches Codegolf.
 * @author el13
 */
public class Main {
  /**
   * Constructs a new Main and runs it.
   * @param args The arguments from the command line.
   */
  public static void main(String[] args) {
    new Main(args).run();
  }

  private String[] args;
  private File db;

  /**
   * Creates a new instance of Main.
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
    OptionSet options = parser.parse(args);

    if (options.has("gui")) {
      Server.runSparkServer();
    } else {
      REPL.run();
    }
  }
}
