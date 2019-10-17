// $Id: Functions.java 290 2012-03-22 20:18:34Z gabe.johnson@gmail.com $

package org.six11.util.pen;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import static java.lang.Math.*;

import static org.six11.util.Debug.bug;
import static org.six11.util.Debug.num;
import org.six11.util.data.Statistics;
import org.six11.util.math.EllipseFit;

/**
 * This is a whole slew of static methods that operate on points, lines, vectors, and point
 * sequences.
 **/
public abstract class Functions {

  public static double HARD_TURN_THRESHOLD = 0.30;
  public static double EQ_TOL = 0.00001;

  public static final int PARTITION_LEFT = -1;
  public static final int PARTITION_ON_BORDER = 0;
  public static final int PARTITION_RIGHT = 1;

  /**
   * Given a source sequence, find the locations along the path defined by a destination sequence
   * that is closest to the destination. Each point in src is matched with a point along the path
   * defined by dst. For example, a source point (0,0) is nearest to the path from (0, 1) to (1, 0)
   * at about (0.707, 0.707). The return sequence contains each 'nearest path' point such as (0.707,
   * 0.707) in this example. Each point in src maps to the point in the return value with the same
   * index.
   */
  public static List<Pt> getSequenceTweenTarget(List<Pt> src, List<Pt> dst) {
    List<Pt> ret = new ArrayList<Pt>();
    for (int i = 0; i < src.size(); i++) {
      Pt ptA = src.get(i);
      Pt ptB = getNearestPointOnSequence(ptA, dst);
      int idxB = dst.indexOf(ptB);
      Pt ptC = (idxB > 0) ? dst.get(idxB - 1) : null;
      Pt ptD = (idxB < dst.size() - 1) ? dst.get(idxB + 1) : null;
      Pt leftPt = null;
      Pt rightPt = null;
      if (ptC != null) {
        leftPt = getNearestPointWithinSegment(ptA, new Line(ptC, ptB));
      }
      if (ptD != null) {
        rightPt = getNearestPointWithinSegment(ptA, new Line(ptB, ptD));
      }
      Pt candidate = ptB;
      double distToCandidate = ptA.distance(candidate);
      if (leftPt != null) {
        double d = ptA.distance(leftPt);
        if (d < distToCandidate) {
          candidate = leftPt;
          distToCandidate = ptA.distance(candidate);
        }
      }
      if (rightPt != null) {
        double d = ptA.distance(rightPt);
        if (d < distToCandidate) {
          candidate = rightPt;
        }
      }
      if (candidate != null) {
        ret.add(candidate);
      } else {
        bug("Failage at point " + i + ". Points a, b, c, d: " + ptA + ", " + ptB + ", " + ptC
            + ", " + ptD);
      }

    }
    return ret;
  }

  /**
   * Given a point and a line segment, find the location on the segment that is closets to the
   * point. If that location is outside the boundary of the segment, this returns null. Otherwise it
   * returns a point somewhere in the range between the start and end locations of the segment.
   */
  public static Pt getNearestPointWithinSegment(Pt pt, Line segment) {
    return getNearestPointWithinSegment(pt, segment, false);
  }

  public static Pt getNearestPointWithinSegment(Pt pt, Line segment, boolean allowEndpoints) {
    Pt ret = null;
    Pt ixResult;
    ixResult = getNearestPointOnLine(pt, segment, true);
    double r = ixResult.getDouble("r");
    if (r >= 0 && r <= 1) {
      ret = ixResult;
    } else if (allowEndpoints) {
      if (r < 0) {
        ret = segment.a;
      } else {
        ret = segment.b;
      }
    }
    return ret;
  }

  public static double getDistanceBetween(Pt a, Pt b) {
    double dx = b.getX() - a.getX();
    double dy = b.getY() - a.getY();
    return Math.hypot(dx, dy);
  }

  public static Pt getPointBetween(Pt a, Pt b) {
    return new Pt((a.getX() + b.getX()) / 2.0, (a.getY() + b.getY()) / 2.0);
  }

  public static double getSlope(Pt a, Pt b) {
    double dy = b.getY() - a.getY();
    double dx = b.getX() - a.getX();
    if (dx == 0.0) {
      return Double.MAX_VALUE;
    } else {
      return dy / dx;
    }
  }

  public static boolean eq(Pt a, Pt b, double tolerance) {
    double xOk = Math.abs(a.getX() - b.getX());
    double yOk = Math.abs(a.getY() - b.getY());
    return (xOk < tolerance && yOk < tolerance);
  }

  public static boolean eq(double a, double b, double tolerance) {
    return (Math.abs(a - b) < tolerance);
  }

  /**
   * Tells you if a > b, and that the difference between them is larger than a tolerance value.
   */
  public static boolean gt(double a, double b, double tolerance) {
    return (a > b && !eq(a, b, tolerance));
  }

  /**
   * Tells you if a < b, and that the difference between them is larger than a tolerance value.
   */
  public static boolean lt(double a, double b, double tolerance) {
    return (a < b && !eq(a, b, tolerance));
  }

  public static Pt getMean(Pt... points) {
    List<Pt> list = new ArrayList<Pt>();
    for (Pt pt : points) {
      list.add(pt);
    }
    return getMean(list);
  }

  /**
   * This returns the centroid of a point cloud.
   */
  public static Pt getMean(Collection<Pt> points) {
    double sumX = 0.0;
    double sumY = 0.0;
    for (Pt pt : points) {
      sumX = sumX + pt.getX();
      sumY = sumY + pt.getY();
    }
    double n = (double) points.size();
    return new Pt(sumX / n, sumY / n);
  }

  public static Pt rotatePointAboutPivot(Pt point, Pt pivot, double radians) {
    Pt ret = new Pt();
    AffineTransform matrix = getRotationInstance(pivot, radians);
    matrix.transform(point, ret);
    return ret;
  }

  public static Sequence rotatePointsAboutPivot(Sequence points, Pt pivot, double radians) {
    Sequence ret = new Sequence();
    AffineTransform matrix = getRotationInstance(pivot, radians);
    Pt transformed;
    for (Pt pt : points) {
      transformed = new Pt();
      matrix.transform(pt, transformed);
      ret.add(transformed);
    }
    return ret;
  }

  public static AffineTransform getRotationInstance(Pt pivot, double radians) {
    // note that this is equivalent to the function
    // AffineTransform.getRotateInstance(angle, x, y). Seeing this in
    // action is instructive.

    // Say you have a pivot point and you want to transform other
    // points about it. Draw it on the whiteboard. Think about how you
    // may do it.

    // To set up a transformation matrix, do the following:
    // Step 1: translate everything so the pivot is at the origin.
    // Step 2: rotate everything by the proper angle.
    // Step 3: translate things back the opposite amount as step 1.

    // After you have a transformation matrix, you can apply that
    // matrix on every point and it will do the right thing. The
    // matrix is independent of the data it is transforming.

    // So when you actually write the code to do this, you have to
    // ==REVERSE== the steps you took on the whiteboard. The reason
    // for this is that the matrices are being multiplied together,
    // and due to the fact that matrix multiplication isn't
    // commutative (A*B isn't the same as B*A). It turns out that the
    // operation you want to happen first actually has to be pushed
    // onto the stack (meaning, multiplied onto the matrix) at the
    // end. This is confusing, but true.
    AffineTransform matrix = new AffineTransform();
    matrix.translate(pivot.getX(), pivot.getY()); // step 3
    matrix.rotate(radians); // step 2
    matrix.translate(-pivot.getX(), -pivot.getY()); // step 1
    return matrix;
  }

  public static Pt getEndPoint(Pt pt, Vec vec) {
    return new Pt(pt.getX() + vec.getX(), pt.getY() + vec.getY());
  }

  /**
   * Given some sequence of points (or subsequence) return the average vector that represents the
   * change in position.
   */
  public static Vec getAverageChangeVector(Sequence seq, int start, int end) {
    Pt prev = null;
    Vec vec;
    double xComp = 0d;
    double yComp = 0d;
    for (Pt pt : seq.from(start, end)) {
      if (prev != null) {
        vec = new Vec(prev, pt);
        xComp += vec.getX();
        yComp += vec.getY();
      }
      prev = pt;
    }
    int n = end - start;
    double aveX = xComp / n;
    double aveY = yComp / n;
    return new Vec(aveX, aveY);
  }

  public static double getPathLength(List<Pt> points, int idxStartInclusive, int idxEndInclusive) {
    double ret = 0.0;
    Pt prev = null;
    for (int i = idxStartInclusive; i <= idxEndInclusive; i++) {
      Pt pt = points.get(i);
      if (prev != null) {
        ret += prev.distance(pt);
      }
      prev = pt;
    }
    return ret;
  }

  /**
   * This interprets the line segment as a finite length, and returns the minimum distance between
   * the input point and any point on the segment.
   */
  public static double getDistanceBetweenPointAndSegment(Pt pt, Line seg) {
    double ret = 0;
    Pt a = seg.getStart();
    Pt b = seg.getEnd();
    Vec ab = new Vec(a, b);
    Vec ac = new Vec(a, pt);
    double mag = ab.mag();
    double r = Functions.getDotProduct(ac, ab) / (mag * mag);
    if (r < 0) {
      ret = pt.distance(a);
    } else if (r > 1) {
      ret = pt.distance(b);
    } else {
      ret = getDistanceBetweenPointAndLine(pt, seg);
    }
    return ret;
  }

  public static double getDistanceBetweenPointAndLine(Pt pt, Line line) {
    return line.ptLineDist(pt);
  }

  // public static Pt getNearestPoint(Pt pt, Sequence seq) {
  // double dist = Double.MAX_VALUE;
  // Pt nearest = null;
  // for (Pt s : seq) {
  // double thisDist = s.distance(pt);
  // if (thisDist < dist) {
  // nearest = s;
  // dist = thisDist;
  // }
  // }
  // return nearest;
  // }

