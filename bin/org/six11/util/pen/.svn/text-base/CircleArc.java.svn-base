package org.six11.util.pen;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.six11.util.Debug;
import org.six11.util.gui.shape.ShapeFactory.ArcData;
import org.six11.util.pen.Functions;
import org.six11.util.pen.Pt;
import org.six11.util.pen.Sequence;

/**
 * 
 * 
 * @author Gabe Johnson <johnsogg@cmu.edu>
 */
public class CircleArc implements Comparable<CircleArc> {

  public static Comparator<CircleArc> comparator = new Comparator<CircleArc>() {
    public int compare(CircleArc o1, CircleArc o2) {
      return o1.compareTo(o2);
    }
  };

  double radius;

  public Pt start;
  public Pt mid;
  public Pt end;
  public Pt center; // can be null in the event of colinear input points.

  /**
   * This creates a CircleArc based on a center and a radius. CircleArcs made in this way will fail
   * if you call getArcLength() because that method requires start/mid/end points, so you have to
   * use the other constructor if you want that. (getArcLength() makes no sense for this
   * constructor.)
   */
  public CircleArc(Pt center, double radius) {
    this.center = center;
    this.radius = radius;
  }

  public CircleArc(Pt start, Pt mid, Pt end) {
    this.start = start;
    this.mid = mid;
    this.end = end;
    this.center = Functions.getCircleCenter(start, mid, end);
    if (center == null) {
      this.radius = Double.POSITIVE_INFINITY;
    } else {
      this.radius = mid.distance(center);
    }
  }

  public boolean isValid() {
    return center != null;
  }

  public boolean contains(Pt other) {
    boolean ret = false;
    if (center != null) {
      double d = center.distance(other);
      ret = Functions.lt(d, radius, Functions.EQ_TOL);
    }
    return ret;
  }

  public int compareTo(CircleArc o) {
    int ret = 0;
    if (radius < o.radius) {
      ret = -1;
    } else if (radius > o.radius) {
      ret = 1;
    }
    return ret;
  }

  public Pt getCenter() {
    return center;
  }

  public double getRadius() {
    return radius;
  }

  public double getArcLength() {
    double ret = 0;
    if (start == null || mid == null || end == null) {
      throw new RuntimeException(
          "CircleArc does not have start/mid/end points. You must use the correct constructor.");
    } else {
      ArcData data = new ArcData(start, mid, end);  
      if (data.isValid()) {
        ret = (2 * Math.PI) / Math.toRadians(Math.abs(data.extent));
      }
    }
    return ret;
  }

  public static void bug(String what) {
    Debug.out("CircleArc", what);
  }

  public static CircleArc makeBestCircle(int start, int end, Sequence seq) {
    CircleArc ret = null;
    List<CircleArc> arcs = new ArrayList<CircleArc>();
    for (int j = start + 1; j < end; j++) {
      CircleArc ca = new CircleArc(seq.get(start), seq.get(j), seq.get(end));
      if (!Double.isInfinite(ca.getRadius())) {
        arcs.add(ca);
      }
    }
    double lowestError = Double.MAX_VALUE;
    for (CircleArc arc : arcs) {
      double error = 0;
      for (int i = start; i <= end; i++) {
        error += arc.center.distance(seq.get(i));
      }
      if (error < lowestError) {
        lowestError = error;
        ret = arc;
      }
    }
    return ret;
  }
}
