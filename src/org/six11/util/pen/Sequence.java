// $Id: Sequence.java 284 2012-03-15 19:30:44Z gabe.johnson@gmail.com $

package org.six11.util.pen;

import java.util.NoSuchElementException;
import java.awt.geom.FlatteningPathIterator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.six11.util.data.Statistics;

import static org.six11.util.Debug.bug;
import static org.six11.util.Debug.num;

/**
 * Implementation of a bi-directional linked list for Pt objects, which are just Point2D.Double
 * objects with some added stuff. Many of the methods are deferred to Pt. This class handles the
 * issues that concern the lot of them -- e.g. moving all points, selecting/deseleting all points,
 * getting the size of the list, etc.
 **/
public class Sequence implements Shape, Iterable<Pt> {

  private static int ID_COUNTER = 1;
  protected List<Pt> points;
  //  protected DrawFunction drawFunction;
  protected Map<String, Object> attributes;
  Statistics statsX, statsY;
  double roughDensity, roughArea;

  protected int id;

  /**
   * True if this sequence represents the boundary of a 2D shape, false if it simply represents a
   * polyline.
   */
  protected boolean closedRegion;
  private double statsNormFactor;

  public Sequence() {
    this(++ID_COUNTER);
  }

  public Sequence(int id) {
    this.id = id;
    points = new ArrayList<Pt>();
    closedRegion = false;
    attributes = new HashMap<String, Object>();
    ID_COUNTER = Math.max(ID_COUNTER, id);
  }

  public Sequence(List<Pt> data) {
    this();
    for (Pt pt : data) {
      add(pt);
    }
  }

  /**
   * Returns true if the sequence is nondecreasing in time.
   */
  public boolean isForward() {
    boolean ret = true;
    long prev = 0;
    for (Pt pt : this) {
      if (pt.getTime() < prev) {
        ret = false;
        break;
      }
      prev = pt.getTime();
    }
    return ret;
  }

  /**
   * Returns true if the sequence is nonincreasing in time.
   */
  public boolean isBackward() {
    boolean ret = true;
    long prev = Long.MAX_VALUE;
    for (Pt pt : this) {
      if (pt.getTime() > prev) {
        ret = false;
        break;
      }
      prev = pt.getTime();
    }
    return ret;
  }

  public int getId() {
    return id;
  }

  public int getNamedPointIndex(String key) {
    return (Integer) getAttribute(key);
  }

  public void setNamedPoint(String key, Pt pt) {
    setAttribute(key, indexOf(pt));
  }

  public void clearNamedPoint(String key) {
    setAttribute(key, null);
  }

  public Set<String> getAttributeNames() {
    return attributes.keySet();
  }

  public boolean hasAttribute(String key) {
    return attributes.get(key) != null;
  }

  public Object getAttribute(String key) {
    return attributes.get(key);
  }

  public void setAttribute(String key, Object value) {
    attributes.put(key, value);
  }

  /**
   * Make a sequence with a default list of points. This is equivalent to making a blank sequence
   * and adding each point. If the input list of points represents a closed sequence, set the second
   * param (closed) to true.
   * 
   * This does NOT ensure that the first and last points are coincident so you need to do that
   * outside of this code.
   */
  public Sequence(List<Pt> points, boolean closed) {
    this();
    for (Pt pt : points) {
      add(pt);
    }
    setClosedRegion(closed);
  }

  public String toString() {
    StringBuffer buf = new StringBuffer("[ ");
    for (Pt pt : this) {
      buf.append(num(pt) + " ");
    }
    buf.append("]");
    return buf.toString();
  }

  public boolean isClosedRegion() {
    return closedRegion;
  }

  public void setClosedRegion(boolean closedRegion) {
    this.closedRegion = closedRegion;
  }

  //  public void setDrawFunction(DrawFunction df) {
  //    this.drawFunction = df;
  //  }

  public Sequence copy() {
    Sequence ret = new Sequence();
    for (Pt pt : this) {
      ret.add(pt.copy());
    }
    return ret;
  }

  public Sequence copy(int beginInclusive, int endExclusive) {
    Sequence ret = new Sequence();
    for (int i = beginInclusive; i < endExclusive; i++) {
      ret.add(get(i));
    }
    return ret;
  }