  public static double getSignedDistanceBetweenPointAndLine(Pt pt, Line line) {
    double dist = getDistanceBetweenPointAndLine(pt, line);
    Vec lineVec = new Vec(line);
    Vec ptVec = new Vec(line.getStart(), pt);
    double det = getDeterminant(lineVec, ptVec);
    if (det < 0.0) {
      dist = -1.0 * dist;
    }
    return dist;
  }

  public static double getHausdorffDistance(Rectangle2D p, Rectangle2D q) {
    return Hausdorff.getHausdorffDistance(p, q);
  }

  public static Pt getRectangleCenter(Rectangle2D r) {
    double x = r.getX() + (r.getWidth() / 2.0);
    double y = r.getY() + (r.getHeight() / 2.0);
    return new Pt(x, y);
  }

  public static double getRectangleArea(Rectangle2D r) {
    return r.getWidth() * r.getHeight();
  }

  public static double getDistanceToCenter(Rectangle2D r, Pt p) {
    Pt mid = Functions.getRectangleCenter(r);
    return mid.distance(p);
  }

  /**
   * Just like the three-param version with retainrR = false.
   */
  public static Pt getNearestPointOnLine(Pt c, Line line) {
    return getNearestPointOnLine(c, line, false);
  }

  /**
   * Starting from point c, this returns the nearest point on the provided line. The resulting point
   * might be between the start and end point of the given line, or it might be on an extension of
   * the line segment. Sometimes it is useful to know where the resulting point is in relation to
   * the start/end points of the provided line. To gain access to this, set the retainR parameter to
   * true, and access the return value's 'r' value:
   * 
   * Pt pt = getNearestPointOnLine(c, line, true); // ensure the point's "r" double value is set.
   * double r = pt.getDouble("r");
   */
  public static Pt getNearestPointOnLine(Pt c, Line line, boolean retainR) {
    // Assuming line is AB and the provided point is C, the nearest
    // point on the line P is found with a parameter r:
    //
    // @@@@ AC dot AB
    // r = -----------
    // @@@@ mag(AB)
    // 
    // if r == 0, then P == A
    // if r == 1, then P == B
    // for other r, P is somewhere along the line AB:
    // Px = Ax + r(Bx-Ax)
    // Py = Ay + r(By-Ay)
    Pt a = line.getStart();
    Pt b = line.getEnd();
    Vec ab = new Vec(a, b);
    Vec ac = new Vec(a, c);
    double mag = ab.mag();
    double r = Functions.getDotProduct(ac, ab) / (mag * mag);
    double x = a.getX() + r * (b.getX() - a.getX());
    double y = a.getY() + r * (b.getY() - a.getY());
    long t = a.getTime() + (long) (r * (b.getTime() - a.getTime()));
    Pt ret = new Pt(x, y, t);
    if (retainR) {
      ret.setDouble("r", r);
    }
    return ret;
  }

  // int See if p is left(-1)/on(0)/right(1) of a Line D int getPartition(Pt, Line)
  public static int getPartition(Pt pt, Line line) {
    return getPartition(pt, line.getStart(), line.getEnd());
  }

  public static int getPartition(Pt pt, Pt lineA, Pt lineB) {
    // translate both by 'lineA'
    Vec vecToPt = new Vec(pt, lineA);
    Vec vecForLine = new Vec(lineB, lineA);
    double det = getDeterminant(vecForLine, vecToPt);
    int ret = PARTITION_ON_BORDER;
    if (det < 0) {
      ret = PARTITION_LEFT;
    } else if (det > 0) {
      ret = PARTITION_RIGHT;
    }
    return ret;
  }

  public static Pt getCircleCenter(Pt a, Pt b, Pt c) {
    double ax = a.getX();
    double ay = a.getY();
    double bx = b.getX();
    double by = b.getY();
    double cx = c.getX();
    double cy = c.getY();

    double A = bx - ax;
    double B = by - ay;
    double C = cx - ax;
    double D = cy - ay;

    double E = A * (ax + bx) + B * (ay + by);
    double F = C * (ax + cx) + D * (ay + cy);

    double G = 2 * (A * (cy - by) - B * (cx - bx));
    if (G == 0.0)
      return null; // a, b, c must be collinear

    double px = (D * E - B * F) / G;
    double py = (A * F - C * E) / G;
    return new Pt(px, py);
  }

  /**
   * Gives you the curvature at a point. It computes the midpoint of the circle that is defined by
   * the three points (if such a circle exists; if the three points are collinear, there is no
   * circle). The curvature returned is 0 for a straight line. For nonstraight samples, the
   * curvature is signed. If you were to walk in a straight line from point a to point c, b will
   * either be on your left (negative curvature) or on your right.
   */
  public static double getCurvature(Pt a, Pt b, Pt c) {
    Pt mid = getCircleCenter(a, b, c);
    double ret = 0.0;
    if (mid != null) {
      double dist = mid.distance(b);
      ret = 1 / dist;
    }
    // signededness depends on orientation of b compared to vector ac
    Vec ab = new Vec(a, b);
    Vec ac = new Vec(a, c);
    double angle = Functions.getAngleBetween(ab, ac);
    if (angle < 0) {
      ret = ret * -1.0;
    }
    return ret;
  }

  public static Vec getVectorNormal(Vec vec) {
    return vec.getNormal();
  }

  public static Vec getScaledVector(Vec vec, double scaleFactor) {
    double x = vec.getX() * scaleFactor;
    double y = vec.getY() * scaleFactor;
    return new Vec(x, y);
  }

  public static Vec getVectorOfMagnitude(Vec vec, double desiredMag) {
    double current = vec.mag();
    double scaleFactor = desiredMag / current;
    return Functions.getScaledVector(vec, scaleFactor);
  }

  public static double getAngleBetween(Line a, Line b) {
    Vec va = getLineVector(a);
    Vec vb = getLineVector(b);
    return getAngleBetween(va, vb);
  }

  /**
   * Gets the angle between two vectors by using arc tangent (atan2). Since arc tangent is an
   * error-prone method of calculating angle, use this with caution. Consider using
   * getSignedAngleBetween(a, b) and taking the absolute value if needed.
   * 
   * @deprecated
   */
  public static double getAngleBetween(Vec a, Vec b) {
    double a1 = Math.atan2(b.getY(), b.getX());
    double a2 = Math.atan2(a.getY(), a.getX());
    double ret = a1 - a2;
    return ret;
  }

  // public static double getAngleBetweenFullcircle(Vec a, Vec b) {
  // double ret = 0.0;
  // double numerator = Functions.getDotProduct(a, b);
  // double denom = a.mag() * b.mag();
  // if (denom != 0.0) {
  // double cos = numerator / denom;
  // double det = Functions.getDeterminant(a, b);
  // double ang = Math.acos(cos);
  // if (det > 0.0) {
  // ang = Math.PI + ang;
  // }
  // ret = ang;
  // }
  // return ret;
  // }

  /**
   * This uses dot and cross products to determine the signed angle. It does not rely on arc
   * tangent, which presents problems due to discontinuities.
   */
  public static double getSignedAngleBetween(Vec a, Vec b) {
    double dot = Functions.getDotProduct(a, b);
    double cross = Functions.getCrossProduct(a, b);
    double angle = acos(dot / ((a.mag() * b.mag())));
    double curvature = angle;
    if (cross < 0) {
      curvature = -angle;
    }
    return curvature;
  }

  public static double getDotProduct(Vec a, Vec b) {
    return a.getX() * b.getX() + a.getY() * b.getY();
  }

  public static double getCrossProduct(Vec a, Vec b) {
    return (a.getX() * b.getY()) - (a.getY() * b.getX()); // notice this is the same as
    // getDeterminant
  }

  public static double getDeterminant(Vec a, Vec b) {
    // det(ab, cd) = a*d - b*c;
    return a.getX() * b.getY() - a.getY() * b.getX();
  }

  public static double getDeterminant(Pt ptA, Pt ptB, Pt ptC, Pt ptD) {
    // Forms a determinant from the matrix:
    // [ Ax Ay (Ax^2 + Ay^2) 1 ]
    // [ Bx By (Bx^2 + By^2) 1 ]
    // [ Cx Cy (Cx^2 + Cy^2) 1 ]
    // [ Dx Dy (Dx^2 + Dy^2) 1 ]
    double dx2 = ptD.x * ptD.x;
    double dy2 = ptD.y * ptD.y;
    double a = ptA.x - ptD.x;
    double b = ptA.y - ptD.y;
    double c = ((ptA.x * ptA.x - dx2) + (ptA.y * ptA.y - dy2));
    double d = ptB.x - ptD.x;
    double e = ptB.y - ptD.y;
    double f = ((ptB.x * ptB.x - dx2) + (ptB.y * ptB.y - dy2));
    double g = ptC.x - ptD.x;
    double h = ptC.y - ptD.y;
    double i = ((ptC.x * ptC.x - dx2) + (ptC.y * ptC.y - dy2));
    double term1 = a * e * i;
    double term2 = a * f * h;
    double term3 = b * f * g;
    double term4 = b * d * i;
    double term5 = c * d * h;
    double term6 = c * e * g;
    double ret = term1 - term2 + term3 - term4 + term5 - term6;
    return ret;
  }

  public static Vec getLineVector(Line line) {
    double dx = 0.0;
    double dy = 0.0;
    if (line.isValid()) {
      dx = line.getEnd().getX() - line.getStart().getX();
      dy = line.getEnd().getY() - line.getStart().getY();
    }
    return new Vec(dx, dy);
  }

  public static IntersectionData getIntersectionData(Line a, Line b) {
    return new IntersectionData(a, b);
  }

  public static Pt getIntersectionPoint(CircleArc arc0, CircleArc arc1) {
    Pt ret = null;
    Pt[] ix = getIntersectionPoints(arc0, arc1);
    if (ix != null) {
      double dist0 = Math.min(ix[0].distance(arc0.start), ix[1].distance(arc0.end));
      double dist1 = Math.min(ix[0].distance(arc1.start), ix[1].distance(arc1.end));
      ret = dist0 < dist1 ? ix[0] : ix[1];
    }
    return ret;
  }

