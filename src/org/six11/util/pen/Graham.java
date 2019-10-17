// $Id: Graham.java 157 2011-10-18 21:04:52Z gabe.johnson@gmail.com $

package org.six11.util.pen;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import org.six11.util.Debug;

import java.io.FileWriter;

/**
 * Implements the Graham Scan convex hull finder for a point set.
 * 
 *
 * @author Gabe Johnson <johnsogg@cmu.edu>
 */
public class Graham {

  public static void main(String[] args) {
    // List<Pt> unsorted = new ArrayList<Pt>();
    // if (args.length == 0) {
    // System.out.println("give me x y pairs, or a single number indicating "
    // + " number of random points to generate.");
    // } else if (args.length == 1) {
    // // args[0] is a filename (read data points from file)
    // unsorted = Sequence.loadFromFile(args[0]).getPoints();
    // } else {
    // for (int i = 0; i < args.length; i += 2) {
    // unsorted.add(new Pt(Double.valueOf(args[i]), Double.valueOf(args[i + 1])));
    // }
    // }
    // // all of the above served to populate a list of points called
    // // 'unsorted'. In a single call, I can get the convex hull:
    // List<Pt> hull = Graham.getConvexHull(unsorted);
    // for (Pt pt : hull) {
    // System.out.println(pt.toString());
    // }
    System.out.println("Broken until Sequence.loadFromFile() has a replacement.");
  }

  public static void outputFile(String file, List<Pt> points) {
    try {
      FileWriter writer = new FileWriter(file);
      for (Pt pt : points) {
        writer.write(pt.getX() + "\t" + pt.getY() + "\n");
      }
      writer.close();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public static List<Pt> makeList(Line line) {
    List<Pt> blah = new ArrayList<Pt>();
    blah.add(line.getStart());
    blah.add(line.getEnd());
    return blah;
  }

  public static List<Pt> getConvexHull(List<Pt> unsorted) {
    if (unsorted.size() < 3) {
      List<Pt> ret = new ArrayList<Pt>();
      ret.addAll(unsorted);
      return ret;
    }

    // 1. Sort all points in 'unsorted' using their x coordinates into list 'sorted'.
    List<Pt> sorted = new ArrayList<Pt>();
    sorted.addAll(unsorted);
    Collections.sort(sorted, Pt.sortByX);

    // Assign leftmost and rightmost point, and remove from sorted list.
    Pt left = sorted.remove(0);
    Pt right = sorted.remove(sorted.size() - 1);

    Pt pt;
    int partitionSide;
    List<Pt> upper = new ArrayList<Pt>();
    List<Pt> lower = new ArrayList<Pt>();

    // Put each point from the sorted list into the 'lower' or 'upper' list. The assignment is based
    // on which side of the partition line from 'left' to 'right' each point falls.
    while (!sorted.isEmpty()) {
      pt = sorted.remove(0); // remove Point from sorted list
      partitionSide = Functions.getPartition(pt, left, right);
      if (partitionSide == Functions.PARTITION_LEFT) {
        upper.add(pt); // add Point to the end of array upper
        removeDuplicateX(upper, Functions.PARTITION_LEFT);
      } else {
        lower.add(pt); // add Point to the end of array lower
        removeDuplicateX(lower, Functions.PARTITION_RIGHT);
      }
    }

    // add 'right' to the lists. This is needed to ensure that the
    // hulls use the rightmost point.
    lower.add(right);
    upper.add(right);

    //
    // Construct the lower hull
    //
    List<Pt> lower_hull = new ArrayList<Pt>();
    lower_hull.add(left); // Add left to lower_hull

    while (!lower.isEmpty()) { // While lower is not empty
      pt = lower.remove(0); // pop first Point of lower
      lower_hull.add(pt); // add Point to the end of lower_hull

      // while size(lower_hull has at least 3 and the those points are not convex
      while (lower_hull.size() >= 3
          && lastThreeNotConvex(lower_hull, Functions.PARTITION_LEFT, false)) {
        // remove the next to last element from lower_hull
        lower_hull.remove(lower_hull.size() - 2);
      }
    }

    //
    // Construct the upper hull
    //
    List<Pt> upper_hull = new ArrayList<Pt>();
    upper_hull.add(left); // Add left to upper_hull

    while (!upper.isEmpty()) { // While upper is not empty
      pt = upper.remove(0); // pop first Point of upper
      upper_hull.add(pt); // add Point to the end of upper_hull

      // while size(upper_hull has at least 3 and the those points are not convex
      while (upper_hull.size() >= 3
          && lastThreeNotConvex(upper_hull, Functions.PARTITION_RIGHT, false)) {
        // remove the next to last element from upper_hull
        upper_hull.remove(upper_hull.size() - 2);
      }
    }

    // Merge upper_hull and lower_hull to form hull
    List<Pt> hull = new ArrayList<Pt>();
    lower_hull.remove(lower_hull.size() - 1);
    hull.addAll(lower_hull);
    Collections.reverse(upper_hull); // begin at 'right'
    hull.addAll(upper_hull);

    if (hull.get(0).equals(hull.get(hull.size() - 1))) {
      hull.remove(hull.size() - 1);
    }

    // it makes life easier for users of the resulting hull if it
    // adheres to the right-hand rule.
    if (lastThreeNotConvex(hull, Functions.PARTITION_LEFT, false)) {
      Collections.reverse(hull);
    }

    return hull;

  }

  public static boolean lastThreeNotConvex(List<Pt> hull, int violatingPartitionResult,
      boolean debug) {
    boolean ret = false;
    int n = hull.size();
    if (n >= 3) {
      int partition = Functions.getPartition(hull.get(n - 2), hull.get(n - 3), hull.get(n - 1));
      ret = (partition == violatingPartitionResult || partition == Functions.PARTITION_ON_BORDER);
    }
    if (debug) {
      Debug.out("Graham", "last three convex at size=" + hull.size() + ": " + ret);
    }
    return ret;
  }

  public static void removeDuplicateX(List<Pt> list, int partitionDir) {
    if (list.size() < 2) {
      // Debug.out("Graham", "list too small, bailing");
      return;
    }
    Pt a = list.get(list.size() - 1);
    Pt b = list.get(list.size() - 2);
    // Debug.out("Graham", "same? " + Debug.num(a) + " and " + Debug.num(b));
    if (Functions.eq(a.getX(), b.getX(), Functions.EQ_TOL)) {
      // Debug.out("Graham", "found a dupe: " + Debug.num(a) + " and " + Debug.num(b));
      if (partitionDir == Functions.PARTITION_LEFT) {
        // remove the one with the larger Y value
        list.remove(a.getY() > b.getY() ? a : b);
      } else {
        // remove the one with the smaller Y value
        list.remove(a.getY() > b.getY() ? b : a);
      }
    }
  }

}
