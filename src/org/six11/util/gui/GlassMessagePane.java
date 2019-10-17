// $Id: GlassMessagePane.java 12 2009-11-09 22:58:47Z gabe.johnson $

package org.six11.util.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.six11.util.Debug;
import org.six11.util.pen.Pt;

/**
 * A message pane intended to be used as a top level container's glass pane. Just set the message
 * with the setMessage( ) function, and it will give you a hovering message on top of all your other
 * components, without having to mess around with JDialogs.
 * 
 * @author Gabe Johnson <johnsogg@cmu.edu>
 */
public class GlassMessagePane extends JComponent {

  private int padding;
  private String message;
  private Color borderColor;
  private Stroke borderStroke;

  public static void main(String[] args) {
    Debug.useColor = false;
    ApplicationFrame af = new ApplicationFrame("GlassMessagePane Test");
    af.setSize(new Dimension(750, 400));
    JButton[] buttons = new JButton[36];
    JPanel content = new JPanel();
    content.setLayout(new GridLayout(6, 6));
    final GlassMessagePane giganto = new GlassMessagePane();
    ActionListener off = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        giganto.clear();
      }
    };
    final Timer timer = new Timer(1000, off);
    timer.setRepeats(false);
    ActionListener on = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        giganto.setMessage("It looks like you clicked on " + e.getActionCommand());
        timer.start();
      }
    };

    for (int i = 0; i < 36; i++) {
      buttons[i] = new JButton("Button " + (i + 1));
      content.add(buttons[i]);
      buttons[i].addActionListener(on);
    }
    af.add(content);

    af.setGlassPane(giganto);
    af.center();
    af.setVisible(true);
  }

  /**
   * Builds a GlassMessagePane with some default visual properties that can be overriden with
   * the setters. By default it uses a bold 28 point Dialog font, black text with a thin black
   * border, and a semi-transparent, reddish background.
   */
  public GlassMessagePane() {
    message = null;
    padding = 20;
    borderColor = Color.BLACK;
    borderStroke = Strokes.THIN_STROKE;
    setForeground(Color.BLACK);
    setBackground(new Color(255, 0, 0, 128));
    setFont(new Font("Dialog", Font.BOLD, 28));
  }

  public void paintComponent(Graphics g1) {
    if (message == null) {
      return;
    }
    Graphics2D g = (Graphics2D) g1;
    Font font = getFont();
    FontRenderContext frc = g.getFontRenderContext();
    TextLayout layout = new TextLayout(message, font, frc);
    Rectangle2D bounds = layout.getBounds();

    Pt center = Components.getCenter(this);
    Pt start = new Pt(center.x - (bounds.getWidth() / 2.0), center.y + (bounds.getHeight() / 2.0));
    bounds.setRect(center.x - ((bounds.getWidth() + padding) / 2.0), center.y
        - ((bounds.getHeight() + padding) / 2.0), bounds.getWidth() + padding, bounds.getHeight()
        + padding);
    g.setColor(getBackground());
    g.fill(bounds);
    g.setColor(getForeground());
    layout.draw(g, (float) start.getX(), (float) start.getY());
    g.setStroke(borderStroke);
    g.setColor(borderColor);
    g.draw(bounds);
  }

  @SuppressWarnings("unused")
  private static void bug(String what) {
    Debug.out("GlassMessagePane", what);
  }

  public int getPadding() {
    return padding;
  }

  public void setPadding(int padding) {
    this.padding = padding;
    repaint();
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
    setVisible(message != null);
    repaint();
  }

  public void clear() {
    setMessage(null);
  }

  public Color getBorderColor() {
    return borderColor;
  }

  public void setBorderColor(Color borderColor) {
    this.borderColor = borderColor;
  }

  public Stroke getBorderStroke() {
    return borderStroke;
  }

  public void setBorderStroke(Stroke borderStroke) {
    this.borderStroke = borderStroke;
  }

}
