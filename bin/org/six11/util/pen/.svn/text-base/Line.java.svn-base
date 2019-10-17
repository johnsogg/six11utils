// $Id$

package org.six11.util.pen;

import java.awt.geom.Line2D;
import java.util.HashMap;
import java.util.Map;

/**
 * A Line that behaves how I want it to as I go through a series of
 * points. This is just for clearer code.
 **/
public class Line extends Line2D.Double {

  Pt a;
  Pt b;
  protected Map<String, Object> attribs;

  public Line(Pt a, Pt b) {
    super(a, b);
    this.a = a;
    this.b = b;
  }

  public Line() {
    // this line has no points!
  }
  
  public Line(Pt vertex, Vec direction) {
    this(vertex, new Pt(vertex.getX() + direction.getX(),
			vertex.getY() + direction.getY()));
  }

  /**
   * Returns true if the other object is a Line and their start and
   * end points are at the same location (using
   * Pt.isSameLocation(pt)).
   */
  public boolean equals(Object other) {
    boolean ret = false;
    if (other instanceof Line) {
      Line otherLine = (Line) other;
      ret = (otherLine.getStart().isSameLocation(getStart()) &&
	     otherLine.getEnd().isSameLocation(getEnd()));
    }
    return ret;
  }

  public boolean isValid() {
    return (a != null) && (b != null);
  }

  public Pt getStart() {
    return a;
  }

  public void push(Pt newEnd) {
    a = b;
    b = newEnd;
    redo();
  }

  private void redo() {
    if (isValid()) {
      setLine(a, b);
    }
  }

  public void setStart(Pt a) {
    this.a = a;
    redo();
  }

  public void setEnd(Pt b) {
    this.b = b;
    redo();
  }

  public Pt getEnd() {
    return b;
  }

  public double getLength() {
    double dx = Math.abs(getStart().getX() - getEnd().getX());
    double dy = Math.abs(getStart().getY() - getEnd().getY());
    return Math.hypot(dx, dy);
  }

  public boolean isAdjacentTo(Line other) {
    return (getStart().equals(other.getStart()) || getStart().equals(other.getEnd()) ||
	    getEnd().equals(other.getStart()) || getEnd().equals(other.getEnd()));
  }

  public Pt getMidpoint() {
    return new Pt((a.getX() + b.getX()) / 2.0,
		  (a.getY() + b.getY()) / 2.0);
  }

  public void clear() {
    a = null;
    b = null;
  }
  
  public Map<String, Object> getAttribs() {
    if (attribs == null) {
      attribs = new HashMap<String, Object>();
    }
    return attribs;
  }
  
  public boolean hasAttribute(String name) {
    return getAttribs().containsKey(name);
  }
  
  public void setAttribute(String name, Object value) {
    getAttribs().put(name, value);
  }

  public Object getAttribute(String name) {
    return getAttribs().get(name);
  }

  public void removeAttribute(String name) {
    getAttribs().remove(name);
  }
}
