// $Id: GenericPathIterator.java 43 2010-01-27 22:41:54Z gabe.johnson@gmail.com $

package org.six11.util.gui;

import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.ArrayList;
import java.util.NoSuchElementException;

/**
 * A programmable, reusable PathIterator. Give it some points and types, and use it! Make sure you
 * hit 'reset'.
 */
public class GenericPathIterator implements PathIterator {

  static {
    System.out.println("You should not use GenericPathIterator. I made this "
        + "before I was aware of GeneralPath, which is better.");
  }
  // this has no knowledge of the thing that it describes -- it's
  // just a list of move types and coordinates.
  List<Point2D> points = new ArrayList<Point2D>();
  List<Integer> types = new ArrayList<Integer>();
  int index = 0;

  public List<Point2D> getPoints() {
    return points;
  }

  public void add(int type, Point2D pt) {
    types.add(type);
    points.add(pt);
  }

  public int getWindingRule() {
    return WIND_NON_ZERO;
  }

  public boolean isDone() {
    return index >= points.size();
  }

  public void next() {
    index++;
  }

  public void reset() {
    index = 0;
  }

  public int currentSegment(float[] coords) {
    if (isDone()) {
      throw new NoSuchElementException("Sequence path iterator out of bounds");
    }
    int type = types.get(index);
    coords[0] = (float) points.get(index).getX();
    coords[1] = (float) points.get(index).getY();

    return type;
  }

  public int currentSegment(double[] coords) {
    if (isDone()) {
      throw new NoSuchElementException("Sequence path iterator out of bounds");
    }
    int type = types.get(index);
    coords[0] = points.get(index).getX();
    coords[1] = points.get(index).getY();
    return type;
  }

}