  public static Pt[] getIntersectionPoints(CircleArc arc0, CircleArc arc1) {
    // This code was taken from http://local.wasp.uwa.edu.au/~pbourke/geometry/2circle/tvoght.c
    // Modified to use Java syntax, comments reformatted. Original C code by Tim Voght.
    Pt[] ret = null;
    double x0 = arc0.getCenter().x;
    double y0 = arc0.getCenter().y;
    double r0 = arc0.getRadius();
    double x1 = arc1.getCenter().x;
    double y1 = arc1.getCenter().y;
    double r1 = arc1.getRadius();
    double a, dx, dy, d, h, rx, ry;
    double x2, y2;

    dx = x1 - x0; // dx and dy are the vertical and horizontal
    dy = y1 - y0; // distances between the circle centers.

    d = Math.hypot(dx, dy); // Determine the straight-line distance between the centers

    if (d > (r0 + r1)) { // Check for solvability.
      // no solution. circles do not intersect.
    } else if (d < Math.abs(r0 - r1)) {
      // no solution. one circle is contained in the other
    }

    // 'point 2' is the point where the line through the circle intersection
    // points crosses the line between the circle centers.

    // Determine the distance from point 0 to point 2.
    a = ((r0 * r0) - (r1 * r1) + (d * d)) / (2.0 * d);

    x2 = x0 + (dx * a / d); // Determine the coordinates of point 2.
    y2 = y0 + (dy * a / d);

    // Determine the distance from point 2 to either of the intersection points.
    h = Math.sqrt((r0 * r0) - (a * a));

    // Now determine the offsets of the intersection points from point 2.
    rx = -dy * (h / d);
    ry = dx * (h / d);

    Pt p3 = new Pt(x2 + rx, y2 + ry); // Determine the absolute intersection points p3 and p4.
    Pt p4 = new Pt(x2 - rx, y2 - ry);
    ret = new Pt[] {
        p3, p4
    };
    return ret;
  }

  public static Pt getIntersectionPoint(CircleArc arc, Line line) {
    Pt ret = null;
    Pt[] points = getIntersectionPoints(arc, line);
    if (points != null) {
      double distA = Math.min(points[0].distance(line.getP1()), points[0].distance(line.getP2()));
      double distB = Math.min(points[1].distance(line.getP1()), points[1].distance(line.getP2()));
      ret = distA < distB ? points[0] : points[1];
    }
    return ret;
  }

  public static Pt[] getIntersectionPoints(CircleArc arc, Line line) {
    Pt[] ret = null;
    double x1, y1, x2, y2, r, r2, dx, dy, dr, dr2, det, disc, sign, radical, absdy;
    // translate the circle center to the origin, and everything else by the same amount.
    double tx = arc.getCenter().x;
    double ty = arc.getCenter().y;
    x1 = line.x1 - tx;
    y1 = line.y1 - ty;
    x2 = line.x2 - tx;
    y2 = line.y2 - ty;
    dx = x2 - x1;
    dy = y2 - y1;
    dr = Math.sqrt(dx * dx + dy * dy);
    dr2 = dr * dr;
    det = x1 * y2 - x2 * y1;
    r = arc.radius;
    r2 = r * r;
    disc = r2 * dr2 - (det * det);
    sign = dy < 0 ? -1 : 1;
    radical = Math.sqrt(r2 * dr2 - (det * det));
    absdy = Math.abs(dy);
    if (disc < 0) {
      bug("Discriminant is less than zero, indicating there is no intersection.");
      bug(" ... arc : " + arc);
      bug(" ... line:" + line);
    } else {
      // one or two intersections (just see if the resulting points differ).
      double ax = ((det * dy) + (sign * dx * radical)) / dr2;
      double bx = ((det * dy) - (sign * dx * radical)) / dr2;
      double ay = ((-det * dx) + (absdy * radical)) / dr2;
      double by = ((-det * dx) - (absdy * radical)) / dr2;

      // get translated points a and b, and pick the one that is closer to line's endpoint.
      Pt a = new Pt(ax + tx, ay + ty);
      Pt b = new Pt(bx + tx, by + ty);
      ret = new Pt[2];
      ret[0] = a;
      ret[1] = b;
    }
    return ret;
  }

  public static Pt[] getIntersectionPoints(Rectangle r, Line line) {
    Pt[] ret = new Pt[2];
    List<Pt> corners = new ArrayList<Pt>();
    double x = r.getX();
    double y = r.getY();
    double w = r.getWidth();
    double h = r.getHeight();
    corners.add(new Pt(x, y));
    corners.add(new Pt(x + w, y));
    corners.add(new Pt(x + w, y + h));
    corners.add(new Pt(x, y + h));
    corners.add(new Pt(x, y));
    int retIdx = 0;
    for (int i = 0; i < corners.size() - 1; i++) {
      Line side = new Line(corners.get(i), corners.get(i + 1));
      IntersectionData id = getIntersectionData(line, side);
      if (id.intersectsOnLineTwo()) {
        ret[retIdx] = id.getIntersection();
        retIdx = retIdx + 1;
        if (retIdx == ret.length) {
          break;
        }
      }
    }
    return ret;
  }

  /**
   * Returns each corner of the given rectangle. Notice this returns FIVE points: the first point is
   * doubled-up at the end. This way you can draw lines from each point to the next and get a
   * complete visual rectangle. If you only need the four points make sure you don't hit the last
   * one!
   * 
   * @param r
   * @return
   */
  public static List<Pt> getRectangleCorners(Rectangle r) {
    List<Pt> corners = new ArrayList<Pt>();
    double x = r.getX();
    double y = r.getY();
    double w = r.getWidth();
    double h = r.getHeight();
    corners.add(new Pt(x, y));
    corners.add(new Pt(x + w, y));
    corners.add(new Pt(x + w, y + h));
    corners.add(new Pt(x, y + h));
    corners.add(new Pt(x, y));
    return corners;
  }

  /**
   * Returns the intersection point of the two given lines. If they are colinear or parallel this
   * will return null.
   */
  public static Pt getIntersectionPoint(Line a, Line b) {
    IntersectionData id = getIntersectionData(a, b);
    Pt ret = id.getIntersection();
    return ret;
  }

  /**
   * Make a normalized sequence where each point is the same distance <b>along the surface</b> of
   * the input sequence. This differs from the other (non-curvilinear) normalization function in
   * that the returned points here may not be of uniform spacing in a Euclidean way. This method of
   * normalizing/interpolating has the nice property that you can chop up a sequence into N pieces
   * that are mostly uniform.
   * 
   * @param seq
   *          The source sequence.
   * @param start
   *          The start index. A copy of this point is included in the return value.
   * @param end
   *          The end index (inclusive). A copy of this is included in the return value.
   * @param threshold
   *          The curvilinear distance that separates the returned points.
   * @return
   */
  public static Sequence getCurvilinearNormalizedSequence(Sequence seq, int start, int end,
      double threshold) {
    Sequence ret = new Sequence();
    double patchDist = 0;
    Pt prev = seq.get(start).copyXYT();
    ret.add(prev);
    int seqPointer = start + 1;
    if (seqPointer < seq.size()) {

      Pt next = seq.get(seqPointer);
      while (seqPointer <= end) {

        double dist = prev.distance(next);
        if (dist + patchDist > threshold) {
          // patch point between prev and next. update patchDist and prev.
          double remainder = threshold - patchDist;
          double frac = remainder / dist;
          double dx = frac * (next.getX() - prev.getX());
          double dy = frac * (next.getY() - prev.getY());
          long dt = (long) (frac * (double) (next.getTime() - prev.getTime()));
          Pt patchPt = new Pt(prev.getX() + dx, prev.getY() + dy, prev.getTime() + dt);
          ret.add(patchPt);
          prev = patchPt;
          patchDist = 0;
        } else {
          // no patch point. update patchDist, prev and next, and seqPointer.
          patchDist = patchDist + dist;
          prev = next;
          seqPointer++;
          if (seqPointer <= end) {
            next = seq.get(seqPointer);
          } else {
            break;
          }
        }
      }
      ret.add(seq.get(end).copyXYT());
    }
    return ret;
  }

  public static Sequence getNormalizedSequence(Sequence seq, double d) {
    Sequence normalizedSequence = new Sequence();
    normalizedSequence.add(seq.get(0).copy());
    List<Pt> normPoints = getNormalizedSequence(seq.getPoints(), d);
    for (Pt pt : normPoints) {
      normalizedSequence.add(pt);
    }
    normalizedSequence.add(seq.getLast().copy());
    return normalizedSequence;
  }

  public static List<Pt> getNormalizedSequence(List<Pt> points, double d) {

    List<Pt> ret = new ArrayList<Pt>();

    Vec bigVec;
    Vec smallVec;
    Pt prev = points.get(0);
    for (Pt n : points) {
      if (n.equals(points.get(0))) {
        ret.add(n.copy());
      }
      bigVec = new Vec(prev, n);
      while (bigVec.mag() > d) {
        smallVec = bigVec.getVectorOfMagnitude(d);
        Pt addMe = smallVec.add(prev);
        // interpolate the timestamp. Points are prev < addMe < n
        long diff = n.getTime() - prev.getTime();
        double magFraction = smallVec.mag() / bigVec.mag();
        long timeGuess = prev.getTime() + (long) ((double) diff * magFraction);
        addMe.setTime(timeGuess);
        ret.add(addMe);
        prev = addMe;
        bigVec = new Vec(prev, n);
      }
    }
    ret.add(points.get(points.size() - 1).copy());

    return ret;
  }

  /**
   * Returns a list of samples from the provided list that are separated by at least 'time'
   * milliseconds. It doesn't interpolate, so the actual time distances will be slightly longer than
   * the duration given.
   */
  public static List<Pt> getTimeNormalizedSequence(List<Pt> points, long time) {
    List<Pt> ret = new ArrayList<Pt>();
    long accrued = time;
    for (int i = 0; i < points.size() - 1; i++) {
      long delta = points.get(i + 1).time - points.get(i).time;
      accrued = accrued + delta;
      if (accrued > time) {
        ret.add(points.get(i));
        accrued = accrued - time;
      }
    }
    if (ret.get(ret.size() - 1) != points.get(points.size() - 1)) {
      ret.add(points.get(points.size() - 1));
    }
    return ret;
  }