  public Sequence getSubSequence(int beginInclusive, int endExclusive) {
    Sequence ret = new Sequence();
    for (int i = beginInclusive; i < endExclusive; i++) {
      ret.add(get(i));
    }
    return ret;
  }

  public Sequence getSubSequence(Pt startInclusive, Pt endInclusive) {
    int idxStart = indexOf(startInclusive);
    int idxEnd = indexOf(endInclusive);
    return getSubSequence(idxStart, idxEnd + 1);
  }

  /**
   * Return a unit vector representing the direction that points from the begining index towards the
   * end index.
   */
  public Vec getDirectionOfSubsequence(int beginInclusive, int endExclusive) {
    Pt s = get(beginInclusive);
    Pt e = get(endExclusive - 1);
    return new Vec(s, e).getUnitVector();
  }

  /**
   * Returns an interpolated point that falls on the sequence that is some curvilinear distance from
   * a given index.
   * 
   * @param beginIdx
   *          the index of the point to begin the search. Must be between 0 and size()-1.
   * @param curvilinearDistance
   *          the distance to travel from the starting point.
   * @param dir
   *          to move in the positive (larger index) direction, supply a positive number. To move in
   *          the negative (lesser index) direction, supply a non-positive number.
   */
  public Pt getInterpolatedPoint(int beginIdx, double curvilinearDistance, int dir) {
    int incr = (dir > 0 ? 1 : -1);
    Pt prev = null;
    Pt ret = null;
    for (int i = beginIdx; i >= 0 && i < size(); i += incr) {
      if (prev != null) {
        double chunkDist = getPathLength(Math.min(i, beginIdx), Math.max(i, beginIdx));
        bug("From " + beginIdx + " to " + i + ": " + num(chunkDist) + " of "
            + num(curvilinearDistance) + ". Too far? " + (chunkDist > curvilinearDistance));
        if (chunkDist > curvilinearDistance) {
          // now we know that we've gone prevDist units to prev, and it it is a little too far when
          // going to the current point at i, so simply interpolate the difference.
          double diff = chunkDist - curvilinearDistance;
          Vec lastPart = new Vec(prev, get(i)).getVectorOfMagnitude(diff);
          ret = lastPart.add(prev);
          break;
        }
      }
      prev = get(i);
    }
    //    bug("getInterpolatedPoint(" + beginIdx + ", " + num(curvilinearDistance) + ", " + dir
    //        + ") returns " + num(ret));
    return ret;
  }

  /**
   * This has the same semantics of List.indexOf(pt). It returns -1 if the point is not present.
   */
  public int indexOf(Pt pt) {
    return points.indexOf(pt);
  }

  public Pt get(int idx) {
    return points.get(idx);
  }

  public Pt getFirst() {
    return points.get(0);
  }

  public Pt getLast() {
    return points.get(points.size() - 1);
  }

  public void add(Pt pt) {
    points.add(pt);
  }

  public void remove(int idx) {
    points.remove(idx);
  }

  public void removeLast() {
    remove(points.size() - 1);
  }

  public int size() {
    return points.size();
  }

  /**
   * Returns the arc length of the entire sequence, assuming straight lines between each point pair.
   */
  public double length() {
    return getPathLength(0, points.size() - 1);
  }

  /**
   * Returns the arc length of the portion of the sequence beginning and ending at the given
   * indices, assuming straight lines between each pair of successive points.
   */
  public double getPathLength(int idxStartInclusive, int idxEndInclusive) {
    double ret = 0;
    if (hasAttribute("cachedPathLength")) {
      bug("Using cached");
      for (int i = idxStartInclusive; i < idxEndInclusive; i++) {
        ret += get(i).getDouble("cachedDist");
      }
    } else {
      ret = Functions.getPathLength(points, idxStartInclusive, idxEndInclusive);
    }
    return ret;
  }

