// $Id: MovableShape.java 57 2010-02-23 05:03:37Z gabe.johnson@gmail.com $

package org.six11.util.gui.shape;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.awt.geom.PathIterator;
import java.awt.geom.FlatteningPathIterator;
import java.util.List;
import java.util.ArrayList;
import org.six11.util.gui.BoundingBox;
import org.six11.util.gui.GenericPathIterator;
import org.six11.util.pen.Functions;
import org.six11.util.pen.Pt;
import org.six11.util.pen.Sequence;

/**
 * 
 **/
public abstract class MovableShape implements Shape {

  protected List<Sequence> originalGeometry;
  protected List<Sequence> transformedGeometry;
  protected boolean dirty;
  protected AffineTransform lastGoodTransform;

  protected double scale;
  protected double rotate;
  protected double translateX, translateY;

  public MovableShape() {
    originalGeometry = new ArrayList<Sequence>();
    transformedGeometry = new ArrayList<Sequence>();
    resetTransform();
  }

  public void resetTransform() {
    scale = 1.0;
    rotate = 0.0;
    translateX = 0.0;
    translateY = 0.0;
    dirty = true;
  }

  public void rotateBy(double radians) {
    rotate += radians;
    dirty = true;
  }

  public void rotateTo(double radians) {
    rotate = radians;
    dirty = true;
  }

  public void scaleBy(double amt) {
    scale += amt;
    dirty = true;
  }

  public void scaleTo(double amt) {
    scale = amt;
    dirty = true;
  }

  public void translateBy(double x, double y) {
    translateX += x;
    translateY += y;
    dirty = true;
  }

  public void translateTo(double x, double y) {
    translateX = x;
    translateY = y;
    dirty = true;
  }

  public AffineTransform getTransform() {

    if (dirty) {
      AffineTransform result = new AffineTransform();
      result.concatenate(AffineTransform.getTranslateInstance(translateX, translateY));
      result.concatenate(AffineTransform.getScaleInstance(scale, scale));
      result.concatenate(AffineTransform.getRotateInstance(rotate));
      lastGoodTransform = result;
    }
    return lastGoodTransform;
  }

  public abstract String getInitString();

  public String toString() {
    return getInitString();
  }

  public List<Sequence> getGeometry() {
    if (dirty) {
      transformedGeometry.clear();
      Pt pt2;
      AffineTransform transform = getTransform();
      for (Sequence in : originalGeometry) {
        Sequence out = new Sequence();
        for (Pt pt : in) {
          pt2 = new Pt();
          transform.transform(pt, pt2);
          out.add(pt2);
        }
        transformedGeometry.add(out);
      }
      dirty = false;
    }
    return transformedGeometry;
  }

  public Rectangle getBounds() {
    Rectangle2D twodee = getBounds2D();
    return new Rectangle((int) twodee.getX(), (int) twodee.getY(), (int) twodee.getWidth(),
        (int) twodee.getHeight());
  }

  public Rectangle2D getBounds2D() {
    BoundingBox bb = new BoundingBox();
    for (Sequence seq : getGeometry()) {
      for (Pt pt : seq) {
        bb.add(pt);
      }
    }
    return bb.getRectangle();
  }

  public boolean contains(double x, double y) {
    int numCrossings = Functions.getCrossingNumberSequences(new Pt(x, y), getGeometry());
    return ((numCrossings % 2) == 1);
  }

  public boolean contains(Point2D p) {
    return contains(p.getX(), p.getY());
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
    List<Sequence> geometry = getGeometry();
    for (Sequence seq : geometry) {
      Pt first = null;
      for (Pt pt : seq) {
        if (first == null) {
          gpi.add(PathIterator.SEG_MOVETO, pt);
          first = pt;
        } else {
          gpi.add(PathIterator.SEG_LINETO, pt);
        }
      }
      if (seq.size() > 2) {
        gpi.add(PathIterator.SEG_LINETO, first);
      }
    }
    return gpi;
  }

  public PathIterator getPathIterator(AffineTransform affine, double flatness) {
    return new FlatteningPathIterator(getPathIterator(affine), flatness);
  }

}