  public static double getMinDistBetweenSequences(Sequence seqA, Sequence seqB) {
    double ret = Double.MAX_VALUE;
    for (Pt pt : seqA) {
      double v = getMinDistBetweenPointAndSequence(pt, seqB);
      ret = Math.min(v, ret);
    }
    return ret;
  }

  public static double getMinDistBetweenPointAndSequence(Pt pt, Sequence seq) {
    double ret = Double.MAX_VALUE;
    Pt nearest = getNearestPointOnSequence(pt, seq);
    if (nearest != null) {
      ret = nearest.distance(pt);
    }
    return ret;
  }

  /**
   * Returns the point (or the oldest point, in the event of a tie) on the given sequence that is
   * nearest the provided epicenter point.
   */
  public static Pt getNearestPointOnSequence(Pt epicenter, Sequence seq) {
    return getNearestPointOnSequence(epicenter, seq.getPoints());
  }

  /**
   * Returns the point in the given list that is nearest the epicenter.
   */
  public static Pt getNearestPointOnSequence(Pt epicenter, List<Pt> seq) {
    double minDist = Double.MAX_VALUE;
    Pt nearest = null;
    for (Pt pt : seq) {
      if (epicenter.distance(pt) < minDist) {
        nearest = pt;
        minDist = epicenter.distance(pt);
      }
    }
    return nearest;
  }

  /**
   * Finds the point on the polyline that is closest to the epicenter. The 'polyline' is chopped
   * into discrete line segments, and the distance between epicenter and that line segment is
   * calculated. If the nearest point on the segment is outside the segment bounds it is not used.
   * It is therefore possible for this function to return null.
   * 
   * @param epicenter
   *          A target point
   * @param polyline
   *          A list of points that define a polyline.
   * @return a point that appears somewhere along the interpolated polyline, or null if no such
   *         point can be found. If the return value is non-null, a the "nearest-polyline" double
   *         value is set to the distance to the polyline.
   */
  public static Pt getNearestPointOnPolyline(Pt epicenter, List<Pt> polyline) {
    double minDist = Double.MAX_VALUE;
    Pt nearest = null;
    if (polyline.size() > 1) {
      for (int i = 0; i < polyline.size() - 1; i++) {
        Pt vert = polyline.get(i);
        double dist = vert.distance(epicenter);
        if (dist < minDist) {
          minDist = dist;
          nearest = vert;
        }
        Line line = new Line(polyline.get(i), polyline.get(i + 1));
        Pt pt = getNearestPointWithinSegment(epicenter, line);
        if (pt != null) {
          dist = pt.distance(epicenter);
          if (dist < minDist) {
            minDist = dist;
            nearest = pt;
          }
        }
      }
    }
    if (nearest != null) {
      nearest.setDouble("nearest-polyline", minDist);
    }
    return nearest;
  }

  /**
   * Return the index of the point in the input list that is in the range [startIncl, endIncl] that
   * is farthest from the given line. If there is a tie the first index is returned.
   * 
   * @param points
   *          The source point list.
   * @param startIncl
   *          The start index, inclusive.
   * @param endIncl
   *          The end index, inclusive.
   * @param line
   *          A line to measure distance.
   * @return the index of the farthest point from the line.
   */
  public static int getIndexFarthestFromLine(List<Pt> points, int startIncl, int endIncl, Line line) {
    int ret = -1;
    double maxDist = -1;
    for (int i = startIncl; i <= endIncl; i++) {
      Pt here = points.get(i);
      double dist = getDistanceBetweenPointAndLine(here, line);
      if (dist > maxDist) {
        ret = i;
        maxDist = dist;
      }
    }
    return ret;
  }

  public static double getMinDistBetween(List<Pt> listA, List<Pt> listB) {
    double ret = Double.MAX_VALUE;
    for (Pt ptA : listA) {
      for (Pt ptB : listB) {
        ret = Math.min(ret, ptA.distance(ptB));
      }
    }
    return ret;
  }

  public static Statistics getClosenessStatistics(List<Pt> listA, List<Pt> listB) {
    Statistics stats = new Statistics();
    for (Pt ptA : listA) {
      double closest = Double.MAX_VALUE;
      for (Pt ptB : listB) {
        closest = Math.min(ptA.distance(ptB), closest);
      }
      stats.addData(closest);
    }
    return stats;
  }

  public static double getMinDistBetweenPointsOnSequence(Sequence seq, Pt a, Pt b) {
    boolean foundA = false;
    boolean foundB = false;
    double dist = 0.0;
    Pt prev = null;
    for (Pt pt : seq) {
      if ((prev != null) && (foundA ^ foundB))
        dist += prev.distance(pt);
      if (pt.equals(a))
        foundA = true;
      if (pt.equals(b))
        foundB = true;
      if (foundA && foundB)
        break;
      prev = pt;
    }
    return dist;
  }

  /**
   * The provided epicenter point may or may not be on the sequence. Wherever it is, there is a
   * point on the sequence that is closest to it. This method runs through all points in the
   * sequence and writes the distance from that closest point to each point, and puts it in the
   * attribute slot with the provided name.
   */
  public static void setDistancesOnSequence(String attribName, Sequence seq, Pt epicenter) {
    Pt zero = getNearestPointOnSequence(epicenter, seq);
    if (zero == null) {
      return;
    }
    zero.setDouble(attribName, 0.0);
    int zeroIdx = seq.indexOf(zero);
    double runDist = 0.0;
    Pt prev = null;
    for (Pt pt : seq.backward(zeroIdx)) {
      if (prev != null && !pt.equals(zero)) {
        runDist += prev.distance(pt);
        pt.setDouble(attribName, runDist);
      }
      prev = pt;
    }
    prev = null;
    runDist = 0.0;
    for (Pt pt : seq.forward(zeroIdx)) {
      if (prev != null && !pt.equals(zero)) {
        runDist += prev.distance(pt);
        pt.setDouble(attribName, runDist);
      }
      prev = pt;
    }
  }

  public static void calculateCurvature(Sequence seq) {
    calculateCurvature(seq.getPoints());
  }

  public static void calculateCurvature(List<Pt> points) {
    Pt a = null;
    Pt b = null;
    Pt c = null;
    for (int i = 1; i < points.size() - 1; i++) {
      a = points.get(i - 1);
      b = points.get(i);
      c = points.get(i + 1);
      double curvature = Functions.getCurvature(a, b, c);
      b.setDouble("curvature", curvature);
    }
    // the first and last points have zero curvature
    points.get(0).setDouble("curvature", 0.0);
    points.get(points.size() - 1).setDouble("curvature", 0.0);
  }

  /**
   * Calculate curvilinear distances for the given list of points away from the target point. This
   * is used in flow selection, for example.
   * 
   * @param points
   * @param target
   *          a point that should be in the point list.
   * @return curvilinear distances corresponding to the indices of the points parameter, or an array
   *         of zeros if it can't be calculated for some reason.
   */
  public static double[] calculateCurvilinearDistance(List<Pt> points, Pt target) {
    double[] ret = new double[points.size()];
    int idxTarget = getClosestIndex(points, target);
    if (idxTarget >= 0) {
      // calculate curvilinear distance starting at idxTarget, first ascending then descending.
      double curveDist = 0;
      for (int i = idxTarget + 1; i < points.size(); i++) {
        double d = points.get(i - 1).distance(points.get(i));
        curveDist = curveDist + d;
        ret[i] = curveDist;
      }
      curveDist = 0;
      for (int i = idxTarget - 1; i >= 0; i--) {
        double d = points.get(i + 1).distance(points.get(i));
        curveDist = curveDist + d;
        ret[i] = curveDist;
      }
    }
    return ret;
  }

  public static int getClosestIndex(List<Pt> points, Pt target) {
    // first find the index of 'target'
    int idxTarget = -1;
    double bestDist = Double.MAX_VALUE;
    for (int i = 0; i < points.size(); i++) {
      Pt pt = points.get(i);
      double d = pt.distance(target);
      if (d < bestDist) {
        idxTarget = i;
        bestDist = d;
      }
    }
    return idxTarget;
  }

  public static double[] calculateCurvilinearDistanceClosedCircuit(List<Pt> points, Pt target) {
    int n = points.size();
    double[] ret = new double[n];
    int idxTarget = getClosestIndex(points, target);
    if (idxTarget >= 0) {
      // do this the easy way: iterate through the list forward then
      // backward, and keep the shortest distance for each.
      //
      // forward
      double dist = 0;
      for (int counter = 1; counter < points.size(); counter++) {
        int i = wrapMod(idxTarget + counter, n);
        int j = wrapMod(i - 1, n);
        double d = points.get(i).distance(points.get(j));
        dist = dist + d;
        ret[i] = dist;
      }
      // backward
      dist = 0;
      for (int counter = 1; counter < points.size(); counter++) {
        int i = wrapMod(idxTarget - counter, n);
        int j = wrapMod(i + 1, n);
        double d = points.get(i).distance(points.get(j));
        dist = dist + d;
        ret[i] = min(ret[i], dist);
      }
    }
    return ret;
  }

  public static Rectangle2D getSequenceBoundingBox(Sequence seq) {

    double maxX = Double.MIN_VALUE;
    double minX = Double.MAX_VALUE;
    double maxY = Double.MIN_VALUE;
    double minY = Double.MAX_VALUE;

    for (Pt pt : seq) {
      maxX = Math.max(maxX, pt.getX());
      minX = Math.min(minX, pt.getX());
      maxY = Math.max(maxY, pt.getY());
      minY = Math.min(minY, pt.getY());
    }

    return new Rectangle2D.Double(minX, minY, maxX - minX, maxY - minY);
  }

