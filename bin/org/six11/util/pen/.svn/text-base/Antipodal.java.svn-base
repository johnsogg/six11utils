// $Id$

package org.six11.util.pen;

import java.util.List;
import java.util.ArrayList;

/**
 * 
 **/
public class Antipodal {

  List<Pt> mbr; // minimum bounding rectangle

  public Antipodal(List<Pt> convexHull) {
    double bestArea = Double.MAX_VALUE;
    double d;
    List<Pt> r;
    if (convexHull.size() == 1) {
      // convex hull is a degenerate case (point): make a one-pixel box.
      mbr = new ArrayList<Pt>();
      Pt p = convexHull.get(0);
      mbr.add(new Pt(p.x - 0.5, p.y - 0.5));
      mbr.add(new Pt(p.x - 0.5, p.y + 0.5));
      mbr.add(new Pt(p.x + 0.5, p.y + 0.5));
      mbr.add(new Pt(p.x + 0.5, p.y - 0.5));
    } else if (convexHull.size() == 2) {
      // convex hull is a degenerate case (line): make it one pixel wide.
      mbr = new ArrayList<Pt>();
      Vec primeDir = new Vec(convexHull.get(0), convexHull.get(1));
      Vec offsetDir = primeDir.getNormal().getVectorOfMagnitude(0.5);
      mbr.add(offsetDir.add(convexHull.get(0)));
      mbr.add(offsetDir.add(convexHull.get(1)));
      offsetDir = offsetDir.getFlip();
      mbr.add(offsetDir.add(convexHull.get(1)));
      mbr.add(offsetDir.add(convexHull.get(0)));
    } else {
      for (int i = 0; i < convexHull.size(); i++) {
        r = findBox(i, convexHull);
        d = area(r);
        if (d < bestArea) {
          bestArea = d;
          mbr = r;
        }
      }
    }
    // just debug the best one
    // findBox(bestIdx, convexHull); // necessary?
  }

  private static double area(List<Pt> rect) {
    double distA = Functions.getDistanceBetween(rect.get(0), rect.get(1));
    double distB = Functions.getDistanceBetween(rect.get(1), rect.get(2));
    return distA * distB;
  }

  private List<Pt> findBox(int i, List<Pt> convexHull) {
    // algorithm is on page 161
    convexHull.size();
    Line a = lineAt(i, convexHull);
    Vec aVec = new Vec(a);
    Vec aVecNormal = aVec.getNormal();
    Extreme indices = findExtremePoints(a, convexHull);
    int j = indices.getAbsoluteMaxIdx();
    Line b = new Line(convexHull.get(j), aVec);
    Line perpA = new Line(convexHull.get(i), aVecNormal);
    Pt k = Functions.getIntersectionPoint(perpA, b);
    Line c = new Line(convexHull.get(i), k);
    indices = findExtremePoints(c, convexHull);
    Line d = new Line(convexHull.get(indices.lowestIdx), aVecNormal);
    Line e = new Line(convexHull.get(indices.highestIdx), aVecNormal);
    List<Pt> ix = new ArrayList<Pt>();
    ix.add(Functions.getIntersectionPoint(a, d));
    ix.add(Functions.getIntersectionPoint(a, e));
    ix.add(Functions.getIntersectionPoint(b, d));
    ix.add(Functions.getIntersectionPoint(b, e));
    List<Pt> ret = Graham.getConvexHull(ix);
    return ret;
  }

  private Extreme findExtremePoints(Line line, List<Pt> convexHull) {
    double smallestVal = Double.MAX_VALUE;
    double highestVal = -Double.MAX_VALUE;
    int smallestIdx = -1;
    int highestIdx = -1;
    double d;
    for (int i = 0; i < convexHull.size(); i++) {
      d = Functions.getSignedDistanceBetweenPointAndLine(convexHull.get(i), line);
      if (d < smallestVal) {
        smallestIdx = i;
        smallestVal = d;
      }
      if (d > highestVal) {
        highestIdx = i;
        highestVal = d;
      }
    }
    return new Extreme(smallestIdx, smallestVal, highestIdx, highestVal);
  }

  private static class Extreme {
    int lowestIdx;
    double lowestVal;
    int highestIdx;
    double highestVal;

    Extreme(int lowestIdx, double lowestVal, int highestIdx, double highestVal) {
      this.lowestIdx = lowestIdx;
      this.lowestVal = lowestVal;
      this.highestIdx = highestIdx;
      this.highestVal = highestVal;
    }

    int getAbsoluteMaxIdx() {
      return ((Math.abs(lowestVal) > Math.abs(highestVal)) ? lowestIdx : highestIdx);
    }
  }

  public List<Pt> getMinimumBoundingRect() {
    return new ArrayList<Pt>(mbr);
  }

  public double getLongDimensionLength() {
    double d1 = mbr.get(0).distance(mbr.get(1));
    double d2 = mbr.get(1).distance(mbr.get(2));
    return Math.max(d1, d2);
  }

  public double getShortDimensionLength() {
    double d1 = mbr.get(0).distance(mbr.get(1));
    double d2 = mbr.get(1).distance(mbr.get(2));
    return Math.min(d1, d2);
  }

  /**
   * This is the distance along the 'first' side of the bounding box, which is relevant if you are
   * forming an ellipse.
   */
  public double getFirstDimension() {
    return mbr.get(0).distance(mbr.get(1));
  }
  
  public double getSecondDimension() {
    return mbr.get(1).distance(mbr.get(2));
  }

  /**
   * Returns the short dimension divided by the long dimension. This will give a value in the range
   * (0, 1].
   */
  public double getAspectRatio() {
    return getShortDimensionLength() / getLongDimensionLength();
  }

  public double getArea() {
    double d1 = mbr.get(0).distance(mbr.get(1));
    double d2 = mbr.get(1).distance(mbr.get(2));
    return d1 * d2;
  }

  public Pt getCentroid() {
    double sumX = 0;
    double sumY = 0;
    for (Pt pt : mbr) {
      sumX += pt.x;
      sumY += pt.y;
    }
    return new Pt(sumX / mbr.size(), sumY / mbr.size());
  }

  public double getAngle() {
    double dx = mbr.get(1).x - mbr.get(0).x;
    double dy = mbr.get(1).y - mbr.get(0).y;
    return -Math.atan2(dy, dx);
  }

  private static Line lineAt(int i, List<Pt> points) {
    int j = inc(points, i);
    return new Line(points.get(i), points.get(j));
  }

  private static int inc(List<Pt> list, int current) {
    return inc(list.size(), current);
  }

  private static int inc(int n, int current) {
    return (current + 1) % n;
  }
}
