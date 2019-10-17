// $Id: Holder.java 12 2009-11-09 22:58:47Z gabe.johnson $

package org.six11.util.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * A Holder is a simple component to be used in conjunction with the FrontEnd layout object. A
 * Holder contains a single sub-component (hereafter referred to as 'the component'), which is
 * centered horizontally and vertically.
 */
public class Holder extends JPanel implements LayoutManager {
  protected int vAlign;
  protected int hAlign;
  protected Component comp;
  protected boolean stretchWidth;
  protected boolean stretchHeight;
  protected int hpad;
  protected int vpad;
  protected boolean hiddenW;
  protected boolean hiddenH;
  protected boolean debug = false;

  /**
   * Same as Holder(comp, false, false, hpad, vpad, false, false);
   */
  public Holder(Component comp, int hpad, int vpad) {
    this(comp, false, false, hpad, vpad, false, false);
  }

  /**
   * Same as Holder(comp, false, false, 0, 0, false, false);
   */
  public Holder(Component comp) {
    this(comp, false, false, 0, 0, false, false);
  }

  public Holder(Component comp, boolean stretchWidth, boolean stretchHeight, int hpad, int vpad) {
    this(comp, stretchWidth, stretchHeight, hpad, vpad, true, true);
  }

  public Holder(Component comp, boolean stretchWidth, boolean stretchHeight, int hpad, int vpad,
      boolean hiddenW, boolean hiddenH) {
    setLayout(this);
    add(comp);
    this.vAlign = SwingConstants.CENTER;
    this.hAlign = SwingConstants.CENTER;
    this.comp = comp;
    this.stretchWidth = stretchWidth;
    this.stretchHeight = stretchHeight;
    this.hpad = hpad;
    this.vpad = vpad;
    this.hiddenW = hiddenW;
    this.hiddenH = hiddenH;
  }

  public boolean isComponentVisible() {
    return comp.isVisible();
  }

  public Component getComponent() {
    return comp;
  }

  public void setComponentVisible(final boolean state) {
    Runnable task = new Runnable() {
      public void run() {
        if ((state != isComponentVisible()) || (state != isVisible())) {
          setVisible(state);
          comp.setVisible(state);
          revalidate();
          repaint();
        }
      }
    };

    if (SwingUtilities.isEventDispatchThread()) {
      task.run();
    } else {
      SwingUtilities.invokeLater(task);
    }
  }

  public void setDebug(boolean debug) {
    this.debug = debug;
  }

  public void setVerticalAlign(int vAlign) {
    this.vAlign = vAlign;
  }

  public void setHorizontalAlign(int hAlign) {
    this.hAlign = hAlign;
  }

  public void layoutContainer(Container parent) {
    Dimension p = parent.getSize();

    // make a copy of the component's dimension because we are going
    // to assign to it.
    Dimension c = new Dimension(comp.getPreferredSize());
    boolean visWidth = isComponentVisible() || hiddenW;
    boolean visHeight = isComponentVisible() || hiddenH;

    if (!visWidth) {
      c.width = 0;
    }

    if (!visHeight) {
      c.height = 0;
    }

    int x;
    int y;
    int w;
    int h;

    if (stretchWidth && visWidth) {
      w = p.width - hpad;

      if (hAlign == SwingConstants.LEFT) {
        x = 0;
      } else if (hAlign == SwingConstants.RIGHT) {
        x = hpad;
      } else {
        x = hpad / 2;
      }
    } else {
      w = c.width - hpad;

      if (hAlign == SwingConstants.LEFT) {
        x = 0;
      } else if (hAlign == SwingConstants.RIGHT) {
        x = (p.width - c.width);
      } else {
        x = (p.width - c.width) / 2;
      }
    }

    if (stretchHeight && visHeight) {
      h = p.height - vpad;

      if (vAlign == SwingConstants.TOP) {
        y = 0;
      } else if (vAlign == SwingConstants.BOTTOM) {
        y = hpad;
      } else {
        y = vpad / 2;
      }
    } else {
      h = c.height;

      if (vAlign == SwingConstants.LEFT) {
        y = 0;
      } else if (vAlign == SwingConstants.RIGHT) {
        y = (p.height - c.height);
      } else {
        y = (p.height - c.height) / 2;
      }
    }

    comp.setBounds(x, y, w, h);

    if ((getName() != null) && debug) {
      StringBuffer buf = new StringBuffer();
      buf.append("Holder " + getName() + " [");
      buf.append("Pref Size: " + FrontEnd.dim(comp.getPreferredSize()) + "; ");
      buf.append("Visible: " + isComponentVisible() + "; ");
      buf.append("Stretch W, H: " + stretchWidth + ", " + stretchHeight + "; ");
      buf.append("Hidden W,H: " + hiddenW + ", " + hiddenH + "; ");
      buf.append("Bounds: " + FrontEnd.rect(comp.getBounds()));
      buf.append("]");

      // logger.log(buf.toString());
    }
  }

  public void addLayoutComponent(String name, Component comp) {
  }

  public void removeLayoutComponent(Component comp) {
  }

  /**
   * Gives you the preferred size of the component plus any horizontal and vertical padding. If the
   * component is not visible and the Holder has been instructed to not use vertical or horizontal
   * space, the vertical or horizontal dimensions will be zero. This is true even if horizontal or
   * vertical padding has been requested. Padding IS used when the component is invisible but the
   * Holder has been set to use that space anyway.
   */
  public Dimension preferredLayoutSize(Container parent) {
    return getPaddedSize(comp.getPreferredSize());
  }

  /**
   * Just like the preferred size, except it uses the component's minimum size. See the
   * preferredLayoutSize method.
   */
  public Dimension minimumLayoutSize(Container parent) {
    return getPaddedSize(comp.getMinimumSize());
  }

  protected Dimension getPaddedSize(Dimension base) {
    Dimension ret = new Dimension(base);
    boolean visWidth = isComponentVisible() || hiddenW;
    boolean visHeight = isComponentVisible() || hiddenH;

    if (!visWidth) {
      ret.width = 0;
    } else {
      ret.width += hpad;
    }

    if (!visHeight) {
      ret.height = 0;
    } else {
      ret.height += vpad;
    }

    return ret;
  }

  public String toString() {
    return "Holder " + comp.getName() + " [" + comp.getClass().getName() + "]";
  }
}
