// $Id: Debug.java 291 2012-03-25 00:15:36Z gabe.johnson@gmail.com $

package org.six11.util;

import static org.six11.util.Debug.bug;

import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;

import org.six11.util.pen.Pt;
import org.six11.util.pen.Sequence;
import org.six11.util.pen.Vec;

import java.nio.FloatBuffer;

/**
 * Crazy-useful debugging class. It will colorize the name of the debugging client class, remember
 * how much space to leave for those fancy colored names. In future versions, this will do your
 * laundry (sorry Boober).
 **/
public class Debug {

  public enum Direction {
    Left, Right
  }

  public static final String BLACK = "\033[0;30m"; // Black
  public static final String DARK_GRAY = "\033[1;30m"; // Dark Gray
  public static final String RED = "\033[0;31m"; // Red
  public static final String LIGHT_RED = "\033[1;31m"; // Light Red
  public static final String GREEN = "\033[0;32m"; // Green
  public static final String LIGHT_GREEN = "\033[1;32m"; // Light Green
  public static final String BROWN = "\033[0;33m"; // Brown
  public static final String YELLOW = "\033[1;33m"; // Yellow
  public static final String BLUE = "\033[0;34m"; // Blue
  public static final String LIGHT_BLUE = "\033[1;34m"; // Light Blue
  public static final String PURPLE = "\033[0;35m"; // Purple
  public static final String LIGHT_PURPLE = "\033[1;35m"; // Light Purple
  public static final String CYAN = "\033[0;36m"; // Cyan
  public static final String LIGHT_CYAN = "\033[1;36m"; // Light Cyan
  public static final String LIGHT_GRAY = "\033[0;37m"; // Light Gray
  public static final String WHITE = "\033[1;37m"; // White
  public static final String NEUTRAL = "\033[0m"; // Neutral

  public static final String[] goodColors = new String[] {
      LIGHT_RED, LIGHT_CYAN, YELLOW, LIGHT_BLUE, LIGHT_PURPLE, LIGHT_GREEN, WHITE
  };

  public static boolean useColor = true;
  public static boolean useTime = true;
  public static boolean enabled = true;

  public static PrintStream outputStream = System.out;
  private static final Map<String, String> colorbag = new HashMap<String, String>();
  private static int colorCounter = 0;
  private static NumberFormat df = new DecimalFormat("0.0#");
  private static Map<Integer, NumberFormat> dfs = new HashMap<Integer, NumberFormat>();
  private static Map<Integer, String> dfFormatStrings = new HashMap<Integer, String>();
  private static Map<String, FileWriter> logFiles = new HashMap<String, FileWriter>();

  static int largestWho = 12;
  static StringBuffer buf;

  private static String getColor(String who) {
    String ret = null;
    if (colorbag.containsKey(who)) {
      ret = colorbag.get(who);
    } else {
      ret = goodColors[colorCounter];
      colorCounter = (colorCounter + 1) % goodColors.length;
      colorbag.put(who, ret);
    }
    return ret;
  }

  public static void setDecimalOutputFormat(String formatString) {
    df = new DecimalFormat(formatString);
  }

  public static NumberFormat getNumberFormat(int numSigFigs) {
    if (!dfs.containsKey(numSigFigs)) {
      String format = getFormatString(numSigFigs);
      NumberFormat f = new DecimalFormat(format);
      dfs.put(numSigFigs, f);
    }
    return dfs.get(numSigFigs);
  }

  public static String getFormatString(int numSigFigs) {
    String format = dfFormatStrings.get(numSigFigs);
    if (format == null) {
      StringBuilder buf = new StringBuilder("0.0");
      for (int i = 1; i < numSigFigs; i++) {
        buf.append("#");
      }
      format = buf.toString();
      dfFormatStrings.put(numSigFigs, format);
    }
    return format;
  }

  public static void stacktrace(String message, int levelsIn) {
    stacktraceIf(null, message, levelsIn);
  }

