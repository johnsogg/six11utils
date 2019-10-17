package org.six11.util.spud;

import org.six11.util.pen.Pt;

/**
 * 
 * 
 * @author Gabe Johnson <johnsogg@cmu.edu>
 */
public class CPoint extends Geom {

  public CPoint() {
    super();
    addSlot("Pt");
  }

  /**
   * @param pt
   */
  public CPoint(Pt pt) {
    this();
    offer(pt);
  }

  public String getHumanReadableName() {
    return "point " + getName();
  }

  public String getDebugString() {
    String ret = null;
    if (slots.get("Pt").isValid()) {
      ret = slots.get("Pt").toString();
    } else if (solutionSpace.size() > 0) {
      ret = "Solution Space: " + solutionSpace.size() + " items";
    } else {
      ret = "<unknown>";
    }
    return getName() + " " + ret;
  }

  public void offer(CLine line) {
    solutionSpace.add(line);
    solveSolutionSpace();
  }

  private void solveSolutionSpace() {
    Geom restrictedSolutionSpace = getSolutionSpace();
    if (restrictedSolutionSpace instanceof CPoint) {
      offer(((CPoint) restrictedSolutionSpace).getPt());
    }
  }

  public void offer(CCircle arc) {
    solutionSpace.add(arc);
    solveSolutionSpace();
  }

  public void offer(Pt data) {
    slots.get("Pt").setValue(data);
    solved = true;
  }

  public Pt getPt() {
    return slots.get("Pt").getPt();
  }

  public Geom intersectCircle(CCircle circ) {
    return circ.intersectPoint(this);
  }

  public Geom intersectLine(CLine line) {
    return line.intersectPoint(this);
  }

  public Geom intersectPoint(CPoint pt) {
    Geom ret = new Nothing();
    if (pt.getPt().isSameLocation(this.getPt())) {
      ret = this;
    }
    return ret;
  }

  public Geom intersectPointSet(CPointSet ptset) {
    Geom ret = new Nothing();
    for (Pt pt : ptset.getPoints()) {
      if (pt.isSameLocation(this.getPt())) {
        ret = this;
        break;
      }
    }
    return ret;
  }

  @Override
  public boolean isDiscrete() {
    return true;
  }

  public Type getType() {
    return Type.Point;
  }

}
