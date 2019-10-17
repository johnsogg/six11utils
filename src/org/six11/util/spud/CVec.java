package org.six11.util.spud;

import org.six11.util.pen.Vec;

/**
 * 
 *
 * @author Gabe Johnson <johnsogg@cmu.edu>
 */
public class CVec extends Geom {

  public CVec() {
    super();
    addSlot("Dir");
  }

  public CVec(double dx, double dy) {
    this();
    offer(new Vec(dx, dy).getUnitVector());
  }

  public String getDebugString() {
    return getName() + " " + slots.get("Dir").toString();
  }
  
  public void offer(Vec dir) {
    slots.get("Dir").setValue(dir);
    solved = true;
  }

  public String getHumanReadableName() {
    return "dir " + getName();
  }

  public Vec getDir() {
    return slots.get("Dir").getDirection();
  }

  public Geom intersectCircle(CCircle circ) {
    return new Nothing();
  }

  public Geom intersectLine(CLine line) {
    return new Nothing();
  }

  public Geom intersectPoint(CPoint pt) {
    return new Nothing();
  }

  public Geom intersectPointSet(CPointSet ptset) {
    return new Nothing();
  }

  @Override
  public boolean isDiscrete() {
    return true;
  }

  public Type getType() {
    return Type.Vector;
  }
}
