package org.six11.util.spud;

import org.six11.util.pen.Pt;

public class CNearPoint extends Geom {

  public CNearPoint() {
    super();
    addSlot("Pt");
  }
  
  public CNearPoint(Pt pt) {
    this();
    offer(pt);
  }
  
  public void offer(Pt data) {
    slots.get("Pt").setValue(data);
    solved = true;
  }

  @Override
  public String getDebugString() {
    String ret = null;
    if (slots.get("Pt").isValid()) {
      ret = slots.get("Pt").toString();
    } else {
      ret = "<unknown>";
    }
    return getName() + " " + ret;
  }

  @Override
  public boolean isDiscrete() {
    return false;
  }

  @Override
  public Geom intersectCircle(CCircle circ) {
    return circ;
  }

  @Override
  public Geom intersectPoint(CPoint pt) {
    return pt;
  }

  @Override
  public Geom intersectPointSet(CPointSet ptset) {
    Geom ret = new Nothing();
    double best = Double.MAX_VALUE;
    Pt pt = getPt();
    for (Pt other : ptset.getPoints()) {
      double thisDist = pt.distance(other);
      if (thisDist < best) {
        ret = new CPoint(other);
        best = thisDist;
      }
    }
    return ret;
  }

  public Pt getPt() {
    return slots.get("Pt").getPt();
  }
  
  @Override
  public Geom intersectLine(CLine line) {
    // TODO Auto-generated method stub
    warn(getClass().getSimpleName() + ".intersectLine not yet implemented.");
    return null;
  }

  @Override
  public String getHumanReadableName() {
    return "near-point " + getName();
  }

  public Type getType() {
    return Type.NearPoint;
  }

}