  public static List<Pt> getConvexHull(Sequence seq) {
    return getConvexHull(seq.getPoints());
  }

  public static List<Pt> getConvexHull(List<Pt> unsortedPoints) {
    return Graham.getConvexHull(unsortedPoints);
  }

  public static List<Pt> sortPointListWithDouble(final String attribKey, final boolean ascending,
      List<Pt> list) {
    List<Pt> input = new ArrayList<Pt>(list);
    Comparator<Pt> sortationDevice = new Comparator<Pt>() {
      public int compare(Pt o1, Pt o2) {
        int ret = 0;
        double v1 = o1.getDouble(attribKey);
        double v2 = o2.getDouble(attribKey);
        if (ascending) {
          if (v1 < v2) {
            ret = -1;
          } else if (v1 > v2) {
            ret = 1;
          }
        } else {
          if (v1 < v2) {
            ret = 1;
          } else if (v1 > v2) {
            ret = -1;
          }
        }
        return ret;
      }
    };
    Collections.sort(input, sortationDevice);
    return input;
  }

  public static List<Pt> getIntersectionsOfLineAndSequence(Line line, Sequence seq) {
    List<Pt> ret = new ArrayList<Pt>();
    Pt prev = null;
    for (Pt pt : seq) {
      if (prev != null) {
        Line patch = new Line(prev, pt);
        IntersectionData data = Functions.getIntersectionData(patch, line);
        if (data.intersectsOnLineOne() && !ret.contains(data.getIntersection())) {
          ret.add(data.getIntersection());
        }
      }
      prev = pt;
    }
    return ret;
  }

  public static void getSplinePatch(double x0, double x1, double x2, double x3, double y0,
      double y1, double y2, double y3, Sequence seq, double numSteps) {

    // patch is drawn between points 1 and 2; if they are the same
    // point, don't draw the patch!
    // if ((x1 == x2) && (y1 == y2)) return;

    double step = 1.0 / numSteps;
    double x = 0, y = 0;

    double t0 = 0, t1 = 0, t2 = 0, t3 = 0;
    for (double u = 0.0; u <= 1.0; u += step) {

      t0 = x0 * (Math.pow((1.0 - u), 3.0)) / (6.0);
      t1 = x1 * ((3.0 * Math.pow(u, 3.0)) - (6.0 * Math.pow(u, 2.0)) + (4.0)) / (6.0);
      t2 = x2 * ((-3.0 * Math.pow(u, 3.0)) + (3.0 * Math.pow(u, 2.0)) + (3.0 * u) + (1.0)) / (6.0);
      t3 = x3 * (Math.pow(u, 3.0)) / (6.0);

      x = t0 + t1 + t2 + t3;

      t0 = y0 * (Math.pow((1.0 - u), 3.0)) / (6.0);
      t1 = y1 * ((3.0 * Math.pow(u, 3.0)) - (6.0 * Math.pow(u, 2.0)) + (4.0)) / (6.0);
      t2 = y2 * ((-3.0 * Math.pow(u, 3.0)) + (3.0 * Math.pow(u, 2.0)) + (3.0 * u) + (1.0)) / (6.0);
      t3 = y3 * (Math.pow(u, 3.0)) / (6.0);

      y = t0 + t1 + t2 + t3;

      seq.add(new Pt(x, y));
    }
  }

  public static int getSplinePatch(Pt a, Pt b, Pt c, Pt d, Sequence seq, int numSteps) {
    int before = seq.size();
    getSplinePatch(a.getX(), b.getX(), c.getX(), d.getX(), a.getY(), b.getY(), c.getY(), d.getY(),
        seq, numSteps);
    int after = seq.size();
    return after - before;
  }

  /**
   * Gets a spline with some number of control points. To get a straight line, use numCtrlPoints of
   * two.
   */
  public static Sequence getSpline(Sequence input, int numCtrlPoints, double density) {
    int numPatches = numCtrlPoints - 1;
    double inputLength = input.length();
    double lengthPerPatch = inputLength / (double) numCtrlPoints;
    Sequence ctrlPoints = Functions.getNormalizedSequence(input, lengthPerPatch);
    numPatches = ctrlPoints.size() - 1;
    int inSize = input.size();
    double pointsPerPatch = ((double) inSize / (double) numPatches);
    int numSteps = (int) Math.ceil(pointsPerPatch * density);
    Sequence ret = new Sequence();
    Pt a, b, c, d;

    int idxA, idxB, idxC, idxD;
    for (int i = 0; i <= ctrlPoints.size(); i++) {
      idxA = numInRange(i - 2, 0, numPatches);
      idxB = numInRange(i - 1, 0, numPatches);
      idxC = numInRange(i, 0, numPatches);
      idxD = numInRange(i + 1, 0, numPatches);

      a = ctrlPoints.get(idxA);
      b = ctrlPoints.get(idxB);
      c = ctrlPoints.get(idxC);
      d = ctrlPoints.get(idxD);

      getSplinePatch(a, b, c, d, ret, numSteps);
    }

    return ret;

  }

  /**
   * Make a spline using points from the input sequence as control points. This approach differs
   * from 'getSpline' because this method does not make a normalized squence from which control
   * points are extracted. For example, if an input sequence has 40 points and numCtrlPoints is 9,
   * it will use the first and last points, and pick seven other points that are nearest to 1/7 of
   * the curvilinear distance along the entire path. Further, the target minimum density specifies
   * the minimum distance between points in each spline patch. In other words, the distance between
   * points i and i+1 in the return spline will not exceed targetMinDensity.
   */
  public static Sequence getSplineAuthentic(List<Pt> src, int numCtrlPoints, double targetMinDensity) {
    Sequence ret = new Sequence();
    Sequence srcSequence = new Sequence();
    for (Pt srcPt : src) {
      srcSequence.add(srcPt);
    }
    double totalLength = srcSequence.getPathLength(0, src.size() - 1);
    double curviLengthBetweenControlPoints = totalLength / (double) (numCtrlPoints - 1);
    List<Pt> controlPoints = new ArrayList<Pt>();
    controlPoints.add(src.get(0).copy());
    Pt prev = null;
    double prevDist = 0;
    double runningDist = 0;
    double thisDist;
    double nextBarrier = curviLengthBetweenControlPoints;
    for (int i = 0; i < src.size() - 1; i++) {
      Pt pt = src.get(i);
      if (prev != null) {
        thisDist = prev.distance(pt);
        runningDist = runningDist + thisDist;
        if (runningDist > nextBarrier) {
          // pick prev or pt, whichever is closer to nextBarrier.
          double prevAbs = Math.abs(prevDist - nextBarrier);
          double thisAbs = Math.abs(runningDist - nextBarrier);
          if (prevAbs < thisAbs) {
            controlPoints.add(prev);
          } else {
            controlPoints.add(pt);
          }
          nextBarrier = nextBarrier + curviLengthBetweenControlPoints;
        }
        prevDist = runningDist;
      }
      prev = pt;
    }
    controlPoints.add(src.get(src.size() - 1));
    int numSteps = (int) Math.ceil((double) curviLengthBetweenControlPoints / targetMinDensity);
    ret = makeNaturalSpline(numSteps, controlPoints);

    return ret;
  }

  public static Sequence makeClosedNaturalSpline(int numSteps, List<Pt> controlPoints) {
    Sequence ret = new Sequence();
    int n = controlPoints.size();

    int idxA, idxB, idxC, idxD;
    Pt a, b, c, d;
    for (int i = 0; i < n; i++) {

      idxA = wrapMod(i - 1, n);
      idxB = wrapMod(i, n);
      idxC = wrapMod(i + 1, n);
      idxD = wrapMod(i + 2, n);

      a = controlPoints.get(idxA);
      b = controlPoints.get(idxB);
      c = controlPoints.get(idxC);
      d = controlPoints.get(idxD);

      getSplinePatch(a, b, c, d, ret, numSteps);
    }
    return ret;
  }

  /**
   * Modulo function that allows the first parameter to be negative, allowing array index access for
   * negative values to wrap around to the end. Example output (using @ as the operator symbol):
   * 
   * <pre>
   * -2 @ 5 = 3
   * -1 @ 5 = 4
   * 0 @ 5 = 0
   * 1 @ 5 = 1
   * 2 @ 5 = 2
   * 3 @ 5 = 3
   * </pre>
   * 
   * @param i
   *          the index. may be any integer
   * @param n
   *          the denominator
   */
  public static int wrapMod(int i, int n) {
    int res = i % n;
    if (res < 0) {
      res = n + res;
    }
    return res;
  }

  public static Sequence makeNaturalSpline(int numSteps, List<Pt> controlPoints) {
    Sequence ret = new Sequence();
    int numPatches = controlPoints.size() - 1;

    int idxA, idxB, idxC, idxD;
    Pt a, b, c, d;
    for (int i = 0; i <= controlPoints.size(); i++) {

      idxA = numInRange(i - 2, 0, numPatches);
      idxB = numInRange(i - 1, 0, numPatches);
      idxC = numInRange(i, 0, numPatches);
      idxD = numInRange(i + 1, 0, numPatches);

      a = controlPoints.get(idxA);
      b = controlPoints.get(idxB);
      c = controlPoints.get(idxC);
      d = controlPoints.get(idxD);

      getSplinePatch(a, b, c, d, ret, numSteps);
    }
    return ret;
  }

  private static int numInRange(int i, int low, int high) {
    if (i < low) {
      i = low;
    }
    if (i > high) {
      i = high;
    }
    return i;
  }

  /**
   * Se the other getPointAtDistance for docs.
   */
  public static Pt getPointAtDistance(Sequence seq, int knownIndex, double knownDistance,
      int direction, double desiredDistance) {
    return getPointAtDistance(seq, knownIndex, knownDistance, direction, desiredDistance, true);
  }

