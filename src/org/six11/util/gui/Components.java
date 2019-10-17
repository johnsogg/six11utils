// $Id: Components.java 295 2012-05-14 00:24:03Z gabe.johnson@gmail.com $

package org.six11.util.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

import org.six11.util.pen.Pt;

/**
 * Miscellaneous static functions for getting information about and doing things tocomponents and
 * the GUI environment.
 **/
public class Components {
  public static Pt getCenter(Component c) {
    Dimension d = c.getSize();
    return new Pt(d.getWidth() / 2.0, d.getHeight() / 2.0);
  }

  /**
   * Sets KEY_INTERPOLATION to VALUE_INTERPOLATION_BICUBIC
   */
  public static void interpolate(Graphics2D g) {
    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, //
        RenderingHints.VALUE_INTERPOLATION_BICUBIC);
  }

  /**
   * Sets KEY_RENDERING to VALUE_RENDER_QUALITY. The default value is apparently VALUE_RENDER_CRAP.
   */
  public static void quality(Graphics2D g) {
    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
  }

  /**
   * @param g
   *          Sets KEY_ANTIALIASING to VALUE_ANTIALIAS_ON.
   */
  public static void antialias(Graphics2D g) {
    g.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON));
  }

  public static void centerComponent(Component c) {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = c.getSize();
    int x = (screenSize.width - frameSize.width) / 2;
    int y = (screenSize.height - frameSize.height) / 2;
    c.setLocation(x, y);
  }

  /**
   * Returns the upper left corner location that the given dimension should be if it would be placed
   * onscreen in the center.
   */
  public static Point2D centerRectangle(Dimension dim) {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    double x = ((screenSize.width - dim.getWidth()) / 2);
    double y = ((screenSize.height - dim.getHeight()) / 2);
    return new Point2D.Double(x, y);
  }

  public static void attachKeyboardAccelerators(JRootPane rp, Map<String, Action> actions) {
    for (Action action : actions.values()) {
      KeyStroke s = (KeyStroke) action.getValue(Action.ACCELERATOR_KEY);
      if (s != null) {
        rp.registerKeyboardAction(action, s, JComponent.WHEN_IN_FOCUSED_WINDOW);
      }
    }
  }
  
  public static Graphics2D getHeadlessGraphics() {
    BufferedImage im = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
    return im.createGraphics();
  }

}
