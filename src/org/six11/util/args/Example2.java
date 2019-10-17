// $Id: Example2.java 22 2009-11-24 20:00:53Z gabe.johnson $
package org.six11.util.args;

import static org.six11.util.args.Arguments.ArgType;
import static org.six11.util.args.Arguments.ValueType;

/**
 * A more involved example demonstrating how to configure your Arguments instance, give
 * documentation, and use it in a slightly more complex way. This is still way less complicated than
 * many other command line parsing tools out there. If you need to do advanced things like
 * conditional requirements (e.g. require a if b is present) you'll have to use something else.
 * 
 * @author Gabe Johnson <johnsogg@cmu.edu>
 */
public class Example2 {

  public static void main(String[] args) {
    Arguments a = new Arguments();

    a.setProgramName("look"); // set name and documentation for the program as a whole
    a.setDocumentationProgram("Lists files and directories.");

    // configure Arguments. Specify which are required, and which take values (e.g. --foo=bar).
    a.addFlag("suffix", ArgType.ARG_OPTIONAL, ValueType.VALUE_REQUIRED,
        "Specify the suffix to show.");
    a.addFlag("h", ArgType.ARG_OPTIONAL, ValueType.VALUE_IGNORED,
        "Show file sizes in a more human-readable form.");
    a.addFlag("l", ArgType.ARG_OPTIONAL, ValueType.VALUE_IGNORED,
        "Long listing. Show many details about a file.");
    a.addFlag("help", ArgType.ARG_OPTIONAL, ValueType.VALUE_IGNORED, "Shows this help.");
    a.addFlag("long-help", ArgType.ARG_OPTIONAL, ValueType.VALUE_IGNORED, "Shows extended help.");
    a.addPositional(0, "dir", ValueType.VALUE_REQUIRED, "The starting directory.");

    a.parseArguments(args); // apply rules from above to user-supplied input.

    if (a.hasFlag("help")) { // check for --help
      System.out.println(a.getUsage());
      System.exit(0);
    }

    if (a.hasFlag("long-help")) { // check for --help
      System.out.println(a.getDocumentation());
      System.exit(0);
    }

    try {
      a.validate(); // Ensure user input conforms to our specification and stop if it does not.
    } catch (IllegalArgumentException ex) {
      System.out.println(ex.getMessage());
      System.out.println(a.getUsage());
      System.exit(-1);
    }

    // Now we can use the arguments in our simple application that doesn't do anything useful.
    System.out.println("List files in directory " + a.getValue("dir"));
    if (a.hasFlag("l")) {
      System.out.println("  ... use long listing.");
    }
    if (a.hasFlag("h")) {
      System.out.println("  ... use human-readable file sizes.");
    }
    if (a.hasFlag("suffix")) {
      System.out.println("  ... use suffix = '" + a.getValue("suffix") + "'");
    }
  }
}
