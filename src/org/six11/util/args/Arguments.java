// $Id: Arguments.java 46 2010-02-02 15:04:53Z gabe.johnson@gmail.com $
package org.six11.util.args;

import java.util.*;

/**
 * This parses command line arguments optimized for ease of programmer use. It is NOT a
 * swiss-army-knife of command line parsers. It is designed to be easy enough to use and remember
 * that your average programmer (e.g. me) can use it without consulting any documentation aside from
 * an example.
 * 
 * It accepts boolean-presence short args like -x. It also accepts long arguments like
 * (--enable-debugging) followed by an optional word (e.g. --enable-debugging=false). All arguments
 * that do not begin with a dash are considered positional.
 * 
 * Order does not matter except for how positional arguments are in relation to one another. So
 * 
 * <pre>
 * -x --username=billybob myFile
 * </pre>
 * 
 * is equivalent to
 * 
 * <pre>
 * --username=billybob myfile -x
 * </pre>
 * 
 * The following is an example of how to use it in a very simple but powerful way:
 * 
 * <pre>
 * public static void main(String[] args) {
 *   Arguments a = new Arguments(args);
 *   if (a.hasFlag(&quot;foo&quot;)) {
 *     System.out.println(&quot;You provided the 'foo' flag.&quot;);
 *   } else {
 *     System.out.println(&quot;Maybe try passing in the 'foo' flag.&quot;);
 *   }
 *   if (a.hasValue(&quot;foo&quot;)) {
 *     System.out.println(&quot;Huzzah! You provided a value for foo: &quot; + a.getValue(&quot;foo&quot;));
 *   } else {
 *     System.out.println(&quot;You can assign foo a value like this: --foo=blahblah&quot;);
 *   }
 * }
 * </pre>
 * 
 * The following is a more involved example showing how to configure, validate, and use flags.
 * 
 * <pre>
 * public static void main(String[] args) {
 *   Arguments a = new Arguments();
 * 
 *   a.setProgramName(&quot;look&quot;); // set name and documentation for the program as a whole
 *   a.setDocumentationProgram(&quot;Lists files and directories.&quot;);
 * 
 *   // configure Arguments. Specify which are required, and which take values (e.g. --foo=bar).
 *   a.addFlag(&quot;suffix&quot;, ArgType.ARG_OPTIONAL, ValueType.VALUE_REQUIRED,
 *       &quot;Specify the suffix to show.&quot;);
 *   a.addFlag(&quot;h&quot;, ArgType.ARG_OPTIONAL, ValueType.VALUE_IGNORED,
 *       &quot;Show file sizes in a more human-readable form.&quot;);
 *   a.addFlag(&quot;l&quot;, ArgType.ARG_OPTIONAL, ValueType.VALUE_IGNORED,
 *       &quot;Long listing. Show many details about a file.&quot;);
 *   a.addFlag(&quot;help&quot;, ArgType.ARG_OPTIONAL, ValueType.VALUE_IGNORED, &quot;Shows this help.&quot;);
 *   a.addFlag(&quot;long-help&quot;, ArgType.ARG_OPTIONAL, ValueType.VALUE_IGNORED, &quot;Shows extended help.&quot;);
 *   a.addPositional(0, &quot;dir&quot;, ValueType.VALUE_REQUIRED, &quot;The starting directory.&quot;);
 * 
 *   a.parseArguments(args); // apply rules from above to user-supplied input.
 * 
 *   if (a.hasFlag(&quot;help&quot;)) { // check for --help
 *     System.out.println(a.getUsage());
 *     System.exit(0);
 *   }
 * 
 *   if (a.hasFlag(&quot;long-help&quot;)) { // check for --help
 *     System.out.println(a.getDocumentation());
 *     System.exit(0);
 *   }
 * 
 *   try {
 *     a.validate(); // Ensure user input conforms to our specification and stop if it does not.
 *   } catch (IllegalArgumentException ex) {
 *     System.out.println(ex.getMessage());
 *     System.out.println(a.getUsage());
 *     System.exit(-1);
 *   }
 * 
 *   // Now we can use the arguments in our simple application that doesn't do anything useful.
 *   System.out.println(&quot;List files in directory &quot; + a.getValue(&quot;dir&quot;));
 *   if (a.hasFlag(&quot;l&quot;)) {
 *     System.out.println(&quot;  ... use long listing.&quot;);
 *   }
 *   if (a.hasFlag(&quot;h&quot;)) {
 *     System.out.println(&quot;  ... use human-readable file sizes.&quot;);
 *   }
 *   if (a.hasFlag(&quot;suffix&quot;)) {
 *     System.out.println(&quot;  ... use suffix = '&quot; + a.getValue(&quot;suffix&quot;) + &quot;'&quot;);
 *   }
 * }
 * </pre>
 * 
 * The second example can be found in Example2.
 * 
 * The Arguments parser can handle arguments in strange orders, and ignores things it does not
 * understand. For example:
 * 
 * <pre>
 * $ ./run org.six11.util.args.Example2 -h --this-flag-is-ignored Monkeychowder bacon --suffix=jpg -l
 * List files in directory Monkeychowder
 *   ... use long listing.
 *   ... use human-readable file sizes.
 *   ... use suffix = 'jpg'
 * </pre>
 * 
 * If you run the program without any arguments it shows you the usage (like --help would):
 * 
 * <pre>
 * $ ./run org.six11.util.args.Example2
 * Wrong number of positional arguments. Expected 1, received 0
 * look: Lists files and directories.
 * look  [ -h -l ]  [ --help --long-help --suffix=... ] dir
 * </pre>
 * 
 * Finally, if you run the above with --long-help it shows you this:
 * 
 * <pre>
 * $ ./run org.six11.util.args.Example2 --long-help
 * 
 * look: Lists files and directories.
 * 
 *   ===   Non-required flags:
 * 
 * h:                    Show file sizes in a more human-readable form.
 * help:                 Shows this help.
 * l:                    Long listing. Show many details about a file.
 * long-help:            Shows extended help.
 * suffix:               Specify the suffix to show. [Must specify value]
 * 
 *    ===   Positional fields:
 * 
 * (1) dir (required):   The starting directory.
 * </pre>
 * 
 * @author Gabe Johnson <johnsogg@cmu.edu>
 */
