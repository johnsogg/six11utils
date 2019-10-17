// $Id$

package org.six11.util.gui;

import java.util.Random;
import java.awt.Color;
import java.util.Map;
import java.util.HashMap;

import org.six11.util.Debug;

/**
 * This defines a set of named colors, such as "Background", "Selected", "Active", and so on. You
 * may use instances of this class to configure certain Swing components as well as any components
 * that you may write. The constants defined here all have good defaults.
 **/
public class Colors {
  public final static String BACKGROUND = "Background";
  public final static String FOREGROUND = "Foreground";
  public final static String SEPARATOR = "Separator";
  public final static String SELECTED_BG_ACTIVE = "Selected BG (Active)";
  public final static String SELECTED_BG_INACTIVE = "Selected BG (Inactive)";
  public final static String SELECTED_FG_ACTIVE = "Selected FG (Active)";
  public final static String SELECTED_FG_INACTIVE = "Selected FG (Inactive)";
  public final static String HIGHLIGHT_BG_PALE = "Highlight BG (Pale)";
  public final static String HIGHLIGHT_BG_NORMAL = "Highlight BG (Normal)";
  public final static String HIGHLIGHT_BG_BRIGHT = "Highlight BG (Bright)";
  public final static String HIGHLIGHT_FG_PALE = "Highlight FG (Pale)";
  public final static String HIGHLIGHT_FG_NORMAL = "Highlight FG (Normal)";
  public final static String HIGHLIGHT_FG_BRIGHT = "Highlight FG (Bright)";
  public final static String MESSAGE_BG_BRIGHT = "Message BG (Bright)";
  public final static String MESSAGE_BG_SUBTLE = "Message BG (Subtle)";
  public final static String MESSAGE_FG_BRIGHT = "Message FG (Bright)";
  public final static String MESSAGE_FG_SUBTLE = "Message FG (Subtle)";
  public final static String ACCENT = "Accent";

  private static Random rand;

  public static final Colors def = new Colors();
  public static final Map<String, Colors> named = new HashMap<String, Colors>();

  public final Map<String, Color> vals;

  public Colors() {
    vals = new HashMap<String, Color>();
    vals.put(BACKGROUND, new Color(255, 255, 255));
    vals.put(SEPARATOR, new Color(90, 90, 90));
    vals.put(SELECTED_BG_ACTIVE, new Color(80, 133, 253));
    vals.put(SELECTED_BG_INACTIVE, new Color(170, 177, 195));
    vals.put(SELECTED_FG_ACTIVE, new Color(255, 255, 255));
    vals.put(SELECTED_FG_INACTIVE, new Color(255, 255, 255));
    vals.put(HIGHLIGHT_BG_PALE, new Color(208, 209, 150));
    vals.put(HIGHLIGHT_BG_NORMAL, new Color(220, 221, 104));
    vals.put(HIGHLIGHT_BG_BRIGHT, new Color(247, 249, 88));
    vals.put(HIGHLIGHT_FG_PALE, new Color(0, 0, 0));
    vals.put(HIGHLIGHT_FG_NORMAL, new Color(0, 0, 0));
    vals.put(HIGHLIGHT_FG_BRIGHT, new Color(0, 0, 0));
    vals.put(MESSAGE_BG_BRIGHT, new Color(0, 255, 24));
    vals.put(MESSAGE_BG_SUBTLE, new Color(150, 209, 156));
    vals.put(MESSAGE_FG_BRIGHT, new Color(0, 0, 0));
    vals.put(MESSAGE_FG_SUBTLE, new Color(0, 0, 0));
    vals.put(ACCENT, new Color(197, 38, 38));
  }

  public static Colors getDefault() {
    return def;
  }

  /**
   * Return a color based on c, with an alpha value of a.
   * 
   * @param c
   *          a Color, maybe one of the Color.* constants.
   * @param a
   *          the alpha value in the range 0.0 (completely transparent) to 1.0 (completely opaque).
   */
  public static Color makeAlpha(Color c, float a) {
    return makeAlpha(c, (int) (255.0 * a));
  }

  /**
   * Return a color based on c, with an alpha value of a.
   * 
   * @param c
   *          a Color, maybe one of the Color.* constants.
   * @param a
   *          the alpha value in the range 0 (completely transparent) to 255 (completely opaque).
   */
  public static Color makeAlpha(Color c, int a) {
    return new Color(c.getRed(), c.getGreen(), c.getBlue(), a);
  }

  public Colors copy() {
    Colors c = new Colors();
    c.vals.putAll(vals);
    return c;
  }

  public static boolean isDark(Color c) {
    boolean ret = false;
    int a = c.getAlpha();
    if (a > 100) {
      int r = c.getRed();
      int g = c.getGreen();
      int b = c.getBlue();
      int sumSq = r * r + g * g + b * b;
      ret = (sumSq < 22500);
    }
    return ret;
  }

  public Color get(String name) {
    return vals.get(name);
  }
  
  public void set(String name, Color color) {
    vals.put(name, color);
  }

  public static Colors getNamed(String name) {
    return named.get(name);
  }

  public static void setName(String name, Colors colors) {
    named.put(name, colors);
  }

  public static Color getRandomLightColor() {
    if (rand == null) {
      rand = new Random(System.currentTimeMillis());
    }
    int r = 225 - rand.nextInt(96);
    int g = 225 - rand.nextInt(96);
    int b = 225 - rand.nextInt(96);
    return new Color(r, g, b);
  }

  /**
   * Expects a string such as "#f00", "f00", "#ff0000", or "ff0000" and returns a Color (in this
   * case, red).
   */
  public static Color decodeHtmlHexTriplet(String s) {
    int r = 0;
    int g = 0;
    int b = 0;
    String in = s.replace("#", "");
    if (in.length() == 3 || in.length() == 6) {
      int len = in.length() / 3;
      int mult = 1; // for strings like "ff0000"
      if (len == 1) {
        mult = 16; // for strings like "f00"
      }
      int idx = 0;
      r = mult * Integer.decode("0x" + in.substring(idx, idx + len));
      idx = idx + len;
      g = mult * Integer.decode("0x" + in.substring(idx, idx + len));
      idx = idx + len;
      b = mult * Integer.decode("0x" + in.substring(idx, idx + len));
    } else {
      bug("Invalid hex triplet string: " + s);
    }
    return new Color(r, g, b);
  }

  public static void bug(String what) {
    Debug.out("Colors", what);
  }
}
