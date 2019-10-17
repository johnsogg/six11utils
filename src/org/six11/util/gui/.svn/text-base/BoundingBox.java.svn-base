// $Id$

package org.six11.util.gui;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.List;

import org.six11.util.Debug;
import org.six11.util.pen.Pt;

/**
 * A handy class for determining the bounding box of 2D objects--just give it some points and it
 * will give you the bounding box. This class is only necessary because the Java Rectangle classes
 * include the origin in the rectangle by default, which isn't what we want at all.
 **/
public class BoundingBox {
  boolean hasX = false;
  double minX;
  double maxX;

  boolean hasY = false;
  double minY;
  double maxY;

  public BoundingBox() {

  }

  public BoundingBox(Collection<Pt> points) {
    for (Pt pt : points) {
      add(pt);
    }
  }

  public BoundingBox(Rectangle2D rect) {
    add(rect.getMinX(), rect.getMinY());
    add(rect.getMaxX(), rect.getMaxY());
  }

  public BoundingBox copy() {
    BoundingBox ret = new BoundingBox();
    ret.hasX = hasX;
    ret.minX = minX;
    ret.maxX = maxX;
    ret.hasY = hasY;
    ret.minY = minY;
    ret.maxY = maxY;
    return ret;
  }

  public void translate(Pt amt) {
    translate(amt.getX(), amt.getY());
  }

  public void translate(double dx, double dy) {
    minX = minX + dx;
    maxX = maxX + dx;
    minY = minY + dy;
    maxY = maxY + dy;
  }

  public String toString() {
    StringBuilder buf = new StringBuilder();
    buf.append("BoundingBox[");
    if (hasX) {
      buf.append(Debug.num(minX) + ", ");
    } else {
      buf.append("?, ");
    }
    if (hasY) {
      buf.append(Debug.num(minY) + ", ");
    } else {
      buf.append("?, ");
    }
    buf.append(" -- " + Debug.num((maxX - minX)) + " x " + Debug.num(maxY - minY));
    buf.append("]");
    return buf.toString();
  }

  /**
   * Tells you if the bounding box has both x and y dimensions (e.g. is not empty.)
   */
  public boolean isValid() {
    return (hasX && hasY);
  }

  /**
   * Gives you the minimum X value.
   */
  public double getX() {
    if (!hasX) {
      Debug.stacktrace("There's no real x value yet!", 4);
    }
    return minX;
  }

  public double getMinX() {
    return getX();
  }

  public double getMaxX() {
    if (!hasX) {
      Debug.stacktrace("There's no real x value yet!", 4);
    }
    return maxX;
  }

  public double getMinY() {
    return getY();
  }

  public double getMaxY() {
    if (!hasY) {
      Debug.stacktrace("There's no real y value yet!", 4);
    }
    return maxY;
  }

  /**
   * The total width, calculated by the difference of the max and min X values.
   */
  public double getWidth() {
    if (!hasX)
      Debug.stacktrace("There's no real x value yet!", 4);
    return maxX - minX;
  }

  /**
   * Returns the integer form of the width, plus one.
   */
  public int getWidthInt() {
    // return (int) Math.ceil(getWidth()) + 1;
    return (int) Math.ceil(getWidth());
  }

  /**
   * Like getWidthInt, but for height.
   */
  public int getHeightInt() {
    // return (int) Math.ceil(getHeight()) + 1;
    return (int) Math.ceil(getHeight());
  }

  /**
   * Gives you the minimum Y value.
   */
  public double getY() {
    if (!hasY)
      Debug.stacktrace("There's no real y value yet!", 4);
    return minY;
  }

  /**
   * The total height, calculated from the difference of the max and min Y values.
   */
  public double getHeight() {
    if (!hasY)
      Debug.stacktrace("There's no real y value yet!", 4);
    return maxY - minY;
  }

  /**
   * The distance from one corner to the opposite.
   */
  public double getDiagonal() {
    return Math.hypot(getWidth(), getHeight());
  }

  /**
   * Forces this bounding box to include the extents of the given bounding box.
   */
  public void add(BoundingBox other) {
    add(other.getRectangle());
  }

  /**
   * Forces the bounding box to include the extents of the given rectangle.
   */
  public void add(Rectangle2D rec) {
    add(rec.getX(), rec.getY());
    add(rec.getX() + rec.getWidth(), rec.getY() + rec.getHeight());
  }

  public void add(Rectangle2D rec, double padding) {
    add(rec.getX(), rec.getY(), padding);
    add(rec.getX() + rec.getWidth(), rec.getY() + rec.getHeight(), padding);
  }

  /**
   * Forces the bounding box to include points within the given padding distance of the provided
   * point (using manhattan block distance).
   */
  public void add(double x, double y, double padding) {
    add(x + padding, y + padding);
    add(x - padding, y + padding);
    add(x + padding, y - padding);
    add(x - padding, y - padding);
  }

  public void add(Point2D pt, double padding) {
    add(pt.getX() + padding, pt.getY() + padding);
    add(pt.getX() - padding, pt.getY() + padding);
    add(pt.getX() + padding, pt.getY() - padding);
    add(pt.getX() - padding, pt.getY() - padding);
  }

  /**
   * Forces the bounding box to include the following point.
   */
  public void add(Point2D pt) {
    add(pt.getX(), pt.getY());
  }

  public void add(double x, double y) {
    if (!hasX) {
      maxX = x;
      minX = maxX;
      hasX = true;
    } else {
      maxX = Math.max(maxX, x);
      minX = Math.min(minX, x);
    }
    if (!hasY) {
      maxY = y;
      minY = maxY;
      hasY = true;
    } else {
      maxY = Math.max(maxY, y);
      minY = Math.min(minY, y);
    }
  }

  /**
   * Returns a rectangle that exactly covers the bounding box.
   */
  public Rectangle2D getRectangle() {
    return new Rectangle2D.Double(getX(), getY(), getWidth(), getHeight());
  }

  /**
   * Returns an integer-defined rectangle that is at least as big as the double form. This takes the
   * floor function for the X and Y coordinates, and uses getWidthInt/getHeightInt for the rectangle
   * sizes.
   */
  public Rectangle2D getRectangleLoose() {
    return new Rectangle2D.Double(Math.floor(getX()), Math.floor(getY()), getWidthInt(),
        getHeightInt());
  }

  public void grow(double scaleFactor) {
    double w = getWidth();
    double newW = w * scaleFactor;
    double h = getHeight();
    double newH = h * scaleFactor;
    double dw = (newW - w) / 2;
    double dh = (newH - h) / 2;
    minX -= dw;
    maxX += dw;
    minY -= dh;
    maxY += dh;
  }

  public void bug(String what) {
    Debug.out("BoundingBox", what);
  }

  public Pt getTopRight() {
    return new Pt(minX, maxY);
  }

  public Pt getBotRight() {
    return new Pt(maxX, maxY);
  }

  public Pt getTopLeft() {
    return new Pt(minX, minY);
  }

  public Pt getBotLeft() {
    return new Pt(maxX, minY);
  }

  public void translateToOrigin() {
    translate(-minX, -minY);
  }

  public void addAll(List<Pt> points) {
    for (Pt pt : points) {
      add(pt);
    }
  }

  public void addAll(Pt... points) {
    for (Pt pt : points) {
      add(pt);
    }
  }

}
