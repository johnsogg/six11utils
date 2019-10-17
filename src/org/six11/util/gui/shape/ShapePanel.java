// $Id: ShapePanel.java 12 2009-11-09 22:58:47Z gabe.johnson $

package org.six11.util.gui.shape;

import java.util.List;
import java.util.ArrayList;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import javax.swing.JPanel;

import org.six11.util.Debug;
import org.six11.util.gui.shape.MovableShape;

/**
 * 
 **/
public class ShapePanel extends JPanel {
  
  List<MovableShape> shapes;
  
  public ShapePanel() {
    shapes = new ArrayList<MovableShape>();
  }

  public void addShape(MovableShape s) {
    shapes.add(s);
  }

  public void paintComponent(Graphics g1) {
    Graphics2D g = (Graphics2D) g1;
    g.setColor(Color.BLUE);
    g.fill(getBounds());
    for (MovableShape s : shapes) {
      Debug.out("ShapePanel", "drawing shape: " + s);
      g.setColor(Color.RED);
      g.fill(s);
      g.setColor(Color.BLACK);
      g.draw(s);
    }
  }
}
