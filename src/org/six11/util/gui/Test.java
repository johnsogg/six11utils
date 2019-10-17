// $Id: Test.java 12 2009-11-09 22:58:47Z gabe.johnson $

package org.six11.util.gui;

import org.six11.util.gui.shape.MovableShape;
import org.six11.util.gui.shape.ShapePanel;
import org.six11.util.gui.shape.Square;
import org.six11.util.gui.shape.Triangle;
/**
 * 
 **/
public class Test {
  
  public static void main(String[] args) {
    ApplicationFrame appFrame = new ApplicationFrame("six11 util gui test", 400, 400);
    ShapePanel shapePanel = new ShapePanel();

    double rotateStep = Math.PI / 100d;
    double rotation = 0d;
    for (int i=0; i < 10; i++) {
      double xcoord = ((double) i) * 40d;
      for (int j=0; j < 10; j++) {
	double ycoord = ((double) j) * 40d;
	MovableShape ms;
	if ((i % 2) == 0) {
	  ms = new Triangle(xcoord, ycoord, 20d);
	} else {
	  ms = new Square(xcoord, ycoord, 20d);
	}
	ms.rotateBy(rotation);
	rotation = rotation + rotateStep;
	shapePanel.addShape(ms);
      }
    }
    appFrame.add(shapePanel);
    appFrame.setVisible(true);
  }
}