public class Arguments {

  public static enum ArgType {
    ARG_OPTIONAL, ARG_REQUIRED
  };

  public static enum ValueType {
    VALUE_OPTIONAL, VALUE_REQUIRED, VALUE_IGNORED
  }

  private String[] originalArgs;
  private Set<String> shortArgs = new HashSet<String>();
  private Map<String, String> longArgs = new HashMap<String, String>();
  private List<String> positionalArgs = new ArrayList<String>();
  private Map<String, String> docs = new HashMap<String, String>();
  private List<List<String>> positionalDocs = new ArrayList<List<String>>();
  private Set<String> requireFlag = new HashSet<String>();
  private Set<String> requireValue = new HashSet<String>();
  private int requiredPositionArgs = -1;
  private Set<String> optionalFlag = new HashSet<String>();
  private Set<String> optionalValue = new HashSet<String>();
  private String shortProgramDoc = "";
  private String programName = "";

  private String space = "   ";

  /**
   * Make a blank Arguments instance suitable for re-use.
   * 
   * Example usage:
   * 
   * <pre>
   * Arguments args = new Arguments();
   * args.addFlag(&quot;load-path&quot;, ArgType.ARG_OPTIONAL, ValueType.VALUE_REQUIRED,
   *     &quot;Specifies the root load path for Slippy code.&quot;);
   * args.parseArguments(userCommandStringArray);
   * args.validate(); // throws IllegalArgumentException if something is wrong
   * String loadPath = args.hasValue(&quot;load-path&quot;) ? args.getValue(&quot;load-path&quot;) : &quot;.&quot;;
   * </pre>
   */
  public Arguments() {
    // do nothing. Let the programmer configure it first.
  }

