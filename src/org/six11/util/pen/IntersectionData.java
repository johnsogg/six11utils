// $Id: IntersectionData.java 108 2011-01-08 00:53:46Z gabe.johnson@gmail.com $

package org.six11.util.pen;

import org.six11.util.Debug;

/**
 * Gives you information regarding the intersection of two lines as specified by line segments. If
 * the two lines are not parallel, then an intersection exists. A frequent use of finding line
 * intersections is to know if two line segments intersect (e.g. do these two lines intersect
 * somewhere between these four points). You can get at this information using the
 * intersectsInSegments() method.
 **/
public class IntersectionData {

  double r; // parameter for line a
  double s; // parameter for line b

  double denominator; // for both r and s
  double num_r; // numerator for r
  double num_s; // numerator for s

  boolean parallel; // true if both lines go same direction
  boolean collinear; // true if lines are along same linear path
  boolean intersectInSegments; // true if lines intersect between their endpoints

  Line one;
  Line two;

  Pt intersection;

  public IntersectionData(Line one, Line two) {
    this.one = one;
    this.two = two;

    Pt a = one.getStart();
    Pt b = one.getEnd();
    Pt c = two.getStart();
    Pt d = two.getEnd();

    // @formatter: off
    //
    //     (Ay - Cy) (Dx - Cy) - (Ax - Cx) (Dy - Cy)
    // r = -----------------------------------------
    //     (Bx - Ax) (Dy - Cy) - (By - Ay) (Dx - Cx)
    //
    //     (Ay - Cy) (Bx - Ax) - (Ax - Cx) (By - Ay)
    // s = -----------------------------------------
    //     (Bx - Ax) (Dy - Cy) - (By - Ay) (Dx - Cx)
    //
    // @formatter: on

    num_r = (a.getY() - c.getY()) * (d.getX() - c.getX()) - (a.getX() - c.getX())
        * (d.getY() - c.getY());
    num_s = (a.getY() - c.getY()) * (b.getX() - a.getX()) - (a.getX() - c.getX())
        * (b.getY() - a.getY());
    denominator = (b.getX() - a.getX()) * (d.getY() - c.getY()) - (b.getY() - a.getY())
        * (d.getX() - c.getX());

    parallel = (denominator == 0.0);
    collinear = (parallel && num_r == 0.0);

    if (!parallel) {
      r = num_r / denominator;
      s = num_s / denominator;
      intersectInSegments = r >= 0.0 && r <= 1.0 && s >= 0.0 && s <= 1.0;
      double intersectionX = a.getX() + r * (b.getX() - a.getX());
      double intersectionY = a.getY() + r * (b.getY() - a.getY());
      intersection = new Pt(intersectionX, intersectionY);
    }
  }

  public static void main(String[] args) {
    Pt a = new Pt(Double.parseDouble(args[0]), Double.parseDouble(args[1]));
    Pt b = new Pt(Double.parseDouble(args[2]), Double.parseDouble(args[3]));
    Pt c = new Pt(Double.parseDouble(args[4]), Double.parseDouble(args[5]));
    Pt d = new Pt(Double.parseDouble(args[6]), Double.parseDouble(args[7]));
    Line one = new Line(a, b);
    Line two = new Line(c, d);
    IntersectionData id = new IntersectionData(one, two);
    Pt cross = id.getIntersection();
    Debug.out("IntersectionData", Debug.num(one) + " and " + Debug.num(two) + " intersect at "
        + Debug.num(cross) + "(r=" + id.getLineOneParam() + ")");
  }

  public double getLineOneParam() {
    return r;
  }

  public double getLineTwoParam() {
    return s;
  }

  public boolean isParallel() {
    return parallel;
  }

  public boolean isCollinear() {
    return collinear;
  }

  public boolean isSameSegments() {
    return ((one.getStart().equals(two.getStart()) && one.getEnd().equals(two.getEnd())) || (one
        .getStart().equals(two.getEnd()) && one.getEnd().equals(two.getStart())));
  }

  public boolean intersectsInSegments() {
    return intersectInSegments;
  }

  public boolean intersectsOnLineOne() {
    return (r >= 0 && r <= 1);
  }

  public boolean intersectsOnLineTwo() {
    return (s >= 0 && s <= 1);
  }

  public boolean intersectsStrictlyInsideSegments() {
    return (r > 0 && r < 1) && (s > 0 && s < 1) && !Functions.eq(r, 0, Functions.EQ_TOL)
        && !Functions.eq(r, 1, Functions.EQ_TOL) && !Functions.eq(s, 0, Functions.EQ_TOL)
        && !Functions.eq(s, 1, Functions.EQ_TOL);
  }

  public Line getLineOne() {
    return one;
  }

  public Line getLineTwo() {
    return two;
  }

  public Pt getIntersection() {
    return intersection;
  }

}
