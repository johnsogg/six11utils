package org.six11.util.pen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.six11.util.Debug;
import org.six11.util.gui.shape.ShapeFactory;
import org.six11.util.pen.CardinalSpline;
import org.six11.util.pen.CircleArc;
import org.six11.util.pen.Functions;
import org.six11.util.pen.Line;
import org.six11.util.pen.Pt;
import org.six11.util.pen.Sequence;

/**
 * Represents a portion of a drawn stroke that might (or might not) be a visually distinct
 * component. This implementation can represent straight lines and arcs (of circles).
 * 
 * @author Gabe Johnson <johnsogg@cmu.edu>
 */
public class Segment implements Comparable<Segment> {
  private static int ID = 1;
  int id;
  // String label;
  Pt start, end;
  int idxStart, idxEnd;
  double errorLine, errorCircle, errorSpline;
  Sequence seq;
  CircleArc bestCircle;

  SortedSet<Pt> splineControlPoints;
  List<Pt> splinePoints;

  public static enum Type {
    LINE, ARC, SPLINE
  }

  public Segment(Pt start, Pt end, Sequence seq, boolean lineOK, boolean arcOK, boolean splineOK,
      double splineErrorThresh, double lineMult, double arcMult, double splineMult) {
    this.id = ID++;
    this.start = start;
    this.end = end;
    this.seq = seq;
    this.idxStart = seq.indexOf(start);
    this.idxEnd = seq.indexOf(end);
    this.errorLine = lineOK ? calculateLineError() * lineMult : Double.POSITIVE_INFINITY;
    this.errorCircle = arcOK ? calculateCircleError() * arcMult : Double.POSITIVE_INFINITY;
    this.errorSpline = splineOK ? calculateSplineError(splineErrorThresh) * splineMult
        : Double.POSITIVE_INFINITY;
  }

  private double calculateSplineError(double splineErrorThresh) {
    double ret = 0;

    splineControlPoints = new TreeSet<Pt>();
    splineControlPoints.add(start);
    splineControlPoints.add(end);
    ret = splinify(splineErrorThresh, seq.indexOf(start), seq.indexOf(end));
    List<Pt> ctrl = new ArrayList<Pt>(splineControlPoints);
    // CardinalSpline.calculateCardinalSlopeVectors(ctrl, 1.0);
    splinePoints = CardinalSpline.interpolateCardinal(ctrl, 1.0, 2.0);
    return ret;
  }

  private double splinify(double splineErrorThresh, int a, int b) {
    double err = calculateMaxLineError(a, b);
    if (err > splineErrorThresh) {
      int idxSplit = getMostDistant(a, b);
      splineControlPoints.add(seq.get(idxSplit));
      double e1 = splinify(splineErrorThresh, a, idxSplit) * (idxSplit - a);
      double e2 = splinify(splineErrorThresh, idxSplit, b) * (b - idxSplit);
      err = (e1 + e2) / (b - a);
    } else {
      err = calculateLineError(a, b);
    }
    return err;
  }

  /**
   * Gives the index of the point in the subsequence from a to b that is farthest away from the line
   * connecting a and b.
   */
  private int getMostDistant(int a, int b) {
    int ret = a;
    double farthest = 0;
    Line line = new Line(seq.get(a), seq.get(b));
    for (int i = a; i <= b; i++) {
      Pt pt = seq.get(i);
      if (!splineControlPoints.contains(pt)) {
        double v = Functions.getDistanceBetweenPointAndLine(pt, line);
        if (farthest < v) {
          ret = i;
          farthest = v;
        }
      }
    }
    return ret;
  }

  public double getBestError() {
    return getError(getBestType());
  }

  public double getError(Type t) {
    double ret = Double.POSITIVE_INFINITY;
    switch (t) {
      case LINE:
        ret = errorLine;
        break;
      case ARC:
        ret = errorCircle;
        break;
      case SPLINE:
        ret = errorSpline;
        break;
    }
    return ret;
  }

  public Type getBestType() {
    Type ret;
    if (isProbablyLine() || (errorLine < errorCircle && errorLine < errorSpline)) {
      ret = Type.LINE;
    } else if (isProbablyArc() || (errorCircle < errorLine && errorCircle < errorSpline)) {
      ret = Type.ARC;
    } else {
      ret = Type.SPLINE;
    }
    return ret;
  }

  public boolean isProbablyLine() {
    double ideal = start.distance(end);
    double actual = seq.getPathLength(idxStart, idxEnd);
    boolean ret = ((ideal / actual) > 0.95); // this is screaming to be a parameter.
    return ret;
  }

