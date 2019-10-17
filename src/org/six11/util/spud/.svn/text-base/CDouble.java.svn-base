package org.six11.util.spud;

/**
 * 
 * 
 * @author Gabe Johnson <johnsogg@cmu.edu>
 */
public class CDouble extends Geom {

  public CDouble() {
    super();
    addSlot("Double");
  }

  protected CDouble(double val) {
    this();
    offer(val);
  }

  public double getDouble() {
    return slots.get("Double").getDouble();
  }

  public void offer(double amt) {
    slots.get("Double").setValue(amt);
    solved = true;
  }

  public String getDebugString() {
    return getName() + " CDouble: " + slots.get("Double");
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

  public String getHumanReadableName() {
    return "double " + getName();
  }

  @Override
  public boolean isDiscrete() {
    return true;
  }

  public Type getType() {
    return Type.Number;
  }

}
