package org.six11.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class MakePlotCommand {

  /**
   * @param args
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {
    String inFile = args[0];
    BufferedReader reader = new BufferedReader(new FileReader(inFile));
    String firstLine = reader.readLine();
    StringTokenizer toks = new StringTokenizer(firstLine);
    // tokens in order are:
    //   1: iteration
    //   2: total error
    //   3: heat
    // and the rest are in groups of three, one for each constraint:
    //   4: type
    //   5: num points involved
    //   6: constraint error
    String iteration = toks.nextToken();
    String totalError = toks.nextToken();
    String heat = toks.nextToken();
    List<String> types = new ArrayList<String>();
    List<String> num = new ArrayList<String>();
    List<String> error = new ArrayList<String>();
    while (toks.hasMoreTokens()) {
      System.out.println("Getting next constraint...");
      String s;
      s = toks.nextToken();
      System.out.println("   Type: " + s);
      types.add(s);
      s = toks.nextToken();
      System.out.println("    Num: " + s);
      num.add(s);
      s = toks.nextToken();
      System.out.println("  Error: " + s);
      error.add(s);
    }
    System.out.println(iteration + " " + totalError + " " + heat + " " + types.size()
        + " constraints");
    StringBuilder buf = new StringBuilder();
//    plot "./constraint-solver-3.txt" using 1:6 with lines, "./constraint-solver-3.txt" using 1:9 with lines, "./constraint-solver-3.txt" using 1:12 with lines, "./constraint-solver-3.txt" using 1:2 title "total error" with linespoints
    String qfile = "\"" + args[0] + "\"";
    buf.append("plot " + qfile + " using 1:2 title \"total error\" with linespoints");
    for (int i=0; i < types.size(); i++) {
      int startCol = 4 + (i * 3);
      int errCol = startCol + 2;
      String title = types.get(i);
      buf.append(", " + qfile + " using 1:" + errCol + " title \"" + title + "\" with lines");
    }
    System.out.println("Plot command:\n\n\t" + buf.toString());
  }

}