  /**
   * Calculates the distance between each point <code>i</code> and the next <code>i+1</code> and
   * stores it in a <code>double</code> attribute keyed off the string "cachedDist" in point
   * <code>i</code>. Call this once and subsequent calls to getPathLength(int, int) will be more
   * efficient because it only needs to add these values. The last point's value is set to zero.
   */
  public void createPathLengthCache() {
    int n = size();
    String k = "cachedDist";
    for (int i = 0; i < n - 1; i++) {
      Pt here = get(i);
      Pt there = get(i + 1);
      double dist = here.distance(there);
      here.setDouble(k, dist);
    }
    getLast().setDouble(k, 0);
    setAttribute("cachedPathLength", true);
  }

  /**
   * For each point in the sequence, calculate the curvilinear distance from the start point and
   * stores it in each point's 'curvilinear-distance' property (double). This returns the total
   * curvilinear length of the sequence.
   */
  public double calculateCurvilinearDistances() {
    Pt prev = null;
    double sum = 0.0;
    for (Pt pt : this) {
      if (prev != null) {
        sum += prev.distance(pt);
      }
      pt.setDouble("curvilinear-distance", sum);
      prev = pt;
    }
    return getLast().getDouble("curvilinear-distance");
  }

  /**
   * Returns the length of the line segment that directly connects the first and last points. If
   * there are not at least two points in the sequence this returns zero.
   */
  public double getEndpointDistance() {
    double ret = 0.0;
    if (points.size() > 1) {
      Pt a = points.get(0);
      Pt b = points.get(points.size() - 1);
      ret = a.distance(b);
    }
    return ret;
  }

  /**
   * Simply returns the total arc length devided by the distance between endpoints. This checks for
   * division by zero, in which case Double.MAX_VALUE is returned.
   */
  public double getRoundaboutness() {
    double num = length();
    double den = getEndpointDistance();
    double ret = Double.MAX_VALUE;
    if (den != 0.0) {
      ret = num / den;
    }
    return ret;
  }

  /**
   * Returns the sum of each point's "curvature" attribute. If this attribute is not set on all of
   * the points on the line, you will get a bogus result.
   */
  public double getSignedCurvatureSum() {
    double ret = 0.0;
    for (Pt pt : points) {
      if (pt.hasAttribute("curvature")) {
        ret += pt.getDouble("curvature");
      }
    }
    return ret;
  }

  /**
   * Calculates the curvature for each point (using getCurvature()), and returns the sum of the
   * ABSOLUTE VALUE of the curves.
   */
  public double calculateCurvature(int windowSize) {
    double sum = 0.0;
    for (int i = 0; i < size(); i++) {
      sum += Math.abs(getCurvature(i, windowSize)); // sets the 'curvature' attribute in each point.
    }
    return sum;
  }

  public double getCurvature(int idx, int windowSize) {
    double ret = 0.0;
    int lower = idx - windowSize;
    int upper = idx + windowSize;
    if (lower >= 0 && upper < points.size()) {
      double dx = points.get(upper).x - points.get(lower).x;
      double dy = points.get(upper).y - points.get(lower).y;
      double numer = Math.atan2(dy, dx);
      double denom = getPathLength(lower, upper);
      ret = numer / denom;
    } else if (windowSize > 1) {
      ret = getCurvature(idx, windowSize - 1);
    }
    points.get(idx).setDouble("angle", ret);

    new RuntimeException(
        "getCurvature(int, int) is hosed --- need to compute curvature from angles.")
        .printStackTrace();
    return ret;
  }