  /**
   * Make a new Arguments instance and parse the given arguments. This does not validate them (as
   * there are no instructions on how to validate it). This is the fastest way to use Arguments.
   * Simply pass your command line input here and as for values using hasFlag(), getValue(), and
   * getPosition().
   * 
   * @param args
   *          the arguments, probably as provided to the main() function.
   */
  public Arguments(String[] args) {
    parseArguments(args);
  }

  /**
   * Sets a short documentation string for the program. This should be one sentence that tells the
   * user what the program does. It should not be an extended discourse.
   */
  public void setDocumentationProgram(String pd) {
    shortProgramDoc = pd;
  }

  /**
   * Sets the program name---what the user types in to invoke the command.
   */
  public void setProgramName(String pn) {
    programName = pn;
  }

  /**
   * Documents a given flag.
   */
  private void setDocumentation(String flag, String doc) {
    // allow a null value, but don't overwrite something existing with a null value.
    if (!docs.containsKey(flag) || (docs.containsKey(flag) && doc != null)) {
      docs.put(flag, doc);
    }
  }

  private void setDocumentationPositional(int pos, String label, String doc) {
    // first ensure there's a spot.
    while (positionalDocs.size() <= pos) {
      List<String> unknown = new ArrayList<String>();
      unknown.add("?");
      unknown.add("?");
      positionalDocs.add(unknown);
    }
    positionalDocs.get(pos).set(0, label);
    positionalDocs.get(pos).set(1, doc);
  }

  /**
   * Make a String padded on the right with spaces that has the given total length.
   * 
   * Example: makePadded("foo", 6) will return "foo   ".
   */
  private static String makePadded(String in, int totalLength) {
    StringBuilder buf = new StringBuilder();
    buf.append(in);
    while (buf.length() <= totalLength) {
      buf.append(" ");
    }
    return buf.toString();
  }

  /**
   * Make an ordered list of Strings based on the input, none of which is longer than the given
   * length. It assumes the String is broken up by spaces. This is helpful when printing blocks of
   * text that can not be too long.
   */
  private static List<String> restricLineLength(String in, int length) {
    List<String> ret = new ArrayList<String>();
    StringBuilder buf = new StringBuilder();
    if (in != null) {
      StringTokenizer toks = new StringTokenizer(in);
      while (toks.hasMoreTokens()) {
        String tok = toks.nextToken();
        if (buf.length() + tok.length() < length) {
          buf.append(" " + tok);
        } else {
          ret.add(buf.toString().trim());
          buf.setLength(0);
          buf.append(tok);
        }
      }
      if (buf.length() > 0) {
        ret.add(buf.toString().trim());
      }
    }
    return ret;
  }

  /**
   * Gives a short synopsis of how to provide arguments, including which are required and optional,
   * and which long arguments take values.
   */
  public String getUsage() {
    StringBuilder buf = new StringBuilder();
    if (programName.length() > 0) {
      buf.append(programName + ": ");
    }
    if (shortProgramDoc.length() > 0) {
      buf.append(shortProgramDoc + "\n");
    } else {
      buf.append("usage synopsis...\n");
    }
    SortedSet<String> smallRequired = new TreeSet<String>();
    SortedSet<String> smallNotRequired = new TreeSet<String>();
    SortedSet<String> bigRequired = new TreeSet<String>();
    SortedSet<String> bigNotRequired = new TreeSet<String>();

    for (String f : docs.keySet()) {
      boolean req = requireFlag.contains(f);
      boolean sh = f.length() == 1 && !requireValue.contains(f);
      if (req && sh) {
        smallRequired.add(f);
      } else if (req && !sh) {
        bigRequired.add(f);
      } else if (!req && sh) {
        smallNotRequired.add(f);
      } else if (!req && !sh) {
        bigNotRequired.add(f);
      }
    }

    if (programName.length() > 0) {
      buf.append(programName + " ");
    }
    for (String f : smallRequired) {
      buf.append("-" + f + " ");
    }
    if (smallNotRequired.size() > 0) {
      buf.append(" [ ");
      for (String f : smallNotRequired) {
        buf.append("-" + f + " ");
      }
      buf.append("] ");
    }
    for (String f : bigRequired) {
      if (requireValue.contains(f)) {
        buf.append("--" + f + "=..." + " ");
      } else {
        buf.append("--" + f + "[=...]" + " ");
      }
    }
    if (bigNotRequired.size() > 0) {
      buf.append(" [ ");
      for (String f : bigNotRequired) {
        if (requireValue.contains(f)) {
          buf.append("--" + f + "=..." + " ");
        } else {
          buf.append("--" + f + " ");
        }
      }
      buf.append("] ");
    }
    for (int i = 0; i < positionalDocs.size(); i++) {
      if (i == requiredPositionArgs) {
        buf.append(" [ ");
      }
      buf.append(positionalDocs.get(i).get(0) + " ");
      if (i == requiredPositionArgs) {
        buf.append(" ] ");
      }

    }
    return buf.toString();
  }

