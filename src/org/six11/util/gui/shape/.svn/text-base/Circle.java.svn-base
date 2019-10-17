// $Id$

package org.six11.util.gui.shape;

import java.awt.geom.Ellipse2D;

import org.six11.util.pen.Pt;
import org.six11.util.pen.Sequence;

import java.util.List;
import java.util.ArrayList;

/**
 * This is just a very thin veneer over Ellipse2D.Double. "Circle"
 * just seems nicer to work with than "Ellipse2D.Double". Call me
 * crazy.
 **/
public class Circle extends Ellipse2D.Double {

  protected int permahash;
  protected double mid_x, mid_y, r;

  /**
   * Calls the constructor Ellipse2D.Double(x, y, 2r ,2r).
   */
  public Circle(double x, double y, double r) {
    super(x - r, y - r, r*2, r*2);
    permahash = super.hashCode();
    this.mid_x = x;
    this.mid_y = y;
    this.r = r;
  }

  public Circle(Pt pt, double r) {
    this(pt.getX(), pt.getY(), r);
  }
  
  public double getRadius() {
    return r;
  }
  
  public Pt getCenter() {
    return new Pt(mid_x, mid_y);
  }

  public List<Sequence> getGeometry() {
    // as you can see this is kinda bogus.
    List<Sequence> ret = new ArrayList<Sequence>();
    Sequence seq = new Sequence();
    seq.add(new Pt(mid_x, mid_y));
    seq.add(new Pt(mid_x+r, mid_y));
    ret.add(seq);
    return ret;
  }

  public int hashCode() {
    return permahash;
  }

  public void moveBy(double dx, double dy) {
    setFrame(getX() + dx, getY() + dy, getWidth(), getHeight());
  }

  public void moveTo(double x, double y) {
    double wh = getWidth() / 2.0;
    setFrame(x - wh, y - wh, getWidth(), getHeight());
  }

  public String getInitString() {
    return "Circle(" + mid_x + ", " + mid_y + ", " + r + ")";
  }

}