  /**
   * This calculates the direction (the double "angle" in radians) and the signed curvature (the
   * "curvature" double value) for each point in the sequence. Curvature is simply the change in
   * angle from the preceding point to the next. For those points that are close to the edges of the
   * sequence, the nearest valid value is used. Curvature at the endpoints is defined to be zero.
   */
  public double calculateCurvatureEuclideanWindowSize(double windowEuclideanSize) {
    // double sum = 0.0;
    List<Pt> front = new ArrayList<Pt>(); // place to cache the points at beginning
    List<Pt> back = new ArrayList<Pt>(); // ... and the end. Need to assign angle value after.

    double frontAngle = -1.0;
    double backAngle = -1.0;
    for (int i = 0; i < size(); i++) {
      getAngleEuclideanWindowSize(i, windowEuclideanSize);
      if (!points.get(i).hasAttribute("angle")) {
        if (frontAngle >= 0.0) {
          back.add(points.get(i));
        } else {
          front.add(points.get(i));
        }
      } else {
        if (frontAngle < 0) {
          frontAngle = points.get(i).getDouble("angle");
        } else {
          backAngle = points.get(i).getDouble("angle");
        }
      }
    }

    // assign the front and back angles.
    for (Pt pt : front) {
      pt.setDouble("angle", frontAngle);
    }
    for (Pt pt : back) {
      pt.setDouble("angle", backAngle);
    }

    // Now that angle is set on every point, we can calculate curvature.
    double ret = 0.0;
    for (int i = 0; i < size(); i++) {
      Pt pt = points.get(i);
      if (i == 0) {
        pt.setDouble("curvature", 0.0);
      } else if (i == size() - 1) {
        pt.setDouble("curvature", 0.0);
      } else {
        double prev = points.get(i - 1).getDouble("angle");
        double next = points.get(i + 1).getDouble("angle");
        double curvature = next - prev;
        if (curvature < -Math.PI) {
          curvature = curvature + 2.0 * Math.PI;
        } else if (curvature > Math.PI) {
          curvature = curvature - 2.0 * Math.PI;
        }
        pt.setDouble("curvature", curvature);
        // bug(i + " curvature: " + Debug.num(next) + "-" + Debug.num(prev) + " = " +
        // Debug.num(pt.getDouble("curvature")));
      }
      ret += Math.abs(pt.getDouble("curvature"));
    }

    return ret;
  }

  /**
   * This installs the "angle" property as a double for the given point using the provided EUCLIDEAN
   * window size. If there is not enough room it will use a smaller window.
   */
  public double getAngleEuclideanWindowSize(int idx, double windowEuclideanSize) {
    int k = 2;
    double ret = 0;
    while (idx - k >= 0 && idx + k < size()
        && getPathLength(idx - k, idx + k) < windowEuclideanSize) {
      k += 1;
    }
    if (idx - k >= 0 && idx + k < size()) {
      double dx = points.get(idx + k).x - points.get(idx - k).x;
      double dy = points.get(idx + k).y - points.get(idx - k).y;
      double numer = Math.atan2(dy, dx);
      ret = numer;
      points.get(idx).setDouble("angle", ret);
    }
    return ret;
  }

  /**
   * Returns the absolue value of each point's "curvature" attribute. See getSignedCurvatureSum().
   */
  @Deprecated
  public double getAbsoluteCurvatureSum() {
    double ret = 0.0;
    for (Pt pt : points) {
      if (pt.hasAttribute("curvature")) {
        ret += Math.abs(pt.getDouble("curvature"));
      }
    }
    return ret;
  }

  public double calculateSpeed() {
    double sum = 0.0;
    for (int i = 0; i < size(); i++) {
      sum += getSpeed(i); // calculates speed and stores in "speed" attribute.
    }
    return sum;
  }

  public double getSpeed(int idx) {
    double ret = 0.0;
    if (idx > 0 && idx < (size() - 1)) {
      double numer = getPathLength(idx - 1, idx + 1);
      double denom = points.get(idx + 1).time - points.get(idx - 1).time;
      ret = numer / denom;
    }
    points.get(idx).setDouble("speed", ret);
    return ret;
  }