  /**
   * Returns a verbose String that documents this Arguments instance. It summarizes your options
   * into required, non-required, and positional fields. For long args it also tells you which
   * fields should have a value if it is present.
   */
  public String getDocumentation() {
    StringBuilder buf = new StringBuilder("\n");
    int maxFlagSize = 0;
    SortedSet<String> reqList = new TreeSet<String>();
    SortedSet<String> nonReqList = new TreeSet<String>();

    if (programName.length() > 0) {
      buf.append(programName + ": ");
    }
    if (shortProgramDoc.length() > 0) {
      buf.append(shortProgramDoc + "\n");
    } else {
      buf.append("Complete documentation...\n");
    }

    // add flags to required/non-required sets
    for (String docMe : docs.keySet()) {
      maxFlagSize = Math.max(maxFlagSize, docMe.length());
      if (requireFlag.contains(docMe)) {
        reqList.add(docMe);
      } else {
        nonReqList.add(docMe);
      }
    }

    // add positional fields to required/non-required sets
    for (int i = 0; i < positionalDocs.size(); i++) {
      List<String> posDoc = positionalDocs.get(i);
      String pseudoFlag = getPseudoFlag(i, posDoc.get(0));
      maxFlagSize = Math.max(maxFlagSize, pseudoFlag.length());
    }
    if (reqList.size() > 0) {
      buf.append("  ===   Required flags:\n\n");
      buf.append(getDocumentation(reqList, maxFlagSize));
    }
    if (nonReqList.size() > 0) {
      buf.append("\n  ===   Non-required flags:\n\n");
      buf.append(getDocumentation(nonReqList, maxFlagSize));
    }
    if (positionalDocs.size() > 0) {
      buf.append("\n   ===   Positional fields:\n\n");
      for (int i = 0; i < positionalDocs.size(); i++) {
        List<String> posDoc = positionalDocs.get(i);
        String pseudoFlag = getPseudoFlag(i, posDoc.get(0));
        buf.append(formatDocumentation(pseudoFlag, space, maxFlagSize, posDoc.get(1), 70));
      }
    }
    return buf.toString();
  }

  private String getPseudoFlag(int pos, String flag) {
    return "(" + (pos + 1) + ") " + flag + ((pos < requiredPositionArgs) ? " (required)" : "");
  }

  private static String formatDocumentation(String f, String space, int maxFlagSize,
      String docString, int maxLineLength) {
    StringBuilder buf = new StringBuilder();
    buf.append(Arguments.makePadded(f + ":", maxFlagSize));
    buf.append(space);
    List<String> flagDoc = Arguments.restricLineLength(docString, maxLineLength - maxFlagSize);
    String padSpace = Arguments.makePadded("", maxFlagSize + space.length());
    for (int i = 0; i < flagDoc.size(); i++) {
      if (i > 0) {
        buf.append(padSpace);
      }
      buf.append(flagDoc.get(i) + "\n");
    }
    if (flagDoc.size() == 0) { // no docs for this one
      buf.append("\n");
    }
    return buf.toString();
  }

