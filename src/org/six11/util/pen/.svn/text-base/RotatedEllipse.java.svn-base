package org.six11.util.pen;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.min;
import static java.lang.Math.max;
import static java.lang.Math.atan2;
import static java.lang.Math.toDegrees;

import static org.six11.util.Debug.num;
import static org.six11.util.Debug.bug;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 
 * @author Gabe Johnson <johnsogg@cmu.edu>
 */
public class RotatedEllipse {

  Pt center; // the centroid of this ellipse.
  double a; // the 'horizontal' radius when the ellipse isn't rotated.
  double b; // the 'vertical' radius when the ellipse isn't rotated.
  double ellipseRotation; // the radian angle of rotation

  private double arc1T, arc2T, arc3T; // angular parameter for the start, mid, and end points on an arc path.

  private boolean restrictedArc;
  private double extent;
  private double startAngle;
  private double midAngle;
  private double endAngle;

  public List<Pt> regionPoints;

  //  private List<Pt> restrictedArcPath;

  public RotatedEllipse(Pt center, double a, double b, double ellipseRotation) {
    this.center = center;
    this.a = a;
    this.b = b;
    this.ellipseRotation = ellipseRotation;
  }

  public boolean isRestrictedArc() {
    return restrictedArc;
  }

  public double getStartAngle() {
    return startAngle;
  }

  public double getExtent() {
    return extent;
  }

  public double getMajorRadius() {
    return max(a, b);
  }

  public double getMinorRadius() {
    return min(a, b);
  }

  public double getEccentricity() {
    double big = getMajorRadius();
    double small = getMinorRadius();
    big = big * big;
    small = small * small;
    double rootMe = ((big - small) / big);
    return Math.sqrt(rootMe);
  }

  /**
   * Specify three points that are *near* the ellipse surface. Points a and b are endpoints, and m
   * is any point on the inside.
   */
  public void setArcRegion(Pt a, Pt m, Pt b) {
    restrictedArc = true;
    Pt s = getCentroidIntersect(a);
    Pt mid = getCentroidIntersect(m);
    Pt e = getCentroidIntersect(b);
    regionPoints = new ArrayList<Pt>();
    regionPoints.add(s);
    regionPoints.add(mid);
    regionPoints.add(e);
    startAngle = Functions.makeAnglePositive(toDegrees(-atan2(s.y - center.y, s.x - center.x)));
    midAngle = Functions.makeAnglePositive(toDegrees(-atan2(mid.y - center.y, mid.x - center.x)));
    endAngle = Functions.makeAnglePositive(toDegrees(-atan2(e.y - center.y, e.x - center.x)));

    double midDecreasing = Functions.getNearestAnglePhase(startAngle, midAngle, -1);
    double midIncreasing = Functions.getNearestAnglePhase(startAngle, midAngle, 1);
    double endDecreasing = Functions.getNearestAnglePhase(midDecreasing, endAngle, -1);
    double endIncreasing = Functions.getNearestAnglePhase(midIncreasing, endAngle, 1);

    extent = 0;
    if (Math.abs(endDecreasing - startAngle) < Math.abs(endIncreasing - startAngle)) {
      extent = endDecreasing - startAngle;
    } else {
      extent = endIncreasing - startAngle;
    }
  }

  public List<Pt> getRegionPoints() {
    return regionPoints;
  }

  public void translate(double dx, double dy) {
    center.setLocation(center.getX() + dx, center.getY() + dy);
  }

  public double getRotation() {
    return ellipseRotation;
  }

  public void setRotation(double newRotation) {
    this.ellipseRotation = newRotation;
  }

  public RotatedEllipse copy() {
    return new RotatedEllipse(new Pt(center.getX(), center.getY()), a, b, ellipseRotation);
  }

  /**
   * Returns the point on the ellipse boundary that is between the centroid and the target point.
   * This is NOT the nearest point on the ellipse to the target, but that is more complicated to
   * calculate.
   */
  public Pt getCentroidIntersect(Pt target) {
    double x0 = target.x - center.x;
    double y0 = target.y - center.y;
    double xRot = x0 * cos(-ellipseRotation) + y0 * sin(-ellipseRotation);
    double yRot = -x0 * sin(-ellipseRotation) + y0 * cos(-ellipseRotation);
    double denom = Math.sqrt((a * a * yRot * yRot) + (b * b * xRot * xRot));
    double xTermRot = (a * b * xRot) / denom;
    double yTermRot = (a * b * yRot) / denom;
    double xTerm = xTermRot * cos(ellipseRotation) + yTermRot * sin(ellipseRotation);
    double yTerm = -xTermRot * sin(ellipseRotation) + yTermRot * cos(ellipseRotation);
    Pt xNeg = new Pt(-xTerm + center.x, -yTerm + center.y);
    Pt xPos = new Pt(xTerm + center.x, yTerm + center.y);
    double distNeg = xNeg.distance(target);
    double distPos = xPos.distance(target);
    Pt ret = distNeg < distPos ? xNeg : xPos;
    ret.setTime(target.getTime());
    return ret;
  }

  /**
   * Returns a point on the ellipse boundary, parameterized by the given radial angle. If you call
   * this a bunch of times for t=0..2pi you sample the entire ellipse.
   */
  public Pt getEllipticalPoint(double t) {
    double x = (a * cos(t));
    double y = (b * sin(t));
    double xRot = x * cos(ellipseRotation) + y * sin(ellipseRotation);
    double yRot = -x * sin(ellipseRotation) + y * cos(ellipseRotation);
    Pt ret = new Pt(xRot + center.x, yRot + center.y);
    ret.setDouble("ellipse_t", t);
    return ret;
  }