  public static void stacktraceIf(String filter, String message, int levelsIn) {
    // we will ignore the stacktrace element for this method because
    // it is of no use to the user (Debug.stacktrace)
    int levels = levelsIn + 1;
    Throwable t = new Throwable();
    StackTraceElement[] elms = t.getStackTrace();
    StackTraceElement elm;
    int stop = Math.min(elms.length, levels);
    boolean hasIt = true;
    if (filter != null) {
      hasIt = false;
      for (int i = 1; i < stop; i++) {
        elm = elms[i];
        hasIt = (elm.getFileName().contains(filter) || elm.getClassName().contains(filter) || elm
            .getMethodName().contains(filter));
        if (hasIt) {
          break;
        }
      }
    }
    if (hasIt) {
      Debug.out("<< stacktrace >>", message);
      for (int i = 2; i < stop; i++) {
        elm = elms[i];
        Debug.out("<< stacktrace >>", "  " + elm.getFileName() + ":" + elm.getLineNumber() + " "
            + elm.getClassName() + "#" + elm.getMethodName());
      }
    }
  }

  public static String spaces(int n) {
    String ret = "";
    for (int i = 0; i < n; i++) {
      ret += " ";
    }
    return ret;
  }

  public static String pad(int width, String what, Direction dir) {
    String ret = "";
    if (what.length() == width) { // input exact length, just return it.
      ret = what;
    } else if (what.length() > width) { // input too long, just truncate it.
      ret = what.substring(0, width);
    } else { // input too short. pad on left or right with spaces.
      int numSpaces = width - what.length();
      String sp = spaces(numSpaces);
      switch (dir) {
        case Left:
          ret = sp + what;
          break;
        case Right:
          ret = what + sp;
          break;
      }
    }
    return ret;
  }

  /**
   * Return a String that represents the current time in a human-readable format:
   */
  public static String now() {
    Date currentDate = new Date();
    return DateFormat.getTimeInstance(DateFormat.MEDIUM).format(currentDate).toString();
  }

  public static String nowFilenameFriendly() {
    Date currentDate = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("MMM-dd-yyyy");
    return sdf.format(currentDate);
  }

  public static String num(double[] d) {
    StringBuffer buf = new StringBuffer("[");
    for (int i = 0; i < d.length; i++) {
      buf.append(num(d[i]));
      if (i < (d.length - 1)) {
        buf.append(", ");
      }
    }
    buf.append("]");
    return buf.toString();
  }

  public static String num(double d) {
    if (Double.isInfinite(d)) {
      return "+inf";
    } else if (d == Double.MAX_VALUE) {
      return "+inf";
    } else if (Double.isNaN(d)) {
      return "NaN";
    } else {
      return df.format(d);
    }
  }

  public static String num(double d, int decimalPlaces) {
    StringBuffer b = new StringBuffer();
    b.append("0.");
    for (int i = 1; i < decimalPlaces; i++) {
      b.append("0");
    }
    b.append("#");
    NumberFormat manyDecimals = new DecimalFormat(b.toString());
    return manyDecimals.format(d);
  }

  public static String numTime(Pt pt) {
    if (pt == null)
      return "null";
    return "(" + Debug.num(pt.getX()) + ", " + Debug.num(pt.getY()) + ", " + pt.getTime() + ")";
  }

  public static String num(Pt pt) {
    if (pt == null)
      return "null";
    return "(" + Debug.num(pt.getX()) + ", " + Debug.num(pt.getY()) + ")";
  }

  public static String num(Pt pt, int digits) {
    NumberFormat f = getNumberFormat(digits);
    return "(" + f.format(pt.getX()) + ", " + f.format(pt.getY()) + ")";
  }

  public static String num(Point2D p2) {
    if (p2 == null)
      return "null";
    return "(" + Debug.num(p2.getX()) + ", " + Debug.num(p2.getY()) + ")";
  }

  public static String num(Vec vec) {
    if (vec == null) {
      return "null";
    } else {
      return "(" + Debug.num(vec.getX()) + ", " + Debug.num(vec.getY()) + ")";
    }
  }

