// $Id: PieChart.java 23 2009-11-24 21:09:08Z gabe.johnson $

package org.six11.util.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Arc2D;

import javax.swing.JComponent;
import javax.swing.Timer;

import org.six11.util.Debug;
import org.six11.util.gui.shape.Circle;
import org.six11.util.pen.Pt;

/**
 * A visual timer in the style of a pie chart.
 * 
 * @author Gabe Johnson <johnsogg@cmu.edu>
 */
public class PieChart extends JComponent {

  private Color fullCircleColor;
  private Color arcColor;
  private Color borderColor;
  private boolean drawInnerBorder;
  private double angle;
  private Timer timer;
  private ActionListener tick;
  private long totalTime;

  private transient long startTime;
  private transient long endTime;

  /**
   * @param args
   */
  public static void main(String[] args) {
    Debug.useColor = false;
    PieChart pie = new PieChart();
    pie.setPreferredSize(new Dimension(300, 300));
    ApplicationFrame f = new ApplicationFrame("PieChart Test");
    f.setSize(400, 400);
    f.add(pie);
    f.center();
    f.setVisible(true);
    pie.setTotalTime(6000);
    pie.startTimer();
  }

  public PieChart() {
    super();
    fullCircleColor = Color.GREEN;
    arcColor = Color.RED;
    borderColor = Color.BLACK;
    drawInnerBorder = true;

    tick = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        long now = System.currentTimeMillis();
        double percent = ((double) (now - startTime)) / ((double) totalTime);
        if (percent >= 1.0) {
          pauseTimer();
        } else {
          setAngleDegrees(360.0 * percent);
          repaint();
        }
      }
    };
    timer = new Timer((int) (1000d / 60d), tick);
  }

  public void startTimer() {
    startTime = System.currentTimeMillis();
    endTime = startTime + totalTime;
    timer.start();
  }

  public void resetTimer() {
    timer.stop();
    angle = 0.0;
    repaint();
  }

  public void pauseTimer() {
    timer.stop();
    repaint();
  }

  public void paintComponent(Graphics g1) {
    // make sure component isn't too small.
    Graphics2D g = (Graphics2D) g1;
    int size = Math.min(getWidth(), getHeight()) - 8;
    if (size < 10)
      return;

    // set up housekeeping vars and graphics environment
    Components.antialias(g);
    double sizeD = (double) size;
    Pt center = Components.getCenter(this);

    // draw the full circle
    Circle c = new Circle(center.x, center.y, (double) size);
    g.setColor(isTimeElapsed() ? arcColor : fullCircleColor);
    g.fill(c);

    // draw the partial circle
    if (isArcVisible()) {
      Arc2D.Double arc = new Arc2D.Double();
      arc.setArc(center.x - sizeD / 2.0, center.y - sizeD / 2.0, sizeD, sizeD, 0.0, angle,
          Arc2D.PIE);
      g.setColor(arcColor);
      g.fill(arc);

      // maybe draw the border of the inside arc
      if (!isTimeElapsed() && drawInnerBorder) {
        g.setStroke(Strokes.THIN_STROKE);
        g.setColor(borderColor);
        g.draw(arc);
      }
    }

    // draw the border around the full circle
    g.setColor(borderColor);
    g.setStroke(Strokes.BOLD_STROKE);
    g.draw(c);
  }

  public boolean isArcVisible() {
    return angle > 0.01;
  }

  public boolean isTimeElapsed() {
    return System.currentTimeMillis() > endTime;
  }

  public Color getFullCircleColor() {
    return fullCircleColor;
  }

  public void setFullCircleColor(Color fullCircleColor) {
    this.fullCircleColor = fullCircleColor;
  }

  public Color getArcColor() {
    return arcColor;
  }

  public void setArcColor(Color arcColor) {
    this.arcColor = arcColor;
  }

  public Color getBorderColor() {
    return borderColor;
  }

  public void setBorderColor(Color borderColor) {
    this.borderColor = borderColor;
  }

  public boolean isDrawInnerBorderSet() {
    return drawInnerBorder;
  }

  public void setDrawInnerBorder(boolean drawInnerBorder) {
    this.drawInnerBorder = drawInnerBorder;
  }

  public double getAngleDegrees() {
    return angle;
  }

  public void setAngleDegrees(double angleDegrees) {
    if (angleDegrees < 0.0 || angleDegrees > 360.0) {
      throw new IllegalArgumentException("Pie chart angle must be in [0..360]");
    }
    this.angle = angleDegrees;
  }

  public double getTotalTime() {
    return totalTime;
  }

  public void setTotalTime(long totalTime) {
    this.totalTime = totalTime;
    resetTimer();
  }

}