  /**
   * Given some point pt on the ellipse surface, what is the parameter t for which pt =
   * getEllipticalPoint(t)? I am nearly certain there is an analytic way to calculate this, but I
   * don't know what that is. So instead I do a binary search and return when the angle is within
   * some small amount (e.g. 0.001 radians).
   * 
   * @param pt
   * @return
   */
  public double searchForParameter(Pt pt) {
    double ret = 0;
    double ptTheta = getSignedEllipticalAngle(pt);
    Pt closest = null;
    double bestDiff = Double.POSITIVE_INFINITY;
    double step = (Math.PI * 2.0) / (double) 60;
    for (double t = 0; t <= (Math.PI * 2.0); t += step) {
      Pt stepPt = getEllipticalPoint(t);
      double stepTheta = getSignedEllipticalAngle(stepPt);
      double diff = Math.abs(ptTheta - stepTheta);
      if (diff < bestDiff) {
        closest = stepPt;
        bestDiff = diff;
      }
    }
    Pt left = getEllipticalPoint(getT(closest) - step);
    Pt right = getEllipticalPoint(getT(closest) + step);
    if (left.isSameLocation(right)) {
      bug("hmm... left same spot as right. step: " + step + ", left: " + num(left) + ", right: "
          + num(right));
    }
    ret = searchForParameter(left, right, pt);
    return ret;
  }

  private double getT(Pt p) {
    return p.getDouble("ellipse_t");
  }

  private double searchForParameter(Pt left, Pt right, Pt target) {
    double ret = Double.MAX_VALUE;
    double midM = Double.MAX_VALUE;
    double leftT, rightT;
    int numIterations = 0;
    StringBuilder logData = new StringBuilder();
    do {
      //      logData.setLength(0);
      leftT = getT(left);
      rightT = getT(right);
      double midT = (leftT + rightT) / 2.0;
      Pt mid = getEllipticalPoint(midT);
      Vec leftV = new Vec(center, left);
      Vec targetV = new Vec(center, target);
      Vec midV = new Vec(center, mid);
      double leftM = targetV.cross(leftV);
      midM = targetV.cross(midV);
      logData.append(String.format(
          "%1.2f\t%1.2f\t%1.2f\t%1.2f\t%1.2f\t%1.2f\t%1.2f\t%1.2f\t%1.2f\t%1.2f\t%1.2f\t%1.2f\n",
          leftT, midT, rightT, left.x, left.y, mid.x, mid.y, right.x, right.y, leftM, midM,
          getT(mid)));
      if (Math.signum(leftM) == Math.signum(midM)) {
        left = mid;
      } else {
        right = mid;
      }
      ret = getT(mid);

      if (numIterations > 40) {
        System.out.println("numIterations: " + numIterations);
        System.out
            .println("leftT\tmidT\trightT\tleft.x\tleft.y\tmid.x\tmid.y\tright.x\tright.y\tleftM\tmidM\tmidT");
        System.out.println(logData.toString());
        break;
      }
      numIterations++;
    } while (rightT - leftT > 0.01);
    //    } while (abs(midM) > 0.01);
    return ret;
  }

  public double getArea() {
    return Math.PI * a * b;
  }

  public Pt getCentroid() {
    return center;
  }

  public String getDebugString() {
    return "RotatedEllipse[maj:" + num(getMajorRadius()) + ", min: " + num(getMinorRadius())
        + ", rot: " + num(getRotation()) + ", center: " + num(center) + "]";
  }

  public double getParamA() {
    return a;
  }

  public double getParamB() {
    return b;
  }

  public Vec getAxisA() {
    return getVector(0).getScaled(a);
  }

  public Vec getAxisB() {
    return getVector(Math.PI / 2).getScaled(b);
  }

  public Vec getVector(double ang) {
    double x = (a * cos(ang));
    double y = (b * sin(ang));
    double xRot = x * cos(ellipseRotation) + y * sin(ellipseRotation);
    double yRot = -x * sin(ellipseRotation) + y * cos(ellipseRotation);
    return new Vec(xRot, yRot).getUnitVector();
  }

  /**
   * Returns the angle of the point on the ellipse in radians.
   * 
   * @param pt
   * @return
   */
  public double getEllipticalAngle(Pt target) {
    double ang = Math.toRadians(Functions.makeAnglePositive(toDegrees(-atan2(target.y - center.y,
        target.x - center.x))));
    return ang;
  }

  public double getSignedEllipticalAngle(Pt target) {
    double ang = atan2(target.y - center.y, target.x - center.x);
    target.setDouble("ellipse_theta", ang);
    return ang;
  }

  public final List<Pt> initArc() {
    Pt arc1 = getRegionPoints().get(0);
    Pt arc2 = getRegionPoints().get(1);
    Pt arc3 = getRegionPoints().get(2);
    arc1T = searchForParameter(arc1);
    arc2T = searchForParameter(arc2);
    arc3T = searchForParameter(arc3);
    List<Pt> surface = new ArrayList<Pt>();
    List<Double> arcParams = Functions.makeMonotonicallyIncreasingAngles(arc1T, arc2T, arc3T);
    double numSteps = 60;
    double start = arcParams.get(0);
    double end = arcParams.get(2);
    double step = (end - start) / numSteps;
    if (step < 0.001) {
      bug("Going to have issues. Step size is " + step);
    }
    try {
      for (double t = start; t <= end; t += step) {
        surface.add(getEllipticalPoint(t));
      }
    } catch (Throwable t) {
//      t.printStackTrace();
      System.out.println("Got " + t + ". Not showing stack trace because it would make me run out of heap space.");
      System.out.println("  start: " + num(start));
      System.out.println("    end: " + num(end));
      System.out.println("   step: " + num(step));
    }
    return surface;
  }
}
