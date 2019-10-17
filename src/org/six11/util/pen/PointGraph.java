package org.six11.util.pen;

/**
 * This is a datastructure that stores x,y,t points and may be efficiently searched to locate other
 * points based on search criteria.
 * 
 * @author Gabe Johnson <johnsogg@cmu.edu>
 */
public class PointGraph {
//
//  public transient boolean debugEnabled = false;
//
//  List<Pt> byX;
//  List<Pt> byY;
//  List<Pt> byT;
//
//  public PointGraph() {
//    this.byX = new ArrayList<Pt>();
//    this.byY = new ArrayList<Pt>();
//    this.byT = new ArrayList<Pt>();
//  }
//
//  /**
//   * Returns all the points sorted by their time stamps. The returned value is one of the backing
//   * lists, so if you modify it, the world might likely end.
//   */
//  public Collection<Pt> getPoints() {
//    return byT;
//  }
//
//  public void add(Pt pt) {
//    if (debugEnabled) {
//      Debug.stacktrace("Adding point " + pt.getID(), 4);
//    }
//    int where = Collections.binarySearch(byX, pt, Pt.sortByX);
//    if (where < 0) {
//      where = (where + 1) * -1;
//    }
//    byX.add(where, pt);
//
//    where = Collections.binarySearch(byY, pt, Pt.sortByY);
//    if (where < 0) {
//      where = (where + 1) * -1;
//    }
//    byY.add(where, pt);
//
//    where = Collections.binarySearch(byT, pt, Pt.sortByT);
//    if (where < 0) {
//      where = (where + 1) * -1;
//    }
//    byT.add(where, pt);
//  }
//
//  public int size() {
//    if (byX.size() != byY.size()) {
//      bug("byX not same size as byY!");
//    }
//    if (byX.size() != byT.size()) {
//      bug("byX not same size as byT!");
//    }
//    if (byY.size() != byT.size()) {
//      bug("byY not same size as byY!");
//    }
//    return byX.size();
//  }
//
//  public void remove(Pt pt) {
//    if (debugEnabled) {
//      Debug.stacktrace("Removing point " + pt.getID(), 4);
//    }
//    byX.remove(pt);
//    byY.remove(pt);
//    byT.remove(pt);
//  }
//
//  public Set<Pt> getNear(Pt target, double dist) {
//    // first, messily get all points that are in a square around the target.
//    Set<Pt> ret = getNearX(target, dist);
//    ret.retainAll(getNearY(target, dist));
//    // Now remove those that aren't strictly in the circle.
//    Set<Pt> doomed = new TreeSet<Pt>();
//    for (Pt pt : ret) {
//      if (target.distance(pt) > dist) {
//        doomed.add(pt);
//      }
//    }
//    ret.removeAll(doomed);
//    return ret;
//  }
//
//  @SuppressWarnings("unused")
//  private String getBugString(Collection<Pt> aList) {
//    StringBuilder buf = new StringBuilder();
//    buf.append("[");
//    for (Pt pt : aList) {
//      buf.append(Debug.num(pt) + " ");
//    }
//    return buf.toString().trim() + "]";
//  }
//
//  private void bugByIndex(Collection<Pt> points) {
//    int idx = 0;
//    for (Pt pt : points) {
//      Sequence seq = (Sequence) pt.getAttribute(SketchBook.SEQUENCE);
//      System.out.println(idx + "\t" + num(pt) + (seq != null ? " " + seq.getId() : ""));
//      idx++;
//    }
//  }
//
//  private Set<Pt> getNearX(Pt target, double dist) {
////    bug(" --------");
////    bug("Searching for " + num(target) + " within " + num(dist) + " in following list (X):");
////    bugByIndex(byX);
//    Set<Pt> xSet = new TreeSet<Pt>(Pt.sortById);
//    Pt x1 = new Pt(target.x - dist / 2, Double.MAX_VALUE);
//    Pt x2 = new Pt(target.x + dist / 2, Double.MAX_VALUE);
//    int idxA = -(Collections.binarySearch(byX, x1, Pt.sortByX) + 1);
//    int idxB = -(Collections.binarySearch(byX, x2, Pt.sortByX) + 1);
////    bug("Index range: " + idxA + " to " + idxB);
//    for (int i = idxA; i < idxB; i++) {
//      xSet.add(byX.get(i));
//    }
////    bug("Returning X set:");
////    bugByIndex(xSet);
////    bug(" --------");
//    return xSet;
//  }
//
//  private Set<Pt> getNearY(Pt target, double dist) {
//    Set<Pt> ySet = new TreeSet<Pt>(Pt.sortById);
//    Pt y1 = new Pt(Double.MAX_VALUE, target.y - dist / 2);
//    Pt y2 = new Pt(Double.MAX_VALUE, target.y + dist / 2);
//    int idxA = -(Collections.binarySearch(byY, y1, Pt.sortByY) + 1);
//    int idxB = -(Collections.binarySearch(byY, y2, Pt.sortByY) + 1);
//    for (int i = idxA; i < idxB; i++) {
//      ySet.add(byY.get(i));
//    }
//    return ySet;
//  }
//
//  /**
//   * Returns a list of points (most recent to least recent) that are within the given timeout of t.
//   * This only looks backward in time, not forward.
//   */
//  public List<Pt> getRecent(long timeout, long t) {
//    List<Pt> ret = new ArrayList<Pt>();
//    long timeThresh = t - timeout;
//    for (int i = byT.size() - 1; i >= 0; i--) {
//      Pt pt = byT.get(i);
//      if (pt.getTime() <= t && pt.getTime() >= timeThresh) {
//        ret.add(pt);
//      }
//      if (pt.getTime() < timeThresh) {
//        break;
//      }
//    }
//    return ret;
//  }
//
//  @SuppressWarnings("unused")
//  private static void bug(String what) {
//    Debug.out("PointGraph", what);
//  }
//
//  public void addAll(Sequence seq) {
//    for (Pt pt : seq) {
//      add(pt);
//    }
//  }
//
//  public Pt getNearest(Pt pt) {
//    // This is probably a braindead way of finding the nearest point, but it works. I just wanted
//    // to avoid iterating through every point.
//    if (byX.size() == 0) {
//      return null;
//    }
//    double dist = 10;
//    Set<Pt> near = null;
//    while (near == null || near.size() == 0) {
//      near = getNear(pt, dist);
//      dist = dist + 20;
//    }
//    double bestDist = Double.MAX_VALUE;
//    Pt bestPt = null;
//    for (Pt zed : near) {
//      double thisDist = zed.distance(pt);
//      if (thisDist < bestDist) {
//        bestPt = zed;
//        bestDist = thisDist;
//      }
//    }
//    return bestPt;
//  }

}
