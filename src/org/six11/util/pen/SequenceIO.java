package org.six11.util.pen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.six11.util.Debug;

/**
 * Functions related to reading and writing Sequence data to disk.
 *
 * @author Gabe Johnson <johnsogg@cmu.edu>
 */
public abstract class SequenceIO {
  
  public static String SEQ_START = "seq_start";
  public static String SEQ_END = "seq_end";
  public static String PT = "pt";
  
  public static void write(Sequence seq, Writer out) throws IOException {
    out.write(SEQ_START + "\n");
    for (Pt pt : seq) {
      out.write(PT + " " + pt.x + " " + pt.y + " " + pt.getTime() + "\n");
    }
    out.write(SEQ_END + "\n");
    out.flush();
  }
  
  public static Sequence read(BufferedReader in) throws IOException {
    Sequence ret = null;
    if (in.ready() && in.readLine().trim().equals(SEQ_START)) {
      ret = new Sequence();
      while (in.ready()) {
        String line = in.readLine().trim();
        if (line.equals(SEQ_END)) {
          break;
        } else if (line.startsWith(PT)) {
          StringTokenizer toks = new StringTokenizer(line.substring(PT.length()), " ");
          double x = Double.parseDouble(toks.nextToken());
          double y = Double.parseDouble(toks.nextToken());
          long t = Long.parseLong(toks.nextToken());
          ret.add(new Pt(x, y, t));
        } else {
          throw new IOException("File format error around '" + line + "'");
        }
      }
    }
    return ret;
  }

  public static void writeAll(List<Sequence> sequences, Writer out) throws IOException {
    for (Sequence seq : sequences) {
      write(seq, out);
    }
  }
  
  public static List<Sequence> readAll(BufferedReader in) throws IOException {
    List<Sequence> ret = new ArrayList<Sequence>();
    Sequence next = null;
    while ((next = read(in)) != null) {
      ret.add(next);
    }
    return ret;
  }
  
  public static void bug(String what) {
    Debug.out("SequenceIO", what);
  }
}
