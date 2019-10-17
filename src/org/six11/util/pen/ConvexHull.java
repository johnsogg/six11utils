// $Id: ConvexHull.java 192 2011-11-20 20:46:52Z gabe.johnson@gmail.com $

package org.six11.util.pen;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

/**
 * 
 **/
public class ConvexHull {

  protected List<Pt> input;
  protected List<Pt> points;
  protected List<Pt> pointsClosed;
  protected List<Pt> rotatedRect;
  protected double rotatedRectArea;
  protected Pt convexCentroid;
  protected double convexArea;
  protected GeneralPath hullShape;
  protected boolean dirty = true;

  public ConvexHull(List<Pt> input) {
    reset();
    this.input = input;
    calculate();
  }

  public ConvexHull() {
    this.input = new ArrayList<Pt>();
  }

  public final void calculate() {
    if (dirty) {
      points = Functions.getConvexHull(input);
      dirty = false;
    }
  }

  private final void reset() {
    pointsClosed = null;
    rotatedRect = null;
    rotatedRectArea = -1.0;
    convexCentroid = null;
    convexArea = -1.0;
    hullShape = null;
    dirty = true;
  }

  public void addPoints(Pt... pts) {
    reset();
    for (Pt pt : pts) {
      input.add(pt);
    }
  }

  public void addPoints(Collection<Pt> pts) {
    reset();
    for (Pt pt : pts) {
      input.add(pt);
    }
  }

  public List<Pt> getHull() {
    calculate(); 
    return points;
  }

  public Shape getHullShape() {
    calculate();
    if (hullShape == null) {
      hullShape = new GeneralPath();
      for (int i = 0; i < points.size(); i++) {
        Pt pt = points.get(i);
        if (i == 0) {
          hullShape.moveTo(pt.getX(), pt.getY());
        } else {
          hullShape.lineTo(pt.getX(), pt.getY());
        }
      }
    }
    return hullShape;
  }

  /**
   * Returns the convex hull with the first and last points doubled up, to facilitate easier
   * drawing.
   */
  public List<Pt> getHullClosed() {
    calculate();
    if (pointsClosed == null) {
      pointsClosed = new ArrayList<Pt>(points);
      pointsClosed.add(points.get(0));
    }
    return pointsClosed;
  }

  public List<Pt> getRotatedRect() {
    calculate();
    if (rotatedRect == null) {
      // Antipodal anti = new Antipodal(points);
      Antipodal anti = new Antipodal(points);
      rotatedRect = anti.getMinimumBoundingRect();
    }
    return rotatedRect;
  }

  public double getRotatedRectArea() {
    calculate();
    if (rotatedRectArea < 0.0) {
      List<Pt> rect = getRotatedRect();
      double distA = Functions.getDistanceBetween(rect.get(0), rect.get(1));
      double distB = Functions.getDistanceBetween(rect.get(1), rect.get(2));
      rotatedRectArea = distA * distB;
    }
    return rotatedRectArea;
  }

  public Pt getConvexCentroid() {
    calculate();
    if (convexCentroid == null) {
      calcCentroidAndArea();
    }
    return convexCentroid;
  }

  public double getConvexArea() {
    calculate();
    if (convexArea < 0.0) {
      calcCentroidAndArea();
    }
    return convexArea;
  }

  private void calcCentroidAndArea() {
    calculate();
    List<Pt> rect = getRotatedRect();
    Pt m = Functions.getMean(rect); // the mean of the rect.
    List<Pt> c = new ArrayList<Pt>(); // triangle centroids
    List<Double> a = new ArrayList<Double>(); // triangle areas
    int n = points.size();
    Pt p1, p2;
    for (int i = 0; i < n; i++) {
      p1 = points.get(i);
      p2 = points.get(next(i, n));
      c.add(Functions.getMean(p1, p2, m));
      Vec v1 = new Vec(p1, m);
      Vec v2 = new Vec(p2, m);
      a.add(Math.abs(Functions.getDeterminant(v1, v2)));
    }
    convexArea = Functions.getSum(a);
    if (convexArea == 0.0) {
      convexArea = 1.0;
      convexCentroid = m;
    } else {
      double sumX = 0.0;
      double sumY = 0.0;
      for (int i = 0; i < n; i++) {
        Pt scaledC = (c.get(i).getScaled(a.get(i)));
        sumX = sumX + scaledC.getX();
        sumY = sumY + scaledC.getY();
      }
      double cX = sumX / convexArea;
      double cY = sumY / convexArea;
      convexCentroid = new Pt(cX, cY);
    }
  }

  private static int next(int cur, int upperBound) {
    return (cur + 1) % upperBound;
  }

}
