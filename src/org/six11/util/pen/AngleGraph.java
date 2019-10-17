package org.six11.util.pen;

/**
 * A datastructure that stores line segments ordered by their angle.
 * 
 * The interest here is to be able to locate line segments that have some angular relationship with
 * some other line segment. For example, we might want a list of segments that are approximately
 * parallel or perpendicular to a stroke. This datastructure lets you search the possibly large set
 * of possibilities efficiently.
 * 
 * A line segment's angle is determined by translating the line such that the 'left-most' endpoint
 * at the origin. This is the point with the smaller X value is considered left-most (in case of a
 * tie, the one with the larger Y value is the 'left most' point). The angle then is atan2(y,x) of
 * the other point.
 * 
 * In case you don't have a trig co-processor in your brain, here's what the graphs look like (after
 * aligning the left-most point to the make believe origin):
 * 
 * Horizontal lines have angles right around zero. If they dip below the x axis they are slightly
 * positive. Completely vertical lines, and those that are nearly vertical but end below the x axis
 * have positive angles and are close to Pi/2. Nearly vertical lines that end above the x axis have
 * negative angles and are close to -Pi/2.
 * 
 * If you ask for an angle that is outside the range -Pi/2..Pi/2 it will do the right thing by
 * wrapping the range around. Therefore, nearly vertical lines that have a negaive angle are almost
 * parallel with vertical lines that have a positive angle.
 * 
 * @author Gabe Johnson <johnsogg@cmu.edu>
 */
public class AngleGraph {
//
//  public static void main(String[] args) {
//
//  }
//
//  List<AntSegment> segments;
//  static Comparator<AntSegment> orderByAngle = new Comparator<AntSegment>() {
//
//    public int compare(AntSegment o1, AntSegment o2) {
//      return ((Double) o1.getFixedAngle()).compareTo(o2.getFixedAngle());
//    }
//
//  };
//
//  public AngleGraph() {
//    segments = new ArrayList<AntSegment>();
//  }
//
//  public void add(AntSegment seg) {
//    int where = Collections.binarySearch(segments, seg, orderByAngle);
//    if (where < 0) {
//      where = (where + 1) * -1;
//    }
//    segments.add(where, seg);
//  }
//
//  public void remove(AntSegment p) {
//    segments.remove(p);
//  }
//
//  public Set<AntSegment> getNear(AntSegment in, double angle, double tolerance) {
//    Set<AntSegment> buddies = getNear(in.getFixedAngle() + angle, tolerance);
//    buddies.remove(in);
//    return buddies;
//  }
//
//  /**
//   * Gives a set of line segments that are about the given angle, plus or minus the tolerance. For
//   * example, say your input segment is almost horizontal (angle is about 0.1). If you are
//   * interested in parallel lines, supply angle of 0.1. If you want perpendicular lines, use angle
//   * of 0.1 + Pi/2.
//   * 
//   * Then, supply a tolerance that describes how much error you're willing to deal with. A pretty
//   * tight tolerance is 12 degrees (0.21 radians); a loose tolerance is 30 degrees (0.52 radians).
//   * These are the values Alvarado used in her thesis. The returned segments have angles that are
//   * +/- tolerance/2 of the angle.
//   * 
//   * Both angles are interpreted as radians.
//   * 
//   * This returns the set of AntSegments that are within tolerance radians of the given angle.
//   */
//  public Set<AntSegment> getNear(double angle, double tolerance) {
//    double half = tolerance / 2;
//    Set<AntSegment> ret = new HashSet<AntSegment>();
//    double theta = getNormalizedAngle(angle);
//    double a = getNormalizedAngle(theta - half);
//    double b = getNormalizedAngle(theta + half);
//    if (a < theta && theta <= b) {
//      ret.addAll(search(a, b));
//    } else {
//      ret.addAll(search(-Math.PI / 2, b));
//      ret.addAll(search(a, Math.PI / 2));
//    }
//    return ret;
//  }
//
//  private Set<AntSegment> search(double a, double b) {
//    Set<AntSegment> ret = new HashSet<AntSegment>();
//    int idxA = search(a);
//    int idxB = search(b);
//    for (int cursor = idxA; cursor <= idxB; cursor++) {
//      ret.add(segments.get(cursor));
//    }
//    return ret;
//  }
//
//  /**
//   * Returns the index where a line segment with the given angle would be inserted.
//   */
//  private int search(double target) {
//    return search(0, segments.size(), target);
//  }
//
//  private int search(int low, int high, double target) {
//    int mid = (low + high) / 2;
//    double thisAngle = segments.get(mid).getFixedAngle();
//    if (thisAngle == target) {
//      return mid;
//    } else {
//      // when the search comes down to adjacent slots, the insert point is going to be before or
//      // between the slots.
//      if (high - low == 1) {
//        if (segments.get(low).getFixedAngle() > target) {
//          return low;
//        } else {
//          return Math.min(high, segments.size() - 1);
//        }
//      }
//      if (target < thisAngle) {
//        return search(low, mid, target);
//      } else {
//        return search(mid, high, target);
//      }
//    }
//  }
//
//  private double getNormalizedAngle(double angle) {
//    double ret = angle;
//    while (ret > Math.PI / 2) {
//      ret = ret - Math.PI;
//    }
//    while (ret < -Math.PI / 2) {
//      ret = ret + Math.PI;
//    }
//    return ret;
//  }
//
//  @SuppressWarnings("unused")
//  private static void bug(String what) {
//    Debug.out("AngleGraph", what);
//  }

}