  public static String num(Dimension d) {
    if (d == null)
      return "null";
    return d.getWidth() + "x" + d.getHeight();
  }

  public static String num(Rectangle2D r) {
    if (r == null)
      return "null";
    return "(" + Debug.num(r.getX()) + ", " + Debug.num(r.getY()) + " - " + Debug.num(r.getWidth())
        + "x" + Debug.num(r.getHeight()) + ")";
  }

  public static String num(Line2D line) {
    if (line == null)
      return "null";
    return "Line from " + Debug.num(line.getP1()) + " to " + num(line.getP2());
  }

  public static String num(Sequence seq) {
    StringBuffer buf = new StringBuffer("Sequence w/ " + seq.size() + " points: [");
    for (Pt pt : seq) {
      buf.append(num(pt) + " ");
    }
    buf.append("]");
    return buf.toString();
  }

  public static String num(int[] vals) {
    boolean first = true;
    StringBuffer buf = new StringBuffer();
    for (Integer i : vals) {
      buf.append((!first ? ", " : "") + i);
      first = false;
    }
    return "[" + buf.toString() + "]";
  }

  public static String num(String[] strings, String separator) {
    List<Object> l = new ArrayList<Object>();
    for (String s : strings) {
      l.add(s);
    }
    return num(l, separator);
  }

  public static String num(Map<?, ?> map) {
    StringBuilder buf = new StringBuilder();
    buf.append("{");
    for (Map.Entry<?, ?> entry : map.entrySet()) {
      buf.append(" [" + entry.getKey());
      buf.append(" = ");
      buf.append(entry.getValue() + "] ");
    }
    buf.append(" }");
    return buf.toString();
  }

  public static String num(Collection<?> col, String separator) {
    boolean first = true;
    StringBuffer buf = new StringBuffer();
    for (Object o : /* set */col) {
      buf.append(!first ? separator : "");
      first = false;
      buf.append(o.toString());
    }
    return buf.toString();
  }

  public static String num(Object[] objects, String separator) {
    boolean first = true;
    StringBuffer buf = new StringBuffer();
    for (Object o : objects) {
      buf.append(!first ? separator : "");
      first = false;
      buf.append(o.toString());
    }
    return buf.toString();
  }

  public static String num(FloatBuffer fbuf, int size) {
    StringBuffer buf = new StringBuffer("[");
    for (int i = 0; i < size; i++) {
      buf.append(num(fbuf.get(i)));
      if (i < (size - 1)) {
        buf.append(", ");
      }
    }
    buf.append("]");
    return buf.toString();
  }

  public static String num(float[][] vals) {
    StringBuffer buf = new StringBuffer();
    for (int i = 0; i < vals.length; i++) {
      buf.append("[" + num(vals[i]) + "]\n");
    }
    return buf.toString();
  }

  public static String num(float[] vals) {
    StringBuffer buf = new StringBuffer("[");
    for (int i = 0; i < vals.length; i++) {
      buf.append(num(vals[i]));
      if (i < (vals.length - 1)) {
        buf.append(", ");
      }
    }
    buf.append("]");
    return buf.toString();
  }

  public static void findNull(String who, Object... args) {
    boolean anythingNull = false;
    StringBuffer buf = new StringBuffer("findNull: indexes (starting from 1) that are null: ");
    for (int i = 0; i < args.length; i++) {
      if (args[i] == null) {
        buf.append("" + (i + 1));
        anythingNull = true;
      }
    }
    if (anythingNull) {
      out(who, buf.toString());
    } else {
      out(who, "findNull: Nothing in the list was null");
    }
  }

  public static void initLog(String fileName, boolean append, String optionalComment) {
    try {
      FileWriter logFile = new FileWriter(fileName, append);
      if (optionalComment != null && optionalComment.length() > 0) {
        logFile.write("# " + optionalComment + "\n\n");
        logFile.flush();
      }
      logFiles.put(fileName, logFile);
    } catch (IOException ex) {
      Debug.log("Debug", "Unable to open file '" + fileName + "' for writing.");
    }
  }

