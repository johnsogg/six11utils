package org.six11.util.pen;

import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.six11.util.Debug;
import org.six11.util.gui.ApplicationFrame;
import org.six11.util.gui.Colors;
import org.six11.util.gui.Components;
import org.six11.util.gui.Strokes;

/**
 * This is the primary sketching input/output component. It does not collect mouse/pen data on its
 * own: supply an OliveMouseThing in the calling context. It will draw two kinds of data based on
 * the OliveSoup: the current sequence (raw points) and all DrawingBuffers.
 **/
public class OliveDrawingSurface extends JComponent {

  // border and background UI variables
  private Color bgColor;
  private Color penEnabledBorderColor;
  private Color penDisabledBorderColor;
  private double borderPad;
  private boolean penEnabled = false;
  private OliveSoup soup;


  /**
   * A simple main method that shows a drawing surface.
   */
  public static void main(String[] args) {
    Debug.useColor = false;
    ApplicationFrame af = new ApplicationFrame("Olive Test GUI");
    OliveDrawingSurface ds = new OliveDrawingSurface();
    af.setLayout(new BorderLayout());
    af.add(ds, BorderLayout.CENTER);
    af.setSize(500, 400);
    af.center();
    af.setVisible(true);
  }

  /**
   * Make an Olive drawing surface, but do not show it.
   */
  public OliveDrawingSurface() {
    // establish border and background variables
    bgColor = Colors.getDefault().get(Colors.BACKGROUND);
    penEnabledBorderColor = Colors.getDefault().get(Colors.ACCENT);
    penDisabledBorderColor = Colors.getDefault().get(Colors.SELECTED_BG_INACTIVE);
    borderPad = 3.0;
    soup = new OliveSoup();
    addMouseMotionListener(soup.getMouseThing());
    addMouseListener(soup.getMouseThing());
    ChangeListener cl = new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        repaint();
      }
    };
    soup.addChangeListener(cl);
  }

  public OliveSoup getSoup() {
    return soup;
  }


  /**
   * Draws a border (the characteristic 'this is a sketching surface' visual), and the soup's
   * current sequence and all complete DrawingBuffers.
   */
  public void paintComponent(Graphics g1) {
    Graphics2D g = (Graphics2D) g1;
    AffineTransform before = new AffineTransform(g.getTransform());
    drawBorderAndBackground(g);
    if (soup != null) {
      g.setTransform(before);
      paintContent(g, true);
    }
    g.setTransform(before);
  }

  public void paintContent(Graphics2D g, boolean useCachedImages) {
    Shape currentSeq = soup.getCurrentSequenceShape(); // the in-progress scribble
    List<DrawingBuffer> buffers = soup.getDrawingBuffers(); // finished visual elements
    
    for (DrawingBuffer buffer : buffers) {
      if (buffer.isVisible() && useCachedImages) {
        buffer.paste(g);
      } else if (buffer.isVisible()) {
        buffer.drawToGraphics(g);
      }
    }
    if (currentSeq != null && soup.isCurrentSequenceShapeVisible()) {
      Components.antialias(g);
      if (soup.getPenColor() != null) {
        g.setColor(soup.getPenColor());
      } else {
        g.setColor(DrawingBuffer.getBasicPen().color);
      }
      float thick = (float) soup.getPenThickness();
      
      g.setStroke(Strokes.get(thick));
      g.draw(currentSeq);
    }
  }

  @SuppressWarnings("unused")
  private void bug(String what) {
    Debug.out("OliveDrawingSurface", what);
  }

  /**
   * Paints the background white and adds the characteristic dashed border.
   */
  private void drawBorderAndBackground(Graphics2D g) {
    Components.antialias(g);
    RoundRectangle2D rec = new RoundRectangle2D.Double(borderPad, borderPad, getWidth() - 2.0
        * borderPad, getHeight() - 2.0 * borderPad, 40, 40);
    g.setColor(bgColor);
    g.fill(rec);
    g.setStroke(Strokes.DASHED_BORDER_STROKE);
    g.setColor(penEnabled ? penEnabledBorderColor : penDisabledBorderColor);
    g.draw(rec);
  }
}