  /**
   * Generate a list of points that the sequence crosses itself within a certain localized area.
   * This is useful for detecting tight loops in the sequence without picking up self-intersections
   * that are far apart. If you are interested in finding all self intersection points, just supply
   * a negative number or Double.MAX_VALUE as your argument. A non-null (but possibly empty) list
   * will be returned.
   */
  public List<Pt> getSelfIntersectionPoints(double localDistance) {
    // find the places that this sequence crosses itself without
    // traversing more than 'localDistance' pixels.

    // a b c d e f g h
    // *----*----*----*-----*----*-----*----*-----*
    // 1 2 3 4 5 6 7 8 9
    //
    // Distance is calculated between the start points of the two line
    // segments under consideration. Adjacent line segments are never
    // compared. Say that points 1 and 5 are within localDistance, but
    // points 1 and 6 are not. This means that segment a will be
    // compared against c, d, and e.

    // Because it is likely that the segment distances involved will
    // be used frequently and will not change, I will cache it the
    // first time using the attribute name "dist_to_next".

    List<Pt> ret = new ArrayList<Pt>();

    double runDist;
    Line lineA = new Line();
    Line lineB = new Line();
    for (int i = 0; i < (points.size() - 3); i++) {
      lineA.push(points.get(i));
      if (lineA.isValid()) {
        runDist = cacheSegmentData(lineA);
        lineB.clear();
        for (int j = i + 1; j < (points.size() - 1); j++) {
          lineB.push(points.get(j));
          if (lineB.isValid()) {
            runDist += cacheSegmentData(lineB);
            if (!lineA.isAdjacentTo(lineB) && lineA.intersectsLine(lineB)) {
              IntersectionData ix = new IntersectionData(lineA, lineB);
              if (ix.intersectsInSegments() && (!ret.contains(ix.getIntersection()))) {
                ret.add(ix.getIntersection());
              }
            }
          }
          if (runDist > localDistance) {
            break;
          }
        }
      }
    }
    return ret;
  }

  private double cacheSegmentData(Line line) {
    if (!line.getStart().hasAttribute("dist_to_next")) {
      double dist = line.getLength();
      line.getStart().setAttribute("dist_to_next", dist);
    }
    return (Double) line.getStart().getAttribute("dist_to_next");
  }

  public List<Pt> getPoints() {
    return points;
  }

  public Iterator<Pt> iterator() {
    return points.iterator();
  }

  /**
   * Get an Iterable<Pt> that runs from idx up to and including the last Pt.
   */
  public Iterable<Pt> forward(final int idx) {
    return from(idx, size() - 1);
  }

  /**
   * Get an Iterable<Pt> that runs from idx down to and including the first (index 0) Pt.
   */
  public Iterable<Pt> backward(final int idx) {
    return from(idx, 0);
  }

  public Iterable<Pt> from(final int idxA, final int idxB) {
    return new Iterable<Pt>() {
      public Iterator<Pt> iterator() {
        return new SequenceCursor(Sequence.this, idxA, idxB);
      }
    };
  }

  public Rectangle getBounds() {
    Rectangle2D twodee = getBounds2D();
    return new Rectangle((int) twodee.getX(), (int) twodee.getY(), (int) twodee.getWidth(),
        (int) twodee.getHeight());
  }

  public Rectangle2D getBounds2D() {
    return Functions.getSequenceBoundingBox(this);
  }

  public boolean contains(double x, double y) {
    int count = 0;
    if (closedRegion) {
      count = Functions.getCrossingNumber(new Pt(x, y), this.getPoints());
    }
    return (count % 2) == 1; // true if count is odd
  }

  // public Sequence transform(SequenceFunction sf) {
  // Sequence ret = new Sequence();
  // for (Pt pt : this) {
  // ret.add(sf.transform(pt));
  // }
  // return ret;
  // }

  public boolean containsVertex(Pt target) {
    boolean ret = false;
    for (Pt pt : this) {
      if (Functions.eq(pt, target, Functions.EQ_TOL)) {
        ret = true;
        break;
      }
    }
    return ret;
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
    return new PtPathIterator(affine);
  }

  public PathIterator getPathIterator(AffineTransform affine, double flatness) {
    return new FlatteningPathIterator(getPathIterator(affine), flatness);
  }

  //  public void draw(Graphics2D g) {
  //    drawFunction.draw(this, g);
  //  }

  /**
   * A PathIterator for going through a Sequence as though it were a Shape. (in fact, this Sequence
   * IS a Shape, and this is what Sequence uses).
   */
  private class PtPathIterator implements PathIterator {
    SequenceCursor cursor;
    AffineTransform affine;

    PtPathIterator(AffineTransform affine) {
      this.cursor = new SequenceCursor(Sequence.this);
      this.affine = affine;
    }

    public int getWindingRule() {
      return WIND_NON_ZERO;
    }

    public boolean isDone() {
      return !cursor.hasNext();
    }

