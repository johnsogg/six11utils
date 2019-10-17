// $Id$

package org.six11.util.gui.shape;


import org.six11.util.pen.Pt;
import org.six11.util.pen.Sequence;

/**
 * Like Cross, this is just a special Shape useful for drawing. It's
 * contribution is that it does the math of figuring out where various
 * points go, because it's ugly math.
 **/
//public class Triangle implements MovableShape {
public class Triangle extends MovableShape {

  public Triangle(double x, double y, double w) {
    super();
    double whalf = 0.5;
    double h = (Math.sqrt(3.0) / 6.0);
    double d = h * 2.0;

    Sequence seq = new Sequence();
    seq.add(new Pt(0d, 0d - d));
    seq.add(new Pt(0d - whalf, 0d + h));
    seq.add(new Pt(0d + whalf, 0d + h));
    originalGeometry.add(seq);
    translateBy(x,y);
    scaleBy(w);
  }

  public String getInitString() {
    return "Triangle";
  }
    
}