  /**
   * From a given index that is a known distance, find the point in some direction (+1 for
   * increasing indexes, -1 for decreasing) that is the specified distance along the sequence. The
   * return value is an interpolated Point based on the surrounding two points that are actually
   * found on the sequence.
   * 
   * For example: given the sequence:
   * 
   * 0 1 2 3 4 5 6 7 distance between points=10.0 * o * * * p * * +---^ With knownIndex = 5,
   * knownDistance = 40.0, direction = 1, and desiredDistance = 53, the returned Point is an
   * interpolated point between points 6 and 7 (closer to 6).
   * 
   * @param seq
   *          the Sequence to search
   * @param knownIndex
   *          the index of a known point p
   * @param knownDistance
   *          the distance that known point p is from some other interesting point o
   * @param direction
   *          the direction to search through the sequence--this parameter must be 1 for increasing
   *          indexes or -1 for decreasing indexes.
   * @param desiredDistance
   *          the target point t should be from the other interesting point o
   * @param extrapolatedOK
   * @return an interpolated point (or, extrapolated if the given distance is outside the first or
   *         last point)
   */
  public static Pt getPointAtDistance(Sequence seq, int knownIndex, double knownDistance,
      int direction, double desiredDistance, boolean extrapolationOK) {
    if (desiredDistance < knownDistance)
      throw new RuntimeException(
          "desiredDistance must be greater than or equal to known distance: "
              + num(desiredDistance) + " !< " + num(knownDistance));
    if (direction != 1 && direction != -1)
      throw new RuntimeException("Direction must be -1 or 1. Provided: " + direction);

    // first thing to do is find the indexes of points that surround
    // the known point, or if the desired point equals one of the
    // points on the sequence. It is possible that the desired point
    // is outside the confines of the sequence, in which case we'll
    // have to extrapolate.

    // at any rate, I'll enter a loop and not come out until I have
    // two indexes, a and b. a is the closer one to the known index,
    // and b is the farther one. In the event of the desired point
    // being on the sequence, a == b. In the event of extrapolation, b
    // < 0.

    int a = knownIndex;
    int b = a + direction;
    double d = knownDistance;
    Pt ret = null;
    while (true) {
      if (b < 0 || b >= seq.size()) {
        b = -1;
        break;
      }
      Pt ptA = seq.get(a);
      Pt ptB = seq.get(b);
      double segDist = ptA.distance(ptB);
      if (segDist + d > desiredDistance) {
        break; // found it!
      }
      d += segDist;
      a = b;
      b += direction;
    }

    if (b >= 0) {
      // if b is non-negative, then a and b are indexes of points that
      // surround the desired point. Interpolate.
      // a is at 'd' distance, b is beyond 'desiredDistance'.
      Pt ptA = seq.get(a);
      Pt ptB = seq.get(b);
      double bdist = ptA.distance(ptB);
      double s = (desiredDistance - d) / bdist;
      double newX = ptA.getX() + ((ptB.getX() - ptA.getX()) * s);
      double newY = ptA.getY() + ((ptB.getY() - ptA.getY()) * s);
      ret = new Pt(newX, newY);
    } else if (extrapolationOK) {
      // if b is negative, then the desired point is outside the
      // boundary of the sequence. Using a and the point before it,
      // extrapolate.
      Pt ptA = seq.get(a - direction);
      Pt ptB = seq.get(a);
      double bdist = ptA.distance(ptB);
      double s = (desiredDistance - d) / bdist;
      double newX = ptB.getX() + ((ptB.getX() - ptA.getX()) * s);
      double newY = ptB.getY() + ((ptB.getY() - ptA.getY()) * s);
      ret = new Pt(newX, newY);
    }
    return ret;
  }

  /**
   * Returns an array with two points marking the points surrounding seq[i] that are dist units
   * along the sequence path. A returned value will be null if the starting location is too near the
   * beginning or end of the sequence. The total length from ret[0] to ret[1] is 2*dist.
   * 
   * @param seq
   * @param idx
   * @param dist
   * @return
   */
  public static Pt[] getCurvilinearWindow(Sequence seq, int idx, double dist) {
    //    Pt[] ret = new Pt[] {
    //        getCurvilinearWindow(seq, idx, dist, -1), getCurvilinearWindow(seq, idx, dist, 1)
    //    };
    //    bug("Getting curvilinear window in two directions. What say you? Index " + idx + " of "
    //        + seq.size());
    //    double toEnd = seq.getPathLength(idx, seq.size() - 1);
    //    double fromStart = seq.getPathLength(0, idx);
    //    bug("Path lengths should be less than " + num(dist));
    //    bug("  fromStart: " + num(fromStart));
    //    bug("  toEnd: " + num(toEnd));
    Pt a = getCurvilinearNeighbor(seq, idx, dist, -1);
    Pt b = getCurvilinearNeighbor(seq, idx, dist, 1);
    //    bug("  a: " + num(a));
    //    bug("  b: " + num(b));
    Pt[] ret = new Pt[] {
        a, b
    };
    return ret;
  }

  public static Pt getCurvilinearWindow(Sequence seq, int idx, double dist, int dir) {
    double d = 0; // distance travelled so far
    Pt ret = null;
    for (int i = idx; i > 1 && i < seq.size() - 1; i = i + dir) {
      Pt a = seq.get(i);
      Pt b = seq.get(i + dir);
      double patchDist = a.distance(b);
      if (patchDist + d > dist) {
        // interpolate and set ret[1]
        double remaining = dist - d;
        double fraction = remaining / patchDist;
        double dx = b.x - a.x;
        double dy = b.y - a.y;
        double x = a.x + (fraction * dx);
        double y = a.y + (fraction * dy);
        ret = new Pt(x, y);
        break;
      } else {
        d = d + patchDist;
      }
    }
    return ret;
  }

  public static Pt getCurvilinearNeighbor(Sequence seq, int start, double targetDist, int dir) {
    Pt ret = null;
    Pt prev = null;
    double soFar = 0;
    for (int i = start; i >= 0 && i < seq.size(); i += dir) {
      Pt pt = seq.get(i);
      if (prev != null) {
        double dist = prev.distance(pt);
        if (soFar + dist > targetDist) {
          double remaining = targetDist - soFar;
          double fraction = remaining / dist;
          double dx = pt.x - prev.x;
          double dy = pt.y - prev.y;
          long dt = pt.getTime() - prev.getTime();
          double x = prev.x + fraction * dx;
          double y = prev.y + fraction * dy;
          long t = prev.getTime() + (long) (fraction * (double) dt);
          ret = new Pt(x, y, t);
          break;
        } else {
          soFar = soFar + dist;
        }
      }
      prev = pt;
    }

    return ret;
  }

  /**
   * Returns an array of values to be used in a matrix equation for performing an affine
   * transformation. The returned values, in order, are: scaleX, shearY, shearX, scaleY, translateX,
   * translateY. The returned array can be used as the sole argument to initialize a
   * java.awt.geom.AffineTransform. The shear values are always zero and are only returned so you
   * can directly use this method in making an AffineTransform.
   */
  public static double[] getAffineTransform(Rectangle2D source, Rectangle2D dest) {
    double scaleX = dest.getWidth() / source.getWidth();
    double scaleY = dest.getHeight() / source.getHeight();
    double transX = dest.getX() - (source.getX() * scaleX);
    double transY = dest.getY() - (source.getY() * scaleY);
    return new double[] {
        scaleX, 0.0, 0.0, scaleY, transX, transY
    };
  }

  /**
   * Creates a rectangle that includes the two points, as well as padW/2 and padH/2 surrounding it.
   */
  public static Rectangle2D makeRectangle(Pt a, Pt b, double padW, double padH) {
    double x = Math.min(a.getX(), b.getX()) - (padW / 2.0);
    double y = Math.min(a.getY(), b.getY()) - (padH / 2.0);
    double w = Math.abs(a.getX() - b.getX()) + padW;
    double h = Math.abs(a.getY() - b.getY()) + padH;
    return new Rectangle2D.Double(x, y, w, h);
  }

  public static Rectangle2D growRectangle(Rectangle2D r, double dx, double dy) {
    return new Rectangle2D.Double(r.getX() - (dx / 2.0), r.getY() - (dy / 2.0), r.getWidth() + dx,
        r.getHeight() + dy);
  }

  /**
   * It is common to need to get the fraction of some value within a range. Assuming your input
   * value is a and the range's lower and upper bounds are b and c respectively, you can use this
   * funtion to get the fraction of a in that range.
   * 
   * @param a
   *          the value that is to be tested inside some range.
   * @param b
   *          the lower (b less than c) value of the range
   * @param c
   *          the upper (c greater than b) value of the range
   * 
   * @return (a-b) / (c-b)
   */
  public static double getFraction(double a, double b, double c) {
    return (a - b) / (c - b);
  }

  /**
   * Returns the same as getFraction except the return value is guaranteed to be clamped to the
   * range 0..1.
   */
  public static double getClampedFraction(double a, double b, double c) {
    double d = getFraction(a, b, c);
    return Math.max(0.0, Math.min(1.0, d));
  }

  public static boolean isPointInRegion(Pt where, List<Pt> region) {
    int crossings = getCrossingNumber(where, region);
    return (crossings % 2 == 1);
  }

  public static int getCrossingNumber(Pt inputPt, List<Pt> points) {
    int ret = 0;
    Line line = new Line();
    for (Pt pt : points) {
      line.push(pt);
      if (line.isValid()) {
        if (Functions.getWindingIntersect(inputPt, line)) {
          ret++;
        }
      }
    }
    // check the line formed by the first and last points
    if (points.size() > 2 && points.get(points.size() - 1) != points.get(0)) {
      line.push(points.get(points.size() - 1));
      line.push(points.get(0));
      if (Functions.getWindingIntersect(inputPt, line)) {
        ret++;
      }
    }
    return ret;
  }

  public static int getCrossingNumberSequences(Pt pt, List<Sequence> sequences) {
    int count = 0;
    for (Sequence seq : sequences) {
      count = count + getCrossingNumber(pt, seq.getPoints());
    }
    return count;
  }

  public static double getSum(double... numbers) {
    List<Double> list = new ArrayList<Double>();
    for (double d : numbers) {
      list.add(d);
    }
    return getSum(list);
  }