  private String getDocumentation(SortedSet<String> list, int maxFlagSize) {

    StringBuilder buf = new StringBuilder();
    for (String f : list) {
      String docStr = docs.get(f) + (requireValue.contains(f) ? " [Must specify value]" : "");
      buf.append(formatDocumentation(f, space, maxFlagSize, docStr, 70));
    }
    return buf.toString();
  }

  /**
   * Parses arguments. This is how the Arguments object is fed with user-data.
   */
  public void parseArguments(String[] args) {
    this.originalArgs = args;
    for (int i = 0; i < args.length; i++) {
      int consumed = parse(i, args);
      i = i + consumed;
    }
  }
  
  public void parseArguments(Arguments original) {
    parseArguments(original.getOriginalArgs());
  }

  /**
   * Supplies the string array provided from the command line.
   */
  public String[] getOriginalArgs() {
    return originalArgs;
  }
  
  /**
   * Tells you if a given flag was provided.
   */
  public boolean hasFlag(String f) {
    return shortArgs.contains(f) || longArgs.containsKey(f);
  }

  /**
   * Tells you if the user provided a value for a given flag or documented positional field.
   * 
   * For example, if the user provided --name="Dorp Zirconium", hasValue("name") returns true.
   * Alternately, if position 3 was documented with the label "name" and your argument string is
   * "foo bar baf", this will also return true (and getValue("name") returns "baf").
   */
  public boolean hasValue(String f) {
    return longArgs.containsKey(f) && longArgs.get(f) != null;
  }

  /**
   * Returns a value associated with a flag or documented positional field.
   * 
   * @return a String if one was found, or null.
   * @see #hasValue(String)
   */
  public String getValue(String f) {
    return longArgs.get(f);
  }

  /**
   * Tells you how many positional arguments (non-flags) were provided.
   */
  public int getPositionCount() {
    return positionalArgs.size();
  }

  /**
   * Returns the value of the free position input. For example, if the command line arguments were:
   * 
   * <code>-a Foo --type=jpeg Bar</code>, getPosition(0) returns Foo and getPosition(1) returns Bar.
   */
  public String getPosition(int n) {
    return positionalArgs.get(n);
  }

  /**
   * Validates user-provided arguments against the known requirements. Arguments should be provided
   * via the Arguments(String[]) constructor, or the parseArguments(String[]) method.
   */
  public void validate() {
    StringBuilder message = new StringBuilder();
    boolean ok = true;
    // ensure required flags are here.
    for (String requireMe : requireFlag) {
      if (!shortArgs.contains(requireMe) && !longArgs.containsKey(requireMe)) {
        ok = false;
        message.append("Missing Flag: " + requireMe + "\n");
      }
    }

    // ensure flags that are present and require values actually have them.
    for (String longPresent : longArgs.keySet()) {
      if (requireValue.contains(longPresent) && longArgs.get(longPresent) == null) {
        ok = false;
        message.append("Missing Value: " + longPresent + " (specify using " + longPresent
            + "=VALUE)");
      }
    }
    if (requiredPositionArgs >= 0 && requiredPositionArgs > positionalArgs.size()) {
      ok = false;
      message.append("Wrong number of positional arguments. Expected " + requiredPositionArgs
          + ", received " + positionalArgs.size());
    }

    if (!ok) {
      throw new IllegalArgumentException(message.toString());
    }
  }

  private void setRequiredFlag(String f) {
    requireFlag.add(f);
  }

  private void setRequiredValue(String f) {
    requireValue.add(f);
  }

  private void setOptionalFlag(String f) {
    optionalFlag.add(f);
    setDocumentation(f, null);
  }

  private void setOptionalValue(String f) {
    optionalValue.add(f);
  }

  private void setRequiredPositionArgs(int n) {
    requiredPositionArgs = n;
  }

