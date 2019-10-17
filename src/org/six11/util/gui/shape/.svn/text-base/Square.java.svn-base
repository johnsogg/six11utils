// $Id$

package org.six11.util.gui.shape;

import org.six11.util.Debug;
import org.six11.util.pen.Pt;
import org.six11.util.pen.Sequence;

public class Square extends MovableShape {
  //  protected int permahash;

  public Square(double x, double y, double s) {
    super();
    double half = 0.5;
    Pt a = new Pt(0.0 - half, 0.0 - half);
    Pt b = new Pt(a.getX() + 1d, a.getY());
    Pt c = new Pt(a.getX() + 1d, a.getY() + 1d);
    Pt d = new Pt(a.getX(), a.getY() + 1d);
    Sequence seq = new Sequence();
    seq.add(a);
    seq.add(b);
    seq.add(c);
    seq.add(d);
    //    seq.add(a);
    originalGeometry.add(seq);
    //    permahash = super.hashCode();
    translateBy(x,y);
    scaleBy(s);
  }

//   public int hashCode() {
//     return permahash;
//   }

//   public boolean equals(Object other) {
//     return other == this;
//   }

  public String getInitString() {
    return "Square " + Debug.num(getBounds2D());
  }
  
}