  public boolean isProbablyArc() {
    ShapeFactory.ArcData data = new ShapeFactory.ArcData(bestCircle.start, bestCircle.mid,
        bestCircle.end);
    double ideal = Math.abs(data.radius * (data.extent / 360));
    double actual = seq.getPathLength(idxStart, idxEnd);
    boolean ret = ((ideal / actual) > 0.95); // this is screaming to be a parameter.
    return ret;
  }

  /**
   * Return the curvilinear distance of this segment.
   */
  double length() {
    return end.getDouble("curvilinear-distance") - start.getDouble("curvilinear-distance");
  }

  /**
   * Compares based on length().
   */
  public int compareTo(Segment other) {
    return orderByLength.compare(this, other);
  }

  public static Comparator<Segment> orderByLength = new Comparator<Segment>() {
    public int compare(Segment s1, Segment s2) {
      int ret = 0;
      if (s1.length() < s2.length()) {
        ret = -1;
      } else if (s1.length() > s2.length()) {
        ret = 1;
      }
      return ret;
    }
  };

  public static Comparator<Segment> orderByPoints = new Comparator<Segment>() {
    public int compare(Segment s1, Segment s2) {
      int ret = 0;
      if (s1.start.getTime() < s2.start.getTime()) {
        ret = -1;
      } else if (s1.start.getTime() > s2.start.getTime()) {
        ret = 1;
      }
      return ret;
    }
  };

  public boolean isProbablyLine(double threshold) {
    double euclideanDistance = end.distance(start);
    double curvilinearDistance = seq.getPathLength(idxStart, idxEnd);
    return (euclideanDistance / curvilinearDistance) > threshold;
  }

  private double calculateLineError() {
    return calculateLineError(idxStart, idxEnd);
  }

  /**
   * Returns the error formed by the subsequence between indices a and b, assuming a straight line
   * between them.
   */
  private double calculateLineError(int a, int b) {
    Line line = new Line(seq.get(a), seq.get(b));
    double ret = 0;
    for (int i = a; i <= b; i++) {
      double dist = Functions.getDistanceBetweenPointAndLine(seq.get(i), line);
      ret += dist * dist;
    }
    return ret / (b - a);
  }

  private double calculateMaxLineError(int a, int b) {
    Line line = new Line(seq.get(a), seq.get(b));
    double ret = 0;
    for (int i = a; i <= b; i++) {
      double dist = Functions.getDistanceBetweenPointAndLine(seq.get(i), line);
      ret = Math.max(ret, dist);
    }
    return ret;
  }

  private double calculateCircleError() {
    double errorSum = 0.0;
    List<CircleArc> arcs = new ArrayList<CircleArc>();
    for (int i = idxStart + 1; i < idxEnd; i++) {
      CircleArc ca = new CircleArc(start, seq.get(i), end);
      arcs.add(ca);
    }
    Collections.sort(arcs, CircleArc.comparator); // sort based on radius
    bestCircle = arcs.get(arcs.size() / 2); // get the arc with median radius

    if (bestCircle.center == null) {
      errorSum = Double.POSITIVE_INFINITY;
    } else {
      for (int i = idxStart; i < idxEnd; i++) {
        Pt pt = seq.get(i);
        double r1 = bestCircle.center.distance(pt);
        double d = r1 - bestCircle.getRadius();
        errorSum += d * d;
      }
    }
    return errorSum / (idxEnd - idxStart);
  }

  public String toString() {
    return "Segment " + id + " [" + idxStart + ", " + idxEnd + ": " + getBestType() + "]";
  }

  public int getId() {
    return id;
  }

  public Pt getStart() {
    return start;
  }

  public Pt getEnd() {
    return end;
  }

  public int getIdxStart() {
    return idxStart;
  }

  public int getIdxEnd() {
    return idxEnd;
  }

  public double getErrorLine() {
    return errorLine;
  }

  public double getErrorCircle() {
    return errorCircle;
  }

  public double getErrorSpline() {
    return errorSpline;
  }

  public Sequence getSeq() {
    return seq;
  }

  public CircleArc getBestCircle() {
    return bestCircle;
  }

  public SortedSet<Pt> getSplineControlPoints() {
    return splineControlPoints;
  }

  public List<Pt> getSplinePoints() {
    return splinePoints;
  }

  @SuppressWarnings("unused")
  private static void bug(String what) {
    Debug.out("Segment", what);
  }
}