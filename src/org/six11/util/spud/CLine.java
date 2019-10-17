package org.six11.util.spud;

import org.six11.util.pen.Functions;
import org.six11.util.pen.Line;
import org.six11.util.pen.Pt;
import org.six11.util.pen.Vec;

/**
 * 
 * 
 * @author Gabe Johnson <johnsogg@cmu.edu>
 */
public class CLine extends Geom {

  /**
   * 
   */
  public CLine() {
    super();

    ///             All slots contain primitive data classes. For example, a slot named 
    ///             "Point" refers to a Pt object.

    addSlot("PtA"); /// <------- These slots are for the components of a line that can
    addSlot("PtB"); ///          combine to form a composite value. Any two of them can
    addSlot("Dir"); ///          form the composite Line object.

    addSlot("Line"); // <------- Once a composite value is known, store it here.
  }

  /**
   * Establishes constraints that ensures the given points are on a line. The line is returned.
   */
  public static CLine makeLine(ConstraintModel model, CPoint p1, CPoint p2) {
    CLine ret = new CLine();
    model.addConstraint(new CPointOnLine(ret, p1));
    model.addConstraint(new CPointOnLine(ret, p2));
    return ret;
  }

  public void offer(Pt pt) {
    if (!slots.get("PtA").isValid()) {
      slots.get("PtA").setValue(pt);
    } else {
      slots.get("PtB").setValue(pt);
    }
    checkSolved();
  }

  public void offer(Vec dir) {
    slots.get("Dir").setValue(dir);
    checkSolved();
  }

  private void checkSolved() {
    if (slotsValid("PtA", "Dir")) {
      Line completeLine = new Line(getPoint(), getDir());
      slots.get("Line").setValue(completeLine);
      solved = true;
    } else if (slotsValid("PtA", "PtB")) {
      Vec dir = new Vec(slots.get("PtA").getPt(), slots.get("PtB").getPt()).getUnitVector();
      offer(dir); // this will cause checkSolved() to run again, and the PtA, Dir part will run.
    }
  }

  public Geom intersectLine(CLine line) {
    Geom ret = null;
    Line other = line.getLine();
    Line myLine = getLine();
    Pt where = Functions.getIntersectionPoint(other, myLine);
    if (where != null) {
      CPoint retPt = new CPoint();
      retPt.offer(where);
      ret = retPt;
    } else {
      ret = new Nothing();
    }
    return ret;
  }

  public Line getLine() {
    return (Line) slots.get("Line").value;
  }

  public Pt getPoint() {
    return (Pt) slots.get("PtA").value;
  }

  public Vec getDir() {
    return (Vec) slots.get("Dir").value;
  }

  public boolean isSlotValid(String slotName) {
    return slots.get(slotName) != null && slots.get(slotName).isValid();
  }

  public Geom intersectPoint(CPoint cpt) {
    Geom ret = null;
    Line myLine = getLine();
    Pt pt = cpt.getPt();
    double dist = Functions.getDistanceBetweenPointAndLine(pt, myLine);
    if (dist < Functions.EQ_TOL) {
      CPoint retPt = new CPoint();
      retPt.offer(pt);
      ret = retPt;
    } else {
      ret = new Nothing();
    }
    return ret;
  }

  public String getDebugString() {
    return getName() + " Line; " + slots.get("PtA") + ", " + slots.get("PtB") + ", "
        + slots.get("Dir");
  }

  public String getHumanReadableName() {
    return "line " + getName();
  }

  public Geom intersectCircle(CCircle circIntersect) {
    return circIntersect.intersectLine(this);
  }

  public Geom intersectPointSet(CPointSet ptset) {
    warn("Geom.intersectPointSet not implemented yet!");
    return null;
  }

  @Override
  public boolean isDiscrete() {
    return true;
  }

  public Type getType() {
    return Type.Line;
  }
  

}
