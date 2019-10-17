// $Id$

package org.six11.util.data;

/**
 * 
 * 
 * @author Gabe Johnson <johnsogg@cmu.edu>
 */
public abstract class TextUtil {

  public static class IntPair {
    public int line;
    public int col;

    public IntPair(int a, int b) {
      this.line = a;
      this.col = b;
    }
  }

  public static IntPair getLineAndColumn(String text, int dot) {
    int lineNum = 1;
    int colNum = 0;
    if (text.length() >= dot) {
      for (int i=0; i < dot; i++) {
        char c = text.charAt(i);
        if (c == '\n') {
          lineNum++;
          colNum = 0;
        } else {
          colNum++;
        }
      }
    } else {
      System.out.println("Sorry. This should have text.length() < dot, but " + text.length()
          + " >= " + dot);
    }
    return new IntPair(lineNum, colNum);
  }
}
