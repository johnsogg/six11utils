// $Id: Cross.java 23 2009-11-24 21:09:08Z gabe.johnson $

package org.six11.util.gui.shape;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.FlatteningPathIterator;

import org.six11.util.gui.GenericPathIterator;
import org.six11.util.pen.Pt;
import org.six11.util.pen.Sequence;

import java.util.List;
import java.util.ArrayList;

/**
 * A simple shape used to put a cross (or, an X, but I didn't want a class named X) on the screen
 * somewhere.
 **/
public class Cross extends MovableShape {

  double w, x, y;
  Pt tl, tr, bl, br;

  public Cross(double x, double y, double w) {
    this.w = w;
    moveTo(x, y);
  }

  public List<Sequence> getGeometry() {
    List<Sequence> ret = new ArrayList<Sequence>();
    Sequence seq1 = new Sequence();
    Sequence seq2 = new Sequence();
    seq1.add(tl);
    seq1.add(br);
    seq2.add(tr);
    seq2.add(bl);
    ret.add(seq1);
    ret.add(seq2);
    return ret;
  }

  public void moveTo(double x, double y) {
    this.x = x;
    this.y = y;
    double whalf = w / 2.0;
    tl = new Pt(x - whalf, y - whalf);
    tr = new Pt(x + whalf, y - whalf);
    bl = new Pt(x - whalf, y + whalf);
    br = new Pt(x + whalf, y + whalf);
  }

  public void moveBy(double dx, double dy) {
    moveTo(x + dx, y + dy);
  }

  public String getInitString() {
    double w = tr.getX() - tl.getX();
    return "Cross(" + (tl.getX() + (w / 2.0)) + ", " + (tl.getY() + (w / 2.0)) + ", " + w + ")";
  }

  public Rectangle getBounds() {
    Rectangle2D twodee = getBounds2D();
    return new Rectangle((int) twodee.getX(), (int) twodee.getY(), (int) twodee.getWidth(),
        (int) twodee.getHeight());
  }

  public Rectangle2D getBounds2D() {
    Rectangle2D ret = new Rectangle2D.Double();
    ret.add(tl);
    ret.add(tr);
    ret.add(bl);
    ret.add(br);

    return ret;
  }

  public boolean contains(double x, double y) {
    return false;
  }

  public boolean contains(Point2D p) {
    return false;
  }

  public boolean intersects(double x, double y, double w, double h) {
    return false;
  }

  public boolean intersects(Rectangle2D rec) {
    return false;
  }

  public boolean contains(double x, double y, double w, double h) {
    return false;
  }

  public boolean contains(Rectangle2D rec) {
    return false;
  }

  public PathIterator getPathIterator(AffineTransform affine) {
    GenericPathIterator gpi = new GenericPathIterator();
    gpi.add(PathIterator.SEG_MOVETO, tl);
    gpi.add(PathIterator.SEG_LINETO, br);
    gpi.add(PathIterator.SEG_MOVETO, tr);
    gpi.add(PathIterator.SEG_LINETO, bl);
    return gpi;
  }

  public PathIterator getPathIterator(AffineTransform affine, double flatness) {
    return new FlatteningPathIterator(getPathIterator(affine), flatness);
  }

}
