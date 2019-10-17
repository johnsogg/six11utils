package org.six11.util.spud;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.six11.util.pen.CircleArc;
import org.six11.util.pen.Pt;
import org.six11.util.pen.Vec;

public abstract class Geom extends Node {

  String name;
  Map<String, Slot> slots;
  List<Constraint> relatedConstraints;
  Set<Geom> solutionSpace;

  public enum Type {
    Point, Line, Circle, Number, NearPoint, PointSet, Rectangle, Vector, Infinity, Nothing
  }

  public Geom() {
    this.slots = new HashMap<String, Slot>();
    this.relatedConstraints = new ArrayList<Constraint>();
    this.solutionSpace = new HashSet<Geom>();
  }

  public abstract Type getType();

  public String getName() {
    String ret = null;
    if (name == null) {
      ret = "<no name>";
    } else {
      ret = "<" + name + ">";
    }
    return ret;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void addConstraint(Constraint c) {
    if (!relatedConstraints.contains(c)) {
      relatedConstraints.add(c);
    }
  }

  public void addSlot(String slotName) {
    slots.put(slotName, new Slot(slotName));
  }

  public String getMondoDebugString(String leadingSpace) {
    StringBuilder buf = new StringBuilder();
    for (String slotName : slots.keySet()) {
      Slot slot = slots.get(slotName);
      buf.append(leadingSpace + slot.toString() + "\n");
    }
    return buf.toString();
  }

  public abstract String getDebugString();

  public void offer(CLine line) {
    warn("Warning: offer(Pt) not implemented by " + getClass().getSimpleName() + ". Override it!");
  }

  public void offer(Pt data) {
    warn("Warning: offer(Pt) not implemented by " + getClass().getSimpleName() + ". Override it!");
  }

  public void offer(Pt[] data) {
    warn("Warning: offer(Pt[]) not implemented by " + getClass().getSimpleName() + ". Override it!");
  }

  public void offer(Vec dir) {
    warn("Warning: offer(Vec) not implemented by " + getClass().getSimpleName() + ". Override it!");
  }

  public void offer(CircleArc circle) {
    warn("Warning: offer(CircleArc) not implemented by " + getClass().getSimpleName()
        + ". Override it!");
  }

  public void offer(CCircle circle) {
    warn("Warning: offer(CCircle) not implemented by " + getClass().getSimpleName()
        + ". Override it!");
  }

  public void offer(double amt) {
    warn("Warning: offer(double) not implemented by " + getClass().getSimpleName()
        + ". Override it!");
  }

  public void solveRelatedConstraints() {
    if (isSolved()) {
      for (Constraint c : relatedConstraints) {
        c.solveSafely();
      }
    }
  }

  protected boolean slotsValid(String... slotNames) {
    boolean ret = true;
    for (String name : slotNames) {
      Slot slot = slots.get(name);
      if (slot != null) {
        ret = slot.valid;
      } else {
        ret = false;
      }
      if (!ret) {
        break;
      }
    }
    return ret;
  }

  public void addSolutionSpaceHint(CNearPoint pt) {
    solutionSpace.add(pt);
  }

  public Geom getSolutionSpace() {
    Geom space = new Infinity();
    Set<Geom> nondiscrete = new HashSet<Geom>();
    for (Geom thing : solutionSpace) {
      if (!thing.isDiscrete()) {
        nondiscrete.add(thing);
      } else {
        space = thing.intersect(space);
      }
    }
    for (Geom thing : nondiscrete) {
      space = thing.intersect(space);
    }
    return space;
  }

  /**
   * A discrete geometric element is one for which there is a closed-form, finite, discrete formula.
   * This includes points, lines, circles, pointsets, and the like. A non-discrete geometric element
   * represents a geometric region in which a solution might be, such as a half-plane (e.g. points
   * above a certain line) or 'near points' that are used as a reference to decide which point among
   * a discrete set should be used.
   * 
   * When intersecting a bunch of geometries, the discrete and non-discrete elements are kept
   * separate until a single discrete item remains. It is then intersected with each non-discrete
   * element.
   * 
   * @return
   */
  public abstract boolean isDiscrete();

  public Geom intersect(Geom other) {
    Geom ret = null;
    if (other instanceof CPoint) {
      ret = intersectPoint((CPoint) other);
    } else if (other instanceof CLine) {
      ret = intersectLine((CLine) other);
    } else if (other instanceof Infinity) {
      ret = this;
    } else if (other instanceof Nothing) {
      ret = other;
    } else if (other instanceof CCircle) {
      ret = intersectCircle((CCircle) other);
    } else if (other instanceof CPointSet) {
      ret = intersectPointSet((CPointSet) other);
    } else {
      if (other == null) {
        warn("Warning: I don't know how to intersect " + getClass().getSimpleName() + " and "
            + null);
      } else {
        warn("Warning: I don't know how to intersect " + getClass().getSimpleName() + " and "
            + other.getClass().getSimpleName());
      }
    }
    return ret;
  }

  public abstract Geom intersectCircle(CCircle circ);

  public abstract Geom intersectPoint(CPoint pt);

  public abstract Geom intersectPointSet(CPointSet ptset);

  public abstract Geom intersectLine(CLine line);

}