  public static void log(String fileName, String what) {
    FileWriter logFile = logFiles.get(fileName);
    if (logFile == null) {
      initLog(fileName, true, " -- log started " + (new Date()));
      Debug.log(fileName, what);
    } else {
      try {
        logFile.write(what + "\n");
        logFile.flush();
      } catch (IOException ex) {
        Debug.log("Debug", "Unable to write to file '" + fileName + "'.");
      }
    }
  }

  public static void bug(String what) {
    StackTraceElement[] stack = Thread.currentThread().getStackTrace();
    String who = stack[2].getClassName();
    if (who.lastIndexOf('.') >= 0) {
      who = who.substring(who.lastIndexOf('.') + 1);
    }
    int METHOD_NAME_LENGTH = 12;
    String meth = stack[2].getMethodName();
    if (meth.length() > METHOD_NAME_LENGTH) {
      meth = pad(METHOD_NAME_LENGTH - 3, stack[2].getMethodName(), Direction.Right) + "...";
    }
    meth = meth + "()";
    String lineNum = pad(4, "" + stack[2].getLineNumber(), Direction.Right);
    out(who, "Line " + lineNum + "\t" + meth + "\t" + what);
  }

  public static void out(String who, String what) {
    if (enabled) {
      if (who.length() > largestWho) {
        largestWho = who.length();
      }
      buf = new StringBuffer();

      int divider = (largestWho - who.length());
      for (int i = 0; i < largestWho; i++) {
        if (i < divider) {
          buf.append(" ");
        } else {
          buf.append(who.charAt(i - divider));
        }
      }

      String spaces = buf.toString();
      String colorStart = "";
      String colorEnd = "";
      String time = "";
      if (useColor) {
        colorStart = getColor(who);
        colorEnd = NEUTRAL;
      }
      if (useTime) {
        time = " (" + now() + ") ";
      }
      outputStream.println(colorStart + spaces + ": " + colorEnd + time + what);
    }
  }

  public static void out(Collection<?> collection, boolean indent, boolean showNumbers) {
    int count = 0;
    for (Object item : collection) {
      System.out.println((indent ? "\t" : "") + (showNumbers ? count + ": " : "") + item);
    }
  }

  /**
   * 
   */
  public static void dumpStack(String who, String msg) {
    out(who, "Intentional stacktrace output below:");
    new RuntimeException(msg).printStackTrace();
  }

  public static void detectNaN(double v) {
    detectNaN(v, "Discovered NaN value");
  }

  public static void detectNaN(double v, String msg) {
    if (Double.isNaN(v) || Double.isInfinite(v)) {
      throw new RuntimeException(msg);
    }
  }

  public static void warn(Object source, String what) {
    out(source.getClass().getSimpleName(), "*** warning *** " + what);
  }

  public static String getPathIteratorDebug(PathIterator pi) {
    StringBuilder buf = new StringBuilder();
    float[] vals = new float[6];
    while (!pi.isDone()) {
      int type = pi.currentSegment(vals);
      String which = "?";
      switch (type) {
        case PathIterator.SEG_CLOSE:
          which = "close";
          break;
        case PathIterator.SEG_CUBICTO:
          which = "cubic";
          break;
        case PathIterator.SEG_LINETO:
          which = "line";
          break;
        case PathIterator.SEG_MOVETO:
          which = "move";
          break;
        case PathIterator.SEG_QUADTO:
          which = "quad";
          break;
      }
      buf.append(which + " ");
      pi.next();
    }
    return buf.toString();
  }

  public static void errorOnNull(Object o, String name) {
    if (o == null) {
      error(name + ": null");
    }
  }
  
  public static void error(String complaint) {
    Toolkit.getDefaultToolkit().beep();
    System.out.println(" -------------------------------------------- ERROR! -------------------------------------------- ");
    stacktrace(complaint, 10);
  }
}