  private int parse(int position, String[] args) {
    String a = args[position];
    int ret = 0;
    if (a.startsWith("--")) {
      assertOK(a.length() > 2, "Empty long argument in slot " + position);
      int eq = a.indexOf('=');
      String lval = null;
      String rval = null;
      if (eq > 0) {
        lval = a.substring("--".length(), eq); // --x=y
        Arguments.assertOK(a.length() > eq + 1, "Malformed long argument: " + a);
        rval = a.substring(eq + 1);
        if (rval.startsWith("\"") && !rval.endsWith("\"")) {
          boolean complete = false;
          for (int i = position + 1; i < args.length; i++) {
            rval = rval + " " + args[i];
            if (rval.endsWith("\"")) {
              complete = true;
              ret = i - position;
              break;
            }
          }
          Arguments.assertOK(complete, "Unterminated double-quoted string beginning in slot "
              + position + ": " + a);
        }
      } else {
        lval = a.substring("--".length());
      }
      if (rval != null && rval.startsWith("\"") && rval.endsWith("\"")) {
        rval = rval.substring(1, rval.length() - 1);
      }
      longArgs.put(lval, rval);
    } else if (a.startsWith("-")) {
      assertOK(a.length() == 2, "Short argument must have one character, e.g. '-x'");
      shortArgs.add(a.substring("-".length()));
    } else {
      positionalArgs.add(a);
      if (positionalDocs.size() >= positionalArgs.size()) {
        List<String> namedPosition = positionalDocs.get(positionalArgs.size() - 1);
        longArgs.put(namedPosition.get(0), a);
      }
    }
    return ret;
  }

  private static void assertOK(boolean ok, String reason) {
    if (!ok) {
      System.out.println(reason);
      System.exit(-1);
    }
  }

  /**
   * Configure the Arguments to understand a given flag. This allows the programmer to call
   * 'validate' and ensure the user's arguments match what is expected. It also records
   * documentation that is used in getUsage() (a terse summary of the flags) and getDocumentation()
   * (which provides all available documentation).
   * 
   * @param flag
   *          the flag label, without dashes. So if you want your user to type "-h", simply provide
   *          "h". If you want "--suffix", provide "suffix".
   * @param a
   *          the argument type: either optional or required. See the validate() function.
   * @param v
   *          the value type: either optional or required. See the validate() function. It is valid
   *          and useful to have a required value for an optional argument. For example, the
   *          'username' flag could be optional, but if it is present, a value must be given.
   * @param documentation
   *          The documentation used in getDocumentation()
   * @see #validate()
   * @see #getDocumentation()
   * @see #getUsage()
   */
  public void addFlag(String flag, ArgType a, ValueType v, String documentation) {

    if (a == ArgType.ARG_OPTIONAL) {
      setOptionalFlag(flag);
    } else if (a == ArgType.ARG_REQUIRED) {
      setRequiredFlag(flag);
    }

    if (v == ValueType.VALUE_OPTIONAL) {
      setOptionalValue(flag);
    } else if (v == ValueType.VALUE_REQUIRED) {
      setRequiredValue(flag);
    }

    setDocumentation(flag, documentation);
  }

  /**
   * Configure the Arguments to understand a positional value, which is a bare string without a flag
   * in front of it.
   * 
   * @param pos
   *          The base-0 position. This number is respecitve only to other positional values. So if
   *          your arguments are "-h -l Monkey --verbose=true Salmon", position 0 is Monkey and
   *          position 1 is Salmon.
   * @param label
   *          The label can be used later to retrieve this value. For example if your http-get
   *          program expects a single argument, you can label it 'url', and retrieve it later using
   *          getValue("url").
   * @param v
   *          The value type. If required, calling 'validate' will complain if it is not present.
   * @param documentation
   *          The documentation string used in getDocumentation().
   * @see #getDocumentation()
   * @see #getUsage()
   * @see #validate()
   */
  public void addPositional(int pos, String label, ValueType v, String documentation) {
    setDocumentationPositional(pos, label, documentation);
    if (v == ValueType.VALUE_REQUIRED) {
      setRequiredPositionArgs(Math.max(pos + 1, requiredPositionArgs));
    }
  }
}