    public void next() {
      cursor.next();
    }

    public int currentSegment(float[] coords) {
      if (isDone()) {
        throw new NoSuchElementException("Sequence path iterator out of bounds");
      }
      int type = cursor.getCurrentIdx() == 0 ? SEG_MOVETO : SEG_LINETO;
      coords[0] = (float) get(cursor.getCurrentIdx()).getX();
      coords[1] = (float) get(cursor.getCurrentIdx()).getY();
      if (affine != null) {
        affine.transform(coords, 0, coords, 0, cursor.getCurrentIdx() == 0 ? 1 : 3);
      }
      return type;
    }

    public int currentSegment(double[] coords) {
      if (isDone()) {
        throw new NoSuchElementException("Sequence path iterator out of bounds");
      }
      int type = cursor.getCurrentIdx() == 0 ? SEG_MOVETO : SEG_LINETO;
      coords[0] = get(cursor.getCurrentIdx()).getX();
      coords[1] = get(cursor.getCurrentIdx()).getY();
      if (affine != null) {
        affine.transform(coords, 0, coords, 0, cursor.getCurrentIdx() == 0 ? 1 : 3);
      }
      return type;
    }
  }

  public Sequence getReverseSequence() {
    Sequence ret = new Sequence();
    for (int i = points.size() - 1; i >= 0; i--) {
      ret.add(points.get(i));
    }
    return ret;
  }

  /**
   * Returns a rough downsample of the input sequence. The returned list of points are at least d
   * units apart along the sequence, except for the last one, which might be close to penultimate
   * point.
   */
  public List<Pt> getDownsample(double d) {
    List<Pt> ret = new ArrayList<Pt>();
    Pt cursor = null;
    double distSinceLast = 0;
    for (Pt pt : points) {
      if (cursor != null) {
        double thisDist = cursor.distance(pt);
        distSinceLast = distSinceLast + thisDist;
        if (distSinceLast > d) {
          ret.add(pt);
          distSinceLast = 0;
        }
      }
      cursor = pt;
    }
    if (ret.get(ret.size() - 1) != cursor) {
      ret.add(cursor);
    }
    return ret;
  }

  public long getDuration() {
    return getLast().getTime() - getFirst().getTime();
  }

  public double getRoughDensity(double normalizationFactor) {
    if (statsNormFactor != normalizationFactor || statsX == null || statsY == null) {
      initRough(normalizationFactor);
    }
    return roughDensity;
  }

  private void initRough(double normalizationFactor) {
    statsNormFactor = normalizationFactor;
    statsX = new Statistics();
    statsY = new Statistics();
    // Note: there is a better way to do this. Normalizing the sequence is expensive. 
    // We could use the line length when calculating density instead.
    Sequence seq = Functions.getNormalizedSequence(this, 5.0 / statsNormFactor);
    for (Pt pt : seq) {
      statsX.addData(pt.getX());
      statsY.addData(pt.getY());
    }
    double dx = (statsX.getMax() - statsX.getMin()) * normalizationFactor;
    double dy = (statsY.getMax() - statsY.getMin()) * normalizationFactor;
    double densityX = ((double) statsX.getN()) / dx;
    double densityY = ((double) statsY.getN()) / dy;
    roughArea = dx * dy;
    roughDensity = densityX * densityY;
  }

  public double getRoughArea(double normalizationFactor) {
    if (statsNormFactor != normalizationFactor || statsX == null || statsY == null) {
      initRough(normalizationFactor);
    }
    return roughArea;
  }
  
  public double getRoughDX(double normalizationFactor) {
    if (statsNormFactor != normalizationFactor || statsX == null || statsY == null) {
      initRough(normalizationFactor);
    }
    return (statsX.getMax() - statsX.getMin());
  }
  
  public double getRoughDY(double normalizationFactor) {
    if (statsNormFactor != normalizationFactor || statsX == null || statsY == null) {
      initRough(normalizationFactor);
    }
    return (statsY.getMax() - statsY.getMin());
  }

  public Pt replace(int i, Pt pt) {
    Pt old = points.remove(i);
    points.add(i, pt);
    return old;
  }

}