  public static double getSum(List<Double> numbers) {
    double sum = 0.0;
    for (double d : numbers) {
      sum = sum + d;
    }
    return sum;
  }

  /**
   * Tells you if a ray projected from the given point to the right (increasing x) intersects with
   * the given line. This uses some simple inequality checks in order to avoid doing math when
   * possible, so it does not calculate the exact intersection point. If you need the exact
   * intersection point, use getIntersectionData or getIntersectionPoint.
   **/
  public static boolean getWindingIntersect(Pt pt, Line line) {
    boolean ret = false;

    // first determine if y is in range
    boolean yInRange = inRange(pt.getY(), line.getStart().getY(), line.getEnd().getY());
    if (yInRange) {
      double p_x = pt.getX();
      double a_x = line.getStart().getX();
      double b_x = line.getEnd().getX();
      // most likely p will be entirely to the left or to the right of
      // the line. We are only interested if the point is to the LEFT
      // of the line, or if it is in the RANGE of the line. If it is
      // to the RIGHT of the line we can give up now.
      boolean isRight = (p_x > a_x) && (p_x > b_x);
      if (!isRight) {
        boolean isLeft = (p_x < a_x) && (p_x < b_x);
        if (isLeft) {
          ret = true;
        } else {
          Line horizon = new Line(pt, new Pt(pt.getX() + 1.0, pt.getY()));
          Pt xsec = Functions.getIntersectionPoint(horizon, line);
          if (xsec != null && xsec.getX() > pt.getX()) {
            ret = true;
          }
        }
      }
    }
    return ret;
  }

  private static boolean inRange(double val, double rangeA, double rangeB) {
    return ((val <= rangeA && val > rangeB) || (val >= rangeA && val < rangeB));
  }

  public static boolean isMonotonicDecreasing(double... values) {
    boolean dec = true;
    for (int i = 0; i < values.length; i++) {
      if (i > 0 && values[i - 1] <= values[i]) {
        dec = false;
        break;
      }
    }
    return dec;
  }

  public static boolean isMonotonicIncreasing(double... values) {
    boolean inc = true;
    for (int i = 0; i < values.length; i++) {
      if (i > 0 && values[i - 1] >= values[i]) {
        inc = false;
        break;
      }
    }
    return inc;
  }

  public static boolean isMonotonic(double... values) {
    boolean inc = isMonotonicIncreasing(values);
    boolean dec = isMonotonicDecreasing(values);
    return inc || dec;
  }

  /**
   * Gives a positive angle measured counter-clockwise from the x-positive axis. The purpose of this
   * is to work with angles that do not have discontinuities at 360.
   * 
   * @return an angle in degrees that is positive.
   */
  public static double makeAnglePositive(double angleDegrees) {
    double ret = angleDegrees;
    if (angleDegrees < 0) {
      ret = 360 + angleDegrees;
    }
    return ret;
  }

  /**
   * Returns an angle that is on the correct side of the limit. For example, if the limit angle is
   * 90, source is 20, and dir is positive (meaning we want an angle bigger than 90), it will return
   * 360+20, which is the first value that is angle-wise equivalent to 20.
   * 
   * @param limitDegrees
   *          a threshold to go over (or under, depending on 'dir')
   * @param sourceDegrees
   *          an input value for which we would like a phase computed.
   * @param dir
   *          supply a positive value to recieve a number bigger than the limit, or negative to get
   *          a number smaller than the limit.
   * @return A phase of the sourceDegrees input value (e.g. the first value for which limit is less
   *         than (or greater than) (sourceDegrees + n * 360) --- returns the parenthesized
   *         portion).
   */
  public static double getNearestAnglePhase(double limitDegrees, double sourceDegrees, int dir) {
    double value = sourceDegrees;
    if (dir > 0) {
      while (value < limitDegrees) {
        value += 360.0;
      }
    } else if (dir < 0) {
      while (value > limitDegrees) {
        value -= 360.0;
      }
    }
    return value;
  }

  public static boolean arePointsColinear(Pt[] pts) {
    List<Pt> pointList = new ArrayList<Pt>();
    for (Pt pt : pts) {
      pointList.add(pt);
    }
    return arePointsColinear(pointList);
  }

  /**
   * Returns true iff there are three or more points in the provided list and either of the
   * following two conditions: (1) they are all at the same location, or (2) all fall directly on
   * the same line.
   */
  public static boolean arePointsColinear(List<Pt> somePoints) {
    boolean ret = false;
    if (somePoints.size() >= 3) {
      // find two points that are not coincident.
      Pt a = somePoints.get(0);
      Line line = null;
      boolean sameSpot = true;
      for (int i = 1; i < somePoints.size(); i++) {
        if (!somePoints.get(i).isSameLocation(a)) {
          sameSpot = false;
          line = new Line(a, somePoints.get(i));
          break;
        }
      }
      if (!sameSpot && line != null) {
        ret = true;
        for (Pt pt : somePoints) {
          double dist = getDistanceBetweenPointAndLine(pt, line);
          if (abs(dist) > EQ_TOL) {
            ret = false;
            break;
          }
        }
      }
    }
    return ret;
  }

  public static double getLineError(Line line, Sequence sequence) {
    return getLineError(line, sequence.getPoints(), 0, sequence.size() - 1);
  }

  public static double getLineError(Line line, List<Pt> points, int startIdx, int endIdxInclusive) {
    double errorSum = 0;
    for (int i = startIdx; i <= endIdxInclusive; i++) {
      Pt pt = points.get(i);
      double error = Functions.getDistanceBetweenPointAndLine(pt, line);
      errorSum = errorSum + (error * error);
    }
    return sqrt(errorSum) / (endIdxInclusive - startIdx);
  }

  /**
   * Make a rotated ellipse given some points. If the points are colinear, or if the resulting
   * ellipse has a minor radius less than 2, this returns null. Be careful to check the return
   * value.
   */
  public static RotatedEllipse createEllipse(List<Pt> somePoints, boolean isArc) {
    //    for (Pt pt : somePoints) {
    //      if (pt.getTime() == 0) {
    //        stacktrace("zero time in createEllipse.", 8);
    //        System.exit(0);
    //      }
    //    }
    RotatedEllipse ret = null;
    List<Pt> prunedList = getUniquePoints(somePoints);
    Sequence somePointsSeq = new Sequence(prunedList);
    if (prunedList.size() > 3 && !Functions.arePointsColinear(prunedList)) {
      Pt midPt = somePointsSeq.get(somePointsSeq.size() / 2);
      RotatedEllipse ellie = EllipseFit.ellipseFit(prunedList);
      // don't want to work with very skinny ellipses.
      if (ellie.getMinorRadius() > 2) {
        if (isArc) {
          ellie.setArcRegion(somePointsSeq.getFirst(), midPt, somePointsSeq.getLast());
        }
        ret = ellie;
      }
    }
    return ret;
  }

  /**
   * Returns true if none of the points are at the same location (using Pt.
   * 
   * @param somePoints
   * @return
   */
  public static List<Pt> getUniquePoints(List<Pt> somePoints) {
    Set<Pt> doomed = new HashSet<Pt>();
    for (int i = 0; i < somePoints.size() - 1; i++) {
      Pt left = somePoints.get(i);
      for (int j = i + 1; j < somePoints.size(); j++) {
        Pt right = somePoints.get(j);
        if (doomed.contains(right)) {
          continue;
        }
        if (left.isSameLocation(right)) {
          doomed.add(right);
        }
      }
    }
    List<Pt> ret = new ArrayList<Pt>(somePoints);
    ret.removeAll(doomed);
    return ret;
  }

  /**
   * Returns the length of the stroke represented as the point list. This does NOT rely on cached
   * values.
   * 
   * @param points
   * @return sum of distance of p[i] to p[i+1]
   */
  public static double getCurvilinearLength(List<Pt> points) {
    double ret = 0;
    for (int i = 0; i < points.size() - 1; i++) {
      ret = ret + points.get(i).distance(points.get(i + 1));
    }
    return ret;
  }

  public static double getEllipseError(RotatedEllipse ellie, List<Pt> points) {
    double ret = 0;
    List<Pt> ellipseSurface = ellie.initArc();
    double errorSum = 0;
    for (Pt pt : points) {
      Pt nearest = Functions.getNearestPointOnSequence(pt, ellipseSurface);
      double error = nearest.distance(pt);
      errorSum = errorSum + (error * error);
    }
    ret = (sqrt(errorSum) / (points.size() - 2));
    return ret;
  }

  /**
   * Calculate the error between the ellipse surface and the target sequence. The ellipse is assumed
   * to be a restricted arc.
   */
  public static double getEllipseError(RotatedEllipse ellie, Sequence target) {
    //    int numPoints = (int) Math.ceil(target.length());
    double ret = 0;
    List<Pt> ellipseSurface = ellie.initArc();//ellie.getRestrictedArcPath(numPoints);
    double errorSum = 0;
    for (Pt pt : target) {
      Pt nearest = Functions.getNearestPointOnSequence(pt, ellipseSurface);
      double error = nearest.distance(pt);
      errorSum = errorSum + (error * error);
    }
    ret = (sqrt(errorSum) / (target.size() - 2));
    return ret;
  }

  public static double getEllipseError(List<Pt> points) {
    double ret = Double.MAX_VALUE;
    RotatedEllipse ellie = createEllipse(points, false);
    if (ellie != null) {
      int n = points.size();
      ellie.setArcRegion(points.get(0), points.get(n / 2), points.get(n - 1));
      ret = getEllipseError(ellie, new Sequence(points));
    }
    return ret;
  }

  /**
   * Find the first index of the point (beginning at the given start location) in the sequence whose
   * timestamp is equal to or greater than the timestamp of the input point. If all the points in
   * the sequence are younger than the input, this returns -1.
   */
  public static int seekByTime(Pt targetTime, List<Pt> seq, int idxStart) {
    int ret = -1;
    long target = targetTime.getTime();
    for (int i = idxStart; i < seq.size(); i++) {
      if (seq.get(i).getTime() >= target) {
        ret = i;
        break;
      }
    }
    return ret;
  }

  /**
   * Rotate and scale the points about points[idxHinge].
   * 
   * @param idxHinge
   *          the index of the point in points that will remain constant. This should be either 0 or
   *          points.size() - 1.
   * @param idxReference
   *          the index of the reference point. It will be transformed fully to match 'target'. This
   *          should either be 0 or points.size() - 1 (whichever the hinge is not).
   * @param target
   *          the point that the reference point will be transformed to
   * @param points
   *          the list of points to use as a source for the transform
   * @return an affine transformation of the points list. It has the same size as the input list.
   */
  public static List<Pt> hinge(int idxHinge, int idxReference, Pt target, List<Pt> points) {
    Pt ref = points.get(idxReference);
    double dx = target.getX() - ref.getX();
    double dy = target.getY() - ref.getY();
    List<Pt> ret = new ArrayList<Pt>();
    int dir = idxHinge == 0 ? 1 : -1;
    double segLength = Functions.getCurvilinearLength(points);
    double runningDist = 0;
    Pt prev = null;
    for (int i = idxHinge; i >= 0 && i < points.size(); i += dir) {
      Pt pt = points.get(i);
      if (prev != null) {
        runningDist = runningDist + prev.distance(pt);
        double frac = runningDist / segLength;
        double localDx = frac * dx;
        double localDy = frac * dy;
        ret.add(new Pt(pt.getX() + localDx, pt.getY() + localDy, pt.getTime()));
      } else {
        ret.add(pt.copyXYT());
      }
      prev = pt;
    }
    return ret;
  }

  /**
   * Create two new lists representing the 'overlap' region. 'a' and 'b' are polylines. For each
   * polyline, the nearest point (vertex, or point along connecting lines) is determined. When that
   * nearest point is closer than 'thresh' units, it is included in the return list. The values in
   * retA are found along polyline 'a'; values in retB are found along 'b'. If you are only
   * interested in finding the overlap at the beginning of each list, set the boolean param to true.
   * 
   * @param a
   * @param b
   * @param retA
   * @param retB
   * @param thresh
   * @param stopWhenThresholdExceeded
   */
  public static void createOverlap(List<Pt> a, List<Pt> b, List<Pt> retA, List<Pt> retB,
      double thresh, boolean stopWhenThresholdExceeded) {
    SortedSet<Pt> sorterA = new TreeSet<Pt>(Pt.sortByT);
    SortedSet<Pt> sorterB = new TreeSet<Pt>(Pt.sortByT);
    //    if (a.size() == b.size()) {
    //      boolean same = true;
    //      bug("List a: " + num(a, " "));
    //      bug("List b: " + num(b, " "));
    //      for (int i=0; i < a.size(); i++) {
    //        bug(num(a.get(i)) + " == " + num(b.get(i)) + "?");
    //        if (a.get(i).isSameLocation(b.get(i))) {
    //          bug("Same so far (" + i + ")...");
    //        } else { 
    //          same = false;
    //          break;
    //        }
    //      }
    //      if (same) {
    //        bug("Doh, same point lists.");
    //      }
    //    }
    for (Pt pt : a) {
      Pt near = getNearestPointOnPolyline(pt, b);
      if (near != null) {
        double dist = near.distance(pt);
        if (dist <= thresh) {
          sorterB.add(near.copyXYT());
          sorterA.add(pt.copyXYT());
        } else {
          break;
        }
      }
    }
    for (Pt pt : b) {
      Pt near = getNearestPointOnPolyline(pt, a);
      if (near != null) {
        double dist = near.distance(pt);
        if (dist <= thresh) {
          sorterA.add(near.copyXYT());
          sorterB.add(pt.copyXYT());
        } else {
          break;
        }
      }
    }
    retA.addAll(sorterA);
    retB.addAll(sorterB);
  }

  /**
   * Returns the index of the point in the list that is closest in time (absolute difference) to the
   * target point's time stamp.
   * 
   * @return the index, or -1 if none are found.
   */
  public static int seekByTimeNearest(Pt targetTime, List<Pt> points) {
    long target = targetTime.getTime();
    int bestIdx = -1;
    long bestDiff = Long.MAX_VALUE;
    for (int i = 0; i < points.size(); i++) {
      Pt pt = points.get(i);
      long diff = abs(pt.getTime() - target);
      if (bestIdx < 0 || (diff < bestDiff)) {
        bestIdx = i;
        bestDiff = diff;
      }
    }
    return bestIdx;
  }

  /**
   * 
   * @param pt
   * @param line1
   * @param line2
   * @param slop
   */
  public static boolean isPointInLineSegment(Pt pt, Pt line1, Pt line2, double slop) {
    boolean ret = false;
    double u = getPointSegmentParam(pt, line1, line2);
    //    bug("u: " + num(u));
    //    bug("Line segment param: " + num(u));
    if (u <= 1.0 && u >= 0.0) {
      double d = getDistanceBetweenPointAndLine(pt, new Line(line1, line2));
      //      bug("d: " + num(d));
      double minusSlop = d - slop;
      //      bug("minusSlop: " + num(minusSlop));
      ret = lt(minusSlop, 0, EQ_TOL);
      //      bug("ret: " + ret);
    }
    //    bug("isPointInLineSegment: " + num(u) + " --> " + ret);
    return ret;
  }

  public static double getPointSegmentParam(Pt pt, Pt line1, Pt line2) {
    double x1 = line1.getX();
    double x2 = line2.getX();
    double x3 = pt.getX();
    double y1 = line1.getY();
    double y2 = line2.getY();
    double y3 = pt.getY();
    double denom = ((x2 - x1) * (x2 - x1)) + ((y2 - y1) * (y2 - y1)); // line length squared
    double u = ((x3 - x1) * (x2 - x1) + (y3 - y1) * (y2 - y1)) / denom;
    return u;
  }

  /**
   * Given some input points that are on the ellipse surface, return a list of angles (in radians)
   * that increase. The input points may be on different sides of the 0-degree line, so for example
   * your points might be at 10, 350, and 320. This means the ellipse arc starts just above the
   * 0-degree line and moves clockwise. The return list of angles would be 320, 350, 370. (370 is
   * equivalent to 10 degrees plus a full rotation of the circle).
   * 
   * @param regionPoints
   *          a list of exactly three points that are on the ellipse arc surface.
   * @return three doubles representing the increasing angles, in radians. If the arc passes through
   *         the 0-degree line, some of the values will be greater than 2*pi.
   */
  public static List<Double> makeMonotonicallyIncreasingAngles(double t1, double t2, double t3) {
    double twoPi = Math.PI * 2;
    StringBuilder buf = new StringBuilder();
    buf.append(getAngleCode(t1, t2)); // a to b
    buf.append(getAngleCode(t2, t3)); // b to c
    buf.append(getAngleCode(t1, t3)); // a to c
    String code = buf.toString();
    // there are eight possible values for the angle code. +++, ++-, +-+, etc.
    // two aren't possible (++- and --+).
    // two others don't require adjustment (+++ and ---).
    // the remaining four require us to add 2pi to one or two values.
    if (code.equals("+-+")) {
      t1 = t1 + twoPi; // adjust a
    }
    if (code.equals("+--")) {
      t3 = t3 + twoPi; // adjust c
    }
    if (code.equals("-++")) {
      t1 = t1 + twoPi; // adjust a and b
      t2 = t2 + twoPi;
    }
    if (code.equals("-+-")) {
      t2 = t2 + twoPi; // adjust b and c
      t3 = t3 + twoPi;
    }
    if (code.equals("++-") || code.equals("--+")) {
      bug("Ugh, encountered an 'impossible' code.");
    }
    buf.setLength(0);
    buf.append(getAngleCode(t1, t2)); // a to b
    buf.append(getAngleCode(t2, t3)); // b to c
    buf.append(getAngleCode(t1, t3)); // a to c
    code = buf.toString();
    List<Double> angles = new ArrayList<Double>();
    angles.add(t1);
    angles.add(t2);
    angles.add(t3);
    if (code.equals("---")) {
      Collections.reverse(angles);
    }
    return angles;
  }

  private static double getArcAngle(Pt target, Pt center) {
    return atan2(target.y - center.y, target.x - center.x);
  }

  public static List<Pt> getCircularArc(Pt arc1, Pt arc2, Pt arc3, Pt center, int numSteps) {
    double arc1T = getArcAngle(arc1, center);
    double arc2T = getArcAngle(arc2, center);
    double arc3T = getArcAngle(arc3, center);
    List<Pt> surface = new ArrayList<Pt>();
    List<Double> arcParams = Functions.makeMonotonicallyIncreasingAngles(arc1T, arc2T, arc3T);
    double start = arcParams.get(0);
    double end = arcParams.get(2);
    double step = (end - start) / numSteps;
    double r = arc1.distance(center);
    if (step > EQ_TOL) {
      for (double t = start; t <= end; t += step) {
        surface.add(getCircularPoint(t, r, center));
      }
    }
    return surface;
  }

  /**
   * Returns a point on the circle boundary, parameterized by the given radial angle. If you call
   * this a bunch of times for t=0..2pi you sample the entire circle.
   */
  public static Pt getCircularPoint(double t, double r, Pt center) {
    double x = (r * cos(t));
    double y = (r * sin(t));
    Pt ret = new Pt(x + center.x, y + center.y);
    return ret;
  }

  private static char getAngleCode(double m, double n) {
    char ret = '+'; // non-decreasing: m < n
    if (m > n) {
      ret = '-';
    }
    return ret;
  }

  public static boolean isPointOnPolyline(Pt target, List<Pt> points, double slop) {
    boolean ret = false;
    for (int i = 0; i < points.size() - 1; i++) {
      if (isPointInLineSegment(target, points.get(i), points.get(i + 1), slop)) {
        ret = true;
        break;
      }
    }
    return ret;
  }

}
